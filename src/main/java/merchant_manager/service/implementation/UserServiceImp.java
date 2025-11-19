package merchant_manager.service.implementation;

import lombok.extern.slf4j.Slf4j;
import merchant_manager.auth.RegisterRequest;
import merchant_manager.auth.SetPassUsernameRequest;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.emailService.EmailService;
import merchant_manager.models.User;
import merchant_manager.models.enums.AccountStatus;
import merchant_manager.models.enums.Role;
import merchant_manager.repository.UserRepository;
import merchant_manager.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TemplateServiceImp templateServiceImp;

    public UserServiceImp(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, EmailService emailService, TemplateServiceImp templateServiceImp) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.templateServiceImp = templateServiceImp;
    }

    @Override
    public List<User> getPendingUser() {
        try {
            List<User> users = userRepository.findByAccountStatus();
            return users;
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException(e.getMessage());
        }
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("User not found"));
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("User not found"));
    }

    public User getLoggedUser() {
        User user = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                user = (User) authentication.getPrincipal();
            } else {
                throw new CustomExceptions.ResourceNotFoundException("You are not logged in. Log in first then try to access the api");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomExceptions.ResourceNotFoundException(e.getMessage());
        }
        return user;
    }

    public User setPassAndUsername(SetPassUsernameRequest request) {

        User user = findById(request.getId());
        try {
            User userNotInSystem = findByUsername(request.getUsername());
        }catch (CustomExceptions.ResourceNotFoundException e){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setUsername(request.getUsername());
            user.setAccountStatus(AccountStatus.ACTIVE);
            return save(user);
        }
        throw new CustomExceptions.CustomValidationException("User already exists");
    }

    public User register(RegisterRequest request) {

        User user = new User();
        try {
            findByUsername(request.getUsername());
        }catch (CustomExceptions.ResourceNotFoundException e){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setUsername(request.getUsername());
            user.setAccountStatus(AccountStatus.ACTIVE);
            user.setName(request.getFirstname());
            user.setLastName(request.getLastname());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setRole(Role.ROLE_USER);
            return save(user);
        }
        throw new CustomExceptions.CustomValidationException("Username already exists");

//        String emailBody = String.format(
//                "New User Registration Details:\n\n" +
//                        "First Name: %s\n" +
//                        "Last Name: %s\n" +
//                        "Email: %s\n" +
//                        "Phone: %s\n" +
//                        "Company Name: %s\n" +
//                        "Job Title: %s\n" +
//                        "MC Number: %s\n" +
//                        "DOT Number: %s\n" +
//                        "Role: %s\n" +
//                        "Status: %s",
//                request.getFirstname(),
//                request.getLastname(),
//                request.getEmail(),
//                request.getPhone(),
//                request.getCompanyName(),
//                request.getJobTitle(),
//                AccountStatus.PENDING
//        );

//        emailService.sendEmail("joni.dajci12@gmail.com", "New Registration", emailBody);
    }

    public User registerUserAndAddTemplates(RegisterRequest request){
        User user = register(request);
        templateServiceImp.addTemplateToUser(user);
        return user;
    }

    public User save(User user) {
        log.info("Saving user: Name " + user.getName() + " Role " + user.getRole());
        return userRepository.save(user);
    }

}
