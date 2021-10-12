/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNodeApp.stepExecution;

import com.app.commons.socketMessages.UsageMsg;
import com.app.commons.socketMessages.sendingItems.UsageValues;
import com.app.secondaryNodeApp.configuration.Configuration;
import com.app.secondaryNodeApp.fileManager.FileManager;
import com.app.secondaryNodeApp.secLoggers.SecExecLogger;
import com.app.secondaryNodeApp.secSocketCommunicators.SecSocketSender;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Filip
 */
public class UsageValuesSender extends Thread {

    /**
     * STATIC PROPERTY *
     */
    private static final int USAGE_NUMBER_OF_ITERATIONS = 6; // execution time is 5 seconds, because first iteration is executed immediately
    private static final int USAGE_DELAY_TIME = 1; // seconds
    private static final String[] USAGE_CMD = {"/bin/sh", "-c", "export HOME={0} && top -d " + USAGE_DELAY_TIME + " -b -n " + USAGE_NUMBER_OF_ITERATIONS + " | sed -e \"s/^[[:space:]]*//g\" | grep -E \"^({1})[[:space:]]\" |  sed -r \"s/[[:space:]]+/\\t/g\""};
    //private static final String[] USAGE_CMD = {"/bin/sh", "-c", "top -d "+USAGE_DELAY_TIME+" -b -n "+USAGE_NUMBER_OF_ITERATIONS+" | sed -e \"s/^[[:space:]]*//g\" | grep -e \"^{0}\" |  sed -e \"s/[[:space:]]\\+/\\t/g\" | cut -f 9,10"};
    private static UsageValuesSender usageValuesSender = null;

    /**
     * OBJECT PROPERTY *
     */
    private final SecExecLogger secExecLogger;
    private final SecSocketSender secSocketSender;

    private final List<Process> stepRuns;       // list of processes, for which the load of node is monited
    //private final Process stepRun;

    /**
     * STATIC METHOD *
     */
    /**
     * The method, which returns the PID of running process.
     *
     * @param p Running process.
     * @return PID of running process.
     * @throws NoSuchFieldException In an NoSuchFieldException occurs.
     * @throws IllegalArgumentException In an IllegalArgumentException occurs.
     * @throws IllegalAccessException In an IllegalAccessException occurs.
     */
    public static long getProcessID(Process p) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        long result = -1;

