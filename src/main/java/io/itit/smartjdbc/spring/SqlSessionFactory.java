package io.itit.smartjdbc.spring;

import java.sql.Connection;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.itit.smartjdbc.SqlInterceptor;
import io.itit.smartjdbc.connection.TransactionManager;
import io.itit.smartjdbc.util.DumpUtil;

/**
 * 
 * @author skydu
 *
 */
public class SqlSessionFactory implements TransactionManager,SqlInterceptor {
	//
	private static Logger logger = LoggerFactory.getLogger(SqlSessionFactory.class);
	//
	private DataSource dataSource;
	//
	private static ThreadLocal<SqlSession> sqlSessions=new ThreadLocal<>();
	//
	public SqlSessionFactory() {
		
	}
	//
	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	//
	/**
	 * 
	 * @return
	 */
	public SqlSession getSession() {
		if (TransactionSynchronizationManager.hasResource(this)) {
			return getCurrentSession();
		} else {
			return openSession();
		}
	}
	/**
	 * 
	 * @return
	 */
	private SqlSession openSession() {
		SqlSession session = new SqlSession(this);
		session.setSessionId(UUID.randomUUID().toString());
		Connection conn = DataSourceUtils.getConnection(dataSource);
		session.setConnection(conn);
		if(TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronization transactionSynchronization = new SmartJdbcTransactionSynchronizationAdapter(this);
			TransactionSynchronizationManager.registerSynchronization(transactionSynchronization);
			TransactionSynchronizationManager.bindResource(this, session);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("openSession mySession:{}",session.getSessionId());
		}
		sqlSessions.set(session);
		return session;
	}

	/**
	 * 
	 * @return
	 */
	private SqlSession getCurrentSession() {
		SqlSession mySession = (SqlSession) TransactionSynchronizationManager.getResource(this);
		return mySession;
	}

	@Override
	public void commit() {
	}
	
	@Override
	public void rollback() {
	}

	@Override
	public Connection getConnecton(String datasourceIndex) {
		SqlSession session = getSession();
		return session.getConnection();
	}
	
	@Override
	public void beforeExcute(String sql, Object... parameters) {
		if(logger.isDebugEnabled()) {
			logger.debug("beforeExcute sql:[{}] parameters:[{}] isSynchronizationActive:[{}]",
				sql,
				DumpUtil.dump(parameters),
				TransactionSynchronizationManager.isSynchronizationActive());
		}
	}
	
	@Override
	public void afterExcute(String sql, Object... parameters) {
		if(logger.isDebugEnabled()) {
			logger.debug("afterExcute sql:[{}] parameters:[{}] isSynchronizationActive:[{}]",
				sql,
				DumpUtil.dump(parameters),
				TransactionSynchronizationManager.isSynchronizationActive());
		}
		if(!TransactionSynchronizationManager.isSynchronizationActive()) {//no transaction
			closeConnection();
		}
	}
	
	public void closeConnection() {
		try {
			TransactionSynchronizationManager.unbindResourceIfPossible(this);
			SqlSession session=sqlSessions.get();
			session.getConnection().close();
			sqlSessions.set(null);
			if(logger.isDebugEnabled()) {
				logger.debug("closeConnection sessionId:[{}]",session.getSessionId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}