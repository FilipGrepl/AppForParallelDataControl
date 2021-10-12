/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import com.app.primaryNodeApp.services.RunTaskServices.CommonRunService;
import com.app.primaryNodeApp.model.database.enums.NodeStatus.NodeStatusEnum;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The data of one secondary node.
 * @author filip
 */
public class NodeData implements Serializable {
    
    /** STATIC PROPERTIES **/
    
    public static enum UsageEnum {
        HOUR("Hodinu"),
        DAY("Den"),
        WEEK("Týden"),
        MONTH("Měsíc"),
        MONTHS_3("3 měsíce"),
        MONTHS_6("6 měsíců"),
        YEAR("Rok");

        private final String message;

        UsageEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
    
    /** OBJECT PROPERTIES **/

    private String nodeName;
    private String nodeIP;
    private Long nodeID;

    private NodeStatusEnum nodeStatus;
    private String nodeStatusClass;

    private String runTaskName;
    private Date startDate;

    private Integer finishedSteps;
    private Integer allSteps;

    private String runningStep;                 

    private Long finishedFiles;                 // number of processed files
    private Long allFiles;                      // number of all files    

    private StepStatusEnum stepStatus;          // step status
    private String stepStatusClass;             // class to set color of displayed status

    private double usagePerHour;
    private double usagePerDay;
    private double usagePerWeek;
    private double usagePerMonth;
    private double displayUsage;

    private Map<Object, Number> graphUsagePerHour;
    private Map<Object, Number> graphUsagePerDay;
    private Map<Object, Number> graphUsagePerWeek;
    private Map<Object, Number> graphUsagePerMonth;
    private Map<Object, Number> graphUsagePer3Months;
    private Map<Object, Number> graphUsagePer6Months;
    private Map<Object, Number> graphUsagePerYear;
    
    /** OBJECT METHODS **/

    public NodeData() {
        this.nodeName = null;
        this.nodeIP = null;
        this.nodeID = null;
        this.nodeStatus = null;
        this.nodeStatusClass = null;
        this.runTaskName = null;
        this.startDate = null;
        this.finishedSteps = null;
        this.allSteps = null;
        this.runningStep = null;
        this.finishedFiles = null;
        this.allFiles = null;
        this.stepStatus = null;
        this.stepStatusClass = null;

        this.usagePerHour = 0;
        this.usagePerDay = 0;
        this.usagePerWeek = 0;
        this.usagePerMonth = 0;
        this.displayUsage = this.usagePerHour;

        this.graphUsagePerHour = null;
        this.graphUsagePerDay = null;
        this.graphUsagePerWeek = null;
        this.graphUsagePerMonth = null;
        this.graphUsagePer3Months = null;
        this.graphUsagePer6Months = new HashMap<>();
        this.graphUsagePerYear = new HashMap<>();
    }

    /** GETTERS AND SETTERS **/
    
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeIP() {
        return nodeIP;
    }

    public void setNodeIP(String nodeIP) {
        this.nodeIP = nodeIP;
    }

    public Long getNodeID() {
        return nodeID;
    }

    public void setNodeID(Long nodeID) {
        this.nodeID = nodeID;
    }

    public NodeStatusEnum getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeStatusEnum nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public String getNodeStatusClass() {
        return nodeStatusClass;
    }

    public void setNodeStatusClass(String nodeStatusClass) {
        this.nodeStatusClass = nodeStatusClass;
    }

    public String getRunTaskName() {
        return runTaskName;
    }

