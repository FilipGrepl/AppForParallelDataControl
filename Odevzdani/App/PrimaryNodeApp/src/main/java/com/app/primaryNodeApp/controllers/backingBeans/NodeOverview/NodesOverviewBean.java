/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.NodeOverview;

import com.app.primaryNodeApp.controllers.backingBeans.NodeOverview.SelectUsage.SelectUsageBean;
import com.app.primaryNodeApp.services.dataClasses.NodeData;
import com.app.primaryNodeApp.services.NodesOverviewServices.NodeOverviewService;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
/**
 * The backing bean for secondary nodes overview page.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class NodesOverviewBean implements Serializable {
    
    /** OBJECT PROPERTIES **/
    
    List<NodeData> nodesData;
    List<NodeData> nodesDataFiltered;    
    NodeData selectedNode;
    
    @Inject
    private SelectUsageBean selectUsageBean;
            
    /** OBJECT METHODS **/

    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        this.nodesData = NodeOverviewService.getNodesOverviewData();
        this.selectUsageBean.createSelectItems();
    }

    /**
     * Getter of all secondary nodes data.
     * @return List of all secondary nodes data.
     */
    public List<NodeData> getNodesData() {
        return nodesData;
    }

    /**
     * Getter of all secondary nodes filtered data.
     * @return List of all secondary nodes filtered data.
     */
    public List<NodeData> getNodesDataFiltered() {
        return nodesDataFiltered;
    }

    /**
     * Setter of all secondary nodes filtered data.
     * @param nodesDataFiltered New value of list of all secondary nodes filtered data.
     */
    public void setNodesDataFiltered(List<NodeData> nodesDataFiltered) {
        this.nodesDataFiltered = nodesDataFiltered;
    }

    /**
     * Getter of select usage bean.
     * @return SelectUsageBean.
     */
    public SelectUsageBean getSelectUsageBean() {
        return selectUsageBean;
    }

    /**
     * Getter of selected node in the table of secondary nodes.
     * @return Selected node in the table of secondary nodes.
     */
    public NodeData getSelectedNode() {
        return selectedNode;
    }

    /**
     * Setter of selected node in the table of secondary nodes.
     * @param selectedNode New value of selected node in the table of secondary nodes.
     */
    public void setSelectedNode(NodeData selectedNode) {
        this.selectedNode = selectedNode;
    }
        
    /**
     * Getter of all running steps on secondary nodes for filter selector.
     * @return List of all running steps on secondary nodes.
     */
    public List<String> getRunningSteps() {
        List<String> runningSteps = new ArrayList<>();

        for (NodeData nodeData : this.nodesData) {
            if (nodeData.getRunningStep() != null && !runningSteps.contains(nodeData.getRunningStep())) {
                runningSteps.add(nodeData.getRunningStep());
            }
        }
        return runningSteps;
    }

    /**
     * Getter of all step statuses on secondary nodes for filter selector.
     * @return List of all step status on secondary nodes.
     */
    public List<String> getStepStatuses() {
        List<String> statuses = new ArrayList<>();
        for (NodeData nodeData : nodesData) {
            if (nodeData.getStepStatus() != null && !statuses.contains(nodeData.getStepStatus().getMessage())) {
                statuses.add(nodeData.getStepStatus().getMessage());
            }
        }
        return statuses;
    }
    
    /**
     * Getter of all node statuses on secondary nodes for filter checkboxes.
     * @return List of all node statuses.
     */
    public List<String> getNodeStatuses() {
        List<String> statuses = new ArrayList<>();
        for (NodeData nodeData : nodesData) {
            if (!statuses.contains(nodeData.getNodeStatus().getMessage())) {
                statuses.add(nodeData.getNodeStatus().getMessage());
            }
        }
        return statuses;
    }
    
    /**
     * Handler of change event on selectOneButton which is used to change scale of usage.
     */
    public void ajaxSelectHandler() {
        this.nodesData.forEach(nodeData -> {
            nodeData.setDisplayUsage(this.selectUsageBean.getSelectValue());
        });
    }
    
    /**
     * Method, which redirects user to nodeDetail page.
     * @throws IOException If an IOException occurs.
     */
    public void redirectToNodeDetail() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("nodeDetail.xhtml?i=1&nodeID="+selectedNode.getNodeID());
    }
    
    /**
     * Update node data when server push event is received on client side.
     */
    public void update() {
        this.nodesData = NodeOverviewService.getNodesOverviewData();
        this.ajaxSelectHandler();
    }
}
