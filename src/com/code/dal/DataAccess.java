package com.code.dal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.dal.orm.BaseEntity;
import com.code.dal.orm.audit.AuditLog;
import com.code.enums.AuditOperationsEnum;
import com.code.exceptions.DatabaseException;

public class DataAccess {
    protected static SessionFactory sessionFactory;
    private static int batchSize;

    protected DataAccess() {
    }

    public static void init() {
	try {
	    Configuration configuration = new Configuration().configure("com/code/dal/hibernate.cfg.xml");
	    StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
	    sessionFactory = configuration.buildSessionFactory(standardRegistry);
	    batchSize = Integer.parseInt(configuration.getProperty("hibernate.jdbc.batch_size"));
	} catch (HibernateException exception) {
	    System.out.println("Problem creating session Factory!");
	    exception.printStackTrace();
	}

    }

    public static CustomSession getSession() {
	return new CustomSession(sessionFactory.openSession());
    }

    public static String getTableName(Class<?> className) {
	Class<?> c = className;
	Table table = c.getAnnotation(Table.class);
	return table.name();
    }

    public static void addEntity(BaseEntity bean, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = false;
	if (useSession != null && useSession.length > 0)
	    isOpenedSession = true;

	Session session = isOpenedSession ? useSession[0].getSession() : sessionFactory.openSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    session.save(bean);
	    audit(bean, AuditOperationsEnum.INSERT, session);

	    if (!isOpenedSession)
		session.getTransaction().commit();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.getTransaction().rollback();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static <T> void addMultipleEntities(List<T> beans, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = false;
	if (useSession != null && useSession.length > 0)
	    isOpenedSession = true;

	Session session = isOpenedSession ? useSession[0].getSession() : sessionFactory.openSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (int i = 0; i < beans.size(); i++) {
		session.save(beans.get(i));
		if (i % batchSize == 0 && i != 0) {
		    session.flush();
		    session.clear();
		}
	    }

	    auditMultipleEntities(beans, AuditOperationsEnum.INSERT, session);
	    if (!isOpenedSession)
		session.getTransaction().commit();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.getTransaction().rollback();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateEntity(BaseEntity bean, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = false;
	if (useSession != null && useSession.length > 0)
	    isOpenedSession = true;

	Session session = isOpenedSession ? useSession[0].getSession() : sessionFactory.openSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    session.saveOrUpdate(bean);
	    audit(bean, AuditOperationsEnum.UPDATE, session);

	    if (!isOpenedSession)
		session.getTransaction().commit();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.getTransaction().rollback();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static <T> void updateMultipleEntities(List<T> beans, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = false;
	if (useSession != null && useSession.length > 0)
	    isOpenedSession = true;

	Session session = isOpenedSession ? useSession[0].getSession() : sessionFactory.openSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (int i = 0; i < beans.size(); i++) {
		session.saveOrUpdate(beans.get(i));
		if (i % batchSize == 0 && i != 0) {
		    session.flush();
		    session.clear();
		}
	    }

	    DataAccess.auditMultipleEntities(beans, AuditOperationsEnum.UPDATE, session);
	    if (!isOpenedSession)
		session.getTransaction().commit();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.getTransaction().rollback();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void deleteEntity(BaseEntity bean, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = false;
	if (useSession != null && useSession.length > 0)
	    isOpenedSession = true;

	Session session = isOpenedSession ? useSession[0].getSession() : sessionFactory.openSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    session.delete(bean);
	    audit(bean, AuditOperationsEnum.DELETE, session);

	    if (!isOpenedSession)
		session.getTransaction().commit();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.getTransaction().rollback();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static long getNextValFromSequence(String sequenceName) throws DatabaseException {
	StringBuffer nativeQueryBuffer = new StringBuffer("SELECT " + sequenceName + ".NEXTVAL FROM DUAL");
	List<BigDecimal> result = executeNativeQuery(BigDecimal.class, nativeQueryBuffer, null);
	if (result.isEmpty())
	    throw new DatabaseException("error_general");

	return result.get(0).longValue();
    }

    public static <T> List<T> executeNamedQuery(Class<T> dataClass, String queryName, Map<String, Object> parameters) throws DatabaseException {
	return executeQuery(dataClass, queryName, null, null, parameters);
    }

    protected static <T> List<T> executeDynamicQuery(Class<T> dataClass, StringBuffer dynamicQueryBuffer, Map<String, Object> parameters) throws DatabaseException {
	return executeQuery(dataClass, null, dynamicQueryBuffer, null, parameters);
    }

    /**
     * useSession parameter added for a case when selecting a column from a transaction that is not committed yet , Use it with caution
     **/
    public static <T> List<T> executeNativeQuery(Class<T> dataClass, StringBuffer nativeQueryBuffer, Map<String, Object> parameters, CustomSession... useSession) throws DatabaseException {
	return executeQuery(dataClass, null, null, nativeQueryBuffer, parameters, useSession);
    }

    public static <T> List<T> executeNamedQueryWithPagination(String queryName, Integer offset, Integer pageSize, Map<String, Object> parameters) throws DatabaseException {
	return executeQueryWithPagination(queryName, null, null, offset, pageSize, parameters);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> executeQuery(Class<T> dataClass, String queryName, StringBuffer dynamicQueryBuffer, StringBuffer nativeQueryBuffer, Map<String, Object> parameters, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = false;
	if (useSession != null && useSession.length > 0)
	    isOpenedSession = true;

	Session session = isOpenedSession ? useSession[0].getSession() : sessionFactory.openSession();

	try {
	    Query q = null;
	    if (queryName != null)
		q = session.getNamedQuery(queryName);
	    else if (dynamicQueryBuffer != null)
		q = session.createQuery(dynamicQueryBuffer.toString());
	    else if (nativeQueryBuffer != null)
		q = session.createSQLQuery(nativeQueryBuffer.toString());

	    if (parameters != null) {
		for (String paramName : parameters.keySet()) {
		    Object value = parameters.get(paramName);

		    if (value instanceof Integer)
			q.setInteger(paramName, (Integer) value);
		    else if (value instanceof String)
			q.setString(paramName, (String) value);
		    else if (value instanceof Long)
			q.setLong(paramName, (Long) value);
		    else if (value instanceof Float)
			q.setFloat(paramName, (Float) value);
		    else if (value instanceof Double)
			q.setDouble(paramName, (Double) value);
		    else if (value instanceof Date)
			q.setDate(paramName, (Date) value);
		    else if (value instanceof Object[])
			q.setParameterList(paramName, (Object[]) value);
		}
	    }

	    List<T> result = q.list();

	    if (result == null || result.size() == 0)
		result = new ArrayList<T>();

	    return result;
	} catch (Exception e) {
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> executeQueryWithPagination(String queryName, StringBuffer dynamicQueryBuffer, StringBuffer nativeQueryBuffer, Integer offset, Integer pageSize, Map<String, Object> parameters, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = false;
	if (useSession != null && useSession.length > 0)
	    isOpenedSession = true;

	Session session = isOpenedSession ? useSession[0].getSession() : sessionFactory.openSession();

	try {

	    Query q = null;
	    if (queryName != null)
		q = session.getNamedQuery(queryName);
	    else if (dynamicQueryBuffer != null)
		q = session.createQuery(dynamicQueryBuffer.toString());
	    else if (nativeQueryBuffer != null)
		q = session.createSQLQuery(nativeQueryBuffer.toString());

	    q.setMaxResults(pageSize).setFirstResult(offset);

	    if (parameters != null) {
		for (String paramName : parameters.keySet()) {
		    Object value = parameters.get(paramName);

		    if (value instanceof Integer)
			q.setInteger(paramName, (Integer) value);
		    else if (value instanceof String)
			q.setString(paramName, (String) value);
		    else if (value instanceof Long)
			q.setLong(paramName, (Long) value);
		    else if (value instanceof Float)
			q.setFloat(paramName, (Float) value);
		    else if (value instanceof Double)
			q.setDouble(paramName, (Double) value);
		    else if (value instanceof Date)
			q.setDate(paramName, (Date) value);
		    else if (value instanceof Object[])
			q.setParameterList(paramName, (Object[]) value);
		}
	    }

	    List<T> result = q.list();

	    if (result == null || result.size() == 0)
		result = new ArrayList<T>();

	    return result;
	} catch (Exception e) {
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void executeUpdateNamedQuery(String queryName, Map<String, Object> parameters, List<BaseEntity> auditBeans, CustomSession... useSession) throws DatabaseException {
	executeTransactionalNamedQuery(queryName, parameters, auditBeans, useSession);
    }

    public static void executeDeleteNamedQuery(String queryName, Map<String, Object> parameters, List<BaseEntity> auditBeans, CustomSession... useSession) throws DatabaseException {
	executeTransactionalNamedQuery(queryName, parameters, auditBeans, useSession);
    }

    private static void executeTransactionalNamedQuery(String queryName, Map<String, Object> parameters, List<BaseEntity> auditBeans, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = false;
	if (useSession != null && useSession.length > 0)
	    isOpenedSession = true;

	Session session = isOpenedSession ? useSession[0].getSession() : sessionFactory.openSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    Query q = session.getNamedQuery(queryName);
	    if (parameters != null) {
		for (String paramName : parameters.keySet()) {
		    Object value = parameters.get(paramName);

		    if (value instanceof Integer)
			q.setInteger(paramName, (Integer) value);
		    else if (value instanceof String)
			q.setString(paramName, (String) value);
		    else if (value instanceof Long)
			q.setLong(paramName, (Long) value);
		    else if (value instanceof Float)
			q.setFloat(paramName, (Float) value);
		    else if (value instanceof Double)
			q.setDouble(paramName, (Double) value);
		    else if (value instanceof Date)
			q.setDate(paramName, (Date) value);
		    else if (value instanceof Object[])
			q.setParameterList(paramName, (Object[]) value);
		}
	    }
	    q.executeUpdate();
	    auditMultipleEntities(auditBeans, AuditOperationsEnum.UPDATE, session);
	    if (!isOpenedSession)
		session.getTransaction().commit();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.getTransaction().rollback();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void audit(BaseEntity bean, AuditOperationsEnum operation, Session session) {
	if ((AuditOperationsEnum.INSERT.equals(operation) && bean instanceof InsertableAuditEntity) ||
		(AuditOperationsEnum.UPDATE.equals(operation) && bean instanceof UpdatableAuditEntity) ||
		(AuditOperationsEnum.DELETE.equals(operation) && bean instanceof DeletableAuditEntity)) {

	    // It is intended not to check for the cast exception here to announce the wrong usage.
	    AuditEntity auditableBean = (AuditEntity) bean;

	    // If the system user has a value, this means that this transaction will audit this entity.
	    if (auditableBean.getSystemUser() != null) {
		AuditLog log = new AuditLog();
		log.setSystemUser(Long.parseLong(auditableBean.getSystemUser()));
		log.setOperation(operation.toString());
		log.setOperationDate(new Date());
		log.setContentEntity(auditableBean.getClass().getCanonicalName());
		log.setContentId(auditableBean.calculateContentId());
		log.setContent(auditableBean.calculateContent());
		session.save(log);
	    }
	}
    }

    private static <T> void auditMultipleEntities(List<T> beans, AuditOperationsEnum operation, Session session) throws DatabaseException {
	int i = 0;
	if (beans != null && !beans.isEmpty()) {
	    if ((AuditOperationsEnum.INSERT.equals(operation) && beans.get(0) instanceof InsertableAuditEntity) ||
		    (AuditOperationsEnum.UPDATE.equals(operation) && beans.get(0) instanceof UpdatableAuditEntity) ||
		    (AuditOperationsEnum.DELETE.equals(operation) && beans.get(0) instanceof DeletableAuditEntity)) {
		for (T bean : beans) {
		    // It is intended not to check for the cast exception here to announce the wrong usage.
		    AuditEntity auditableBean = (AuditEntity) bean;

		    // If the system user has a value, this means that this transaction will audit this entity.
		    if (auditableBean.getSystemUser() != null) {
			AuditLog log = new AuditLog();
			log.setSystemUser(Long.parseLong(auditableBean.getSystemUser()));
			log.setOperation(operation.toString());
			log.setOperationDate(new Date());
			log.setContentEntity(auditableBean.getClass().getCanonicalName());
			log.setContentId(auditableBean.calculateContentId());
			log.setContent(auditableBean.calculateContent());
			session.save(log);
			i++;
		    }
		    if (i % batchSize == 0 && i != 0) {
			session.flush();
			session.clear();
		    }
		}
	    }
	}
    }

    public static void destroy() {
	sessionFactory.close();
    }
}