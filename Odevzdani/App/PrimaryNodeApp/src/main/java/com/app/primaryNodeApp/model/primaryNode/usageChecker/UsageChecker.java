/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.primaryNode.usageChecker;

import com.app.primaryNodeApp.model.database.dao.NodeDao;
import com.app.primaryNodeApp.model.database.dao.NodeUtilizationDao;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.NodeUtilization;
import com.app.primaryNodeApp.model.database.enums.NodeStatus.NodeStatusEnum;
import com.app.primaryNodeApp.model.database.enums.RecordType;
import com.app.primaryNodeApp.model.primaryNode.serverPushBean.enums.FirePushEvents.FirePushEventsEnum;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 * The bean for saving utilization data about all secondary nodes.
 * @author filip
 */

@Singleton
@Startup
public class UsageChecker implements Runnable {
    
    /** STATIC PROPERTIES **/

    public static final Integer MINUTES_IN_HOUR = 60;
    public static final Integer MINUTES_IN_HALF_HOUR = 30;
    public static final Integer HOURS_IN_DAY = 24;
    public static final Integer HOURS_IN_HALF_DAY = 12;
    public static final Integer HOURS_IN_WEEK = 168;
    public static final Integer DAYS_IN_WEEK = 7;
    public static final Integer DAYS_IN_MONTH = 30;
    public static final Integer DAYS_IN_YEAR = 365;
    
    private static final int USAGE_CHECKER_REPEAT_DELAY = 5000;
    
    public static String RECORD_TIME_ZONE;
    
    /** OBJECT PROPERTIES **/
    
    private final NodeDao nodeDao;
    private final NodeUtilizationDao nodeUtilizationDao;
    
    private ScheduledExecutorService execService;
    private ScheduledFuture<?> scheduledFuture;
    private boolean isSomeDataAccumulated;
    
    @Resource(name="RECORD_TIME_ZONE")
    private String recordTimezone;
    
    @Inject
    private Event<FirePushEventsEnum> event;


    /**
     * Constructor
     */
    public UsageChecker() {
        nodeDao = new NodeDao();
        nodeUtilizationDao = new NodeUtilizationDao();
    }
    
    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    public void postConstruct() {
        RECORD_TIME_ZONE = this.recordTimezone;
        execService = Executors.newScheduledThreadPool(1);
        scheduledFuture = execService.scheduleAtFixedRate(this, getDelayTime(), USAGE_CHECKER_REPEAT_DELAY, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Method which is called before the bean is unset from scope.
     */
    @PreDestroy
    public void preDestroy() {
        scheduledFuture.cancel(true);
        execService.shutdown();
    }
    
    /**
     * Method, that returns delay to when the first utlization data was saved -> Second must be divided by USAGE_CHECKER_REPEAT_DELAY/1000 in seconds. 
     * @return Delay time in milliseconds.
     */
    private long getDelayTime() {
        Date currentDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(recordTimezone));
        calendar.setTime(currentDate);
        calendar.set(Calendar.MILLISECOND, 0);
        while (calendar.get(Calendar.SECOND) % (USAGE_CHECKER_REPEAT_DELAY/1000) != 0) {
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 1);
        }
        return calendar.getTimeInMillis() - currentDate.getTime();
    }

