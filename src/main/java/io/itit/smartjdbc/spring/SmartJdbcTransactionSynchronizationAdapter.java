package io.itit.smartjdbc.spring;

import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 
 * @author skydu
 *
 */
public class SmartJdbcTransactionSynchronizationAdapter extends  
        TransactionSynchronizationAdapter {  
	//
    private SqlSessionFactory sessionFactory;  
    //
    public SmartJdbcTransactionSynchronizationAdapter(SqlSessionFactory mySessionFactory) {  
        this.sessionFactory = mySessionFactory;  
    }  
  
  
    @Override  
    public void beforeCommit(boolean readOnly) {  
        if(!readOnly){  
            SqlSession mySession = (SqlSession) TransactionSynchronizationManager.getResource(sessionFactory);  
            mySession.beginTransaction();  
        }  
    }  
  
    @Override  
    public void afterCompletion(int status) {  
        SqlSession mySession = (SqlSession) TransactionSynchronizationManager.getResource(sessionFactory);  
        if (STATUS_COMMITTED == status) {  
            mySession.commit();  
        }else if(STATUS_ROLLED_BACK==status) {  
        		mySession.rollback();
        }
    }   
}  