package br.com.jacto.trevo.controller.auth.form;

import br.com.jacto.trevo.model.account.Account;

import javax.validation.constraints.NotNull;

public class AccountRegisterManagerForm extends AccountRegisterForm {
    @NotNull(message = "Obrigatório a permissão")
    private Account.Role accountRole;

    public Account.Role getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(Account.Role accountRole) {
        this.accountRole = accountRole;
    }
}
