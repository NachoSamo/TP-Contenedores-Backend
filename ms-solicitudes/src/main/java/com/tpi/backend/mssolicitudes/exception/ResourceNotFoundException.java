package com.tpi.backend.mssolicitudes.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Integer id) {
        super(String.format("%s con id %d no encontrado", resource, id));
    }
}