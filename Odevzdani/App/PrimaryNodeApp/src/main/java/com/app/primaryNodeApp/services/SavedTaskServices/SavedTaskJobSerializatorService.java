/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.SavedTaskServices;

import com.app.primaryNodeApp.model.database.dao.NodeDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
/**
 * The service for serializing and deserializing saved task.
 * @author filip
 */
public class SavedTaskJobSerializatorService {

    /** STATIC PROPERTIES **/
    
    private static final Type NODE_LIST_TYPE = new TypeToken<List<Node>>() {}.getType();
    
    /** OBJECT METHODS **/
    
    /**
     * Method for serializing saved task.
     * @param job Task to be serialized.
     * @return Serialized task as string.
     */
    public static String serializeJob(Job job) {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation();

        
        JsonSerializer<List<Node>> nodesSerializer = (List<Node> src, Type typeOfSrc, JsonSerializationContext context) -> {
            JsonObject jsonNodes = new JsonObject();

            String nodeNames = "";
            for (Node node : src) {
                nodeNames += node.getNodeName() + (src.indexOf(node) == src.size() - 1 ? "" : ",");
            }
            jsonNodes.addProperty("nodeNames", nodeNames);
            return jsonNodes;
        };

        gsonBuilder.registerTypeAdapter(NODE_LIST_TYPE, nodesSerializer);

        Gson gson = gsonBuilder.create();
        return gson.toJson(job);

    }

    /**
     * Method for deserializing saved task.
     * @param serializedJob Task to be deserialized.
     * @return Deserialized task.
     */
    public static Job deserializeJob(String serializedJob) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        JsonDeserializer<List<Node>> nodesDeserializer = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
            JsonObject jsonObject = json.getAsJsonObject();
            List<Node> nodes = new ArrayList<>();
            NodeDao nodeDao = new NodeDao();
            
            String nodeNames = jsonObject.get("nodeNames").getAsString();
            for (String nodeName : nodeNames.split(",")) {
      
                    Node node = nodeDao.getByName(nodeName);
                    if (node != null)
                        nodes.add(node);
  
            }           
            return nodes;
        };
        
        gsonBuilder.registerTypeAdapter(NODE_LIST_TYPE, nodesDeserializer);        
        Gson gson = gsonBuilder.create();
        
        Job job = gson.fromJson(serializedJob, Job.class);       
        
        job.getJobSteps().forEach(step -> step.setStepOrder(job.getJobSteps().indexOf(step)+1));
        
        return job;
    }

}
