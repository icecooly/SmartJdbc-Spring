package io.itit.smartjdbc.spring;

import java.sql.Connection;

/**
 * 
 * @author skydu
 *
 */
public class SqlSession {
	//
	private SqlSessionFactory sessionFactory;
	/***/
	private String sessionId;
	/***/
	private Connection connection;
	//
	public SqlSession(SqlSessionFactory sessionFactory) {
		this.sessionFactory=sessionFactory;
	}
	//
	public void beginTransaction() {
	}

	public void commit() {
		sessionFactory.closeConnection();
	}

	public void rollback() {
		sessionFactory.closeConnection();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	/**
	 * @return the sessionFactory
	 */
	public SqlSessionFactory getSessionFactory() {
		return sessionFactory;
	}
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SqlSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}