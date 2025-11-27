package merchant_manager.service;

import merchant_manager.models.Company;

import java.util.List;

public interface CompanyService {

    Company createCompany(Company company);

    List<Company> getAllCompaniesForCurrentUser();

    Company getCompanyById(Long id);

    Company updateCompany(Long id, Company companyDetails);

    void deleteCompany(Long id);
}
