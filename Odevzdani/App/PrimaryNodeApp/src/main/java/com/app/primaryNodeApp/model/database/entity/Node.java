/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.entity;

import com.app.primaryNodeApp.model.database.enums.NodeStatus.NodeStatusEnum;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;

/**
 * The Node entity.
 * @author Filip
 */

@Entity
@Table(name = "Node")
public class Node implements Serializable, EntityInterface<Long> {
            
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_nodeID")
    private Long id;
    
    @Column(name = "nodeName", unique=true, nullable=false)
    @Length(max=100)
    private String nodeName;
    
    @Column(name = "nodeStatus")
    @Enumerated(EnumType.STRING)
    private NodeStatusEnum nodeStatus;
    
    @Column(name = "IP_address", unique=true)
    @Length(max=100)
    private String nodeIP;
       
    @OneToMany(mappedBy="node")
    List<StepRunData> stepRunData = new ArrayList<>();

    @ManyToMany(mappedBy = "jobNodes")
    private List<Job> jobs = new ArrayList<>();
    
    @OneToMany(mappedBy="node", cascade = CascadeType.ALL)
    List<NodeUtilization> utilizationData = new ArrayList<>();
    
    public Node() {
        
    }

    public Node(String nodeName, NodeStatusEnum nodeStatus, String nodeIP) {
        this.nodeName = nodeName;
        this.nodeStatus = nodeStatus;
        this.nodeIP = nodeIP;
    }
    
    public Long getId() {
        return id;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public NodeStatusEnum getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeStatusEnum nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

   public String getNodeIP() {
        return  nodeIP;
    }

    public void setNodeIP(String nodeIP) {
        this.nodeIP = nodeIP;
    }

    public List<StepRunData> getStepRunData() {
        return stepRunData;
    }

    public void setStepRunData(List<StepRunData> StepRunData) {
        this.stepRunData = StepRunData;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<NodeUtilization> getUtilizationData() {
        return utilizationData;
    }

    public void setUtilizationData(List<NodeUtilization> utilizationData) {
        this.utilizationData = utilizationData;
    }

    @Override
    public String toString() {
        return 
            "Node{" + 
                "id=" + id + 
                ", nodeName=" + nodeName + 
                ", nodeStatus=" + nodeStatus + 
                ", nodeIP=" + nodeIP +
            '}';
    }  
    
}

