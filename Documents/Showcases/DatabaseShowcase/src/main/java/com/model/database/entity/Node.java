/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.database.entity;

import com.model.database.status.NodeStatus;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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

/**
 *
 * @author Filip
 */

@Entity
@Table(name = "Node")
public class Node implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_nodeID")
    private Long nodeID;
    
    @Column(name = "nodeName")
    private String nodeName;
    
    @Column(name = "nodeStatus")
    @Enumerated(EnumType.STRING)
    private NodeStatus nodeStatus;
    
    @OneToMany(mappedBy="node", cascade = CascadeType.ALL)
    Set<JobRunData> NodeRunData = new HashSet<>();

    @ManyToMany(mappedBy = "jobNodes")
    private Set<Job> jobs = new HashSet<>();
    
    @OneToMany(mappedBy="node", cascade = CascadeType.ALL)
    Set<NodeUtilization> utilizationData = new HashSet<>();
    
    public Node() {
        
    }

    public Node(String nodeName, NodeStatus nodeStatus) {
        this.nodeName = nodeName;
        this.nodeStatus = nodeStatus;
    }
    
    public long getNodeID() {
        return nodeID;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public NodeStatus getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public Set<JobRunData> getNodeRunData() {
        return NodeRunData;
    }

    public void setNodeRunData(Set<JobRunData> NodeRunData) {
        this.NodeRunData = NodeRunData;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    public Set<NodeUtilization> getUtilizationData() {
        return utilizationData;
    }

    public void setUtilizationData(Set<NodeUtilization> utilizationData) {
        this.utilizationData = utilizationData;
    }
}

