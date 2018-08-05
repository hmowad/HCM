package com.code.dal;

import org.hibernate.Session;

public class CustomSession {
    private Session session;
    
    // Package visibility to be used only by Data Access Class.
    CustomSession(Session session){
        this.session = session;
    }

    // Package visibility to be used only by this package.
    Session getSession() {
        return this.session;
    }
    
    //-----------------------------------------------------------------------------
    
    public void beginTransaction(){
        session.beginTransaction();
    }
    
    public void flushTransaction(){
        session.flush();
    }
    
    
    public void commitTransaction(){
        session.getTransaction().commit();
    }
    
    public void rollbackTransaction(){		
        session.getTransaction().rollback();
    }
    
    public void close(){		
        session.close();		
    }
}