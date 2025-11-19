package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import merchant_manager.models.Company;
import merchant_manager.service.implementation.CompanyServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@Tag(name = "Companies", description = "Manage companies for users")
public class CompanyController {

    private final CompanyServiceImp companyService;

    public CompanyController(CompanyServiceImp companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    @Operation(summary = "Create a new company", description = "Create a new company for the authenticated user")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        Company savedCompany = companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCompany);
    }

    @GetMapping
    @Operation(summary = "Get all companies for current user", description = "Retrieve all companies associated with the authenticated user")
    public ResponseEntity<List<Company>> getAllCompaniesForCurrentUser() {
        List<Company> companies = companyService.getAllCompaniesForCurrentUser();
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID", description = "Retrieve a specific company by ID for the authenticated user")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        Company company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update company", description = "Update an existing company for the authenticated user")
    public ResponseEntity<Company> updateCompany(@PathVariable Long id, @Valid @RequestBody Company companyDetails) {
        Company updatedCompany = companyService.updateCompany(id, companyDetails);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete company", description = "Delete a company by ID for the authenticated user")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
