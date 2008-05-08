package org.ncbo.stanford.exception;

import java.lang.Exception;
import java.lang.Throwable;

/**
 * Thrown in case of database error.
 *
 * @author cyoun
 */
public class DatabaseException extends Exception {


    public DatabaseException(String msg) {
        super(msg);
    }


    public DatabaseException(String msg, Exception e) {
        super(msg, e);
    }
    
    
    public DatabaseException(Throwable t) {
        super(t);
    }
    
    public DatabaseException(String msg, Throwable t) {
        super(msg, t);
    }
    
}