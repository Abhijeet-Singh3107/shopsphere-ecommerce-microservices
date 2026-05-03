package com.shopsphere.admin.exception;

public class ServiceCommunicationException extends RuntimeException {
    public ServiceCommunicationException(String service, String message) {
        super("Error communicating with " + service + ": " + message);
    }
}
