/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNodeApp.configuration;

import com.app.commons.eventLogger.EventLogger;
import com.app.secondaryNodeApp.fileManager.FileManager;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class with Secondary Node configuration.
 * @author filip
 */
public class Configuration implements Serializable {
    
    /** STATIC PROPERTIES **/

    
    private static Configuration configuration = null;
    
    private static String pathToConfigFile = null;
    
    /** OBJECT PROPERTIES **/
    
    // path to error folder
    private String pathToLogFolder;
    // socket sender
    private long isAliveSendingPeriod;
    // path to configuration file of program top
    private String pathToBashConfigFolder;

    /** OBJECT METHODS **/
    
    /**
     * Private constructor - Singleton pattern.
     */
    private Configuration() {

    }

    /**
     * Method, that returns singleton instance of Configuration class.
     * @return Instance of Configuration class.
     */
    public static Configuration getInstance() {
        if (configuration == null) {
            Gson gson = new Gson();
            try {
                Reader reader = new FileReader(pathToConfigFile);
                configuration = gson.fromJson(reader, Configuration.class);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, "! ERROR ! - Configuration file with path {0} hasn't been found!. Error is: \n\n{1}\n\n{2}",
                        new Object[] {pathToConfigFile, ex.toString(), EventLogger.getErrorStackTrace(ex)});
                throw new RuntimeException("! ERROR ! - Cannot open FileHandler to given path to log.");
            }
        }        
        return configuration;
    }
    
    /** GETTERS AND SETTERS **/
    
    public String getPathToLogFolder() {
        return pathToLogFolder;
    }

    public void setPathToLogFolder(String pathToLogFolder) {
        this.pathToLogFolder = pathToLogFolder;
    }

    public long getIsAliveSendingPeriod() {
        return isAliveSendingPeriod;
    }

    public void setIsAliveSendingPeriod(long isAliveSendingPeriod) {
        this.isAliveSendingPeriod = isAliveSendingPeriod;
    }

    public static void setPathToConfigFile(String pathToConfigFile) {
        Configuration.pathToConfigFile = pathToConfigFile;
    }

    public String getPathToBashConfigFolder() {
        return pathToBashConfigFolder;
    }

    public void setPathToBashConfigFolder(String pathToBashConfigFolder) {
        this.pathToBashConfigFolder = pathToBashConfigFolder;
    }

    @Override
    public String toString() {
        return "Configuration{" + "pathToLogFolder=" + pathToLogFolder + ", isAliveSendingPeriod=" + isAliveSendingPeriod + ", pathToBashConfigFolder=" + pathToBashConfigFolder + '}';
    }
}
