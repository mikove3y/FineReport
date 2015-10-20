/*
 * Copyright(c) 2001-2010, FineReport Inc, All Rights Reserved.
 */
package com.fr.start;

import com.fr.base.FRContext;
import com.fr.design.DesignerEnvManager;
import com.fr.design.ExtraDesignClassManager;
import com.fr.design.RestartHelper;
import com.fr.design.file.HistoryTemplateListPane;
import com.fr.design.file.MutilTempalteTabPane;
import com.fr.design.file.TemplateTreePane;
import com.fr.design.fun.GlobalListenerProvider;
import com.fr.design.mainframe.DesignerFrame;
import com.fr.design.mainframe.TemplatePane;
import com.fr.design.mainframe.toolbar.ToolBarMenuDock;
import com.fr.design.utils.DesignUtils;
import com.fr.env.SignIn;
import com.fr.file.FILE;
import com.fr.file.FILEFactory;
import com.fr.file.FileFILE;
import com.fr.general.*;
import com.fr.stable.ArrayUtils;
import com.fr.stable.BuildContext;
import com.fr.stable.OperatingSystem;
import com.fr.stable.ProductConstants;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Method;

/**
 * The main class of Report Designer.
 */
public abstract class BaseDesigner extends ToolBarMenuDock {

    private static final int LOAD_TREE_MAXNUM = 10;

    private static final int MESSAGEPORT = 51462;

    public BaseDesigner(String[] args) {
        if (isDebug()) {
            setDebugEnv();
        }
        RestartHelper.deleteRecordFilesWhenStart();

        DesignUtils.setPort(getStartPort());
        // ����˿ڱ�ռ���� ˵�������Ѿ�������һ��,Ҳ����˵���Ѿ�����һ������������������ֻҪ����������������ͺ���
        if (DesignUtils.isStarted()) {
            DesignUtils.clientSend(args);
            return;
        }
        BuildContext.setBuildFilePath(buildPropertiesPath());

        //�����������λ�ò�������������Ϊ��Ӱ�������л�������
        initLanguage();

        SplashWindow splashWindow = new SplashWindow(createSplashPane());
        if (args != null) {
            for (String arg : args) {
                if (ComparatorUtils.equals(arg, "demo")) {
                    DesignerEnvManager.getEnvManager().setCurrentEnv2Default();
                    StartServer.browerDemoURL();
                    break;
                }
            }
        }

        // ��ʼ��look and feel.�����Ԥ����֮ǰִ������ΪlookAndFeel��Ķ�����Ԥ����ʱҲҪ�õ�
        DesignUtils.initLookAndFeel();

        DesignUtils.creatListeningServer(getStartPort(), startFileSuffix());
        // ��ʼ��Log Handler
        DesignerEnvManager.loadLogSetting();
        DesignerFrame df = createDesignerFrame();

        // Ĭ�ϼ��ع���Ŀ¼�����ڶ�ȡLicense
        switch2LastEnv();

        initDefaultFont();
        // �����ȳ�ʼ��Env��ȥstartModule, ��Ȼ�ᵼ��lic��ȡ����
        ModuleContext.startModule(module2Start());

        // �ٴμ��ع���Ŀ¼�����ڶ�ȡ����Ŀ¼�µĸ��ֲ��
        switch2LastEnv();

        ModuleContext.clearModuleListener();
        collectUserInformation();
        showDesignerFrame(args, df, false);
        for (int i = 0; !TemplateTreePane.getInstance().getTemplateFileTree().isTemplateShowing() && i < LOAD_TREE_MAXNUM; i++) {
            TemplateTreePane.getInstance().getTemplateFileTree().refresh();
        }

        splashWindow.setVisible(false);
        splashWindow.dispose();

        bindGlobalListener();
    }

    private void bindGlobalListener() {
        GlobalListenerProvider[] providers = ExtraDesignClassManager.getInstance().getGlobalListenerProvider();
        if (ArrayUtils.isNotEmpty(providers)) {
            for (GlobalListenerProvider provider : providers) {
                Toolkit.getDefaultToolkit().addAWTEventListener(provider.listener(), AWTEvent.KEY_EVENT_MASK);
            }
        }
    }

    protected String[] startFileSuffix() {
        return new String[]{".cpt", ".xls", ".xlsx", ".frm", ".form", ".cht", ".chart"};
    }

