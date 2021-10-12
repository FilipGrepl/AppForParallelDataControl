/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.HistoryTasServices;

import com.app.primaryNodeApp.services.dataClasses.HistoryStepGraphData;
import com.app.primaryNodeApp.services.dataClasses.TaskData;
import com.app.primaryNodeApp.model.database.dao.JobDao;
import com.app.primaryNodeApp.model.database.dao.RunJobDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 * The service for obtaining historical graphs data.
 * @author filip
 */
public class HistoryTaskGraphService {
    
    /** STATIC PROPERTIES **/
    public static final String EXTENDER_NAME = "skinBar";
    
    /** OBJECT PROPERTIES **/

    private final RunJob runJob;
    private final Job job;

    /** OBJECT METHODS **/
    
    /**
     * Constructor.
     * @param runJobID Id of run job for which the graph data will be created.
     */
    public HistoryTaskGraphService(Long runJobID) {
        runJob = new RunJobDao().getByIdWithCollections(runJobID);
        job = new JobDao().getByIdWithCollections(runJob.getJob().getId());
    }

    /**
     * Method that returns extract data for graph with execute time of task on each node.
     * @return BarChartModel of data for graph with execute time of task on each node.
     */
    public BarChartModel getStepExecTimeGraph() {
        BarChartModel stepExecTimeGraph = new BarChartModel();
        ChartSeries execTimeOnNode = new ChartSeries();

        job.getJobNodes().sort((Node node_1, Node node_2) -> node_2.getNodeName().compareTo(node_1.getNodeName()));

        job.getJobNodes().forEach(node -> {
                Predicate<StepRunData> byNodeID = stepRunData -> stepRunData.getNode().getId().equals(node.getId());
                List<StepRunData> nodeStepRunData = runJob.getStepRunData().stream().filter(byNodeID).collect(Collectors.toList());
                nodeStepRunData
                        .stream()
                        .map((stepRunData) -> stepRunData.getFinishedAt().getTime() - stepRunData.getFirstStartedAt().getTime())
                        .reduce(Long::sum)
                        .ifPresent(execSum -> {
                            execTimeOnNode.set(node.getNodeName(), (double)execSum / TaskData.HOURS);
                        });
            });
        stepExecTimeGraph.addSeries(execTimeOnNode);
        stepExecTimeGraph.setSeriesColors("0A3AA2");
        stepExecTimeGraph.setExtender(EXTENDER_NAME);
        
        Axis xAxis = stepExecTimeGraph.getAxis(AxisType.X);
        xAxis.setLabel("Název výpočetního uzlu");
        xAxis.setTickAngle(-45);

        Axis yAxis = stepExecTimeGraph.getAxis(AxisType.Y);
        yAxis.setLabel("Doba provádění [h]");

        return stepExecTimeGraph;
    }

    /**
     * Method, that returns graphs data for each step of task.
     * @return List of graphs data for each step of task.
     */
    public List<HistoryStepGraphData> getHistoryStepGraphData() {

        List<HistoryStepGraphData> historyStepsGraphData = new ArrayList<>();

        this.job.getJobSteps().sort((Step step_1, Step step_2) -> step_1.getStepOrder() - step_2.getStepOrder());
        this.job.getJobSteps().forEach(step -> {

            HistoryStepGraphData historyStepGraphData = new HistoryStepGraphData();
            historyStepGraphData.setStepName(step.getStepName());
            historyStepGraphData.setStepId(step.getId());

            createStepExecModel(historyStepGraphData);
            createFileExecModel(historyStepGraphData);
            createNormalizedFileExecModel(historyStepGraphData);
            createUsageRamModel(historyStepGraphData);
            createUsageCpuModel(historyStepGraphData);

            historyStepsGraphData.add(historyStepGraphData);
        });
        return historyStepsGraphData;
    }

    /**
     * Method, that returns statistic data for each node of specific step.
     * @param stepId Id of step for which the statistic data to be returned.
     * @return List of statistic data for each node of specific step.
     */
    private List<StepRunData> getStepRunData(Long stepId) {
        Predicate<StepRunData> byStepID = stepRunData -> stepRunData.getStep().getId().equals(stepId);
        List<StepRunData> nodeStepRunData = this.runJob.getStepRunData().stream().filter(byStepID).collect(Collectors.toList());
        nodeStepRunData.sort((StepRunData stepRunData_1, StepRunData stepRunData_2)
                -> stepRunData_1.getNode().getNodeName().compareTo(stepRunData_2.getNode().getNodeName()));
        return nodeStepRunData;
    }

