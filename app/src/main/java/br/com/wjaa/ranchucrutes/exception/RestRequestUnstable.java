package br.com.wjaa.ranchucrutes.exception;

import java.io.IOException;

/**
 * Created by wagner on 25/07/15.
 */
public class RestRequestUnstable extends Exception {
    public RestRequestUnstable(String message, Exception e) {
        super(message,e);
    }

    public RestRequestUnstable(String message) {
        super(message);
    }
}
