# Architecture Rules

## Layered Architecture

This application follows a strict layered architecture pattern:

```
Repository -> Service -> Controller
```

### Rules:

1. **Repository Layer**
   - Repositories handle direct database access
   - Repositories extend JpaRepository or similar Spring Data interfaces
   - Repositories are ONLY called from their corresponding Service layer

2. **Service Layer**
   - Services contain business logic
   - Services can call their own repository
   - Services can call OTHER services (but not repositories directly)
   - Services should NOT have circular dependencies

3. **Controller Layer**
   - Controllers handle HTTP requests/responses
   - Controllers call Services (never Repositories directly)
   - Controllers should be thin - delegate business logic to Services

### Dependency Flow:

```
Controller
    ↓
  Service  ←→  Service (allowed)
    ↓
Repository
```

### What is NOT allowed:

❌ Controller → Repository (skip Service layer)
❌ Service → Another Service's Repository (must go through Service)
❌ Circular dependencies between Services

### Example:

**CORRECT:**
```java
@Service
public class UserServiceImp {
    private final UserRepository userRepository;
    private final CompanyServiceImp companyService; // ✓ Service calls another Service

    public User getUser(Long companyId) {
        Company company = companyService.getCompanyById(companyId); // ✓ Through Service
        // ... business logic
    }
}
```

**INCORRECT:**
```java
@Service
public class UserServiceImp {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository; // ✗ Service shouldn't call another Repository

    public User getUser(Long companyId) {
        Company company = companyRepository.findById(companyId); // ✗ Should use CompanyService
        // ... business logic
    }
}
```

### Resolving Circular Dependencies:

When Service A needs Service B and Service B needs Service A:
1. Create a new method in one of the services that combines the logic
2. OR extract shared logic to a utility class
3. OR use events/messaging for loose coupling
4. NEVER use @Lazy annotation as a quick fix

---

Last Updated: 2025-11-29
