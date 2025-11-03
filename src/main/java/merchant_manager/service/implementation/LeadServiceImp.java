package merchant_manager.service.implementation;

import merchant_manager.models.Lead;
import merchant_manager.repository.LeadRepository;
import org.springframework.stereotype.Service;

@Service
public class LeadServiceImp {

    private final LeadRepository leadRepository;

    public LeadServiceImp(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Lead saveLead(Lead lead) {
        return leadRepository.save(lead);
    }
}
