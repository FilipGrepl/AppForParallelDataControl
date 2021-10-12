/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.NodeService;

import com.app.primaryNodeApp.model.database.dao.NodeDao;
import com.app.primaryNodeApp.model.database.entity.Node;
import java.util.List;

/**
 *
 * @author filip
 */
public class NodeService {
    
    /** OBJECT PROPERTIES **/
    
    private final NodeDao nodeDao = new NodeDao();
    
    /** OBJECT METHODS **/
    
    /**
     * Method, that returns Node object by its id.
     * @param id Id of Node to be returned.
     * @return Node entity.
     */
    public Node getById(Long id) {
        return nodeDao.getById(id);
    }
    
     /**
     * Method, that return Node object by its name.
     * @param nodeName Name of Node to be returned.
     * @return Node entity.
     */
    public Node getByName(String nodeName) {
        return nodeDao.getByName(nodeName);
    }
    
    /**
     * Method, that returns all objects of Node entity.
     * @return List of all Node entities.
     */
    public List<Node> getAll() {
        return nodeDao.getAll();
    }
    
    /**
     * Method, that resets IP address of specific node.
     * @param nodeID Id of node for whom the IP address to be reseted.
     */
    public void resetIP(Long nodeID) {
        Node node = nodeDao.getById(nodeID);
        node.setNodeIP(null);
        nodeDao.update(node);
    }
    
    /**
     * Method, that changes the name of Node object.
     * @param nodeID The Id of Node for whom the name to be changed.
     * @param newNodeName The new name of Node.
     */
    public void changeNodeName(Long nodeID, String newNodeName) {
        Node node = nodeDao.getById(nodeID);
        node.setNodeName(newNodeName);
        nodeDao.update(node);
    }
    
}
