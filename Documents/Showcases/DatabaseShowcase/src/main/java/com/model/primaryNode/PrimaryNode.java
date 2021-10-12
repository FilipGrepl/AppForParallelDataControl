/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.primaryNode;


import com.model.dao.JobDao;
import com.model.dao.JobRunDataDao;
import com.model.dao.UserDao;
import com.model.database.entity.Job;
import com.model.database.entity.JobNodeStepKey;
import com.model.database.entity.JobRunData;
import com.model.database.entity.Node;
import com.model.database.entity.Step;
import com.model.database.entity.Error;
import com.model.database.entity.NodeUtilization;
import com.model.database.util.HibernateUtil;
import com.model.database.entity.User;
import com.model.database.status.IOtypes;
import com.model.database.status.NodeStatus;
import com.model.database.status.RecordType;
import com.model.database.status.StepStatus;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import java.sql.Timestamp;

/**
 *
 * @author Filip
 */

@Singleton
@Startup
public class PrimaryNode {
    
    private static final Logger LOG = Logger.getLogger(PrimaryNode.class.getName());

    
    public void saveInitData() {
        User user = new User("Filip", "Grepl", "Filip", "admin");
        User user2 = new User("Katarina", "Galanska", "Katka", "admin"); 
        User user3 = new User("admin", "admin", "admin", "admin");
        
        Job job = new Job("Job_1");
        Job job2 = new Job("Job_2");
        Job job3 = new Job("Job_3");
        
        Node node = new Node("Knot_1", NodeStatus.OCCUPIED);
        Node node2 = new Node("Knot_2", NodeStatus.OCCUPIED);
        Node node3 = new Node("Knot_3", NodeStatus.OCCUPIED);
        Node node4 = new Node("Knot_4", NodeStatus.FREE);
        Node node5 = new Node("Knot_5", NodeStatus.DISCONNECTED);
        
        job.getNodes().add(node);
        job.getNodes().add(node2);
        job.getNodes().add(node3);
        
        Step step = new Step(job, "step_1", "python3", IOtypes.DIRECTORY, "--input", "/mnt/data/in", "(.*)(\\\\d+)(.*)", 1, IOtypes.DIRECTORY,"--output",
                            "/mnt/data/out/$1.vert", "--debuginfo", 10000, true, true,false, 3000, "/mnt/data/err/err.log", false, 1);
        Step step2 = new Step(job, "step_2", "python3", IOtypes.FILE, "--in", "/mnt/data/in/input.in", "", 2, IOtypes.FILE,"--out",
                            "/mnt/data/out/output.out", "--logfile", 5000, false, true,false, 3000, "/mnt/data/err/err.log", false, 2);
        Step step3 = new Step(job, "step_3", "bash", IOtypes.DIR_OF_FILES, "--input", "/mnt/data/step3/inFiles", "(.*)(\\\\d+)(.*)", 2, IOtypes.DIR_OF_FILES,"--output",
                            "/mnt/data/out/$2.vert", "--err /mnt/data/err/err.log", 10000, true, true, true, 5000, "/mnt/data/err/err.log", true, 3);
        Step step4 = new Step(job, "step_4", "bash", IOtypes.DIR_OF_FILES, "--in", "/mnt/data/in/input.in", "", 1, IOtypes.DIRECTORY,"--out",
                            "/mnt/data/out/output.out", "--logfile", 5000, true, false,false, 3000, "/mnt/data/err/err.log", true, 4);
        
        Transaction transaction = null; 
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            session.save(user); session.save(user2); session.save(user3);
            session.save(job);  session.save(job2); session.save(job3);
            session.save(node); session.save(node2); session.save(node3); session.save(node4); session.save(node5);
            session.save(step); session.save(step2); session.save(step3); session.save(step4);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println(e.toString());
        }
        
        JobRunData jobRunData = new JobRunData( new JobNodeStepKey(job.getJobID(), node.getNodeID(), step.getStepID()), StepStatus.RUNNING_ERROR, 
                                                1000, 550, new Timestamp(Timestamp.valueOf("2019-09-02 12:30:00").getTime()), null, 12347, 40, 100, 
                                                80, 20, 55, 40, 1476, 200, 1600, 700);
        JobRunData jobRunData2 = new JobRunData( new JobNodeStepKey(job.getJobID(), node2.getNodeID(), step.getStepID()), StepStatus.RUNNING, 
                                                1000, 510, new Timestamp(Timestamp.valueOf("2019-09-02 12:30:00").getTime()), null, 10110, 45, 78, 
                                                60, 22, 48, 35, 1500, 250, 1430, 610);
        JobRunData jobRunData3 = new JobRunData( new JobNodeStepKey(job.getJobID(), node3.getNodeID(), step.getStepID()), StepStatus.FINISHED, 
                                                1000, 537, new Timestamp(Timestamp.valueOf("2019-09-02 12:30:00").getTime()), 
                                                new Timestamp(Timestamp.valueOf("2019-09-02 19:24:05").getTime()), 11914, 40, 100, 
                                                80, 31, 60, 39, 1480, 268, 1525, 589);
        JobRunData jobRunData4 = new JobRunData( new JobNodeStepKey(job.getJobID(), node3.getNodeID(), step2.getStepID()), StepStatus.RUNNING, 
                                                0, 0, new Timestamp(Timestamp.valueOf("2019-09-02 22:10:48").getTime()), null, 2000, 27, 75, 
                                                69, 40, 80, 70, 490, 0, 0, 0);
        
