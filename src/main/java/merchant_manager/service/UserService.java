package merchant_manager.service;

import merchant_manager.models.User;

import java.util.List;

public interface UserService {

    List<User> getPendingUser();

    List<User> getUsersByCompany(Long companyId);

    List<User> getUsersByCompanyWithAuthorization(Long companyId);
}
