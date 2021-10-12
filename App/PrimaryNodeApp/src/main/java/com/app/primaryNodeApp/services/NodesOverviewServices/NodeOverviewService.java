/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.NodesOverviewServices;

import com.app.primaryNodeApp.services.dataClasses.NodeData;
import com.app.primaryNodeApp.services.dataClasses.NodeData.UsageEnum;
import com.app.primaryNodeApp.services.dataClasses.RunTaskData;
import com.app.primaryNodeApp.services.dataClasses.TaskStatusOnNode;
import com.app.primaryNodeApp.services.RunTaskServices.CommonRunService;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskService;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskOnNodeService;
import com.app.primaryNodeApp.model.database.dao.NodeDao;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.NodeUtilization;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.enums.NodeStatus.NodeStatusEnum;
import com.app.primaryNodeApp.model.database.enums.RecordType;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import com.app.primaryNodeApp.model.primaryNode.usageChecker.UsageChecker;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The service for overview data about nodes.
 * @author filip
 */
public class NodeOverviewService {

    /** STATIC METHODS **/
    
    public static final String GRAPH_DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /** OBJECT METHODS **/
    
    /**
     * Get data about node for specific node.
     * @param nodeID The id of node for which the data to be obtained.
     * @return Data about node.
     */
    public static NodeData getNodeOverviewData(Long nodeID) {
        Node node = new NodeDao().getByIdWithCollections(nodeID);

        return loadNodeOverviewData(node, true);
    }

    /**
     * Get data about all nodes in system.
     * @return List of data abou all nodes in system.
     */
    public static List<NodeData> getNodesOverviewData() {
        List<NodeData> nodesData = new ArrayList<>();
        NodeDao nodeDao = new NodeDao();

        for (Node node : nodeDao.getAll()) {
            Node nodeWithCollections = nodeDao.getByIdWithCollections(node.getId());
            nodesData.add(loadNodeOverviewData(nodeWithCollections, false));
        }
        return nodesData;
    }

     /**
      * Method, that loads node overview data.
      * @param nodeWithCollections Node with loaded all its collections.
      * @param setGraphData Flag that indicate if data for usage graph to be loaded.
      * @return Data about specific node.
      */
    private static NodeData loadNodeOverviewData(Node nodeWithCollections, boolean setGraphData) {
        NodeData nodeData = new NodeData();

        nodeData.setNodeName(nodeWithCollections.getNodeName());
        nodeData.setNodeIP(nodeWithCollections.getNodeIP());
        nodeData.setNodeID(nodeWithCollections.getId());
        nodeData.setNodeStatus(nodeWithCollections.getNodeStatus());
        nodeData.setNodeStatusClass(CommonRunService.getNodeStatusClass(nodeData.getNodeStatus()));

        if (nodeWithCollections.getNodeStatus() == NodeStatusEnum.OCCUPIED) {

            Predicate<StepRunData> byStepStatus = stepRunData -> stepRunData.getStepStatus() == StepStatusEnum.RUNNING || stepRunData.getStepStatus() == StepStatusEnum.RUNNING_ERROR;

            nodeWithCollections.getStepRunData().stream().filter(byStepStatus).findFirst().ifPresent(stepRunData -> {
                RunTaskData runTaskData = new RunTaskService().getRunTaskData(stepRunData.getRunJob().getId());
                TaskStatusOnNode taskStatusOnNode = RunTaskOnNodeService.getTaskStatusOnNodeData(stepRunData.getRunJob().getId(), nodeWithCollections.getId());

                nodeData.setRunTaskName(runTaskData.getTaskName());
                nodeData.setStartDate(runTaskData.getStartedAt());
                nodeData.setFinishedSteps(runTaskData.getFinishedSteps());
                nodeData.setAllSteps(runTaskData.getAllSteps());

                nodeData.setRunningStep(taskStatusOnNode.getRunningStepName());
                nodeData.setFinishedFiles(taskStatusOnNode.getFinishedFiles());
                nodeData.setAllFiles(taskStatusOnNode.getAllFiles());
                nodeData.setStepStatus(taskStatusOnNode.getStepStatus());
                nodeData.setStepStatusClass(taskStatusOnNode.getStatusClass());            
            });
        }
        List<NodeUtilization> nodeUtilizationData = nodeWithCollections.getUtilizationData();

        nodeData.setUsagePerHour(accumulateGraphUsageData(nodeUtilizationData, RecordType.MINUTE, UsageChecker.MINUTES_IN_HOUR));
        nodeData.setUsagePerDay(accumulateGraphUsageData(nodeUtilizationData, RecordType.MINUTES_30, UsageChecker.HOURS_IN_DAY * 2));
        nodeData.setUsagePerWeek(accumulateGraphUsageData(nodeUtilizationData, RecordType.HOUR, UsageChecker.HOURS_IN_WEEK));
        nodeData.setUsagePerMonth(accumulateGraphUsageData(nodeUtilizationData, RecordType.HOURS_12, UsageChecker.DAYS_IN_MONTH * 2));
        nodeData.setDisplayUsage(UsageEnum.HOUR);

        if (setGraphData) {
            nodeData.setGraphUsagePerHour(getGraphUsageData(nodeUtilizationData, RecordType.MINUTE, UsageChecker.MINUTES_IN_HOUR));
            nodeData.setGraphUsagePerDay(getGraphUsageData(nodeUtilizationData, RecordType.MINUTES_30, UsageChecker.HOURS_IN_DAY * 2));
            nodeData.setGraphUsagePerWeek(getGraphUsageData(nodeUtilizationData, RecordType.HOUR, UsageChecker.HOURS_IN_WEEK));
            nodeData.setGraphUsagePerMonth(getGraphUsageData(nodeUtilizationData, RecordType.HOURS_12, UsageChecker.DAYS_IN_MONTH * 2));
            nodeData.setGraphUsagePer3Months(getGraphUsageData(nodeUtilizationData, RecordType.DAY, UsageChecker.DAYS_IN_MONTH * 3));
            nodeData.setGraphUsagePer6Months(getGraphUsageData(nodeUtilizationData, RecordType.DAY, UsageChecker.DAYS_IN_MONTH * 6));
            nodeData.setGraphUsagePerYear(getGraphUsageData(nodeUtilizationData, RecordType.DAY, UsageChecker.DAYS_IN_YEAR));
        }

        return nodeData;
    }

