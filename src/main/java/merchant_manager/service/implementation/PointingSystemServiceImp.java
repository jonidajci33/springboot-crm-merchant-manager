package merchant_manager.service.implementation;

import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.PointingSystem;
import merchant_manager.repository.PointingSystemRepository;
import merchant_manager.service.PointingSystemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointingSystemServiceImp implements PointingSystemService {

    private final PointingSystemRepository pointingSystemRepository;

    public PointingSystemServiceImp(PointingSystemRepository pointingSystemRepository) {
        this.pointingSystemRepository = pointingSystemRepository;
    }

    @Override
    public PointingSystem save(PointingSystem pointingSystem) {
        return pointingSystemRepository.save(pointingSystem);
    }

    @Override
    public PointingSystem getPointingSystemById(Long id) {
        return pointingSystemRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("PointingSystem not found with id: " + id));
    }

    @Override
    public List<PointingSystem> getAllPointingSystems() {
        return pointingSystemRepository.findAll();
    }

    @Override
    public void deletePointingSystem(Long id) {
        pointingSystemRepository.deleteById(id);
    }
}