    /**
     * Method, that returns upper limit for accumulating data.
     * @param initDate The Date from which the upper limit was obtained.
     * @param typeOfAccumulatedRecord Type of time unit for which the upper limit to be obtained.
     * @param unitBefore The number of time units of type 'typeOfAccumulatedRecord' by which the upper limit to be moved.
     * @return The date which represents upper limit for accumulating utilization data.
     */
    public static Date getCalendarUpperAccumulateLimit(Date initDate, RecordType typeOfAccumulatedRecord, int unitBefore) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(RECORD_TIME_ZONE));
        calendar.setTime(initDate);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        switch (typeOfAccumulatedRecord) {
            case SECOND5:
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - unitBefore);
                break;
            case MINUTE:
                if (calendar.get(Calendar.MINUTE) < MINUTES_IN_HALF_HOUR) {
                    calendar.set(Calendar.MINUTE, 0);
                } else {
                    calendar.set(Calendar.MINUTE, MINUTES_IN_HALF_HOUR);
                }
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - MINUTES_IN_HALF_HOUR * unitBefore); // 2
                break;
            case MINUTES_30:
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) - unitBefore); // 24 HOURS_IN_DAY
                break;
            case HOUR:
                calendar.set(Calendar.MINUTE, 0);
                if (calendar.get(Calendar.HOUR_OF_DAY) < HOURS_IN_HALF_DAY) {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, HOURS_IN_HALF_DAY);
                }
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - HOURS_IN_HALF_DAY * unitBefore); // 14 DAYS_IN_WEEK*2
                break;
            case HOURS_12:
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - unitBefore); // 30 DAYS_IN_MONTH
                break;
        }
        return calendar.getTime();
    }

    /**
     * Method, that returns lower limit for accumulating data.
     * @param initDate The Date from which the lower limit was obtained.
     * @param typeOfAccumulatedRecord Type of time unit for which the lower limit to be obtained.
     * @return The date which represents lower limit for accumulating utilization data.
     */
    public static Date getCalendarLowerAccumulateLimit(Date initDate, RecordType typeOfAccumulatedRecord) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(RECORD_TIME_ZONE));
        calendar.setTime(initDate);
        switch (typeOfAccumulatedRecord) {
            case SECOND5:
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 1);
                break;
            case MINUTE:
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - MINUTES_IN_HALF_HOUR);
                break;
            case MINUTES_30:
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
                break;
            case HOUR:
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - HOURS_IN_HALF_DAY);
                break;
            case HOURS_12:
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
                break;
        }
        calendar.set(Calendar.SECOND, - 1);

        return calendar.getTime();
    }

    /**
     * Method, that returns date of new accumulated record.
     * @param lowerLimitToAccumulate The date which represents lower limit for accumulating utilization data.
     * @return Date of new accumulated record. 
     */
    public static Date getDateOfAccumulatedRecord(Date lowerLimitToAccumulate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lowerLimitToAccumulate);
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 1);

        return calendar.getTime();
    }

    /**
     * Method that averages saved records to new averaged record.
     * @param nodeUtilizationData Utilization data of specific node.
     * @param typeOfAccumulatedRecord Type of time unit for which the saved data to be averaged.
     * @param node Node which corresponds with node utilization data.
     * @param unitBefore The number of time units of type 'typeOfAccumulatedRecord' by which the upper limit of accumulation to be moved.
     *                   This parameter determines how much utlization data shouldn't be averaged.
     */
    private void accumulateUsageRecords(List<NodeUtilization> nodeUtilizationData, RecordType typeOfAccumulatedRecord, Node node, int unitBefore) {
        List<NodeUtilization> nodeFilteredUtilizationData = filterByRecordTypeUtilizationData(nodeUtilizationData, typeOfAccumulatedRecord);
        sortByDateUtilizationData(nodeFilteredUtilizationData);
        
        if (nodeFilteredUtilizationData.isEmpty())
            return;
        
        // get lower and upper limit of interval to aacumulate (depends on type of accumulation record)
        Date upperLimitToAccumulate = getCalendarUpperAccumulateLimit(nodeFilteredUtilizationData.get(0).getCreatedAt(), typeOfAccumulatedRecord, unitBefore);
        Date lowerLimitToAccumulate = getCalendarLowerAccumulateLimit(upperLimitToAccumulate, typeOfAccumulatedRecord);
        
        // filter data before/after specific unit (minute, hour or day)
        nodeFilteredUtilizationData = nodeFilteredUtilizationData
                .stream()
                .filter(nodeUtilization -> nodeUtilization.getCreatedAt().before(upperLimitToAccumulate) && nodeUtilization.getCreatedAt().after(lowerLimitToAccumulate))
                .collect(Collectors.toList());
        if (!nodeFilteredUtilizationData.isEmpty()) {
            NodeUtilization nodeUtilization = new NodeUtilization();
            nodeUtilization.setCreatedAt(getDateOfAccumulatedRecord(lowerLimitToAccumulate));
            nodeUtilization.setLoadNode(accumulateUtilizationData(nodeFilteredUtilizationData));
            nodeUtilization.setNode(node);
            nodeUtilization.setRecordType(RecordType.typeAfter(typeOfAccumulatedRecord));
            
            // delete old records and save new (accumulated) record
            nodeUtilizationDao.saveAndDeleteUtilizationRecord(nodeFilteredUtilizationData, nodeUtilization);
            
            this.isSomeDataAccumulated = true;
        }
    }

    /**
     * Method, that averages utlization data of node.
     * @param utilizationData List of utilization data to be averaged.
     * @return The diameter value of node utlization data, especially its load.
     */
    public static Integer accumulateUtilizationData(List<NodeUtilization> utilizationData) {
        Integer accValue = null;

        if (!utilizationData.isEmpty()) {
            Integer sum = utilizationData.stream()
                    .map(nodeUtilization -> nodeUtilization.getLoadNode())
                    .reduce(0, Integer::sum);
            accValue = sum / utilizationData.size();
        }
        return accValue;
    }

    /**
     * Method, that returns utilization data filtered by record type.
     * @param nodeUtilizationData Node utilization data to be filtered.
     * @param filteredRecordType The type of record by which the data to be filtered.
     * @return List of filtered utlization data by record type.
     */
    public static List<NodeUtilization> filterByRecordTypeUtilizationData(List<NodeUtilization> nodeUtilizationData, RecordType filteredRecordType) {
        Predicate<NodeUtilization> byRecordType = nodeUtilization -> nodeUtilization.getRecordType() == filteredRecordType;
        List<NodeUtilization> nodeFilteredUtilizationData = nodeUtilizationData.stream().filter(byRecordType).collect(Collectors.toList());
        return nodeFilteredUtilizationData;
    }

    /**
     * Method, that sorts utilization data in descendent order by date of creation.
     * @param nodeUtilizationData Utilization data to be sorted.
     */
    public static void sortByDateUtilizationData(List<NodeUtilization> nodeUtilizationData) {
        // descendent order (the record with the newest date is at index 0)
        nodeUtilizationData.sort(
                (NodeUtilization nodeUtilization1, NodeUtilization nodeUtilization2) -> nodeUtilization2.getCreatedAt().compareTo(nodeUtilization1.getCreatedAt()));
    }

    /**
     * Method, that accumulates all types of data on specific node.
     * @param nodeID The Id of node for which all possible utilization data to be accumulated.
     */
    private void accumulateNodeUtilizationData(long nodeID) {
        // accumulation of node utilization node data

        Node nodeWithCollections = nodeDao.getByIdWithCollections(nodeID);

        this.accumulateUsageRecords(nodeWithCollections.getUtilizationData(), RecordType.SECOND5, nodeWithCollections, 0);

        this.accumulateUsageRecords(nodeWithCollections.getUtilizationData(), RecordType.MINUTE, nodeWithCollections, MINUTES_IN_HOUR/MINUTES_IN_HALF_HOUR);
        
        this.accumulateUsageRecords(nodeWithCollections.getUtilizationData(), RecordType.MINUTES_30, nodeWithCollections, HOURS_IN_DAY);

        this.accumulateUsageRecords(nodeWithCollections.getUtilizationData(), RecordType.HOUR, nodeWithCollections, DAYS_IN_WEEK*2);
        
        this.accumulateUsageRecords(nodeWithCollections.getUtilizationData(), RecordType.HOURS_12, nodeWithCollections, DAYS_IN_MONTH);

        // delete old day records
        List<NodeUtilization> dayNodeUtilizationData = filterByRecordTypeUtilizationData(nodeWithCollections.getUtilizationData(), RecordType.DAY);
        while (dayNodeUtilizationData.size() > DAYS_IN_YEAR) {
            NodeUtilization oldestRecord = dayNodeUtilizationData.get(dayNodeUtilizationData.size() - 1);
            nodeUtilizationDao.delete(oldestRecord.getId());
            dayNodeUtilizationData.remove(dayNodeUtilizationData.size() - 1);
        }
    }

    /**
     * Method, that saves new record about actual node utlization.
     * @param node Node for which the new record of utilization to be saved.
     */
    private void saveActUtilizationData(Node node) {
        // check usage and save act utlization data
        NodeUtilization nodeUtilization = new NodeUtilization();
        nodeUtilization.setCreatedAt(new Date(System.currentTimeMillis()));
        nodeUtilization.setNode(node);
        nodeUtilization.setRecordType(RecordType.SECOND5);
        if (node.getNodeStatus() == NodeStatusEnum.OCCUPIED) {
            nodeUtilization.setLoadNode(100);
        } else {
            nodeUtilization.setLoadNode(0);
        }
        nodeUtilizationDao.save(nodeUtilization);
    }

    /**
     * Method, that periodically checks utilization of all secondary nodes and saves them and accumulates old records.
     */
    @Override
    public void run() {
        this.isSomeDataAccumulated = false;
        List<Node> nodes = nodeDao.getAll();
        nodes.forEach(node -> {

            // save actual utilization data of node
            saveActUtilizationData(node);

            // accumulate utilization data of node in database
            accumulateNodeUtilizationData(node.getId());

        });
        
        if (this.isSomeDataAccumulated) {
            // new data is accumulated -> update usage statistics
             event.fire(FirePushEventsEnum.NODE_EVENT);
        }
    }
}