    protected DesignerFrame createDesignerFrame() {
        return new DesignerFrame(this);
    }

    protected int getStartPort() {
        return MESSAGEPORT;
    }

    protected void initLanguage() {
        //�������λ�ò�������������Ϊ��Ӱ�������л�������
        FRContext.setLanguage(DesignerEnvManager.getEnvManager().getLanguage());
    }

    protected void initDefaultFont() {

    }

    /**
     * build��·��
     *
     * @return build��·��
     */
    public String buildPropertiesPath() {
        return "/com/fr/stable/build.properties";
    }


    protected SplashPane createSplashPane() {
        return new SplashPane();
    }

    //��VM options�����-Ddebug=true����
    private boolean isDebug() {
        return "true".equals(System.getProperty("debug"));
    }

    private static final int DEBUG_PORT = 51463;

    //�˿ڸ�һ�£����������ļ���һ�¡�����������������������жԱȵ���
    private void setDebugEnv() {
        DesignUtils.setPort(DEBUG_PORT);
        DesignerEnvManager.setEnvFile(new File(ProductConstants.getEnvHome() + File.separator + ProductConstants.APP_NAME + "Env_debug.xml"));
    }

    private void switch2LastEnv() {
        try {
            String current = DesignerEnvManager.getEnvManager().getCurEnvName();
            SignIn.signIn(DesignerEnvManager.getEnvManager().getEnv(current));
            if (!FRContext.getCurrentEnv().testServerConnectionWithOutShowMessagePane()) {
                throw new Exception(Inter.getLocText("Datasource-Connection_failed"));
            }
        } catch (Exception e) {
            TemplatePane.getInstance().dealEvnExceptionWhenStartDesigner();
        }
    }

    private void showDesignerFrame(String[] args, final DesignerFrame df,
                                   boolean isException) {
        try {
            FILE file = null;
            if (args != null && args.length > 0) {
                // p:��Ҫ����������ļ�,������벻��ɾ��.
                for (String arg : args) {
                    if (ComparatorUtils.equals("demo", arg)) {
                        file = FILEFactory.createFILE(FILEFactory.ENV_PREFIX + DesignerEnvManager.getEnvManager().getLastOpenFile());
                        break;
                    }
                    File f = new File(arg);
                    String path = f.getAbsolutePath();
                    boolean pathends1 = path.endsWith(".cpt")
                            || path.endsWith(".xls");
                    boolean pathends2 = path.endsWith(".xlsx")
                            || path.endsWith(".frm");
                    boolean pathends3 = path.endsWith(".form")
                            || path.endsWith(".cht");
                    boolean pathends4 = pathends1 || pathends2 || pathends3;
                    if (pathends4 || path.endsWith(".chart")) {
                        file = new FileFILE(f);
                    }
                }
            } else {
                file = FILEFactory.createFILE(FILEFactory.ENV_PREFIX
                        + DesignerEnvManager.getEnvManager().getLastOpenFile());
            }
            if (file.exists() && !isException) {
                df.openTemplate(file);
            } else {
                df.addAndActivateJTemplate();
                MutilTempalteTabPane.getInstance().setTemTemplate(HistoryTemplateListPane.getInstance().getCurrentEditingTemplate());
            }
            if (OperatingSystem.isMacOS()) {
                enableFullScreenMode(df);
            }
            df.addWindowListener(new WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                    df.getSelectedJTemplate().requestGridFocus();
                }
            });
            df.setVisible(true);
        } catch (Exception e) {
            FRLogger.getLogger().error(e.getMessage());
            if (!isException) {
                showDesignerFrame(args, df, true);
            } else {
                System.exit(0);
            }
        }
    }


    /**
     * @param window
     */
    private void enableFullScreenMode(Window window) {
        String className = "com.apple.eawt.FullScreenUtilities";
        String methodName = "setWindowCanFullScreen";

        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, new Class<?>[]{
                    Window.class, boolean.class});
            method.invoke(null, window, true);
        } catch (Throwable t) {
            FRLogger.getLogger().error("Full screen mode is not supported");
        }
    }


    protected abstract String module2Start();

    // �ռ��û���Ϣ��
    protected void collectUserInformation() {

    }
}