package com.diariasja.aws.exception;

// Estendemos RuntimeException para não sermos obrigados a usar try/catch em todo lugar
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }
}