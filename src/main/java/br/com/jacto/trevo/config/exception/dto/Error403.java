package br.com.jacto.trevo.config.exception.dto;

import java.nio.file.AccessDeniedException;

public class Error403 {

    public Error403(AccessDeniedException exception) {
        this.mensagem = exception.getMessage();
    }

    private final String mensagem;

    public String getMensagem() {
        return mensagem;
    }
}
