package merchant_manager.service.implementation;

import lombok.extern.slf4j.Slf4j;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.Company;
import merchant_manager.models.User;
import merchant_manager.repository.CompanyRepository;
import merchant_manager.repository.UserRepository;
import merchant_manager.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CompanyServiceImp implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final UserServiceImp userServiceImp;
    private final TemplateServiceImp  templateServiceImp;

    public CompanyServiceImp(CompanyRepository companyRepository,
                             UserRepository userRepository,
                             UserServiceImp userServiceImp,
                             TemplateServiceImp templateServiceImp) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.userServiceImp = userServiceImp;
        this.templateServiceImp = templateServiceImp;
    }

    @Override
    @Transactional
    public Company createCompany(Company company) {
        try {
            User currentUser = getCurrentUser();
            company.setCreatedBy(currentUser.getUsername());
            Company company_saved = companyRepository.save(company);
            User managedUser = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("User not found"));
            managedUser.getCompanies().add(company_saved);
            if (managedUser.getCompanyId() == null || managedUser.getCompanyId() == 0L) {
                managedUser.setCompanyId(company_saved.getId());
            }
            userRepository.save(managedUser);
            templateServiceImp.addDefaultTemplateToUser(managedUser,  company_saved);
            log.info("Creating company: {} for user: {}", company_saved.getName(), currentUser.getUsername());
            return company_saved;
        } catch (Exception e) {
            log.error("Error creating company: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Error creating company: " + e.getMessage());
        }
    }

    @Override
    public List<Company> getAllCompaniesForCurrentUser() {
        try {
            User currentUser = getCurrentUser();
            log.info("Fetching all companies for user: {}", currentUser.getUsername());
            return companyRepository.findByUserId(currentUser.getId());
        } catch (Exception e) {
            log.error("Error fetching companies: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Error fetching companies: " + e.getMessage());
        }
    }

    @Override
    public Company getCompanyById(Long id) {
        try {
            User currentUser = getCurrentUser();
            return companyRepository.findByIdAndUserId(id, currentUser.getId())
                    .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Company not found with id: " + id));
        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching company: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Error fetching company: " + e.getMessage());
        }
    }

    @Override
    public Company updateCompany(Long id, Company companyDetails) {
        try {
            User currentUser = getCurrentUser();
            Company company = companyRepository.findByIdAndUserId(id, currentUser.getId())
                    .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Company not found with id: " + id));

            company.setName(companyDetails.getName());
            company.setAddress(companyDetails.getAddress());
            company.setCity(companyDetails.getCity());
            company.setState(companyDetails.getState());
            company.setZipCode(companyDetails.getZipCode());
            company.setCountry(companyDetails.getCountry());
            company.setPhone(companyDetails.getPhone());
            company.setEmail(companyDetails.getEmail());
            company.setWebsite(companyDetails.getWebsite());
            company.setDescription(companyDetails.getDescription());
            company.setLastUpdatedBy(currentUser.getUsername());

            log.info("Updating company: {} for user: {}", company.getName(), currentUser.getUsername());
            return companyRepository.save(company);
        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating company: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Error updating company: " + e.getMessage());
        }
    }

    @Override
    public void deleteCompany(Long id) {
        try {
            User currentUser = getCurrentUser();
            Company company = companyRepository.findByIdAndUserId(id, currentUser.getId())
                    .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Company not found with id: " + id));

            log.info("Deleting company: {} for user: {}", company.getName(), currentUser.getUsername());
            companyRepository.delete(company);
        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting company: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Error deleting company: " + e.getMessage());
        }
    }

    private User getCurrentUser() {
        return userServiceImp.getLoggedUser();
    }
}
