/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.util;

import com.app.primaryNodeApp.controllers.backingBeans.Login.LoginBean;
import com.app.primaryNodeApp.services.dataClasses.RunTaskData;
import com.app.primaryNodeApp.services.dataClasses.TaskData;
import com.app.primaryNodeApp.model.database.dao.JobDao;
import com.app.primaryNodeApp.model.database.dao.StepRunDataDao;
import com.app.primaryNodeApp.model.database.dao.NodeDao;
import com.app.primaryNodeApp.model.database.dao.RunJobDao;
import com.app.primaryNodeApp.model.database.dao.UserDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.RunJobNodeStepKey;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.NodeUtilization;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.model.database.entity.User;
import com.app.primaryNodeApp.model.database.enums.IOtypes.IOtypesEnum;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import com.app.primaryNodeApp.model.database.entity.Error;
import com.app.primaryNodeApp.model.database.enums.NodeStatus.NodeStatusEnum;
import com.app.primaryNodeApp.model.database.enums.RecordType;
import com.app.primaryNodeApp.model.database.enums.UserRoles.UserRolesEnum;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * The class, which saves testing data into database.
 * @author Filip
 */

public class DatabaseInitializer {
    
    /**
     * Method, that saves testing data into database.
     */
    public static void saveInitData() {
        Transaction transaction; 
        Session session = null;
        
        List<User> users = new ArrayList<>();
                
        User user = new User("Filip", "Grepl", "filipgrepl", LoginBean.getPasswordHash("filipgrepl"), UserRolesEnum.ADMIN);
        User user2 = new User("Pavel", "Smrž", "pavelsmrz", LoginBean.getPasswordHash("pavelsmrz"), UserRolesEnum.ADMIN); 
        User user3 = new User("Jan", "Doležal", "jandolezal", LoginBean.getPasswordHash("jandolezal"), UserRolesEnum.ADMIN);
        User user4 = new User("Jaroslav", "Dytrych", "jaroslavdytrych", LoginBean.getPasswordHash("jaroslavdytrych"), UserRolesEnum.ADMIN);
        
        UserDao userDao = new UserDao();
        if (userDao.getByUsername(user.getUsername()) == null) users.add(user);
        if (userDao.getByUsername(user2.getUsername()) == null) users.add(user2);
        if (userDao.getByUsername(user3.getUsername()) == null) users.add(user3);
        if (userDao.getByUsername(user4.getUsername()) == null) users.add(user4);
        
        /*Job job = new Job("Job_1");
        
        Long t = System.currentTimeMillis();
        RunJob runJob = new RunJob(job,  new Date(t - 2 * RunTaskData.DAY), null);
        RunJob runJob2 = new RunJob(job, new Date(Timestamp.valueOf("2019-09-02 12:20:00").getTime()), new Date(Timestamp.valueOf("2019-09-08 20:00:49").getTime()));
        
        Node node = new Node("Knot_1", NodeStatusEnum.OCCUPIED, "192.168.0.1");
        Node node2 = new Node("Knot_2", NodeStatusEnum.OCCUPIED, "192.168.0.2");
        Node node3 = new Node("Knot_3", NodeStatusEnum.OCCUPIED, "192.168.0.3");
        Node node4 = new Node("Knot_4", NodeStatusEnum.FREE, "192.168.0.4");
        Node node5 = new Node("Knot_5", NodeStatusEnum.DISCONNECTED, "192.168.0.5");
        
        job.getJobNodes().add(node);
        job.getJobNodes().add(node2);
        job.getJobNodes().add(node3);           
        job.getJobNodes().add(node4);
        
        final long MilisecondsInHour = 3600000;
        
        /*Job job, String stepName, String commandPrefix, IOtypes inputType, String inputArgument, String inputPath, String inputRegex, int processes, IOtypes outputType, 
        String outputArgument, String outputPath, String commandSuffix, long timeout, boolean ExistNoEmptyOutputFsNode, boolean EmptyStderr, boolean CheckLogFileSize, 
        String pathToLog, int logSizeLessThan, boolean EqualInToOutFiles, boolean EqualInToOutFilesSecondOutput, String pathToSecondOutputFolder, boolean saveStderr, 
        boolean saveErrLog, String pathToErrLog, boolean SynchronizedStep, int stepOrder*/
        
        /*Step step = new Step(job, "step_1", "python3", IOtypesEnum.FOLDER, "--input", "/mnt/d/tmp/in", "(.*)(\\\\d+)(.*)", 1, IOtypesEnum.FOLDER,"--output",
                            "/mnt/d/tmp/out", "--debuginfo", Step.MILLIS_IN_MINUTE*5, true, true,true, "path/to/check/log/size", Step.BYTES_IN_KB*5, true, true, "/path/to/outputFile", 
                            true, true, "/mnt/data/err/err.log", false, 1);
        Step step2 = new Step(job, "step_2", "sleep 18", IOtypesEnum.FOLDER, "--in", "/mnt/d/tmp/in", "", 1, IOtypesEnum.FOLDER, "--out",
                            "/mnt/d/tmp/out", "--logfile", Step.MILLIS_IN_HOUR*2, false, true,false, "", Step.BYTES_IN_MB*2, false, false, null, false, false, "", false, 2);
        Step step3 = new Step(job, "step_3", "bash", IOtypesEnum.FOLDER_OF_FILES, "--input", "/mnt/data/step3/inFiles", "(.*)(\\\\d+)(.*)", 2, IOtypesEnum.FOLDER_OF_FILES, "--output",
                            "/mnt/data/out/$2.vert", "--err /mnt/data/err/err.log", Step.MILLIS_IN_DAY*17, true, true, true, "path/to/check/log/size", Step.BYTES_IN_KB*17, false, false, null, true, true, "/mnt/data/err/err.log", true, 3);
        Step step4 = new Step(job, "step_4", "bash", IOtypesEnum.FOLDER_OF_FILES, "--in", "/mnt/data/in/input.in", "", 1, IOtypesEnum.FOLDER, "--out",
                            "/mnt/data/out/output.out", "--logfile", Step.MILLIS_IN_HOUR*12, true, false,false, "", Step.BYTES_IN_MB*12, true, true, "/path/to/outputFile", true, true, "/mnt/data/err/err.log", true, 4);
        */
        transaction = null; 
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            users.forEach(session::save);
            /*session.save(job);
            session.save(runJob);
            session.save(runJob2);
            session.save(node); session.save(node2); session.save(node3); session.save(node4); session.save(node5);
            session.save(step); session.save(step2); session.save(step3); session.save(step4);*/
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null)
                session.close();
        }
        
        /*List<StepRunData> stepRunData = new ArrayList<>();
        
        /** Step 1 **/
        
        /*stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node.getId(), step.getId()), StepStatusEnum.RUNNING_ERROR, 
                                                new Long(1000), new Long(550),
                                                new Date(Timestamp.valueOf("2020-08-02 12:30:00").getTime()), new Date(Timestamp.valueOf("2020-08-17 12:30:00").getTime()), null,
                                                t-(t-TaskData.HOURS*100), 0, 40, 100, 
                                                80, 20, 55, 40, 1476, 200, "minFile.txt", 100, "minNormalizedFile.txt", 1600, "maxFile.txt", 150, "maxNormalizedFile.txt", 700,0, false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node2.getId(), step.getId()), StepStatusEnum.RUNNING, 
                                                new Long(1000), new Long(510), new Date(Timestamp.valueOf("2020-08-02 12:30:00").getTime()),
                                                null, null, t-(t-TaskData.HOURS*115-TaskData.MINUTES*15), 0, 45, 78, 
                                                60, 22, 48, 35, 1500, 250, "minFile.txt", 150, "minNormalizedFile.txt", 1430, "maxFile.txt", 150, "maxNormalizedFile.txt", 610,0, false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node4.getId(), step.getId()), StepStatusEnum.FINISHED, 
                                               0, 0, new Date(Timestamp.valueOf("2020-08-02 12:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2020-08-15 19:24:05").getTime()), null, t-(t-TaskData.HOURS*47), 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, "minFile.txt", 100, "minNormalizedFile.txt", 0, "maxFile.txt", 150, "maxNormalizedFile.txt", 0,0, false, false)
        );        
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node3.getId(), step.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2020-08-02 12:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2020-08-19 19:24:05").getTime()), null, t-(t-TaskData.HOURS*7-TaskData.MINUTES*39), 0, 40, 100, 
                                                80, 31, 60, 39, 1480, 268, "minFile.txt", 100, "minNormalizedFile.txt", 1525, "maxFile.txt", 150, "maxNormalizedFile.txt", 589,0, false, false)
        );
        /** Step 2 **/
        /*stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node.getId(), step2.getId()), StepStatusEnum.WAITING, 
                                                0, 0, null, null, null, 0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, "minFile.txt", 100, "minNormalizedFile.txt", 0, "maxFile.txt", 150, "maxNormalizedFile.txt", 0, 0, false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node2.getId(), step2.getId()), StepStatusEnum.WAITING, 
                                                0, 0, null, null, null,0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, "minFile.txt", 100, "minNormalizedFile.txt", 0, "maxFile.txt", 150, "maxNormalizedFile.txt", 0, 0, false, false)
        );
        
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node3.getId(), step2.getId()), StepStatusEnum.RUNNING, 
                                                new Long(1000), new Long(5), new Date(Timestamp.valueOf("2020-08-01 12:30:00").getTime()),
                                                null, null,
                                                t-(t-TaskData.MINUTES*14), 0,         // running time
                                                27, 75, 69,     // cpu load
                                                40, 80, 70,     // ram usage
                                                490,            // ram cpu counter
                                                0, "minFile.txt", 100, "minNormalizedFile.txt", 0, "maxFile.txt", 150, "maxNormalizedFile.txt", 0,        // file proc time
                                                0,             // avg ram cpu counter
                                                false, false)          // run with errors
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node4.getId(), step2.getId()), StepStatusEnum.ERROR, 
                                                new Long(1000), new Long(214), new Date(Timestamp.valueOf("2020-08-02 13:30:00").getTime()), null,
                                                new Date(Timestamp.valueOf("2020-08-02 15:42:00").getTime()),
                                                t-(t-TaskData.HOURS*14-TaskData.MINUTES*7), 0,          // running time
                                                27, 75, 69,     // cpu load
                                                40, 80, 70,     // ram usage
                                                490,            // ram cpu counter
                                                1241, "minFile.txt", 100, "minNormalizedFile.txt", 1700, "maxFile.txt", 150, "maxNormalizedFile.txt", 3540,        // file proc time
                                                214,             // avg ram cpu counter
                                                false, false)          // run with errors
        );
        /** Step 3 **/
        /*stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node.getId(), step3.getId()), StepStatusEnum.WAITING, 
                                                0, 0, null, null, null, 0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, null, 0, null, 0, null, 0, null, 0, 0, false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node2.getId(), step3.getId()), StepStatusEnum.WAITING, 
                                                0, 0, null, null, null, 0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, null, 0, null, 0, null, 0, null, 0, 0, false, false)
        );        
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node3.getId(), step3.getId()), StepStatusEnum.WAITING, 
                                                0, 0, null, null,null, 0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, null, 0, null, 0, null, 0, null, 0, 0, false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node4.getId(), step3.getId()), StepStatusEnum.WAITING, 
                                                0, 0, null, null, null, 0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, null, 0, null, 0, null, 0, null, 0, 0, false, false)
        );
        /** Step 4 **/
        /*stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node.getId(), step4.getId()), StepStatusEnum.WAITING, 
                                                0, 0, null, null, null, 0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, null, 0, null, 0, null, 0, null, 0, 0, false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node2.getId(), step4.getId()), StepStatusEnum.WAITING, 
                                                0, 0, null, null, null, 0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, null, 0, null, 0, null, 0, null, 0, 0, false, false)
        );        
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node3.getId(), step4.getId()), StepStatusEnum.WAITING, 
                                                0, 0, null, null, null, 0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, null, 0, null, 0, null, 0, null, 0, 0, false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob.getId(), node4.getId(), step4.getId()), StepStatusEnum.WAITING, 
                                                0, 0, null, null, null, 0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, null, 0, null, 0, null, 0, null, 0, 0, false, false)
        );
        
        
        // data of finished task
        /** node 1 **/
        /*stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node.getId(), step.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-02 12:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-02 19:24:05").getTime()),  null,
                                                TaskData.HOURS*4, TaskData.MINUTES*4,     // running time
                                                14, 69, 58,                 // cpu load
                                                30, 45, 34,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS, "minFile_step_1_node_1.txt", 847, "minNormalizedFile_step_1_node_1.txt", TaskData.HOURS*3+TaskData.MINUTES*17, "maxFile_step_1_node_1.txt", 1050, "maxNormalizedFile_step_1_node_1.txt", TaskData.HOURS*2,          // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node.getId(), step2.getId()), StepStatusEnum.FINISHED, 
                                                new Long(980), new Long(980), new Date(Timestamp.valueOf("2019-09-02 19:24:05").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-03 20:48:05").getTime()),  null,
                                                TaskData.HOURS*12,  TaskData.MINUTES*40,    // running time
                                                68, 81, 75,                 // cpu load
                                                40, 49, 47,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS-TaskData.MINUTES*23, "minFile_step_2_node_1.txt", 286, "minNormalizedFile_step_2_node_1.txt", TaskData.HOURS*4+TaskData.MINUTES*33, "maxFile_step_2_node_1.txt", 1150, "maxNormalizedFile_step_2_node_1.txt", TaskData.HOURS*2,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node.getId(), step3.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-03 22:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-05 05:04:05").getTime()), null,
                                                TaskData.HOURS*20, TaskData.MINUTES*15,// running time
                                                78, 99, 85,                 // cpu load
                                                52, 65, 58,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS+TaskData.MINUTES*12, "minFile_step_3_node_1.txt", 224, "minNormalizedFile_step_3_node_1.txt", TaskData.HOURS*2+TaskData.MINUTES*17, "maxFile_step_3_node_1.txt", 1470, "maxNormalizedFile_step_3_node_1.txt", TaskData.HOURS*2-TaskData.MINUTES*5,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node.getId(), step4.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-05 05:04:05").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-07 22:40:47").getTime()), null,
                                                TaskData.HOURS*52,  TaskData.HOURS*2,     // running time
                                                87, 97, 92,                 // cpu load
                                                32, 48, 41,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS+TaskData.MINUTES+47, "minFile_step_4_node_1.txt", 358, "minNormalizedFile_step_4_node_1.txt", TaskData.HOURS*4+TaskData.MINUTES*39, "maxFile_step_4_node_1.txt", 1780, "maxNormalizedFile_step_4_node_1.txt", TaskData.HOURS*2+TaskData.MINUTES*46,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        
        /** node 2 **/
        /*stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node2.getId(), step.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-02 12:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-02 21:28:05").getTime()), null,
                                                TaskData.HOURS*6,  TaskData.HOURS*7,     // running time
                                                78, 94, 86,                 // cpu load
                                                47, 61, 58,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS+TaskData.MINUTES*23, "minFile_step_1_node_2.txt", 475, "minNormalizedFile_step_1_node_2.txt", TaskData.HOURS*3-TaskData.MINUTES*10, "maxFile_step_1_node_2.txt", 850, "maxNormalizedFile_step_1_node_2.txt", TaskData.HOURS*2-TaskData.MINUTES*7,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node2.getId(), step2.getId()), StepStatusEnum.FINISHED, 
                                                new Long(980), new Long(980), new Date(Timestamp.valueOf("2019-09-02 23:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-04 06:50:05").getTime()), null,
                                                TaskData.HOURS*22,  TaskData.MINUTES*17,     // running time
                                                69, 79, 75,                 // cpu load
                                                42, 73, 50,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS+TaskData.MINUTES*54, "minFile_step_2_node_2.txt", 574, "minNormalizedFile_step_2_node_2.txt", TaskData.HOURS*3+TaskData.MINUTES*43, "maxFile_step_2_node_2.txt", 633, "maxNormalizedFile_step_2_node_2.txt", TaskData.HOURS*2+TaskData.MINUTES*12,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node2.getId(), step3.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-04 08:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-05 22:24:05").getTime()), null,
                                                TaskData.HOURS*45, TaskData.MINUTES*4,    // running time
                                                31, 48, 35,                 // cpu load
                                                35, 88, 50,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS+TaskData.MINUTES*4, "minFile_step_3_node_2.txt", 354, "minNormalizedFile_step_3_node_2.txt", TaskData.HOURS*5+TaskData.MINUTES*15, "maxFile_step_3_node_2.txt", 764, "maxNormalizedFile_step_3_node_2.txt", TaskData.HOURS*2+TaskData.MINUTES*37,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node2.getId(), step4.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-05 22:24:05").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-07 04:28:45").getTime()), null,
                                                TaskData.HOURS*39,  TaskData.HOURS*7,     // running time
                                                37, 80, 65,                 // cpu load
                                                20, 85, 35,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS-TaskData.MINUTES*24, "minFile_step_4_node_2.txt", 684, "minNormalizedFile_4_node_2.txt", TaskData.HOURS*3+TaskData.MINUTES*52, "maxFile_4_node_2.txt", 968, "maxNormalizedFile_4_node_2.txt", TaskData.HOURS*3+TaskData.MINUTES*2,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        
        /** node 3 **/
        /*stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node3.getId(), step.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-02 12:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-02 16:47:05").getTime()), null,
                                                TaskData.HOURS*7,  TaskData.MINUTES*59,    // running time
                                                69, 99, 78,                 // cpu load
                                                23, 31, 27,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS-TaskData.MINUTES*11, "minFile_step_1_node_3.txt", 541, "minNormalizedFile_step_1_node_3.txt", TaskData.HOURS*3+TaskData.MINUTES*3, "maxFile_step_1_node_3.txt", 865, "maxNormalizedFile_step_1_node_3.txt", TaskData.HOURS*2+TaskData.MINUTES*34,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node3.getId(), step2.getId()), StepStatusEnum.FINISHED, 
                                                new Long(980), new Long(980), new Date(Timestamp.valueOf("2019-09-02 19:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-04 08:24:05").getTime()), null,
                                                TaskData.HOURS*25,  TaskData.HOURS*3,    // running time
                                                89, 100, 95,                 // cpu load
                                                40, 70, 68,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS-TaskData.MINUTES*31, "minFile_step_2_node_3.txt", 598, "minNormalizedFile_step_2_node_3.txt", TaskData.HOURS*3+TaskData.MINUTES*12, "maxFile_step_2_node_3.txt", 963, "maxNormalizedFile_step_2_node_3.txt", TaskData.HOURS*2-TaskData.MINUTES*4,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node3.getId(), step3.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-04 08:24:05").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-06 13:36:05").getTime()), null,
                                                TaskData.HOURS*22,  TaskData.MINUTES*41,    // running time
                                                15, 27, 22,                 // cpu load
                                                38, 45, 41,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS+TaskData.MINUTES*23, "minFile_step_3_node_3.txt", 687, "minNormalizedFile_step_3_node_3.txt", TaskData.HOURS*2+TaskData.MINUTES*28, "maxFile_step_3_node_3.txt", 1634, "maxNormalizedFile_step_3_node_3.txt", TaskData.HOURS+TaskData.MINUTES*49,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node3.getId(), step4.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-06 13:36:05").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-08 20:00:49").getTime()), null,
                                                TaskData.HOURS*47,  TaskData.MINUTES*47,    // running time
                                                49, 86, 69,                 // cpu load
                                                40, 69, 53,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS+TaskData.MINUTES*31, "minFile_step_4_node_3.txt", 333, "minNormalizedFile_step_4_node_3.txt", TaskData.HOURS*7+TaskData.MINUTES*21, "maxFile_step_4_node_3.txt", 1248, "maxNormalizedFile_step_4_node_3.txt", TaskData.HOURS*3+TaskData.MINUTES*14,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        
        /** node 4 **/
        /*stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node4.getId(), step.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-02 12:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-02 21:59:05").getTime()), null,
                                                TaskData.HOURS*5,  TaskData.MINUTES*44,    // running time
                                                59, 88, 75,                 // cpu load
                                                47, 73, 55,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS*2-TaskData.MINUTES*11, "minFile_step_1_node_4.txt", 698, "minNormalizedFile_step_1_node_4.txt", TaskData.HOURS*5+TaskData.MINUTES*37, "maxFile_step_1_node_4.txt", 1864, "maxNormalizedFile_step_1_node_4.txt", TaskData.HOURS*4+TaskData.MINUTES*34,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node4.getId(), step2.getId()), StepStatusEnum.FINISHED, 
                                                new Long(980), new Long(980), new Date(Timestamp.valueOf("2019-09-03 03:30:00").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-03 20:24:05").getTime()), null,
                                                TaskData.HOURS*15,  TaskData.MINUTES*33,    // running time
                                                68, 83, 72,                 // cpu load
                                                14, 23, 18,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS*3-TaskData.MINUTES*51, "minFile_step_2_node_4.txt", 487, "minNormalizedFile_step_2_node_4.txt", TaskData.HOURS*8+TaskData.MINUTES*3, "maxFile_step_2_node_4.txt", 1963, "maxNormalizedFile_step_2_node_4.txt", TaskData.HOURS*3+TaskData.MINUTES*19,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node4.getId(), step3.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-03 20:24:05").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-05 17:14:05").getTime()), null,
                                                TaskData.HOURS*25,  TaskData.MINUTES*25,    // running time
                                                83, 97, 93,                 // cpu load
                                                75, 80, 77,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS-TaskData.MINUTES*42, "minFile_step_3_node_4.txt", 358, "minNormalizedFile_step_3_node_4.txt", TaskData.HOURS*3+TaskData.MINUTES*56, "maxFile_step_3_node_4.txt", 1147, "maxNormalizedFile_step_3_node_4.txt", TaskData.HOURS*2+TaskData.MINUTES*16,           // file proc time
                                                214,                        // avg ram cpu counter
                                                false, false)
        );
        stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJob2.getId(), node4.getId(), step4.getId()), StepStatusEnum.FINISHED, 
                                                new Long(1000), new Long(1000), new Date(Timestamp.valueOf("2019-09-05 17:14:05").getTime()),
                                                new Date(Timestamp.valueOf("2019-09-08 01:02:03").getTime()), null,
                                                TaskData.HOURS*42,  TaskData.MINUTES*44,     // running time
                                                67, 89, 76,                 // cpu load
                                                40, 82, 75,                 // ram usage
                                                490,                        // ram cpu counter
                                                TaskData.HOURS-TaskData.MINUTES*37, "minFile_step_4_node_4.txt", 478, "minNormalizedFile_step_4_node_4.txt", TaskData.HOURS*4+TaskData.MINUTES*35, "maxFile_step_4_node_4.txt", 1204, "maxNormalizedFile_step_4_node_4.txt", TaskData.HOURS*3-TaskData.MINUTES*27,           // file proc time
                                                214,                        // avg ram cpu counter
                                                true, false)
        );
        
        
        Error error = new Error("Input_file_1", "This is content of log file 1", "This is content of stderr 1", "Timeout", "command 1", 1, stepRunData.get(0));
        Error error2 = new Error("Input_file_2", "This is content of lo file 2", "This is content of stderr 2", null, "command 2", 143, stepRunData.get(0));
        Error error3 = new Error("Input_file_3", "This is content of log file 3", null, null, "command 3", 2, stepRunData.get(0));
        Error error4 = new Error("Input_file_4", null, "This is content of stderr 4", null, "command 4", 1, stepRunData.get(0));
        
        Error error5 = new Error("Input_file_5", "This is content of log file 5", null, null, "command 5", 143, stepRunData.get(7));
        Error error6 = new Error("Input_file_6", null, "This is content of stderr 6", null, "command 6", 143, stepRunData.get(7));
        
         Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        int seconds = calendar.get(Calendar.SECOND)-(calendar.get(Calendar.SECOND)%5);
        System.out.println(seconds);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.MILLISECOND, 0);
        List<NodeUtilization> nodeUtilizationData = new ArrayList<>();

       
        for (int cnt = 0; cnt < 120; cnt++) {
            nodeUtilizationData.add(new NodeUtilization(RecordType.MINUTE, calendar.getTime(), new Random().nextInt(100), node));
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)-1);
        }
        
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) > 30 ? 30 : 0);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        
        for (int cnt = 0; cnt < 48; cnt++) {
            nodeUtilizationData.add(new NodeUtilization(RecordType.MINUTES_30, calendar.getTime(), new Random().nextInt(100), node));
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 30);
        }
    
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        calendar.set(Calendar.MINUTE, 0);
        
        for (int cnt = 0; cnt < 192; cnt++) {
            nodeUtilizationData.add(new NodeUtilization(RecordType.HOUR, calendar.getTime(), new Random().nextInt(100), node));
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        }
        
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) > 12 ? 12 : 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        
        for (int cnt = 0; cnt < 60; cnt++) {
            nodeUtilizationData.add(new NodeUtilization(RecordType.HOURS_12, calendar.getTime(), new Random().nextInt(100), node));
            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) - 12);
        }
        
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        
        for (int cnt = 0; cnt < 365; cnt++) {
            nodeUtilizationData.add(new NodeUtilization(RecordType.DAY, calendar.getTime(), new Random().nextInt(100), node));
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        }   
        
        
        transaction = null; 
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            

            stepRunData.forEach(session::save);
            
            session.save(error); session.save(error2); session.save(error3); session.save(error4);
            session.save(error5); session.save(error6);
            
            nodeUtilizationData.forEach(session::save);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }   finally {
            if (session != null)
                session.close();
        }   */
    }
}
