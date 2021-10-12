/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import com.app.primaryNodeApp.services.RunTaskServices.CommonRunService;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.io.Serializable;

/**
 * Data of task status on specific secondary node.
 * @author filip
 */
public class TaskStatusOnNode implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private Long stepID;                        
    private Long nodeID;                        
    private String nodeName;                    

    private Integer runningStep;                
    private Integer allSteps;                   

    private String runningStepName;             

    private Long runningTime;                   
    private Long waitingTime;                   // waitng time of task in waiitng queue/error state/stopped state

    private Long finishedFiles;                 
    private Long allFiles;                      

    private StepStatusEnum stepStatus;          
    private String statusClass;                 

    private boolean isRenderDetailErrorBtn;      
    private boolean isRenderStopBtn;            
    private boolean isRenderErrRepeatButton;    
    private boolean isRenderRunButton;
    private boolean isRenderErrSkipButton;
    
    private boolean isRunnableStepRunData;
    
    /** OBJECT METHODS **/

    public TaskStatusOnNode() {
        this.stepID = null;
        this.nodeID = null;
        this.nodeName = null;
        this.runningStep = null;
        this.allSteps = null;
        this.runningStepName = null;
        this.runningTime = null;
        this.waitingTime = null;
        this.finishedFiles = null;
        this.allFiles = null;
        this.stepStatus = null;
        this.statusClass = null;
        this.isRenderStopBtn = false;
        this.isRenderErrRepeatButton = false;
        this.isRenderDetailErrorBtn = false;
        this.isRenderRunButton = false;
        this.isRunnableStepRunData = false;
        this.isRenderErrSkipButton = false;
    }
    
    /** GETTERS AND SETTERS **/
    
    public Long getStepID() {
        return stepID;
    }

    public void setStepID(Long stepID) {
        this.stepID = stepID;
    }

    public Long getNodeID() {
        return nodeID;
    }

    public void setNodeID(Long nodeID) {
        this.nodeID = nodeID;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getRunningStep() {
        return runningStep;
    }

    public void setRunningStep(Integer runningStep) {
        this.runningStep = runningStep;
    }

    public Integer getAllSteps() {
        return allSteps;
    }

    public void setAllSteps(Integer allSteps) {
        this.allSteps = allSteps;
    }

    public String getRunningStepName() {
        return runningStepName;
    }

    public void setRunningStepName(String runningStepName) {
        this.runningStepName = runningStepName;
    }

    public Long getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(Long runningTime) {
        this.runningTime = runningTime;
    }

    public Long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Long waitingTime) {
        this.waitingTime = waitingTime;
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

    public String getStepStatusStr() {
        return stepStatus.getMessage();
    }

    public void setStepStatus(StepStatusEnum stepStatus) {
        this.stepStatus = stepStatus;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }

    public boolean isIsRenderDetailErrorBtn() {
        return isRenderDetailErrorBtn;
    }

    public void setIsRenderDetailErrorBtn(boolean isRenderDetailErrorBtn) {
        this.isRenderDetailErrorBtn = isRenderDetailErrorBtn;
    }

    public boolean isIsRenderStopBtn() {
        return isRenderStopBtn;
    }

    public void setIsRenderStopBtn(boolean isRenderStopBtn) {
        this.isRenderStopBtn = isRenderStopBtn;
    }

    public boolean isIsRenderErrRepeatButton() {
        return isRenderErrRepeatButton;
    }

    public void setIsRenderErrRepeatButton(boolean isRenderErrRepeatButton) {
        this.isRenderErrRepeatButton = isRenderErrRepeatButton;
    }

    public boolean isIsRenderRunButton() {
        return isRenderRunButton;
    }

    public void setIsRenderRunButton(boolean isRenderRunButton) {
        this.isRenderRunButton = isRenderRunButton;
    }

    public boolean isIsRunnableStepRunData() {
        return isRunnableStepRunData;
    }

    public void setIsRunnableStepRunData(boolean isRunnableStepRunData) {
        this.isRunnableStepRunData = isRunnableStepRunData;
    }

    public boolean isIsRenderErrSkipButton() {
        return isRenderErrSkipButton;
    }

    public void setIsRenderErrSkipButton(boolean isRenderErrSkipButton) {
        this.isRenderErrSkipButton = isRenderErrSkipButton;
    }
    
    public String getRunningTimeStr() {
        return CommonRunService.getTimeStr(this.runningTime);
    }
    
    public String getWaitingTimeStr() {
        return CommonRunService.getTimeStr(this.waitingTime);
    }

    public long getRunningTimeInDays() {
        return this.runningTime / RunTaskData.DAY;
    }

    public String getStepsStr() {
        return CommonRunService.getRatioStr(this.runningStep.toString(), this.allSteps.toString());
    }

    public String getFinishedFilesStr() {
        if (this.allFiles == null) {
            return null;
        } else {
            return CommonRunService.getRatioStr(this.finishedFiles.toString(), this.allFiles.toString());
        }
    }

    /**
     * Method, whih returns the percentage of finished files.
     * @return Percentage of finished files.
     */
    public Double getFinishedPercentage() {
        if (this.allFiles == null) {
            if (this.stepStatus == StepStatusEnum.FINISHED) {
                return new Double(100);
            } else {
                return new Double(0);
            }
        } else {
            return ((double) this.finishedFiles) / this.allFiles * 100;
        }
    }

    /**
     * Method, whih returns the percentage of finished files as string.
     * @return Percentage of finished files as string.
     */
    public String getFinishedPercentageStr() {
        if (this.allFiles == null) {
            if (this.stepStatus == StepStatusEnum.FINISHED) {
                return CommonRunService.getPercentageStr("100");
            } else {
                return CommonRunService.getPercentageStr("0");
            }
        } else {
            return CommonRunService.getPercentageStr(Integer.toString(this.getFinishedPercentage().intValue()));
        }
    }
}
