package com.bol.stonepitgame.bolgameapi.exceptions;

/**
 * Class to handle exception messages in the service
 */
public class BolException extends RuntimeException {
    public BolException(String message) {
        super(message);
    }
}
