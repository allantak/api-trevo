package br.com.jacto.trevo.controller.product.dto;

import br.com.jacto.trevo.model.product.Culture;

import java.util.UUID;

public class ProductCultureDto {

    public ProductCultureDto(Culture culture) {
        this.cultureId = culture.getCultureId();
        this.cultureName = culture.getCultureName();
    }

    private UUID cultureId;

    private String cultureName;

    public String getCultureName() {
        return cultureName;
    }

    public void setCultureName(String cultureName) {
        this.cultureName = cultureName;
    }

    public UUID getCultureId() {
        return cultureId;
    }
}
