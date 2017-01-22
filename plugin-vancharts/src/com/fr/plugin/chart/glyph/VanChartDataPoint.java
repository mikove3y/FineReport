package com.fr.plugin.chart.glyph;

import com.fr.base.background.ColorBackground;
import com.fr.base.chart.Glyph;
import com.fr.chart.base.AttrAlpha;
import com.fr.chart.base.AttrBackground;
import com.fr.chart.base.AttrBorder;
import com.fr.chart.base.TextAttr;
import com.fr.chart.chartglyph.DataPoint;
import com.fr.chart.chartglyph.GeneralInfo;
import com.fr.chart.chartglyph.ShapeGlyph;
import com.fr.general.Background;
import com.fr.general.FRFont;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.plugin.chart.attr.VanChartAttrHelper;
import com.fr.plugin.chart.base.AttrFloatColor;
import com.fr.plugin.chart.base.AttrLabel;
import com.fr.plugin.chart.base.AttrTooltip;
import com.fr.stable.Constants;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.web.Repository;

import java.awt.*;

/**
 * Created by Mitisky on 15/10/18.
 */
public class VanChartDataPoint extends DataPoint {
    protected static final int DEFAULT_SIZE = 9;
    private static final long serialVersionUID = 3439431662725988324L;
    //�����⼸�����������������õģ��п���Ϊnil�����ڴ���ǰ̨
    private AttrLabel label;
    private AttrTooltip tooltip;
    protected AttrBackground color;
    private AttrFloatColor floatColor;
    private AttrBorder border;
    private AttrAlpha alpha;

    //����Ƕ�����ϵ�����õı�ǩ���ԡ�����ǩ��ʱ���õ���
    private AttrLabel defaultAttrLabel;
    //�����Ĭ����ɫ����������ֻ������͸���ȣ���Ҫ���Ĭ����ɫ����ɫ���ԡ�
    //����ϵ�е�Ĭ����ɫ��
    protected Color defaultColor;

    /**
     * ���ø����ݵ�� ��ǩ����
     * @param label ��ǩ����
     */
    public void setLabel(AttrLabel label) {
        this.label = label;
    }

    /**
     * ���ظ����ݵ�� ��ǩ����
     * @return ��ǩ����
     */
    public AttrLabel getLabel() {
        return label;
    }

    /**
     * ���ø����ݵ�� ��ʾ����
     * @param tooltip ��ʾ����
     */
    public void setTooltip(AttrTooltip tooltip) {
        this.tooltip = tooltip;
    }

    /**
     * ���ظ����ݵ�� ��ʾ����
     * @return ��ʾ����
     */
    public AttrTooltip getTooltip() {
        return tooltip;
    }

    public void setAlpha(AttrAlpha alpha) {
        this.alpha = alpha;
    }

    public void setBorder(AttrBorder border) {
        this.border = border;
    }

    public void setColor(AttrBackground color) {
        this.color = color;
    }

    public AttrBackground getColor(){
        return this.color;
    }

    public void setFloatColor(AttrFloatColor floatColor) {
        this.floatColor = floatColor;
    }

    public void setDefaultAttrLabel(AttrLabel defaultAttrLabel) {
        this.defaultAttrLabel = defaultAttrLabel;
    }

