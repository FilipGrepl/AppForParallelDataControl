/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.database.entity;

import com.model.database.status.RecordType;
import java.io.Serializable;
import java.sql.Timestamp;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
/**
 *
 * @author Filip
 */

@Entity
@Table(name = "NodeUtilization")
public class NodeUtilization implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_utilizationID")
    private Long utilizationID;
    
    @Column(name = "recordType")
    @Enumerated(EnumType.STRING)
    private RecordType recordType;
    
    @Column(name = "createdAt")
    private Timestamp createdAt;
    
    @Column(name = "loadNode")
    @Min(0)
    @Max(100)
    private int loadNode;
    
    @ManyToOne
    @JoinColumn(name = "FK_nodeID", nullable = false)
    private Node node;
    
    public NodeUtilization() {
    }

    public NodeUtilization(RecordType recordType, Timestamp createdAt, int loadNode, Node node) {
        this.recordType = recordType;
        this.createdAt = createdAt;
        this.loadNode = loadNode;
        this.node = node;
    }

    public long getUtilizationID() {
        return utilizationID;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getLoadCPU() {
        return loadNode;
    }

    public void setLoadCPU(int loadNode) {
        this.loadNode = loadNode;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}