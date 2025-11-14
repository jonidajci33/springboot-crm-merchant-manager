package merchant_manager.service;

import merchant_manager.models.PointingSystem;

import java.util.List;

public interface PointingSystemService {

    PointingSystem save(PointingSystem pointingSystem);

    PointingSystem getPointingSystemById(Long id);

    List<PointingSystem> getAllPointingSystems();

    void deletePointingSystem(Long id);

}
