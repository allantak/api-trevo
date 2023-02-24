package br.com.jacto.trevo.controller.auth.dto;

import br.com.jacto.trevo.model.manager.Manager;

import java.util.UUID;

public class ManagerDto {
    public ManagerDto(Manager manager){
        this.managerId = manager.getManagerId();
    }

    private UUID managerId;

    public UUID getManagerId() {
        return managerId;
    }
}