    public void setDefaultColor(Color color) {
        this.defaultColor = color;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public AttrBorder getBorder() {
        return border;
    }

    public AttrAlpha getAlpha() {
        return alpha;
    }

    /**
     * ����ϵ�е�ı�ǩ
     */
    public void drawLabel(Graphics g, int resolution) {
        // kunsnta: ���ж��¿�ֵ, ����. ��ΪPlotGlyph�Ǳ��ǲ���������.
        if(this.isValueIsNull()) {
            return;
        }
        if(this.getDataLabel() == null){
            return;
        }
        Graphics2D g2d = ((Graphics2D)g);

        Object oldHint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        //����ǩ
        if(isUseCustomFont()){
            drawCustomFontLabelText(g2d, resolution);
        } else {
            drawAutoFontLabelText(g2d, resolution);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
    }

    /**
     * �Ƿ����Զ�����ʽ
     * @return �Ƿ����Զ�����ʽ
     */
    public boolean isUseCustomFont() {
        return label == null ? defaultAttrLabel.isCustom() : label.isCustom();
    }

    public void drawCustomFontLabelText(Graphics g, int resolution) {
        this.getDataLabel().draw(g, resolution);
    }

    public void drawAutoFontLabelText(Graphics g, int resolution) {
        String showText = getDataLabel().getText();
        TextAttr oldTextAttr = this.getDataLabel().getTextAttr();

        String cateAndSeriesText = VanChartAttrHelper.getCateAndSeries(label == null ? defaultAttrLabel.getContent() : label.getContent(), this);
        String valueAndPercentText = VanChartAttrHelper.getValueAndPercent(label == null ? defaultAttrLabel.getContent() : label.getContent(), this);
        TextAttr textAttr = new TextAttr();
        this.getDataLabel().setTextAttr(textAttr);
        FRFont cateAndSeriesFont = isOutside() ? getOutSideCateAndSeriesFont() : getInSideCateAndSeriesFont();
        FRFont valueAndPercentFont = isOutside() ? getOutSideValueAndPercentFont() : getInSideValueAndPercentFont();

        if(StringUtils.isNotEmpty(cateAndSeriesText) && StringUtils.contains(showText, cateAndSeriesText)){
            textAttr.setFRFont(cateAndSeriesFont);
            this.getDataLabel().setText(cateAndSeriesText);
            this.getDataLabel().draw(g, resolution);
        }

        if(StringUtils.isNotEmpty(valueAndPercentText) && StringUtils.contains(showText, valueAndPercentText)){
            textAttr.setFRFont(valueAndPercentFont);
            this.getDataLabel().setText(valueAndPercentText);
            this.getDataLabel().draw(g, resolution);
        }

        this.getDataLabel().setTextAttr(oldTextAttr);
    }

    /**
     * �Ƿ���������
     * @return �Ƿ���������
     */
    public boolean isOutside() {
        return label == null ? defaultAttrLabel.getPosition() == Constants.OUTSIDE : label.getPosition() == Constants.OUTSIDE;
    }

    public FRFont getInSideCateAndSeriesFont() {
        return FRFont.getInstance("verdana", Font.BOLD, DEFAULT_SIZE, Color.white);
    }

    public FRFont getInSideValueAndPercentFont() {
        return FRFont.getInstance("verdana", Font.PLAIN, DEFAULT_SIZE, Color.white);
    }

    public FRFont getOutSideCateAndSeriesFont() {
        Color backgroundColor = getDataPointBackgroundColor();
        if(backgroundColor != null){
            return FRFont.getInstance("verdana", Font.BOLD, DEFAULT_SIZE, backgroundColor);
        }
        return FRFont.getInstance("verdana", Font.BOLD, DEFAULT_SIZE, Color.black);
    }

    public FRFont getOutSideValueAndPercentFont() {
        Color backgroundColor = getDataPointBackgroundColor();
        if(backgroundColor != null){
            return FRFont.getInstance("verdana", Font.PLAIN, DEFAULT_SIZE, backgroundColor);
        }
        return FRFont.getInstance("verdana", Font.PLAIN, DEFAULT_SIZE, Color.black);
    }

    public Color getDataPointBackgroundColor() {
        Glyph glyph = this.getDrawImpl();
        if(glyph instanceof ShapeGlyph) {
            ShapeGlyph shapeGlyph = (ShapeGlyph)glyph;
            GeneralInfo info = shapeGlyph.getGeneralInfo();
            Background back = info.getBackground();
            if(back instanceof ColorBackground && ((ColorBackground)back).getColor() != null) {
                return ((ColorBackground)back).getColor();
            }
        }
        return null;
    }

    /**
     * תΪjson����
     *  @param repo ����
     * @return ����json
     * @throws com.fr.json.JSONException �״�
     */
    public JSONObject toJSONObject(Repository repo) throws JSONException {
        JSONObject js = new JSONObject();

        addXYJSON(js);

        if(label != null && label.isEnable()){
            js.put("dataLabels", label.toJSONObject(repo));
        }

        if(tooltip != null && tooltip.isEnable()){
            js.put("tooltip", tooltip.toJSONObject(repo));
        }

        if(color != null) {
            float alpha = this.alpha == null ? 1 : this.alpha.getAlpha();
            ColorBackground background = (ColorBackground)color.getSeriesBackground();
            js.put("color", VanChartAttrHelper.getRGBAColorWithColorAndAlpha(background.getColor(), alpha));
        } else if (alpha != null && defaultColor != null){
            js.put("color", VanChartAttrHelper.getRGBAColorWithColorAndAlpha(defaultColor, alpha.getAlpha()));
        }

        if(floatColor != null) {
            js.put("mouseOverColor", StableUtils.javaColorToCSSColor(floatColor.getSeriesColor()));
        }

        if(border != null) {
            js.put("borderWidth", VanChartAttrHelper.getAxisLineStyle(border.getBorderStyle()));
            js.put("borderColor", StableUtils.javaColorToCSSColor(border.getBorderColor()));
        }

        if (hyperlink != null) {
            js.put("hyperlink", hyperlink);
        }

        return js;
    }

    protected void addXYJSON(JSONObject js) throws JSONException {
        js.put("x", getCategoryName());
        js.put("y", isValueIsNull() ? "-" : getValue());
    }
}