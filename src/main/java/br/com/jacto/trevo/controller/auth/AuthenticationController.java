package br.com.jacto.trevo.controller.auth;

import br.com.jacto.trevo.controller.auth.dto.ManagerDto;
import br.com.jacto.trevo.controller.auth.form.ManagerForm;
import br.com.jacto.trevo.repository.ManagerRepository;
import br.com.jacto.trevo.service.manager.ManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auths")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private ManagerService managerService;


    @GetMapping
    public ResponseEntity authManager(@RequestBody @Valid ManagerForm user) {
        var token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        var authentication = manager.authenticate(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<ManagerDto> createManager(@RequestBody @Valid ManagerForm user) {
        ManagerDto managerDto = managerService.createManager(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(managerDto);
    }




}
