package merchant_manager.service.implementation;

import merchant_manager.models.Contact;
import merchant_manager.repository.ContactRepository;
import merchant_manager.service.ContactService;
import org.springframework.stereotype.Service;

@Service
public class ContactServiceImp implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImp(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }

}
