package br.com.jacto.trevo.config.dto;

import org.springframework.validation.FieldError;

public class Error400Dto {

    public Error400Dto(FieldError erro){
        this.campo = erro.getField();
        this.mensagem = erro.getDefaultMessage();
    }

    private final String campo;

    private final String mensagem;

    public String getCampo() {
        return campo;
    }

    public String getMensagem() {
        return mensagem;
    }
}
