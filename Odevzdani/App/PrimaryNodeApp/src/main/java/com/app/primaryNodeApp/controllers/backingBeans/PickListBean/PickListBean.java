/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.PickListBean;

import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.services.NodeService.NodeService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.DualListModel;

/**
 * The backing bean for pick list selector of secondary nodes.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class PickListBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private DualListModel<String> nodeNames;
    private List<Node> nodes;
    
    private final NodeService nodeService;
    
    /** OBJECT METHODS **/
    
    /**
     * Constructor.
     */
    public PickListBean() {
        nodeService = new NodeService();
    }
    
    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        this.initDualList();
    }

    /**
     * Method, which inits dual list of secondary nodes to default state.
     */
    public void initDualList() {
        nodes = nodeService.getAll();
        
        List<String> source = new ArrayList<>();
        List<String> target = new ArrayList<>();

        nodes.forEach(node -> source.add(node.getNodeName()));
        nodeNames = new DualListModel<>(source, target);
    }

    /**
     * Getter of secondary nodes DualList.
     * @return DualList of secondary nodes.
     */
    public DualListModel<String> getNodeNames() {
        return nodeNames;
    }

    /**
     * Setter of secondary nodes DualList.
     * @param nodeNames New value of secondary nodes DualList.
     */
    public void setNodeNames(DualListModel<String> nodeNames) {
        this.nodeNames = nodeNames;
    }

    /**
     * Setter of secondary nodes DualList by job - for editing, create task based on difference task.
     * @param job Job based on which the DualList is set.
     */
    public void setDualListByJob(Job job) {
        this.nodeNames.getSource().clear();
        this.nodeNames.getTarget().clear();

        nodeService.getAll().forEach(node -> {
            if (!job.getJobNodes().stream().anyMatch(jobNode -> jobNode.getNodeName().equals(node.getNodeName()))) {
                this.nodeNames.getSource().add(node.getNodeName());
            }
        });
        job.getJobNodes().forEach(node -> this.nodeNames.getTarget().add(node.getNodeName()));
    }
    
    /**
     * Getter of all job nodes.
     * @return List of all job nodes.
     */
    public List<Node> getJobNodes() {
        Predicate<Node> byName = node -> nodeNames.getTarget().contains(node.getNodeName());
        return this.nodes.stream().filter(byName).collect(Collectors.toList());
    }
    
    /**
     * Update node data when server push event is received on client side.
     */
    public void update() {
        nodes = nodeService.getAll();
        this.nodeNames.getSource().clear();
        nodes.forEach((Node node) -> {
            if (!this.nodeNames.getTarget().contains(node.getNodeName())) {
                this.nodeNames.getSource().add(node.getNodeName());
            }
        });
    }
}
