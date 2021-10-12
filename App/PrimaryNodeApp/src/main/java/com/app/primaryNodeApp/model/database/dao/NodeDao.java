/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.dao;

import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.enums.NodeStatus.NodeStatusEnum;
import java.util.List;

/**
 * The Dao object of Node entity.
 * @author Filip
 */
public class NodeDao extends Dao<Node, Long>{
    
    /**
     * Constructor
     */
    public NodeDao() {
        super(Node.class);
    }
    
    /**
     * Method, that initializes statuses of all nodes.
     */
    public void initializeNodesStatus() {
        for (Node node : this.getAll()) {
            if (node.getNodeStatus() != NodeStatusEnum.DISCONNECTED) {
                node.setNodeStatus(NodeStatusEnum.DISCONNECTED);
                this.update(node);
            }
        }
    }
    
    /**
     * Method, that returns node by name.
     * @param nodeName Name of node to be returned.
     * @return Node object. If no entity is found, null is returned.
     */
    public Node getByName(String nodeName) {
        this.prepareCriteria();
        this.queryCriteria.select(this.queryRoot).where(this.queryBuilder.equal(this.queryRoot.get("nodeName"), nodeName));
        List<Node> nodes = this.executeQuery();
        if (nodes == null || nodes.isEmpty())
            return null;
        if (nodes.size() != 1) {
            StringBuilder IDs = new StringBuilder();
            for (Node n : nodes) IDs.append(n.getId()).append(" ");
            daoLogger.logDuplicateNodeNameErr(nodeName, IDs.toString());
        }
        return nodes.get(0);
    }
    
    public Node getByIP(String IPaddress) {
        this.prepareCriteria();
        this.queryCriteria.select(this.queryRoot).where(this.queryBuilder.equal(queryRoot.get("nodeIP"), IPaddress));
        List<Node> nodes = this.executeQuery();
        if (nodes == null || nodes.isEmpty())
            return null;
        if (nodes.size() != 1) {
            StringBuilder IDs = new StringBuilder();
            for (Node n : nodes) IDs.append(n.getId()).append(" ");
            daoLogger.logDuplicateNodeNameErr(IPaddress, IDs.toString());
        }
        return nodes.get(0);
    }
}
