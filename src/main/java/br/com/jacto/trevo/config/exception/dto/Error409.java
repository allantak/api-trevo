package br.com.jacto.trevo.config.exception.dto;

public class Error409 {
    public Error409(Throwable error){
        this.error = error.getMessage();
    }

    private final String error;

    public String getError() {
        return error;
    }
}