    /**
     * Method, that creates model for graph of execute time on each node.
     * @param historyStepGraphData The historical data about specific step of task for which the model to be created.
     */
    public void createStepExecModel(HistoryStepGraphData historyStepGraphData) {

        ChartSeries stepExecTimeSeries = new ChartSeries();
        this.getStepRunData(historyStepGraphData.getStepId()).forEach(stepRunData -> {
            stepExecTimeSeries.set(stepRunData.getNode().getNodeName(), ((double)stepRunData.getExecTime())/ TaskData.HOURS);
        });

        BarChartModel execGraph = new BarChartModel();

        execGraph.addSeries(stepExecTimeSeries);
        execGraph.setTitle("Doba provádění kroku");
        execGraph.setExtender(EXTENDER_NAME);

        Axis xAxis = execGraph.getAxis(AxisType.X);
        xAxis.setLabel("Název výpočetního uzlu");
        xAxis.setTickAngle(-45);

        Axis yAxis = execGraph.getAxis(AxisType.Y);
        yAxis.setLabel("Doba provádění [h]");

        historyStepGraphData.setExecGraph(execGraph);
    }

    /**
     * Method, that creates model for graph of min/avg/max processing time of one input on each node.
     * @param historyStepGraphData The historical data about specific step of task for which the model to be created.
     */
    public void createFileExecModel(HistoryStepGraphData historyStepGraphData) {
        List<String> pathsToMinFileProcGraph = new ArrayList<>();
        List<String> pathsToMaxFileProcGraph = new ArrayList<>();
        
        ChartSeries minFileExecTimeSeries = new ChartSeries();
        ChartSeries avgFileExecTimeSeries = new ChartSeries();
        ChartSeries maxFileExecTimeSeries = new ChartSeries();

        this.getStepRunData(historyStepGraphData.getStepId()).forEach(stepRunData -> {
            minFileExecTimeSeries.set(stepRunData.getNode().getNodeName(), (double)stepRunData.getMinFileProcTime() / TaskData.MINUTES);
            avgFileExecTimeSeries.set(stepRunData.getNode().getNodeName(), (double)stepRunData.getAvgFileProcTime() / TaskData.MINUTES);
            maxFileExecTimeSeries.set(stepRunData.getNode().getNodeName(), (double)stepRunData.getMaxFileProcTime() / TaskData.MINUTES);
            
            pathsToMinFileProcGraph.add(stepRunData.getMinFilePath());
            pathsToMaxFileProcGraph.add(stepRunData.getMaxFilePath());
        });

        BarChartModel fileExecGraph = new BarChartModel();

        minFileExecTimeSeries.setLabel("minimální");
        avgFileExecTimeSeries.setLabel("průměrná");
        maxFileExecTimeSeries.setLabel("maximální");
        
        fileExecGraph.setExtender(EXTENDER_NAME);

        if (historyStepGraphData.isCheckMinFileExecTime()) {
            fileExecGraph.addSeries(minFileExecTimeSeries);
        }
        if (historyStepGraphData.isCheckAvgFileExecTime()) {
            fileExecGraph.addSeries(avgFileExecTimeSeries);
        }
        if (historyStepGraphData.isCheckMaxFileExecTime()) {
            fileExecGraph.addSeries(maxFileExecTimeSeries);
        }

        fileExecGraph.setTitle("Doba zpracování vstupu");
        fileExecGraph.setLegendPosition("ne");
        fileExecGraph.setSeriesColors(
                (historyStepGraphData.isCheckMinFileExecTime() ? "0129CA," : "")
                + (historyStepGraphData.isCheckAvgFileExecTime() ? "F5864A," : "")
                + (historyStepGraphData.isCheckMaxFileExecTime() ? "C70039" : ""));

        Axis xAxis = fileExecGraph.getAxis(AxisType.X);
        xAxis.setLabel("Název výpočetního uzlu");
        xAxis.setTickAngle(-45);

        Axis yAxis = fileExecGraph.getAxis(AxisType.Y);
        yAxis.setLabel("Doba provádění [min]");

        historyStepGraphData.setFileProcGraph(fileExecGraph);
        historyStepGraphData.setPathsToMinFileProcGraph(pathsToMinFileProcGraph);
        historyStepGraphData.setPathsToMaxFileProcGraph(pathsToMaxFileProcGraph);
    }
    
