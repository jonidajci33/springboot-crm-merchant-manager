package merchant_manager.auth;

import lombok.Data;
import merchant_manager.models.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class RegisterRequest {

    @NotBlank(message = "Firstname is required")
    @Size(min = 2, max = 50, message = "Firstname must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Firstname must not contain special characters")
    private String firstname;

    @NotBlank(message = "Lastname is required")
    @Size(min = 2, max = 50, message = "Lastname must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Lastname must not contain special characters")
    private String lastname;

    @NotBlank(message = "Company name is required")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Company name must not contain special characters")
    private String companyName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "Job title must not contain special characters")
    private String jobTitle;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must not contain specialcharacters ")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must me stronger : At least one uppercase letter (A-Z)\n" +
            "At least one lowercase letter (a-z)\n" +
            "At least one digit (0-9)\n" +
            "At least one special character (e.g., !@#$%^&*()_+)\n" +
            "Minimum length of 8 characters")
    private String password;

}
