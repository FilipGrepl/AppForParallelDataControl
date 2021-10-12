/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.dao;

import com.model.database.util.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.lang.reflect.Method;
import java.util.HashSet;

/**
 *
 * @author Filip
 * @param <T>
 * @param <Id>
 * @param <C>
 */
public class Dao<T, Id extends Serializable> {
    protected Transaction transaction = null;
    protected Session session = null;
    protected final Class<T> classType;
    protected final Set<String> collections;
    protected final String OneToManyRetType = "Set";
    
    public Dao(Class<T> classType) {
        this.classType = classType;
        this.collections = new HashSet<String>();
        for (Method method : classType.getDeclaredMethods()) {
            if (method.getReturnType().toString().contains(this.OneToManyRetType)) {
              this.collections.add(method.getName());
            }
        }
    }
        
    public T getById(Id id) {
        T entity = null;
        try {
            this.session = HibernateUtil.getSessionFactory().openSession();
            this.transaction = this.session.beginTransaction();
            entity = session.get(this.classType, id);
            this.transaction.commit();        
        } catch (Exception e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            this.session.close();
        }
        return entity;
    }
    
    public T getByIdWithCollections(Id id)  {
        T entity = null;
        try {
            this.session = HibernateUtil.getSessionFactory().openSession();
            this.transaction = this.session.beginTransaction();
            final T loadEntity = session.get(this.classType, id);
            this.collections.forEach(c -> {
                try {
                    Method method = loadEntity.getClass().getMethod(c);
                    Hibernate.initialize(method.invoke(loadEntity));
                } catch (Exception ex) {
                    Logger.getLogger(Dao.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            this.transaction.commit();
            entity = loadEntity;
        } catch (Exception e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            this.session.close();
        }
        return entity;
    }
    
    public List<T> getAll() {
        List<T> entities = null;
        try {
            this.session = HibernateUtil.getSessionFactory().openSession();
            this.transaction = this.session.beginTransaction();
            
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteria = builder.createQuery(this.classType);
            criteria.from(this.classType);
            entities = session.createQuery(criteria).getResultList();
            this.transaction.commit();
        } catch (Exception e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            this.session.close();
        }
        return entities;
    }
    
    public void save(T entity) {
        try {
            this.session = HibernateUtil.getSessionFactory().openSession();
            this.transaction = this.session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (Exception e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            this.session.close();
        }
    }
    
    public void update(T entity) {
        try {
            this.session = HibernateUtil.getSessionFactory().openSession();
            this.transaction = this.session.beginTransaction();
            
            session.update(entity);
            if (entity != null) {
                session.delete(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            this.session.close();
        }
    }
    
    public void delete(Id id) {
        try {
            this.session = HibernateUtil.getSessionFactory().openSession();
            this.transaction = this.session.beginTransaction();
            
            T entity = this.session.get(this.classType, id);
            if (entity != null) {
                session.delete(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            System.out.println(e.toString());
            e.printStackTrace();
        } finally {
            this.session.close();
        }
    }     
}
