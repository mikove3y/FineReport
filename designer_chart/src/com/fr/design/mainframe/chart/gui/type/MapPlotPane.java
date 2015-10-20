package com.fr.design.mainframe.chart.gui.type;

import com.fr.chart.base.ChartEnumDefinitions;
import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.MapPlot;
import com.fr.chart.chartattr.Plot;
import com.fr.chart.charttypes.MapIndependentChart;
import com.fr.design.chart.series.PlotSeries.MapGroupExtensionPane;
import com.fr.design.chart.series.PlotStyle.ChartSelectDemoPane;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.general.Inter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MapPlotPane extends AbstractChartTypePane {


	private boolean isSvgMap = true; // Ĭ����svg��ͼ
	private MapGroupExtensionPane groupExtensionPane;

	public MapPlotPane() {// ��ȫ������ͼ�����Ͳ�ͬ�Ľ���.
		setLayout(new BorderLayout());
		typeDemo = initDemoList();
		groupExtensionPane = new MapGroupExtensionPane();

		JPanel typePane = FRGUIPaneFactory.createNColumnGridInnerContainer_S_Pane(4);
		for(int i = 0; i < typeDemo.size(); i++) {
			ChartImagePane tmp = typeDemo.get(i);
			typePane.add(tmp);
			tmp.setDemoGroup(typeDemo.toArray(new ChartSelectDemoPane[typeDemo.size()]));
		}

		this.setLayout(new BorderLayout());
		this.add(typePane,BorderLayout.NORTH);
		this.add(groupExtensionPane, BorderLayout.CENTER);
	}

	@Override
	protected List<ChartImagePane> initDemoList() {
		List <ChartImagePane> demoList = new ArrayList<ChartImagePane>();
        String[] iconPaths = getTypeIconPath();
		ChartImagePane pane = new ChartImagePane(iconPaths[0], Inter.getLocText("FR-Chart-Map_Normal"));
		pane.isPressing = true;
		demoList.add(pane);

		demoList.add(new ChartImagePane(iconPaths[1], Inter.getLocText("FR-Chart-Map_Bubble")));

		demoList.add(new ChartImagePane(iconPaths[2], Inter.getLocText("FR-Chart-Map_Pie")));

		demoList.add(new ChartImagePane(iconPaths[3], Inter.getLocText("FR-Chart-Map_Column"), true));

		return demoList;
	}

	@Override
	protected List<ChartImagePane> initStyleList() {
		return new ArrayList<ChartImagePane>();
	}

    @Override
    protected String[] getTypeIconPath() {
        return new String[]{"/com/fr/design/images/chart/MapPlot/type/0.png",
                "/com/fr/design/images/chart/MapPlot/type/1.png",
                "/com/fr/design/images/chart/MapPlot/type/2.png",
                "/com/fr/design/images/chart/MapPlot/type/3.png",
        };
    }

    @Override
    protected String[] getTypeLayoutPath() {
        return new String[]{
        };
    }

	/**
	 * �������
     * @return �������
	 */
	public String title4PopupWindow() {
		return Inter.getLocText("FR-Chart-Map_Map");
	}

	/**
	 * �����������
	 */
	public void updateBean(Chart chart) {
        if(needsResetChart(chart)){
            resetChart(chart);
        }

		MapPlot plot = new MapPlot();
		plot.setSvgMap(this.isSvgMap);
		plot.setMapName(groupExtensionPane.updateBean(plot));// ��������

		if(typeDemo.get(ChartEnumDefinitions.MapType.Map_Normal.ordinal()).isPressing){
			plot.setMapType(ChartEnumDefinitions.MapType.Map_Normal);
		}else if(typeDemo.get(ChartEnumDefinitions.MapType.Map_Bubble.ordinal()).isPressing){
			plot.setMapType(ChartEnumDefinitions.MapType.Map_Bubble);
		}else if(typeDemo.get(ChartEnumDefinitions.MapType.Map_Pie.ordinal()).isPressing){
			plot.setMapType(ChartEnumDefinitions.MapType.Map_Pie);
		}else if(typeDemo.get(ChartEnumDefinitions.MapType.Map_Column.ordinal()).isPressing){
			plot.setMapType(ChartEnumDefinitions.MapType.Map_Column);
		}

        if(plot.getMapType() != ChartEnumDefinitions.MapType.Map_Normal){
            plot.setHeatMap(false);
        }

		chart.setPlot(plot);

		checkDemosBackground();
	}

	/**
	 * ���µ�ͼ���� ����
	 */
	public void populateBean(Chart chart) {
		Plot plot = chart.getPlot();
		if (plot instanceof MapPlot) {
			MapPlot mapPlot = (MapPlot)plot;
			this.isSvgMap = mapPlot.isSvgMap();
			groupExtensionPane.populateBean(mapPlot);

			for(ChartImagePane imagePane : typeDemo) {
				imagePane.isPressing = false;
			}
			typeDemo.get(mapPlot.getMapType().ordinal()).isPressing = true;
			lastTypeIndex = mapPlot.getMapType().ordinal();//todo ��������ǲ��ǿ���ɾ�ˣ�eason
		}

		checkDemosBackground();

        checkState();
	}

    private void checkState(){
        for(int i = 0; i < typeDemo.size(); i++){
            typeDemo.get(i).setEnabled(this.isSvgMap);
        }
        groupExtensionPane.setEnabled(isSvgMap);
    }

    /**
     * �����Ƿ����
     * @param ob ����Ķ���
     * @return �Ƿ���chart����
     */
	public boolean accept(Object ob) {
		if (!super.accept(ob)) {
			return false;
		}
		Chart chart = (Chart) ob;
		Plot plot = chart.getPlot();
		return plot != null && plot.accept(MapPlot.class);
	}


    public Chart getDefaultChart() {
        return MapIndependentChart.mapChartTypes[0];
    }
}