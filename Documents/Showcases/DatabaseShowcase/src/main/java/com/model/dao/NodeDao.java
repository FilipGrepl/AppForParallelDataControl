/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.dao;

import com.model.database.entity.Node;

/**
 *
 * @author Filip
 */
public class NodeDao extends Dao<Node, Long>{
    
    public NodeDao() {
        super(Node.class);
    }
}
