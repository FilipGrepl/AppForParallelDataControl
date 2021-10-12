/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNodeApp.main;

import com.app.commons.eventLogger.EventLogger;
import com.app.secondaryNodeApp.configuration.Configuration;
import com.app.secondaryNodeApp.fileManager.FileManager;
import com.app.secondaryNodeApp.secSocketCommunicators.SecSocketReceiver;
import com.app.secondaryNodeApp.secSocketCommunicators.SecSocketSender;
import com.app.secondaryNodeApp.stepExecution.UsageValuesSender;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.cli.*;

/**
 * Main class.
 *
 * @author Filip
 */
public class Main {

    /**
     * STATIC PROPERY *
     */
    private static SSLSocket SOCKET;
    private static final Logger LOGGER = EventLogger.getInstance().getLogger(Main.class.getName());

    /**
     * STATIC METHODS *
     */
    /**
     * Method for parsing command line arguments.
     *
     * @param args Input arguments
     * @return Map which maps name of parameter to its value.
     */
    private static Map<String, String> parseArguments(String[] args) {
        Options options = new Options();
        Option ip = new Option("ip", "primaryIP", true, "Primary node IP address");
        ip.setRequired(true);
        options.addOption(ip);

        Option port = new Option("port", "primaryPort", true, "Primary node port");
        port.setRequired(true);
        port.setType(int.class);
        options.addOption(port);

        Option nodeName = new Option("name", "secNodeName", true, "Name of Secondary node");
        nodeName.setRequired(true);
        options.addOption(nodeName);

        Option pathToConfigFile = new Option("config", "pathToConfig", true, "Path to configuration file");
        pathToConfigFile.setRequired(true);
        options.addOption(pathToConfigFile);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("SecondaryNode-1.0.jar", options);
        }

        if (cmd == null) {
            return null;
        } else {
            Map<String, String> arguments = new HashMap<>();
            arguments.put("ip", cmd.getOptionValue("ip"));
            arguments.put("port", cmd.getOptionValue("port"));
            arguments.put("name", cmd.getOptionValue("name"));
            arguments.put("config", cmd.getOptionValue("config"));
            return arguments;
        }

    }

    /**
     * Set function to catch CTRL+C interrupt signal.
     */
    private static void setShutdownHook(ScheduledExecutorService execService, ScheduledFuture<?> scheduledFuture) {
        // Catch CTRL+C
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Interrupted - close socket");
                EventLogger.getInstance().closeFileHandler();
                // all threads, which executing step are terminated by SocketReceiver when the socket is closed
                if (!SOCKET.isClosed()) {
                    try {
                        SOCKET.close();
                        scheduledFuture.cancel(true);
                        execService.shutdown();
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "{0}\n\n{1}", new Object[]{e.getMessage(), EventLogger.getErrorStackTrace(e)});
                    }
                }
            }
        });
    }

    /**
     * Main function of Secondary node.
     *
     * @param args Input arguments.
     * @throws InterruptedException If an InterruptedException occurs.
     * @throws IOException If an I/O exception occurs.
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        //System.out.println(FileManager.getPathToOutputFsNode("(.*)\\/(.*)(\\.txt)", "mnt/in/filip/file_1.txt", "mnt/out/filip/$10.out"));
        Map<String, String> arguments = parseArguments(args);
        SecSocketReceiver secSocketReceiver = null;
        if (arguments == null) {
            return;
        }

        // set path to config path if it was entered
        Configuration.setPathToConfigFile(arguments.get("config"));
        
        // check config file of program top
        if (!FileManager.isNonEmptyFile(Configuration.getInstance().getPathToBashConfigFolder()+"/.toprc")) {
            throw new RuntimeException("Configuration file of program top not found");
        }
        
        // set logger file
        EventLogger.getInstance().addFileHandler(Configuration.getInstance().getPathToLogFolder() + arguments.get("name") + "_error.log");

        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SOCKET = (SSLSocket) factory.createSocket(arguments.get("ip"), Integer.valueOf(arguments.get("port")));
            SOCKET.startHandshake();
            SecSocketSender.initialize(SOCKET, arguments.get("name"));
            SecSocketSender secSocketSender = SecSocketSender.getInstance();
            SecSocketReceiver.initialize(SOCKET, secSocketSender);
            secSocketReceiver = SecSocketReceiver.getInstance();
        } catch (IOException | NumberFormatException ex) {
            LOGGER.log(Level.SEVERE, "{0}\n\n{1}", new Object[]{ex.getMessage(), EventLogger.getErrorStackTrace(ex)}); //TODO: logging
            return;
        }

        ScheduledExecutorService execService;
        ScheduledFuture<?> scheduledFuture;
        execService = Executors.newScheduledThreadPool(1);
        scheduledFuture = execService.scheduleAtFixedRate(UsageValuesSender.getInstance(), 0, 5, TimeUnit.SECONDS);

        setShutdownHook(execService, scheduledFuture);

        if (secSocketReceiver != null) {
            secSocketReceiver.run(); /// !!!! must be run       
        }

        scheduledFuture.cancel(true);
        execService.shutdown();
        //System.exit(0);
        // top -d 1 -b -n 5 | grep -e "[[:space:]]\+161" | sed -e "s/[[:space:]]\+/ /g" | cut -d ' ' -f 10 | paste -sd+ | bc | awk '{print $1/5}'
    }
}
