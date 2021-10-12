/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.dao;

import com.app.primaryNodeApp.model.database.entity.EntityInterface;
import com.app.primaryNodeApp.model.database.util.HibernateUtil;
import com.app.primaryNodeApp.primLoggers.DaoLogger;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.criteria.Root;
import org.hibernate.HibernateException;

/**
 * The General Dao object for basic CRUD operations with all objects.
 * @author filip
 * @param <T> Type of entity object.
 * @param <Id> Type of Id of entity T.
 */
public class Dao<T extends EntityInterface<Id>, Id extends Serializable> {
    
    /** STATIC PROPERTIES **/

    protected static final String ONE_TO_MANY_RET_TYPE_REGEX = ".*List.*";
    
    private static final int SIZE_OF_SESSION_CONNECTION_POOL = 100;
    
    //private static final Semaphore SESSION_SEMAPHORE = new Semaphore(SIZE_OF_SESSION_CONNECTION_POOL);;
        
    /** OBJECT PROPERTIES **/

    protected final DaoLogger daoLogger;
    protected final Class<T> classType;
    protected final List<String> collections;

    protected Transaction transaction = null;
    protected Session session = null;

    protected CriteriaBuilder queryBuilder;
    protected CriteriaQuery<T> queryCriteria;
    protected Root<T> queryRoot;
    
    
    
    /** OBJECT METHODS **/

    /**
     * Constructor.
     * @param classType ClassType of T for logger.
     */
    public Dao(Class<T> classType) {
        this.classType = classType;
        this.daoLogger = new DaoLogger(classType);
        this.collections = new ArrayList<>();
        for (Method method : classType.getDeclaredMethods()) {
            if (method.getReturnType().toString().matches(ONE_TO_MANY_RET_TYPE_REGEX)) {
                this.collections.add(method.getName());
            }
        }
    }
    
    /**
     * Method, that opens the session to the database. This method is used for preventing maximum size of Hibernate connection pool.
     */
    protected void openSession() {
        //SESSION_SEMAPHORE.acquireUninterruptibly();
        this.session = HibernateUtil.getSessionFactory().openSession();
    }
    
    /**
     * Method, that closes the session to the database. This method is used for preventing maximum size of Hibernate connection pool.
     */
    protected void closeSession() {
       if (this.session != null && this.session.isOpen()) {
            this.session.close();
        }
        //SESSION_SEMAPHORE.release();
    }

    /**
     * Method, that opens session to the database and prepare all for executing the query.
     */
    protected void prepareCriteria() {
        this.openSession();
        this.queryBuilder = session.getCriteriaBuilder();
        this.queryCriteria = queryBuilder.createQuery(this.classType);
        this.queryRoot = this.queryCriteria.from(this.classType);
    }

    /**
     * Method that executes created query.
     * @return List of query results. If no entity is found, null is returned.
     */
    protected List<T> executeQuery() {
        List<T> entities = null;
        try {
            this.transaction = this.session.beginTransaction();
            entities = session.createQuery(this.queryCriteria).getResultList();
            this.transaction.commit();
            return entities;
        } catch (Exception e) {
            if (entities == null) {
                return null;
            }

            if (this.transaction != null) {
                this.transaction.rollback();
            }
            daoLogger.logInternalDatabaseErr(e);
            throw e;
        } finally {
            this.closeSession();
        }
    }

    /**
     * Method, that returns entity of type T.
     * @param id Id of entity to be returned.
     * @return Entity of type T with id Id.
     */
    public T getById(Id id) {
        T entity = null;
        try {
            this.openSession();
            this.transaction = this.session.beginTransaction();
            entity = session.get(this.classType, id);
            this.transaction.commit();
        } catch (HibernateException e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            daoLogger.logInternalDatabaseErr(e);
            throw e;
        } finally {
            this.closeSession();
        }
        return entity;
    }

    /**
     * Method, that returns entity of type T with all collections.
     * @param id Id of entity to be returned.
     * @return Entity of type T with id Id.
     */
    public T getByIdWithCollections(Id id) {
        try {
            this.openSession();
            this.transaction = this.session.beginTransaction();
            final T loadEntity = session.get(this.classType, id);
            for (String collection : collections) {
                Method method = loadEntity.getClass().getMethod(collection);
                Hibernate.initialize(method.invoke(loadEntity));
            }
            this.transaction.commit();
            return loadEntity;
        } catch (IllegalAccessException
                | IllegalArgumentException
                | NoSuchMethodException
                | SecurityException
                | InvocationTargetException
                | HibernateException e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            daoLogger.logInternalDatabaseErr(e);
            throw new HibernateException(e.getMessage());
        } finally {
            this.closeSession();
        }
    }