        //for windows
        if (System.getProperty("os.name").contains("Windows")) {
            Field f = p.getClass().getDeclaredField("handle");
            f.setAccessible(true);
            long handl = f.getLong(p);
            Kernel32 kernel = Kernel32.INSTANCE;
            WinNT.HANDLE hand = new WinNT.HANDLE();
            hand.setPointer(Pointer.createConstant(handl));
            result = kernel.GetProcessId(hand);
            f.setAccessible(false);
        } //for unix based operating systems
        else {
            Field f = p.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            result = f.getLong(p);
            f.setAccessible(false);
        }
        return result;
    }
    
    public static String getChildPID(Process p) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, IOException {
        if (!System.getProperty("os.name").contains("Windows")) {
            Process child = Runtime.getRuntime().exec("pgrep -P " + UsageValuesSender.getProcessID(p));
            return FileManager.getStreamContent(child.getInputStream()).replace("\n", "");
        } else {
            return null;
        }
    }

    /**
     * OBJECT METHOD *
     */
    /**
     * Private constructor
     *
     * @throws IOException If an I/O expcetion occurs.
     */
    private UsageValuesSender() throws IOException {
        secExecLogger = new SecExecLogger(UsageValuesSender.class);
        secSocketSender = SecSocketSender.getInstance();
        this.stepRuns = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Method which returns reference to singleton UsageValuesSender object.
     *
     * @return Reference to singleton UsageValuesSender object.
     * @throws java.io.IOException If an I/O exception occurs.
     */
    public static synchronized UsageValuesSender getInstance() throws IOException {
        if (usageValuesSender == null) {
            usageValuesSender = new UsageValuesSender();
        }
        return usageValuesSender;
    }

    /**
     * @param stepRun The process to be added to the list of monitored.
     */
    public void addProcessToMonitore(Process stepRun) {
        synchronized (stepRuns) {
            this.stepRuns.add(stepRun);
        }
    }

    public String[] createUsageCmd() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, IOException {
        String[] usageCmd = USAGE_CMD.clone();
        StringBuilder PIDs = new StringBuilder();
        
        synchronized(stepRuns) {
            for (Process p : stepRuns) {
                if (System.getProperty("os.name").contains("Windows")) {
                    PIDs.append(Long.toString(getProcessID(p)));
                } else {
                    PIDs.append(getChildPID(p));     // the child PID must be obtained, because stepRuns are /bin/sh processes
                }
                 
                if (stepRuns.indexOf(p) != stepRuns.size() - 1) {
                    PIDs.append("|");
                }
            }
        }
        usageCmd[usageCmd.length - 1] = MessageFormat.format(usageCmd[usageCmd.length - 1], Configuration.getInstance().getPathToBashConfigFolder(), PIDs.toString());
        return usageCmd;
    }

    /**
     * Method which send every 5 second informations about ram and cpu usage.
     */
    @Override
    public void run() {

        // the usage values cannot be read for windows - the usage value sender is ended
        if (System.getProperty("os.name").contains("Windows")) {
            return;
        }

        Process usageProcess;
        String stdout, stderr;
        UsageValues usageValues;

        try {
            synchronized (stepRuns) {
                Iterator<Process> i = stepRuns.iterator();
                while (i.hasNext()) {
                    Process p = i.next(); // must be called before you can call i.remove()
                    if (p == null || !p.isAlive()) {
                        i.remove();
                    }
                }

                if (stepRuns.isEmpty()) {
                    return;
                }
            }
            String[] usageCmd = this.createUsageCmd();
            
            usageProcess = Runtime.getRuntime().exec(usageCmd);
            usageProcess.waitFor();
            
            if (stepRuns.isEmpty()) {
                return;
            }
            stdout = FileManager.getStreamContent(usageProcess.getInputStream());
            stderr = FileManager.getStreamContent(usageProcess.getErrorStream());
            
            if (!stderr.isEmpty() || usageProcess.exitValue() != 0) {
                secExecLogger.logErrUsageCmdExec(stderr, usageProcess.exitValue());
                return;
            }
            
            usageValues = getUsageValues(stdout);
            secSocketSender.sendMessage(new UsageMsg(usageValues));
        } catch (IOException e) {
            secExecLogger.logIOErrUsageThread(e);
        } catch (InterruptedException e) {
            secExecLogger.logErrWaitToUsageThread(e);
        } catch (NumberFormatException e) {
            secExecLogger.logErrUsageConvert(e);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            secExecLogger.logErrGetPid(e);
        }
    }

    /**
     * Method, which parses the usage values and enumerate the arithmetic mean.
     *
     * @param stdout Content of stdout.
     * @return Arithmetic mean of usage values.
     * @throws NumberFormatException If an NumberFormatException occurs.
     */
    private UsageValues getUsageValues(final String stdout) throws NumberFormatException {
        
        String[] parsedStdout = stdout.split("\n");
        Map<Integer, List<Double>> CPUvalues = new HashMap<>();
        Map<Integer, List<Double>> RAMvalues = new HashMap<>();
        
        for (String line : parsedStdout) {
            if (line.equals("")) {
                continue;
            }
            String[] PidCpuRamValues = line.split("\t");

            Integer PID = Integer.parseInt(PidCpuRamValues[0]);
            CPUvalues.putIfAbsent(PID, new ArrayList<>());
            RAMvalues.putIfAbsent(PID, new ArrayList<>());
            
            CPUvalues.get(PID).add(Double.parseDouble(PidCpuRamValues[1].replace(",", ".")));            
            RAMvalues.get(PID).add(scaleToGiB(PidCpuRamValues[2].replace(",", ".")));
        }
        
        Double RamUsage = RAMvalues
                .values()
                .stream()
                .map(oneProcessRamValues -> oneProcessRamValues.stream().reduce(new Double(0), Double::sum) /oneProcessRamValues.size())
                .reduce(new Double(0), Double::sum);
        
        Double CpuUsage = CPUvalues
                .values()
                .stream()
                .map(oneProcessCpuValues -> oneProcessCpuValues.stream().reduce(new Double(0), Double::sum) /oneProcessCpuValues.size())
                .reduce(new Double(0), Double::sum);
        
        return new UsageValues(CpuUsage, RamUsage);
    }
    
    private double scaleToGiB(String readedValue) {
        String unit = readedValue.substring(readedValue.length()-1);
        
        switch (unit) {
            case "m":
                // read value is in mebibytes
                return Double.parseDouble(readedValue.substring(0, readedValue.length()-2)) / 1024;
            case "g":
                // read value is in gibibytes
                return Double.parseDouble(readedValue.substring(0, readedValue.length()-2));
            case "t":
                // read value is in tebibytes
                return Double.parseDouble(readedValue.substring(0, readedValue.length()-2)) * 1024;
            case "p":
                // read value is in pebibytes
                return Double.parseDouble(readedValue.substring(0, readedValue.length()-2)) * 1024 * 1024;
            default:
                // read value is in kibibytes
                if (unit.equals("k")) {
                    return Double.parseDouble(readedValue.substring(0, readedValue.length()-2)) / (1024 * 1024);
                } else { // unit "k" is not displayed because "k" is default top value
                    return Double.parseDouble(readedValue) / (1024 * 1024);
                }
        }
    }
}
