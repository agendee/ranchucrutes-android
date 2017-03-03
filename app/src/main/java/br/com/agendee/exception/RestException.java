package br.com.agendee.exception;

import br.com.agendee.vo.ErrorMessageVo;

/**
 * Created by wagner on 25/07/15.
 */
public class RestException extends Exception {

    private ErrorMessageVo errorMessage;


    public RestException(ErrorMessageVo errorMessageVo) {
        this.errorMessage = errorMessageVo;
    }

    public ErrorMessageVo getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessageVo errorMessage) {
        this.errorMessage = errorMessage;
    }
}
