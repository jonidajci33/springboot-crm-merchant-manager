# Spring Boot CRM Merchant Manager - Project Patterns & Development Guide

> **Purpose**: This document captures all architectural patterns, coding conventions, and best practices used in this project. When working with AI assistants, reference this file to ensure consistent code generation that matches the existing codebase.

---

## Table of Contents
1. [Technology Stack](#technology-stack)
2. [Repository Patterns](#repository-patterns)
3. [Service Layer Patterns](#service-layer-patterns)
4. [Model/Entity Patterns](#modelentity-patterns)
5. [Project Structure](#project-structure)
6. [Code Examples](#code-examples)
7. [Important Notes](#important-notes)

---

## Technology Stack

- **Spring Boot 3.x** with Jakarta EE
- **Spring Data JPA** for data access
- **Lombok** for boilerplate reduction
- **PostgreSQL** with JSONB support
- **Hibernate** with custom types (vladmihalcea for JSON types)
- **Spring Security** (UserDetails implementation)
- **OpenAPI/Swagger** for API documentation

---

## Repository Patterns

### JpaRepository Extension Pattern

All repositories extend `JpaRepository<Entity, ID>` with `@Repository` annotation.

**Examples**:

```java
// Simple repository with no custom methods
@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}

// Repository with custom query methods
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    Optional<Template> findByMenuId(Long menuId);
}
```

### Query Method Naming Conventions

#### Pattern 1: findBy + Property

- Returns `Optional<Entity>` for single results
- Returns `List<Entity>` for multiple results

**Single result examples**:
```java
Optional<User> findByUsername(String username);
Optional<Template> findByMenuId(Long menuId);
Optional<TemplateForm> findByKey(String key);
```

**Multiple results examples**:
```java
List<TemplateForm> findByTemplateId(Long templateId);
List<TemplateFormDefault> findByTemplateId(Long templateId);
```

#### Pattern 2: findBy + Multiple Properties

```java
Optional<TemplateFormValue> findByTemplateFormIdAndRecordId(Long templateFormId, Long recordId);
```

#### Pattern 3: Custom @Query Annotations

```java
@Query(value = """
    select u from User u where u.accountStatus = "PENDING"
    """)
List<User> findByAccountStatus();

@Query("SELECT tfd FROM TemplateFormDefault tfd WHERE tfd.template.id = :templateId")
List<TemplateFormDefault> findAllByTemplateId(@Param("templateId") Long templateId);
```

### Delete Method Patterns

```java
void deleteByIdAndTemplateId(Long id, Long templateId);
void deleteByKey(String key);
Optional<TemplateFormDefault> removeByKey(String key);  // Returns removed entity
```

### Return Type Patterns

| Return Type | Use Case | Example |
|------------|----------|---------|
| `Optional<Entity>` | Single result that may not exist | `findByUsername(String)` |
| `List<Entity>` | Multiple results (can be empty) | `findByTemplateId(Long)` |
| `void` | Delete operations | `deleteByKey(String)` |
| `Optional<Entity>` | Delete with return | `removeByKey(String)` |

### Common Repository Annotations

- `@Repository` - Marks interface as Spring Data repository
- `@Query` - Custom JPQL queries
- `@Param` - Named parameters in @Query

---

## Service Layer Patterns

### Interface-Implementation Structure

**Pattern**: Service interface + `ServiceImp` implementation class

**IMPORTANT**: Use `ServiceImp` (NOT `ServiceImpl`) - this is the project convention.

**File Structure**:
```
service/
├── MerchantService.java           (interface)
├── TemplateService.java           (interface)
└── implementation/
    ├── MerchantServiceImp.java    (implements MerchantService)
    └── TemplateServiceImp.java    (implements TemplateService)
```

### Service Interface Examples

**Simple Service Interface**:
```java
public interface MerchantService {
    Merchant save(Merchant merchant);
}
```

**Complex Service Interface**:
```java
public interface TemplateService {
    void addDefaultTemplateToUser(User user);
    Template findByMenuId(Long menuId);
}

public interface TemplateFormService {
    List<TemplateForm> addFieldToTemplate(Long menuId, List<TemplateForm> templateForm);
    void removeFieldFromTemplate(List<String> keys);
    List<TemplateForm> getTemplateFields(Long menuId);
}
```

### Implementation Class Naming Convention

**Pattern**: `{ServiceName}Imp` (note: "Imp" not "Impl")

Examples:
- `MerchantServiceImp`
- `TemplateServiceImp`
- `ContactServiceImp`
- `UserServiceImp`
- `DynamicRecordServiceImp`

### Dependency Injection Pattern

**Pattern 1: Constructor Injection (Traditional)**
```java
@Service
public class MerchantServiceImp implements MerchantService {
    private final MerchantRepository merchantRepository;

    public MerchantServiceImp(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }
}
```

**Pattern 2: Constructor Injection with Lombok @RequiredArgsConstructor (PREFERRED)**
```java
@Service
@RequiredArgsConstructor
public class DynamicRecordServiceImp implements DynamicRecordService {
    private final TemplateFormDefaultRepository templateFormDefaultRepository;
    private final TemplateFormValueDefaultRepository templateFormValueDefaultRepository;
    private final TemplateDefaultServiceImp templateServiceImp;
    private final UserServiceImp userServiceImp;
}
```

### Exception Handling Patterns

#### Pattern 1: ResourceNotFoundException with orElseThrow

```java
public Template findByMenuId(Long menuId) {
    return templateRepository.findByMenuId(menuId)
            .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException(
                    "Template Default not found for menu ID: " + menuId));
}

public User findByUsername(String username) {
    return userRepository.findByUsername(username)
            .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("User not found"));
}
```

#### Pattern 2: Try-Catch with Custom Exception Wrapping

```java
@Override
@Transactional
public List<TemplateForm> addFieldToTemplate(Long menuId, List<TemplateForm> templateForm) {
    try {
        // Business logic
        return formList;
    } catch (CustomExceptions.ResourceNotFoundException e) {
        throw e;  // Re-throw specific exceptions
    } catch (Exception e) {
        log.error("Error adding field to template: {}", e.getMessage(), e);
        throw new CustomExceptions.CustomValidationException("Failed to add field to template: " + e.getMessage());
    }
}
```

#### Pattern 3: Role-Based Authorization Exception

```java
public List<TemplateFormDefault> addFieldToDefaultTemplate(Long menuId, List<TemplateFormDefault> templateForm) {
    User user = userServiceImp.getLoggedUser();
    if(user.getRole().equals(Role.ROLE_SUPERUSER)) {
        // Business logic
    } else {
        throw new CustomExceptions.CustomValidationException("User is not a member of this role");
    }
}
```

### Custom Exception Classes

Location: `src/main/java/merchant_manager/customExceptions/CustomExceptions.java`

```java
public class CustomExceptions {
    public static class ResourceNotFoundException extends NullPointerException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }

    public static class CustomValidationException extends RuntimeException {
        public CustomValidationException(String message) {
            super(message);
        }
    }

    public static class SystemErrorException extends RuntimeException {
        public SystemErrorException(String message) {
            super(message);
        }
    }
}
```

### Common Service Layer Annotations

- `@Service` - Marks class as Spring service component
- `@Transactional` - Transaction management for methods that modify data
- `@Slf4j` - Lombok annotation for logging
- `@RequiredArgsConstructor` - Lombok for constructor injection (PREFERRED)

### Service-to-Repository Interaction Pattern

**Direct repository calls**:
```java
public Merchant save(Merchant merchant) {
    return merchantRepository.save(merchant);
}
```

**With business logic and audit fields**:
```java
templateFormCurrent.setTemplate(template);
templateFormCurrent.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
templateFormCurrent.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
templateFormCurrent.setCreatedBy(template.getUser().getUsername());
templateFormCurrent.setLastUpdatedBy(template.getUser().getUsername());
formList.add(templateFormRepository.save(templateFormCurrent));
```

---

## Model/Entity Patterns

### Base Model Pattern

**BaseModel Abstract Class**: Common audit fields for all entities

Location: `src/main/java/merchant_manager/models/BaseModel.java`

```java
@MappedSuperclass
@Data
@AllArgsConstructor
public abstract class BaseModel {
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false)
    protected String createdBy;

    @Column(name = "last_updated_by")
    protected String lastUpdatedBy;

    public BaseModel() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime();
        this.updatedAt = ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime();
    }
}
```

**Key Features**:
- Uses `@MappedSuperclass` (not @Entity)
- Automatic timestamp initialization in constructor
- **Eastern timezone (America/New_York)** - IMPORTANT!
- Audit fields: `createdAt`, `updatedAt`, `createdBy`, `lastUpdatedBy`

### Standard Entity Annotation Pattern

```java
@Entity
@Table(name = "merchant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Merchant extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Other fields...
}
```

### Common Entity Annotations

| Annotation | Purpose | Usage |
|-----------|---------|-------|
| `@Entity` | Marks class as JPA entity | All model classes |
| `@Table(name = "table_name")` | Specifies database table name | All model classes |
| `@Data` | Lombok: generates getters, setters, toString, equals, hashCode | Most entities |
| `@NoArgsConstructor` | Lombok: generates no-args constructor | All entities |
| `@AllArgsConstructor` | Lombok: generates all-args constructor | All entities |
| `@Id` | Marks primary key field | All entities |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Auto-increment ID | All entities |
| `@Column(name = "column_name")` | Maps field to database column | All fields |

### ID Generation Strategy

**Consistent Pattern**: Auto-increment with IDENTITY strategy

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id")
private Long id;
```

### Relationship Patterns

#### @ManyToOne Relationship

```java
@ManyToOne
@JoinColumn(name = "user_id", nullable = false)
private User user;

@ManyToOne
@JoinColumn(name = "menu_id", nullable = false)
private Menu menu;

@ManyToOne
@JoinColumn(name = "template_id", nullable = false)
private Template template;
```

#### @ManyToOne with @JsonIgnore (prevents circular references)

```java
@JsonIgnore
@ManyToOne
@JoinColumn(name = "template_id", nullable = false)
private Template template;
```

#### Join Table Pattern (Example: ContactMerchant)

```java
@Entity
@Table(name = "contact_merchant")
public class ContactMerchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
}
```

### Special Column Types

#### Enum Columns

```java
@Enumerated(EnumType.STRING)
private FieldType type;

@Enumerated(EnumType.STRING)
private AccountStatus accountStatus;

@Enumerated(EnumType.STRING)
private Role role;
```

#### JSON/JSONB Columns (PostgreSQL)

```java
@Type(JsonType.class)
@Column(columnDefinition = "jsonb")
private Map<String, String> options;

@Type(JsonType.class)
@Column(columnDefinition = "jsonb")
private List<Map<String, String>> options;

@Type(JsonType.class)
@Column(columnDefinition = "jsonb")
private Map<String, String> formProps;
```

### Lifecycle Callbacks

**@PrePersist Pattern** (used for auto-generating keys):

```java
@PrePersist
private void generateKey() {
    if (this.key == null || this.key.isEmpty()) {
        this.key = generateUniqueHash();
    }
}

private String generateUniqueHash() {
    String uuid = UUID.randomUUID().toString().replace("-", "");
    long timestamp = System.nanoTime();
    int random = (int) (Math.random() * 10000);
    String combined = uuid + timestamp + random;
    return Integer.toHexString(combined.hashCode()) + uuid.substring(0, 24);
}
```

### Entity Naming Conventions

| Entity | Table Name | Pattern |
|--------|-----------|---------|
| Merchant | merchant | Lowercase |
| Contact | contact | Lowercase |
| Template | template | Lowercase |
| TemplateForm | template_form | Snake case |
| TemplateFormValue | template_form_value | Snake case |
| ContactMerchant | contact_merchant | Snake case (join table) |
| User | _user | Underscore prefix (SQL reserved word) |

### Special Cases

#### User Entity
- Does **NOT** extend BaseModel
- Implements `UserDetails` (Spring Security)
- Manual getters/setters (no Lombok @Data)
- Table name: `_user` (underscore prefix to avoid SQL reserved word)

```java
@Entity
@Table(name = "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Manual getters and setters
}
```

#### ContactMerchant Entity
- Does **NOT** extend BaseModel
- Pure join table pattern
- Simple structure with only relationships

### Entities with Custom Constructors

Some entities provide custom constructors for convenience:

```java
public Template(LocalDateTime createdAt, LocalDateTime updatedAt,
                String createdBy, String lastUpdatedBy,
                Menu menu, User user) {
    super(createdAt, updatedAt, createdBy, lastUpdatedBy);
    this.menu = menu;
    this.user = user;
}
```

---

## Project Structure

### Package Organization

```
src/main/java/merchant_manager/
├── auth/                           # Authentication DTOs and requests
├── config/                         # Spring configuration classes
├── controller/                     # REST API controllers
│   ├── UserController.java
│   ├── TemplateFormController.java
│   ├── TemplateFormDefaultController.java
│   ├── TemplateFormValueController.java
│   ├── TemplateFormValueDefaultController.java
│   └── DynamicRecordController.java
├── ControllerAdvice/               # Global exception handlers
├── customExceptions/               # Custom exception classes
│   └── CustomExceptions.java
├── emailService/                   # Email service classes
├── models/                         # JPA entities
│   ├── enums/                     # Enum types
│   │   ├── AccountStatus.java
│   │   ├── FieldType.java
│   │   ├── Role.java
│   │   └── TokenType.java
│   ├── DTO/                       # Data Transfer Objects
│   ├── BaseModel.java
│   ├── Contact.java
│   ├── Merchant.java
│   ├── Template.java
│   └── [other entities]
├── repository/                     # Spring Data JPA repositories
│   ├── MerchantRepository.java
│   ├── TemplateRepository.java
│   └── [other repositories]
├── service/                        # Service interfaces
│   ├── MerchantService.java
│   ├── TemplateService.java
│   └── implementation/             # Service implementations
│       ├── MerchantServiceImp.java
│       ├── TemplateServiceImp.java
│       └── [other implementations]
├── util/                           # Utility classes
└── MerchantManagerApplication.java # Main Spring Boot application
```

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controller Layer            │ ← REST endpoints, @RestController
│  (TemplateFormDefaultController)    │   @RequestMapping, ResponseEntity
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│         Service Layer               │ ← Business logic, @Service
│  Interface: TemplateFormService     │   @Transactional, exception handling
│  Impl: TemplateFormServiceImp       │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│        Repository Layer             │ ← Data access, @Repository
│  (TemplateFormRepository)           │   extends JpaRepository
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│         Model/Entity Layer          │ ← JPA entities, @Entity
│  (TemplateForm extends BaseModel)   │   @Table, relationships
└─────────────────────────────────────┘
```

### File Naming Conventions

| Layer | Interface Pattern | Implementation Pattern | Example |
|-------|------------------|------------------------|---------|
| Model | `{Entity}.java` | N/A | `Merchant.java`, `Template.java` |
| Repository | `{Entity}Repository.java` | N/A | `MerchantRepository.java` |
| Service | `{Entity}Service.java` | `{Entity}ServiceImp.java` | `MerchantService.java`, `MerchantServiceImp.java` |
| Controller | `{Entity}Controller.java` | N/A | `TemplateFormController.java` |

### Controller Layer Pattern

```java
@RestController
@RequestMapping("/api/template-forms-default")
@Tag(name = "Template Form Default", description = "APIs for managing default template form columns")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class TemplateFormDefaultController {
    private final TemplateFormDefaultServiceImp templateFormDefaultService;

    @GetMapping("/menu/{menuId}")
    @Operation(summary = "Get column definitions for template")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Columns successfully retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<List<TemplateFormDefault>> getColumnsByMenuId(
        @PathVariable Long menuId
    ) {
        List<TemplateFormDefault> columns = templateFormDefaultService.getColumnsByMenuId(menuId);
        return ResponseEntity.ok(columns);
    }
}
```

**Key Controller Patterns**:
- Uses OpenAPI/Swagger annotations (@Operation, @ApiResponses, @Tag)
- Constructor injection with Lombok @RequiredArgsConstructor
- Returns `ResponseEntity<T>` for HTTP responses
- Security requirement annotation for authentication
- RESTful path conventions

---

## Code Examples

### Creating a New Entity

```java
package merchant_manager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "your_entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YourEntity extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Custom constructor that calls super() for BaseModel
    public YourEntity(LocalDateTime createdAt, LocalDateTime updatedAt,
                      String createdBy, String lastUpdatedBy,
                      String name, User user) {
        super(createdAt, updatedAt, createdBy, lastUpdatedBy);
        this.name = name;
        this.user = user;
    }
}
```

### Creating a Repository

```java
package merchant_manager.repository;

import merchant_manager.models.YourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YourEntityRepository extends JpaRepository<YourEntity, Long> {

    // Single result - use Optional
    Optional<YourEntity> findByName(String name);

    // Multiple results - use List
    List<YourEntity> findByUserId(Long userId);

    // Delete methods
    void deleteByName(String name);
}
```

### Creating a Service Interface

```java
package merchant_manager.service;

import merchant_manager.models.YourEntity;

import java.util.List;

public interface YourEntityService {

    YourEntity save(YourEntity entity);

    YourEntity findById(Long id);

    YourEntity findByName(String name);

    List<YourEntity> findAll();

    void deleteById(Long id);
}
```

### Creating a Service Implementation

```java
package merchant_manager.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.User;
import merchant_manager.models.YourEntity;
import merchant_manager.repository.YourEntityRepository;
import merchant_manager.service.YourEntityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class YourEntityServiceImp implements YourEntityService {

    private final YourEntityRepository repository;
    private final UserServiceImp userServiceImp;

    @Override
    @Transactional
    public YourEntity save(YourEntity entity) {
        try {
            User user = userServiceImp.getLoggedUser();

            // Set audit fields
            entity.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
            entity.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
            entity.setCreatedBy(user.getUsername());
            entity.setLastUpdatedBy(user.getUsername());

            return repository.save(entity);
        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
            log.error("Error saving entity: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException(
                "Failed to save entity: " + e.getMessage());
        }
    }

    @Override
    public YourEntity findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException(
                "Entity not found with id: " + id));
    }

    @Override
    public YourEntity findByName(String name) {
        return repository.findByName(name)
            .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException(
                "Entity not found with name: " + name));
    }

    @Override
    public List<YourEntity> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new CustomExceptions.ResourceNotFoundException(
                "Entity not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
```

### Creating a Controller

```java
package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import merchant_manager.models.YourEntity;
import merchant_manager.service.implementation.YourEntityServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/your-entity")
@Tag(name = "Your Entity", description = "APIs for managing your entity")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class YourEntityController {

    private final YourEntityServiceImp service;

    @GetMapping("/{id}")
    @Operation(summary = "Get entity by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entity successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    public ResponseEntity<YourEntity> getById(@PathVariable Long id) {
        YourEntity entity = service.findById(id);
        return ResponseEntity.ok(entity);
    }

    @GetMapping
    @Operation(summary = "Get all entities")
    public ResponseEntity<List<YourEntity>> getAll() {
        List<YourEntity> entities = service.findAll();
        return ResponseEntity.ok(entities);
    }

    @PostMapping
    @Operation(summary = "Create new entity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Entity successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<YourEntity> create(@Valid @RequestBody YourEntity entity) {
        YourEntity saved = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete entity by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Entity successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Important Notes

### 1. Timezone Handling
- **All timestamps use America/New_York timezone**
- Set in BaseModel constructor: `ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime()`
- When manually setting timestamps in service layer, always use this pattern

### 2. Implementation Naming
- **Use `ServiceImp`** (NOT `ServiceImpl`) - this is the project convention
- Examples: `MerchantServiceImp`, `TemplateServiceImp`, `UserServiceImp`

### 3. Security Context
- Use `UserServiceImp.getLoggedUser()` to get authenticated user
- User information available from Spring Security context
- Example: `User user = userServiceImp.getLoggedUser();`

### 4. Transaction Management
- Use `@Transactional` for methods that modify data
- Especially important for delete operations
- Place at service layer method level

### 5. Exception Handling Best Practices
- **Always re-throw** `ResourceNotFoundException` without wrapping
- **Wrap generic exceptions** in `CustomValidationException` with descriptive messages
- **Log errors** before throwing exceptions using `log.error()`
- **Use appropriate exception types**:
  - `ResourceNotFoundException` - When entity not found
  - `CustomValidationException` - Business logic validation failures
  - `UnauthorizedAccessException` - Authorization failures
  - `SystemErrorException` - System-level errors

### 6. Lombok Usage
- Use `@Data` on entities (generates getters, setters, toString, equals, hashCode)
- Use `@RequiredArgsConstructor` on service implementations (PREFERRED for DI)
- Use `@Slf4j` on service implementations for logging
- Use `@NoArgsConstructor` and `@AllArgsConstructor` on entities

### 7. Repository Return Types
- **Single result that may not exist**: `Optional<Entity>`
- **Multiple results**: `List<Entity>` (can be empty)
- **Delete operations**: `void` or `Optional<Entity>` if returning deleted entity
- Service layer should convert `Optional` to entity or throw exception

### 8. Audit Fields Pattern
- All entities extending BaseModel automatically get:
  - `createdAt` (LocalDateTime)
  - `updatedAt` (LocalDateTime)
  - `createdBy` (String)
  - `lastUpdatedBy` (String)
- Set these in service layer when creating/updating entities

### 9. Common Naming Patterns
- **Tables**: lowercase or snake_case (`merchant`, `template_form`)
- **Entities**: PascalCase (`Merchant`, `TemplateForm`)
- **Fields/Variables**: camelCase (`menuId`, `templateForm`)
- **Repositories**: `{Entity}Repository` (`MerchantRepository`)
- **Services**: `{Entity}Service` interface, `{Entity}ServiceImp` implementation
- **Controllers**: `{Entity}Controller` (`MerchantController`)
- **Packages**: lowercase (`merchant_manager`, `repository`, `service`)

### 10. Design Patterns Used
1. **Repository Pattern** - Spring Data JPA repositories
2. **Service Layer Pattern** - Interface + Implementation (Imp)
3. **Inheritance Pattern** - BaseModel for common audit fields
4. **DTO Pattern** - Separate DTO classes in models/DTO
5. **Builder Pattern** - Implicit via Lombok
6. **Dependency Injection** - Constructor injection (preferably with @RequiredArgsConstructor)
7. **Exception Handling Pattern** - Custom exceptions with try-catch wrapping

---

## Quick Reference Checklist

When creating a new feature, follow this checklist:

### Entity Creation
- [ ] Extend `BaseModel` (unless special case like User)
- [ ] Use `@Entity`, `@Table`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`
- [ ] ID field with `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- [ ] Use appropriate `@ManyToOne`, `@OneToMany` relationships
- [ ] Use `@JsonIgnore` on circular references
- [ ] Follow table naming convention (lowercase or snake_case)

### Repository Creation
- [ ] Extend `JpaRepository<Entity, Long>`
- [ ] Add `@Repository` annotation
- [ ] Use `Optional<Entity>` for single results
- [ ] Use `List<Entity>` for multiple results
- [ ] Follow naming: `findBy{Property}`, `deleteBy{Property}`

### Service Creation
- [ ] Create interface in `service/` package
- [ ] Create implementation in `service/implementation/` with `Imp` suffix
- [ ] Add `@Service` on implementation
- [ ] Add `@RequiredArgsConstructor` for dependency injection
- [ ] Add `@Slf4j` for logging
- [ ] Add `@Transactional` on methods that modify data
- [ ] Use `.orElseThrow()` pattern for Optional handling
- [ ] Wrap exceptions in try-catch with custom exceptions
- [ ] Set audit fields when creating/updating entities
- [ ] Get logged user via `userServiceImp.getLoggedUser()`

### Controller Creation (if needed)
- [ ] Add `@RestController`, `@RequestMapping`, `@RequiredArgsConstructor`
- [ ] Add `@Tag` and `@SecurityRequirement` for API docs
- [ ] Use `@Operation` and `@ApiResponses` on methods
- [ ] Return `ResponseEntity<T>`
- [ ] Use `@PathVariable`, `@RequestBody`, `@RequestParam` appropriately
- [ ] Use `@Valid` for request body validation

---

## How to Use This Document

### For AI Assistants
When asked to "understand my project structure" or "read the context file":
1. Read this entire document
2. Follow all patterns and conventions exactly
3. Use the code examples as templates
4. Reference specific sections when generating code
5. Always use the naming conventions specified
6. Follow the exception handling patterns
7. Respect the timezone (America/New_York)
8. Use `ServiceImp` not `ServiceImpl`

### For Developers
1. Reference this document when adding new features
2. Follow existing patterns to maintain consistency
3. Use code examples as starting templates
4. Ensure all new code follows the conventions
5. Update this document when introducing new patterns

---

**Last Updated**: 2025-11-07
**Project**: Spring Boot CRM Merchant Manager
**Purpose**: Comprehensive development guide and pattern reference
