/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.NodesOverviewServices;

import com.app.primaryNodeApp.services.dataClasses.NodeData;
import com.app.primaryNodeApp.services.dataClasses.NodeData.UsageEnum;
import com.app.primaryNodeApp.services.HistoryTasServices.HistoryTaskGraphService;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 * The service for node usage graph.
 * @author filip
 */
public class NodeUsageGraphService {
    
    /**
     * Method, that creates model for node usage graph.
     * @param nodeData Loaded data about specific secondary node.
     * @return The model for node usage graph.
     */
    public static LineChartModel createNodeUsageModel(NodeData nodeData) {
        LineChartModel lineChartModel = new LineChartModel();
        lineChartModel.setTitle("Obsazenost výpočetního uzlu " + nodeData.getNodeName());

        LineChartSeries series = new LineChartSeries();
        series.setData(nodeData.getGraphUsagePerHour());

        lineChartModel.addSeries(series);
        lineChartModel.setExtender(HistoryTaskGraphService.EXTENDER_NAME);
        
        lineChartModel.setZoom(true);
        Axis yAxis = lineChartModel.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(100);
        yAxis.setLabel("Obsazenost [%]");

        DateAxis axis = new DateAxis("Datum");
        axis.setTickAngle(-50);
        
        axis.setTickFormat("%d.%m.%Y, %T");
        
        lineChartModel.getAxes().put(AxisType.X, axis);
        
        return lineChartModel;
    }
    
    /**
     * Method, that sets data of series on node usage graph based on selected value by user.
     * @param lineChartModel Model of node usage graph.
     * @param selectValue Value of time scope selected by user.
     * @param nodeData Loaded data about specific secondary node.
     */
    public static void setSeriesData(LineChartModel lineChartModel, UsageEnum selectValue, NodeData nodeData) {
        LineChartSeries series = (LineChartSeries)lineChartModel.getSeries().get(0);
        
        switch(selectValue) {
            case HOUR:
                series.setData(nodeData.getGraphUsagePerHour());
                break;
            case DAY:
                series.setData(nodeData.getGraphUsagePerDay());
                break;
            case WEEK:
                series.setData(nodeData.getGraphUsagePerWeek());
                break;
            case MONTH:
                series.setData(nodeData.getGraphUsagePerMonth());
                break;
            case MONTHS_3:
                series.setData(nodeData.getGraphUsagePer3Months());
                break;
            case MONTHS_6:
                series.setData(nodeData.getGraphUsagePer6Months());
                break;
            case YEAR:
                series.setData(nodeData.getGraphUsagePerYear());
                break;
        }
        
        DateAxis axis = new DateAxis("Datum");
        axis.setTickAngle(-50);
        switch (selectValue) {
            case HOUR:
            case DAY:
            case WEEK: 
            case MONTH:
                axis.setTickFormat("%d.%m.%Y, %T");
                break;
            default:               
                axis.setTickFormat("%d.%m.%Y");
                break;
        }
        lineChartModel.getAxes().put(AxisType.X, axis);        
    }
    
}
