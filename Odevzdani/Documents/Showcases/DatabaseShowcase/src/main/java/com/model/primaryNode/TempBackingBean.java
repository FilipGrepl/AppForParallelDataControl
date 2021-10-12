/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.model.primaryNode;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.omnifaces.cdi.Eager;

@Named
@ApplicationScoped
@Eager
public class TempBackingBean implements Serializable {

        private static final Logger LOG = Logger.getLogger(TempBackingBean.class.getName());
        private static final String MESSAGE = "This message has been pushed by websocket :)";

        @Inject
        PrimaryNode primaryNode;
        
        @PostConstruct
        public void initialize() {            
            primaryNode.initialize();
        }

        
        public void eventAction() {
            System.out.println("******************************* Button \"Display database data to console\" is pressed! \"*******************************");
            primaryNode.printSomeDataFromDatabase();
            
            
            /********* Testing Sigar library for RAM and CPU usage monitoring *********/
            
            /*System.setProperty("java.library.path", "D:\\FIT\\Ing\\DIP\\Database\\dll");
            System.out.println(System.getProperty("java.library.path"));
            new Thread(() -> {
                    try {
                        // nutno vložit do C:\Program Files\Java\jdk1.8.0_211\bin knihovnu sigar-amd64-winnt.dll (lisi se na linux)
                        final Sigar sigar = new Sigar();
                        while (true) {
                            ProcCpu cpu = sigar.getProcCpu(12796);
                            System.out.println(cpu.getPercent()*100/4);
                            System.out.println(sigar.getCpuPerc());
                            Thread.sleep(1000);
                        }
                    } catch (SigarException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                    Logger.getLogger(TempBackingBean.class.getName()).log(Level.SEVERE, null, ex);
                }   
            }).start();*/
        }
    }
