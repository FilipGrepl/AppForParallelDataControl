/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.NodeOverview;

import com.app.primaryNodeApp.controllers.backingBeans.NodeOverview.SelectUsage.SelectUsageBean;
import com.app.primaryNodeApp.services.dataClasses.NodeData;
import com.app.primaryNodeApp.services.NodesOverviewServices.NodeOverviewService;
import com.app.primaryNodeApp.services.NodesOverviewServices.NodeUsageGraphService;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.services.NodeService.NodeService;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 * The backing bean for secondary node detail page.
 *
 * @author filip
 */
@ManagedBean
@ViewScoped
public class NodeDetailBean implements Serializable {

    /**
     * OBJECT PROPERTIES *
     */
    private Long nodeID;
    private NodeData nodeData;

    private LineChartModel lineChartModel;

    @Inject
    private SelectUsageBean selectUsageBean;
    
    private String newNodeName;
    
    private final NodeService nodeService;

    /*** OBJECT METHODS ***/
    
    /**
     * Constructor
     */
    public NodeDetailBean() {
        nodeService = new NodeService();
    }
    
    /**
     * Method, which is called when URL parameters (nodeID) are processed.
     */
    public void loadData() {
        if (nodeService.getById(nodeID) == null) {            
            throw new RuntimeException("Výpočetní uzel s ID " + nodeID + " neexistuje.");
        }
        nodeData = NodeOverviewService.getNodeOverviewData(nodeID);

        selectUsageBean.setUsageType(SelectUsageBean.SelectUsageTypeEnum.GRAPH_SELECT);
        selectUsageBean.createSelectItems();

        lineChartModel = NodeUsageGraphService.createNodeUsageModel(nodeData);

    }

    /**
     * Getter of node ID.
     *
     * @return Node ID.
     */
    public Long getNodeID() {
        return nodeID;
    }

    /**
     * Setter of node ID.
     *
     * @param nodeID New node ID.
     */
    public void setNodeID(Long nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * Getter of node data.
     *
     * @return Node data.
     */
    public NodeData getNodeData() {
        return nodeData;
    }

    /**
     * Getter of lineChartModel for secondary node usage graph.
     *
     * @return LineChartModel for secondary node usage graph.
     */
    public LineChartModel getLineChartModel() {
        return lineChartModel;
    }

    /**
     * Getter of selectUsageBean.
     *
     * @return SelectUsageBean.
     */
    public SelectUsageBean getSelectUsageBean() {
        return selectUsageBean;
    }

    /**
     * Getter of newNodeName property.
     * @return Value of newNodename property.
     */
    public String getNewNodeName() {
        return newNodeName;
    }

    /**
     * Setter of newNodeName property.
     * @param newNodeName New value of newNodeName property.
     */
    public void setNewNodeName(String newNodeName) {
        this.newNodeName = newNodeName;
    }   

    /**
     * Handler of change event on selectOneButton which is used to change scale
     * of graph usage.
     */
    public void ajaxSelectHandler() {
        NodeUsageGraphService.setSeriesData(lineChartModel, selectUsageBean.getSelectValue(), nodeData);
    }

    /**
     * Handler of click event on reset IP button which is used to delete saved
     * IP of specific seondary node.
     */
    public void resetIPHandler() {
        nodeService.resetIP(nodeID);
        this.update();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "IP adresa byla resetována"));
    }

    /**
     * Method, that is handle click event on change node name button.
     */
    public void showChangeNodeNameDialogHandler() {
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("resizable", false);
        options.put("draggable", false);
        options.put("contentHeight", "100%");
        options.put("includeViewParams", true);
        options.put("closeOnEscape", true);
        options.put("responsive", true);

        PrimeFaces.current().dialog().openDynamic("changeNodeNameDialog", options, null);
    }
    
    /**
     * Method, that is handle click event on save new node name button.
     */
    public void saveNewNodeNameHandler() {        
        if (nodeService.getByName(newNodeName) != null) {
            PrimeFaces.current().dialog().closeDynamic(false);
            return;
        }
        nodeService.changeNodeName(nodeID, newNodeName);       
        PrimeFaces.current().dialog().closeDynamic(true);
    }
    
    /**
     * Method that is called, when the dialog window is called.
     * @param isChanged True if the name of secondary node was successfully changed. False otherwise.
     */
    public void dialogReturnHandler(SelectEvent<Boolean> isChanged) {
        if (isChanged.getObject()) {
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Jméno výpočetního uzlu bylo úspěšně změněno."));
             this.update();
        } else {
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Info", "Výpočetní uzel s daným jménem již existuje."));        
        }
    }    
    
    /**
     * Update node data when server push event is received on client side.
     */
    public void update() {
        nodeData = NodeOverviewService.getNodeOverviewData(nodeID);
        this.ajaxSelectHandler();
    }
}
