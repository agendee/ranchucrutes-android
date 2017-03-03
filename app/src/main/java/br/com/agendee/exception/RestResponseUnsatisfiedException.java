package br.com.agendee.exception;

/**
 * Created by wagner on 25/07/15.
 */
public class RestResponseUnsatisfiedException extends Exception {
    public RestResponseUnsatisfiedException(String message, Exception e) {
        super(message,e);
    }
}