    /**
     * Method, that creates model for graph of min/avg/max processing time of one input on each node normalized by size of file.
     * @param historyStepGraphData The historical data about specific step of task for which the model to be created.
     */
    public void createNormalizedFileExecModel(HistoryStepGraphData historyStepGraphData) {
        
        List<String> pathsToMinFileNormProcGraph = new ArrayList<>();
        List<String> pathsToMaxFileNormProcGraph = new ArrayList<>();
        
        ChartSeries minFileExecTimeSeries = new ChartSeries();
        ChartSeries maxFileExecTimeSeries = new ChartSeries();

        this.getStepRunData(historyStepGraphData.getStepId()).forEach(stepRunData -> {
            minFileExecTimeSeries.set(stepRunData.getNode().getNodeName(), stepRunData.getMinFileNormalizedProcTime());
            maxFileExecTimeSeries.set(stepRunData.getNode().getNodeName(), stepRunData.getMaxFileNormalizedProcTime());
            pathsToMinFileNormProcGraph.add(stepRunData.getMinFileNormalizedPath());
            pathsToMaxFileNormProcGraph.add(stepRunData.getMaxFileNormalizedPath());
        });

        BarChartModel normalizedFileExecGraph = new BarChartModel();

        minFileExecTimeSeries.setLabel("minimální");
        maxFileExecTimeSeries.setLabel("maximální");
        
        normalizedFileExecGraph.setExtender(EXTENDER_NAME);

        if (historyStepGraphData.isCheckNormalizedMinFileExecTime()) {
            normalizedFileExecGraph.addSeries(minFileExecTimeSeries);
        }
        if (historyStepGraphData.isCheckNormalizedMaxFileExecTime()) {
            normalizedFileExecGraph.addSeries(maxFileExecTimeSeries);
        }
        
        normalizedFileExecGraph.setTitle("Doba zpracování vstupu normalizovaná velikostí vstupu");
        normalizedFileExecGraph.setLegendPosition("ne");
        normalizedFileExecGraph.setSeriesColors(
                (historyStepGraphData.isCheckNormalizedMinFileExecTime() ? "0129CA," : "")
                + (historyStepGraphData.isCheckNormalizedMaxFileExecTime() ? "C70039" : ""));

        Axis xAxis = normalizedFileExecGraph.getAxis(AxisType.X);
        xAxis.setLabel("Název výpočetního uzlu");
        xAxis.setTickAngle(-45);

        Axis yAxis = normalizedFileExecGraph.getAxis(AxisType.Y);
        yAxis.setLabel("Doba provádění");

        historyStepGraphData.setNormalizedFileProcGraph(normalizedFileExecGraph);
        historyStepGraphData.setPathsToMinFileNormProcGraph(pathsToMinFileNormProcGraph);
        historyStepGraphData.setPathsToMaxFileNormProcGraph(pathsToMaxFileNormProcGraph);
    }

