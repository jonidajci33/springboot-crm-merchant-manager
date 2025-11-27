package merchant_manager.controller;

import merchant_manager.auth.RegisterRequest;
import merchant_manager.auth.SetPassUsernameRequest;
import merchant_manager.models.User;
import jakarta.validation.Valid;
import merchant_manager.service.implementation.UserServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserServiceImp userServiceImp;

    public UserController(UserServiceImp userServiceImp) {
        this.userServiceImp = userServiceImp;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = userServiceImp.registerUserAndAddTemplates(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/registerCompanyUser")
    public ResponseEntity<User> registerCompanyUserAndAddTemplates(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = userServiceImp.registerCompanyUserAndAddTemplates(request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/getLoggedUser")
    public ResponseEntity<User> getLoggedUser() {
        return ResponseEntity.ok(userServiceImp.getLoggedUser());
    }

}
