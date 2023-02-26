package br.com.jacto.trevo.controller.auth;

import br.com.jacto.trevo.config.security.TokenService;
import br.com.jacto.trevo.controller.auth.dto.ManagerDto;
import br.com.jacto.trevo.controller.auth.dto.TokenDto;
import br.com.jacto.trevo.controller.auth.form.ManagerForm;
import br.com.jacto.trevo.model.manager.Manager;
import br.com.jacto.trevo.service.manager.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/auths")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private TokenService tokenService;


    @GetMapping
    public ResponseEntity<TokenDto> authManager(@RequestBody @Valid ManagerForm user) {
        Authentication verify = managerService.auth(user);
        Authentication authentication = manager.authenticate(verify);
        String token = tokenService.token((Manager) authentication.getPrincipal());
        return ResponseEntity.ok(new TokenDto(token));
    }

    @PostMapping
    public ResponseEntity<ManagerDto> createManager(@RequestBody @Valid ManagerForm user) {
        ManagerDto managerDto = managerService.createManager(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(managerDto);
    }
}
