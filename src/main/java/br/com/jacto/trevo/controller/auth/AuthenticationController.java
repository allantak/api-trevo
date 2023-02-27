package br.com.jacto.trevo.controller.auth;

import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.auth.dto.ManagerDto;
import br.com.jacto.trevo.controller.auth.dto.TokenDto;
import br.com.jacto.trevo.controller.auth.form.ManagerForm;
import br.com.jacto.trevo.model.manager.Manager;
import br.com.jacto.trevo.service.manager.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@Tag(name = "Autenticar", description = "Gerenciamento de gerente")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    @Operation(summary = "Autenticar o gerente", description = "login da conta do gerente")
    public ResponseEntity<TokenDto> authManager(@RequestBody @Valid ManagerForm user) {
        Authentication verify = managerService.auth(user);
        Authentication authentication = manager.authenticate(verify);
        System.out.println(authentication.getPrincipal());
        TokenDto token = tokenService.token((Manager) authentication.getPrincipal());
        return ResponseEntity.ok(token);

    }

    @PostMapping("/register")
    @SecurityRequirement(name = "bearer-key")
    @Operation(summary = "Regitrar gerente")
    public ResponseEntity<ManagerDto> createManager(@RequestBody @Valid ManagerForm user) {
        ManagerDto managerDto = managerService.createManager(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(managerDto);
    }
}
