package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import merchant_manager.auth.RegisterRequest;
import merchant_manager.auth.SetPassUsernameRequest;
import merchant_manager.models.User;
import jakarta.validation.Valid;
import merchant_manager.service.implementation.CompanyServiceImp;
import merchant_manager.service.implementation.UserServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class UserController {

    private final UserServiceImp userServiceImp;
    private final CompanyServiceImp companyServiceImp;

    public UserController(UserServiceImp userServiceImp, CompanyServiceImp companyServiceImp) {
        this.userServiceImp = userServiceImp;
        this.companyServiceImp = companyServiceImp;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user with default templates")
    public ResponseEntity<User> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = userServiceImp.registerUserAndAddTemplates(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register-company-user/{companyId}")
    @Operation(
            summary = "Register company user",
            description = "Register a new user and associate them with a specific company. " +
                         "This endpoint retrieves the company by ID and creates a user with company association and default templates."
    )
    public ResponseEntity<User> registerCompanyUser(
            @PathVariable Long companyId,
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = companyServiceImp.registerCompanyUserWithTemplates(request, companyId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/getLoggedUser")
    @Operation(summary = "Get logged in user", description = "Retrieve the currently authenticated user details")
    public ResponseEntity<User> getLoggedUser() {
        return ResponseEntity.ok(userServiceImp.getLoggedUser());
    }

}