    /**
     * Method, that returns all objects of entity T.
     * @return List of all objects of entity T.
     */
    public List<T> getAll() {
        this.prepareCriteria();
        this.queryCriteria.select(this.queryRoot);
        return this.executeQuery();
    }

    /**
     * Method, that returns all objects of entity T with collections.
     * @return List of all objects of entity T with collections. If no entity is found, null is returned.
     */
    public List<T> getAllWithCollections() {
        List<Id> allIds = new ArrayList<>();
        List<T> allWithCollections = new ArrayList<>();
        List<T> loadEntities;
        List<T> all = this.getAll();
        
        if (all == null)
            return null;
        
        all.forEach(item -> allIds.add(item.getId()));
             
        try {
            this.openSession();
            this.transaction = this.session.beginTransaction();
            
            loadEntities = session.byMultipleIds(this.classType).multiLoad(allIds);
            for (T entity : loadEntities) {
                for (String collection : collections) {
                    Method method = entity.getClass().getMethod(collection);
                    Hibernate.initialize(method.invoke(entity));
                }
                allWithCollections.add(entity);
            }
            this.transaction.commit();
            return allWithCollections;
        } catch (IllegalAccessException
                | IllegalArgumentException
                | NoSuchMethodException
                | SecurityException
                | InvocationTargetException
                | HibernateException e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            daoLogger.logInternalDatabaseErr(e);
            throw new HibernateException(e.getMessage());
        } finally {
            this.closeSession();
        }
    }
    
    /**
     * Method, that loads all collections to all entity T in input list.
     * @param entities List of objects of entity T to which the collections to be loaded.
     * @return List of entered list of entity T with loaded all collections.
     */
    public List<T> getWithCollections(List<T> entities) {
        List<T> runJobsWithCollections = new ArrayList<>();
        entities.forEach(runJob -> {
            runJobsWithCollections.add(this.getByIdWithCollections(runJob.getId()));
        });
        return runJobsWithCollections;
    }
    
/*
    public List<T> getAllWithCollections(List<Id> Ids) {
        try {
            this.openSession();
            this.transaction = this.session.beginTransaction();
            List<T> allWithCollections = new ArrayList<>();
            for (Id id : Ids) {
                final T loadEntity = session.get(this.classType, id);
                for (String collection : collections) {
                    Method method = loadEntity.getClass().getMethod(collection);
                    Hibernate.initialize(method.invoke(loadEntity));
                }
                allWithCollections.add(loadEntity);
            }
            this.transaction.commit();
            return allWithCollections;
        } catch (IllegalAccessException
                | IllegalArgumentException
                | NoSuchMethodException
                | SecurityException
                | InvocationTargetException
                | HibernateException e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            throw new HibernateException(EventLogger.getErrorStackTrace(e));
        } finally {
            this.closeSession();
        }
    }
*/
    
    /**
     * Method, that saves object of T entity.
     * @param entity Object to be saved.
     */
    public void save(T entity) {
        try {
            this.openSession();
            this.transaction = this.session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (HibernateException e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            daoLogger.logInternalDatabaseErr(e);
            throw e;
        } finally {
            this.closeSession();
        }
    }
    
    /**
     * Method, that saves list of objects of T entities.
     * @param entities List of objects to be saved.
     */
    public void save(List<T> entities) {
         try {
            this.openSession();
            this.transaction = this.session.beginTransaction();
            entities.forEach(session::save);
            transaction.commit();
        } catch (HibernateException e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            daoLogger.logInternalDatabaseErr(e);
            throw e;
        } finally {
            this.closeSession();
        }
    }

    /**
     * Method, that updates object of T entity. 
     * @param entity Object to be saved.
     */
    public void update(T entity) {
        try {
            this.openSession();
            this.transaction = this.session.beginTransaction();
            session.update(entity);
            transaction.commit();
        } catch (HibernateException e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            daoLogger.logInternalDatabaseErr(e);
            throw e;
        } finally {
            this.closeSession();
        }
    }

    /**
     * Method, that deletes object of type T with id Id.
     * @param id Id of object to be deleted.
     */
    public void delete(Id id) {
        try {
            this.openSession();
            this.transaction = this.session.beginTransaction();

            T entity = this.session.get(this.classType, id);
            if (entity != null) {
                session.delete(entity);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            daoLogger.logInternalDatabaseErr(e);
            throw e;
        } finally {
            this.closeSession();
        }
    }
}
