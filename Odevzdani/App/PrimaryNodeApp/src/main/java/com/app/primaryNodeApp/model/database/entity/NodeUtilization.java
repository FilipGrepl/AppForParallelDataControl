/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.entity;

import com.app.primaryNodeApp.model.database.enums.RecordType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
/**
 * The NodeUtilization entity.
 * @author Filip
 */

@Entity
@Table(name = "NodeUtilization")
public class NodeUtilization implements Serializable, EntityInterface<Long> {
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_utilizationID")
    private Long id;
    
    @Column(name = "recordType")
    @Enumerated(EnumType.STRING)
    private RecordType recordType;
    
    @Column(name = "createdAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "loadNode")
    @Min(0)
    @Max(100)
    private Integer loadNode;
    
    @ManyToOne
    @JoinColumn(name = "FK_nodeID", nullable = false)
    private Node node;
    
    public NodeUtilization() {
    }

    public NodeUtilization(RecordType recordType, Date createdAt, Integer loadNode, Node node) {
        this.recordType = recordType;
        this.createdAt = createdAt;
        this.loadNode = loadNode;
        this.node = node;
    }

    public Long getId() {
        return id;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLoadNode() {
        return loadNode;
    }

    public void setLoadNode(Integer loadNode) {
        this.loadNode = loadNode;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return 
            "NodeUtilization{" + 
                "id=" + id + 
                ", recordType=" + recordType + 
                ", createdAt=" + createdAt + 
                ", loadNode=" + loadNode + 
            '}';
    }
    
    
    
}