    /**
     * Method, that averages secondary node utilization data.
     * @param nodeUtilizationData The utilization data to be averaged.
     * @param recordType The type of data to be averaged.
     * @param numberOfValues Number of utilization values in time unit which is specified by parameter recordType to be averaged.
     * @return The average usage of node in specific period of time.
     */
    private static double accumulateGraphUsageData(List<NodeUtilization> nodeUtilizationData, RecordType recordType, int numberOfValues) {
        Collection<Number> usageValues = getGraphUsageData(nodeUtilizationData, recordType, numberOfValues).values();
        double usage = usageValues
                .stream()
                .reduce(0, (a, b) -> a.doubleValue() + b.doubleValue())
                .doubleValue();
        
        if (usageValues.size() > 0)
            usage /= usageValues.size();
        return usage;
    }

    /**
     * Method, that returns usage data for graph representation.
     * @param nodeUtilizationData The utilization data to be averaged.
     * @param recordType The type of data to be averaged.
     * @param numberOfValues Number of values in time unit which is specified by parameter recordType.
     * @return The usage data for graph of specific secondary node.
     */
    private static Map<Object, Number> getGraphUsageData(List<NodeUtilization> nodeUtilizationData, RecordType recordType, int numberOfValues) {
        Map<Object, Number> graphUsageData = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GRAPH_DATA_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(UsageChecker.RECORD_TIME_ZONE));

