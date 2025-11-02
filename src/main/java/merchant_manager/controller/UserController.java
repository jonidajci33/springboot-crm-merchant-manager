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
@RequestMapping("/user")
public class UserController {

    private final UserServiceImp userServiceImp;

    public UserController(UserServiceImp userServiceImp) {
        this.userServiceImp = userServiceImp;
    }

    @GetMapping(value = "/secure/pendingUsers")
    public ResponseEntity<List<User>> getPendingUsers(){
        List<User> users =  userServiceImp.getPendingUser();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<User> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = userServiceImp.registerUserAndAddTemplates(request);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/secure/completeUserRegister")
    public ResponseEntity<User> setUsernameAndPassword(
            @Valid @RequestBody SetPassUsernameRequest request
    ) {
        User user = userServiceImp.setPassAndUsername(request);
        return ResponseEntity.ok(user);
    }
}
