package br.com.jacto.trevo.controller.auth.dto;

import br.com.jacto.trevo.model.account.Account;
import br.com.jacto.trevo.model.order.OrderItem;

import java.util.List;

public class AccountOrderDto {

    public AccountOrderDto(Account account){
        this.email = account.getEmail();
        this.name = account.getAccountName();
        this.orders = account.getOrders().stream().map(AccountListOrderDto::new).toList();
    }
    private final String email;

    private final String name;

    private final List<AccountListOrderDto> orders;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public List<AccountListOrderDto> getOrders() {
        return orders;
    }
}