        switch (recordType) {
            case MINUTE:
                break;
            default:
                for (int unitsBeforeActUnit = 0;; unitsBeforeActUnit++) {
                    NodeUtilization nodeUtilization = accumulateUsageRecords(
                            nodeUtilizationData,
                            RecordType.typeBefore(recordType),
                            unitsBeforeActUnit
                    );
                    if (nodeUtilization == null) {
                        break;
                    } else {
                        graphUsageData.put(simpleDateFormat.format(nodeUtilization.getCreatedAt()), nodeUtilization.getLoadNode());
                        numberOfValues--;
                    }
                }
                break;
        }
        graphUsageData.putAll(getSavedGraphUsageData(nodeUtilizationData, recordType, numberOfValues));
        return graphUsageData;
    }

    /**
     * Method, that returns saved usage data for their graph representation
     * @param nodeUtilizationData The utilization data to be averaged.
     * @param recordType The type of data to be averaged.
     * @param numberOfValues Number of values in time unit which is specified by parameter recordType.
     * @return The saved usage data for graph of specific secondary node.
     */
    private static Map<Object, Number> getSavedGraphUsageData(final List<NodeUtilization> nodeUtilizationData, RecordType recordType, int numberOfValues) {
        Map<Object, Number> graphUsageData = new HashMap<>();
        List<NodeUtilization> nodeFilteredUtilizationData = UsageChecker.filterByRecordTypeUtilizationData(nodeUtilizationData, recordType);
        UsageChecker.sortByDateUtilizationData(nodeFilteredUtilizationData);
        if (!nodeFilteredUtilizationData.isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GRAPH_DATA_FORMAT);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(UsageChecker.RECORD_TIME_ZONE));
            for (int cnt = 0; cnt < numberOfValues; cnt++) {
                if (cnt >= nodeFilteredUtilizationData.size()) {
                    break;
                } else {
                    graphUsageData.put(simpleDateFormat.format(nodeFilteredUtilizationData.get(cnt).getCreatedAt()),
                            nodeFilteredUtilizationData.get(cnt).getLoadNode());
                }
            }
        }
        return graphUsageData;
    }

    /**
     * Method, that accumulates old values and returns new value of higher time unit.
     * @param nodeUtilizationData The utilization data to be averaged.
     * @param typeOfAccumulatedRecord The type of data to be averaged.
     * @param unitBefore The units before the newest record in database to be accumulated.
     * @return New value of accumulated data of higher time unit.
     */
    private static NodeUtilization accumulateUsageRecords(List<NodeUtilization> nodeUtilizationData, RecordType typeOfAccumulatedRecord, int unitBefore) {

        List<NodeUtilization> nodeFilteredUtilizationData = UsageChecker.filterByRecordTypeUtilizationData(nodeUtilizationData, typeOfAccumulatedRecord);
        UsageChecker.sortByDateUtilizationData(nodeFilteredUtilizationData);

        if (nodeFilteredUtilizationData.isEmpty()) {
            return null;
        }

        // get lower and upper limit of interval to aacumulate (depends on type of accumulation record)
        Date upperLimitToAccumulate = UsageChecker.getCalendarUpperAccumulateLimit(nodeFilteredUtilizationData.get(0).getCreatedAt(), typeOfAccumulatedRecord, unitBefore);
        Date lowerLimitToAccumulate = UsageChecker.getCalendarLowerAccumulateLimit(upperLimitToAccumulate, typeOfAccumulatedRecord);
        
        nodeFilteredUtilizationData = nodeFilteredUtilizationData
                .stream() // filter data before/after specific hour
                .filter(nodeUtilization -> nodeUtilization.getCreatedAt().before(upperLimitToAccumulate) && nodeUtilization.getCreatedAt().after(lowerLimitToAccumulate))
                .collect(Collectors.toList());

        if (nodeFilteredUtilizationData.isEmpty()) {
            return null;
        } else {
            NodeUtilization nodeUtilization = new NodeUtilization();
            nodeUtilization.setCreatedAt(UsageChecker.getDateOfAccumulatedRecord(lowerLimitToAccumulate));
            nodeUtilization.setLoadNode(UsageChecker.accumulateUtilizationData(nodeFilteredUtilizationData));

            return nodeUtilization;
        }
    }

    /**
     * ******* TEMP *******
     */
    public void tempWriteToFile(List<NodeUtilization> utilizationData) {
        FileWriter fw = null;
        try {
            fw = new FileWriter("/D:/FIT/Ing/DIP/test.txt", true);

            fw.write("Started\n");
            for (NodeUtilization n : utilizationData) {
                String dateInString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(n.getCreatedAt());
                
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GRAPH_DATA_FORMAT);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone(UsageChecker.RECORD_TIME_ZONE));

                String dateInString2 = simpleDateFormat.format(n.getCreatedAt());
                
                fw.write(dateInString + "\n");
                fw.write(dateInString2 + "\n");
            }
            fw.close();
        } catch (IOException ex) {
            if (fw != null)
                try {
                fw.close();
            } catch (IOException ex1) {
            }
        }
    }
}
