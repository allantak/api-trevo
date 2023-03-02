package br.com.jacto.trevo.controller.auth.dto;

import br.com.jacto.trevo.model.manager.Manager;

import java.util.UUID;

public class ManagerCreateDto {

    public ManagerCreateDto(Manager manager) {
        this.managerId = manager.getManagerId();
        this.username = manager.getUsername();
    }

    private UUID managerId;
    private String username;

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
