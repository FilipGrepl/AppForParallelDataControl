/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.util;

/**
 *
 * @author Filip
 */

import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.RunJobNodeStepKey;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.model.database.entity.User;
import com.app.primaryNodeApp.model.database.entity.Error;
import com.app.primaryNodeApp.model.database.entity.NodeUtilization;
import com.app.primaryNodeApp.model.database.entity.RunJob;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Basic class of Hibernate framework.
 * @author filip
 */
public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;
    
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            // Create registry
            registry = new StandardServiceRegistryBuilder().configure().build();

            // Create MetadataSources
            MetadataSources sources = new MetadataSources(registry);
            sources.addAnnotatedClass(Error.class);
            sources.addAnnotatedClass(Job.class);
            sources.addAnnotatedClass(RunJob.class);
            sources.addAnnotatedClass(RunJobNodeStepKey.class);
            sources.addAnnotatedClass(StepRunData.class);
            sources.addAnnotatedClass(Node.class);
            sources.addAnnotatedClass(NodeUtilization.class);
            sources.addAnnotatedClass(Step.class);
            sources.addAnnotatedClass(User.class);

            // Create Metadata
            Metadata metadata = sources.getMetadataBuilder().build();

            // Create SessionFactory
            sessionFactory = metadata.getSessionFactoryBuilder().build();
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
    
}