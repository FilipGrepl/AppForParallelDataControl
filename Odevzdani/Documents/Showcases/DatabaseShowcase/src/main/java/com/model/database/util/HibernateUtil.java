/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.database.util;

/**
 *
 * @author Filip
 */

import com.model.database.entity.Job;
import com.model.database.entity.JobNodeStepKey;
import com.model.database.entity.JobRunData;
import com.model.database.entity.Node;
import com.model.database.entity.Step;
import com.model.database.entity.User;
import com.model.database.entity.Error;
import com.model.database.entity.NodeUtilization;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry
                registry = new StandardServiceRegistryBuilder().configure().build();

                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry);
                sources.addAnnotatedClass(Error.class);
                sources.addAnnotatedClass(Job.class);
                sources.addAnnotatedClass(JobNodeStepKey.class);
                sources.addAnnotatedClass(JobRunData.class);
                sources.addAnnotatedClass(Node.class);
                sources.addAnnotatedClass(NodeUtilization.class);
                sources.addAnnotatedClass(Step.class);
                sources.addAnnotatedClass(User.class);

                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();

                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}