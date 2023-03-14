package br.com.jacto.trevo.controller.auth.dto;

import br.com.jacto.trevo.model.manager.Manager;

import java.util.UUID;

public class ManagerDto {
    public ManagerDto(Manager manager) {
        this.managerId = manager.getManagerId();
        this.username = manager.getUsername();
    }

    private final UUID managerId;

    private final String username;

    public UUID getManagerId() {
        return managerId;
    }

    public String getUsername() {
        return username;
    }
}