    /**
     * Method, that creates model for graph of min/avg/max usage of RAM on each node.
     * @param historyStepGraphData The historical data about specific step of task for which the model to be created.
     */
    public void createUsageRamModel(HistoryStepGraphData historyStepGraphData) {
        BarChartModel usageRamGraph = new BarChartModel();
        ChartSeries minRamUsageSeries = new ChartSeries();
        ChartSeries avgRamUsageSeries = new ChartSeries();
        ChartSeries maxRamUsageSeries = new ChartSeries();

        this.getStepRunData(historyStepGraphData.getStepId()).forEach(stepRunData -> {
            minRamUsageSeries.set(stepRunData.getNode().getNodeName(), stepRunData.getMinRamUsage());
            avgRamUsageSeries.set(stepRunData.getNode().getNodeName(), stepRunData.getAvgRamUsage());
            maxRamUsageSeries.set(stepRunData.getNode().getNodeName(), stepRunData.getMaxRamUsage());
        });

        minRamUsageSeries.setLabel("minimální");
        avgRamUsageSeries.setLabel("průměrná");
        maxRamUsageSeries.setLabel("maximální");
        
        usageRamGraph.setExtender(EXTENDER_NAME);

        if (historyStepGraphData.isCheckMinRamUsage()) {
            usageRamGraph.addSeries(minRamUsageSeries);
        }
        if (historyStepGraphData.isCheckAvgRamUsage()) {
            usageRamGraph.addSeries(avgRamUsageSeries);
        }
        if (historyStepGraphData.isCheckMaxRamUsage()) {
            usageRamGraph.addSeries(maxRamUsageSeries);
        }

        usageRamGraph.setTitle("Využití RAM");

        usageRamGraph.setLegendPosition("ne");
        usageRamGraph.setSeriesColors(
                (historyStepGraphData.isCheckMinRamUsage()? "0129CA," : "")
                + (historyStepGraphData.isCheckAvgRamUsage() ? "F5864A," : "")
                + (historyStepGraphData.isCheckMaxRamUsage() ? "C70039" : ""));

        Axis xAxis = usageRamGraph.getAxis(AxisType.X);
        xAxis.setLabel("Název výpočetního uzlu");
        xAxis.setTickAngle(-45);

        Axis yAxis = usageRamGraph.getAxis(AxisType.Y);
        yAxis.setLabel("Využití RAM [GiB]");
        yAxis.setMin(0);

        historyStepGraphData.setUsageRAMGraph(usageRamGraph);
    }

    /**
     * Method, that creates model for graph of min/avg/max usage of CPU on each node.
     * @param historyStepGraphData The historical data about specific step of task for which the model to be created.
     */
    public void createUsageCpuModel(HistoryStepGraphData historyStepGraphData) {
        BarChartModel usageCpuGraph = new BarChartModel();
        ChartSeries minCpuUsageSeries = new ChartSeries();
        ChartSeries avgCpuUsageSeries = new ChartSeries();
        ChartSeries maxCpuUsageSeries = new ChartSeries();

        this.getStepRunData(historyStepGraphData.getStepId()).forEach(stepRunData -> {
            minCpuUsageSeries.set(stepRunData.getNode().getNodeName(), stepRunData.getMinCpuLoad());
            avgCpuUsageSeries.set(stepRunData.getNode().getNodeName(), stepRunData.getAvgCpuLoad());
            maxCpuUsageSeries.set(stepRunData.getNode().getNodeName(), stepRunData.getMaxCpuLoad());
        });

        minCpuUsageSeries.setLabel("minimální");
        avgCpuUsageSeries.setLabel("průměrná");
        maxCpuUsageSeries.setLabel("maximální");
        
        usageCpuGraph.setExtender(EXTENDER_NAME);

        if (historyStepGraphData.isCheckMinCpuUsage()) {
            usageCpuGraph.addSeries(minCpuUsageSeries);
        }
        if (historyStepGraphData.isCheckAvgCpuUsage()) {
            usageCpuGraph.addSeries(avgCpuUsageSeries);
        }
        if (historyStepGraphData.isCheckMaxCpuUsage()) {
            usageCpuGraph.addSeries(maxCpuUsageSeries);
        }

        usageCpuGraph.setTitle("Využití CPU");

        usageCpuGraph.setLegendPosition("ne");
        usageCpuGraph.setSeriesColors(
                (historyStepGraphData.isCheckMinCpuUsage()? "0129CA," : "")
                + (historyStepGraphData.isCheckAvgCpuUsage() ? "F5864A," : "")
                + (historyStepGraphData.isCheckMaxCpuUsage() ? "C70039" : ""));

        Axis xAxis = usageCpuGraph.getAxis(AxisType.X);
        xAxis.setLabel("Název výpočetního uzlu");
        xAxis.setTickAngle(-45);

        Axis yAxis = usageCpuGraph.getAxis(AxisType.Y);
        yAxis.setLabel("Využití CPU [%]");
        yAxis.setMin(0);

        historyStepGraphData.setUsageCPUGraph(usageCpuGraph);
    }
}