    public void setRunTaskName(String runTaskName) {
        this.runTaskName = runTaskName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getFinishedSteps() {
        return finishedSteps;
    }

    public void setFinishedSteps(Integer finishedSteps) {
        this.finishedSteps = finishedSteps;
    }

    public Integer getAllSteps() {
        return allSteps;
    }

    public void setAllSteps(Integer allSteps) {
        this.allSteps = allSteps;
    }

    public String getRunningStep() {
        return runningStep;
    }

    public void setRunningStep(String runningStep) {
        this.runningStep = runningStep;
    }

    public Long getFinishedFiles() {
        return finishedFiles;
    }

    public void setFinishedFiles(Long finishedFiles) {
        this.finishedFiles = finishedFiles;
    }

    public Long getAllFiles() {
        return allFiles;
    }

    public void setAllFiles(Long allFiles) {
        this.allFiles = allFiles;
    }

    public StepStatusEnum getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(StepStatusEnum stepStatus) {
        this.stepStatus = stepStatus;
    }

    public String getStepStatusClass() {
        return stepStatusClass;
    }

    public void setStepStatusClass(String stepStatusClass) {
        this.stepStatusClass = stepStatusClass;
    }

    public double getUsagePerHour() {
        return usagePerHour;
    }

    public void setUsagePerHour(double usagePerHour) {
        this.usagePerHour = usagePerHour;
    }

    public double getUsagePerDay() {
        return usagePerDay;
    }

    public void setUsagePerDay(double usagePerDay) {
        this.usagePerDay = usagePerDay;
    }

    public double getUsagePerWeek() {
        return usagePerWeek;
    }

    public void setUsagePerWeek(double usagePerWeek) {
        this.usagePerWeek = usagePerWeek;
    }

    public double getUsagePerMonth() {
        return usagePerMonth;
    }

    public void setUsagePerMonth(double usagePerMonth) {
        this.usagePerMonth = usagePerMonth;
    }

    public Map<Object, Number> getGraphUsagePerHour() {
        return graphUsagePerHour;
    }

    public void setGraphUsagePerHour(Map<Object, Number> graphUsagePerHour) {
        this.graphUsagePerHour = graphUsagePerHour;
    }

    public Map<Object, Number> getGraphUsagePerDay() {
        return graphUsagePerDay;
    }

    public void setGraphUsagePerDay(Map<Object, Number> graphUsagePerDay) {
        this.graphUsagePerDay = graphUsagePerDay;
    }

    public Map<Object, Number> getGraphUsagePerWeek() {
        return graphUsagePerWeek;
    }

    public void setGraphUsagePerWeek(Map<Object, Number> graphUsagePerWeek) {
        this.graphUsagePerWeek = graphUsagePerWeek;
    }

    public Map<Object, Number> getGraphUsagePerMonth() {
        return graphUsagePerMonth;
    }

    public void setGraphUsagePerMonth(Map<Object, Number> graphUsagePerMonth) {
        this.graphUsagePerMonth = graphUsagePerMonth;
    }

    public Map<Object, Number> getGraphUsagePer3Months() {
        return graphUsagePer3Months;
    }

    public void setGraphUsagePer3Months(Map<Object, Number> graphUsagePer3Months) {
        this.graphUsagePer3Months = graphUsagePer3Months;
    }

    public Map<Object, Number> getGraphUsagePer6Months() {
        return graphUsagePer6Months;
    }

    public void setGraphUsagePer6Months(Map<Object, Number> graphUsagePer6Months) {
        this.graphUsagePer6Months = graphUsagePer6Months;
    }

    public Map<Object, Number> getGraphUsagePerYear() {
        return graphUsagePerYear;
    }

    public void setGraphUsagePerYear(Map<Object, Number> graphUsagePerYear) {
        this.graphUsagePerYear = graphUsagePerYear;
    }

    /**
     * Getter of ratio of finished steps to all steps as string.
     * @return Ratio of finished steps to all steps as string.
     */
    public String getStepsStr() {
        if (this.finishedSteps == null || this.allSteps == null) {
            return null;
        } else {
            return CommonRunService.getRatioStr(this.finishedSteps.toString(), this.allSteps.toString());
        }
    }

    public Double getStepsPercentage() {
        if (this.finishedSteps == null || this.allSteps == null) {
            return null;
        } else {
            return (new Double(this.finishedSteps)) / this.allSteps * 100;
        }
    }

    /**
     * Getter of ratio of finished files to all files as string.
     * @return Ratio of finished files to all files as string.
     */
    public String getFinishedFilesStr() {
        if (this.finishedFiles == null || this.allFiles == null) {
            return null;
        } else {
            return CommonRunService.getRatioStr(this.finishedFiles.toString(), this.allFiles.toString());
        }
    }

    public Double getFinishedPercentage() {
        if (this.finishedFiles == null || this.allFiles == null) {
            return null;
        } else {
            return (new Double(this.finishedFiles)) / this.allFiles * 100;
        }
    }

    /**
     * Getter of finished percents of task as string value.
     * @return Finished percents of task as string value.
     */
    public String getFinishedPercentageStr() {
        return this.getFinishedPercentage() == null ? null : CommonRunService.getPercentageStr(Integer.toString(this.getFinishedPercentage().intValue()));
    }

    /**
     * Setter of node usage value. it depends on period, which is selected by user.
     * @param usageEnum The period for which the usage to be displayed. 
     */
    public void setDisplayUsage(UsageEnum usageEnum) {
        switch (usageEnum) {
            case HOUR:
                this.displayUsage = this.usagePerHour;
                break;
            case DAY:
                this.displayUsage = this.usagePerDay;
                break;
            case WEEK:
                this.displayUsage = this.usagePerWeek;
                break;
            case MONTH:
                this.displayUsage = this.usagePerMonth;
                break;
            default:
                this.displayUsage = this.usagePerHour;
        }
    }

    public double getDisplayUsage() {
        return this.displayUsage;
    }

    /**
     * Getter of node usage as string value in percents.
     * @return Node usage as string value.
     */
    public String getDisplayUsageStr() {
        return CommonRunService.getPercentageStr(Integer.toString((int)this.displayUsage));
    }
}