        Error error = new Error("Input_file_1", "This is content of input file", "This is content of stderr", "TImeout", jobRunData);
        Error error2 = new Error("Input_file_2", "This is content of input file", "This is content of stderr", "TImeout", jobRunData);
        
        
        NodeUtilization nodeUtilization = new NodeUtilization(RecordType.SECOND5,   new Timestamp(Timestamp.valueOf("2019-09-02 12:34:00").getTime()), 100, node);
        NodeUtilization nodeUtilization2 = new NodeUtilization(RecordType.SECOND5,  new Timestamp(Timestamp.valueOf("2019-09-02 12:34:05").getTime()), 100, node);
        NodeUtilization nodeUtilization3 = new NodeUtilization(RecordType.SECOND5,  new Timestamp(Timestamp.valueOf("2019-09-02 12:34:10").getTime()), 100, node);
        NodeUtilization nodeUtilization4 = new NodeUtilization(RecordType.SECOND5,  new Timestamp(Timestamp.valueOf("2019-09-02 12:34:20").getTime()), 100, node);
        NodeUtilization nodeUtilization5 = new NodeUtilization(RecordType.SECOND5,  new Timestamp(Timestamp.valueOf("2019-09-02 12:34:25").getTime()), 100, node);
        NodeUtilization nodeUtilization6 = new NodeUtilization(RecordType.SECOND5,  new Timestamp(Timestamp.valueOf("2019-09-02 12:34:30").getTime()), 100, node);
        NodeUtilization nodeUtilization7 = new NodeUtilization(RecordType.SECOND5,  new Timestamp(Timestamp.valueOf("2019-09-02 12:34:35").getTime()), 100, node);
        NodeUtilization nodeUtilization8 = new NodeUtilization(RecordType.SECOND5,  new Timestamp(Timestamp.valueOf("2019-09-02 12:34:40").getTime()), 100, node);
        NodeUtilization nodeUtilization9 = new NodeUtilization(RecordType.SECOND5,  new Timestamp(Timestamp.valueOf("2019-09-02 12:34:45").getTime()), 100, node);
        NodeUtilization nodeUtilization10 = new NodeUtilization(RecordType.SECOND5, new Timestamp(Timestamp.valueOf("2019-09-02 12:34:50").getTime()), 100, node);
        NodeUtilization nodeUtilization11 = new NodeUtilization(RecordType.SECOND5, new Timestamp(Timestamp.valueOf("2019-09-02 12:34:50").getTime()), 0, node);
        NodeUtilization nodeUtilization12 = new NodeUtilization(RecordType.SECOND5, new Timestamp(Timestamp.valueOf("2019-09-02 12:34:55").getTime()), 0, node);
        
        NodeUtilization nodeUtilization13 = new NodeUtilization(RecordType.MINUTE, new Timestamp(Timestamp.valueOf("2019-09-02 12:30:00").getTime()), 83, node);
        NodeUtilization nodeUtilization14 = new NodeUtilization(RecordType.MINUTE, new Timestamp(Timestamp.valueOf("2019-09-02 12:31:00").getTime()), 50, node);
        NodeUtilization nodeUtilization15 = new NodeUtilization(RecordType.MINUTE, new Timestamp(Timestamp.valueOf("2019-09-02 12:32:00").getTime()), 25, node);
        NodeUtilization nodeUtilization16 = new NodeUtilization(RecordType.MINUTE, new Timestamp(Timestamp.valueOf("2019-09-02 12:33:00").getTime()), 100, node);
        
        
        transaction = null; 
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            

            session.save(jobRunData); session.save(jobRunData2); session.save(jobRunData3); session.save(jobRunData4);
            session.save(error);
            session.save(error2);
            session.save(nodeUtilization); session.save(nodeUtilization2); session.save(nodeUtilization3);
            session.save(nodeUtilization4); session.save(nodeUtilization5); session.save(nodeUtilization6); 
            session.save(nodeUtilization7); session.save(nodeUtilization8); session.save(nodeUtilization9);
            session.save(nodeUtilization10); session.save(nodeUtilization11); session.save(nodeUtilization12);
            
            session.save(nodeUtilization13); session.save(nodeUtilization14); session.save(nodeUtilization15);
            session.save(nodeUtilization16);
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println(e.toString());
        }
        
    }
    
    // can be annotated with PostConstruct
    public void initialize() {
        System.out.println("Initialize method called");
        this.saveInitData();
        
        // TODO: Other Primary node initialization processees
    }
    
    // Temp method
    public void printSomeDataFromDatabase() {
        
        UserDao udao = new UserDao();
        User s = (User) udao.getById(new Long(1));
         
        System.out.println(s.getFirstName()+' '+s.getLastName()+' '+s.getPassword());
        
        JobRunDataDao JRDDao = new JobRunDataDao();
        JobRunData d = JRDDao.getById(new JobNodeStepKey(1,1,1));

        if (d == null)
            System.out.println("JobRunData not found");
        else
        {
            System.out.println(d.getStepStatus().toString()+' '+d.getStep().getStepName());
        }
        
        JobDao jobDao = new JobDao();
        Job job = jobDao.getById(new Long(1));
        System.out.println(job.getJobName());
        
        Job entity = jobDao.getByIdWithCollections(job.getJobID());
        entity.getSteps().forEach(ss -> {
            System.out.println(ss.getStepName());
            });
        entity.getJobRunData().forEach(dd -> {
            System.out.println(dd.getStepStatus());
        });
        entity.getNodes().forEach(nn -> {
            System.out.println(nn.getNodeName());
        });
        
        
        UserDao userDao = new UserDao();
        User user = userDao.getByIdWithCollections(new Long(2));
        System.out.println(user.getUserName()+' '+user.getFirstName()+' '+user.getLastName());
    }
}

