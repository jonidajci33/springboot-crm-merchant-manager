package merchant_manager.controller;

import merchant_manager.models.PointingSystem;
import merchant_manager.service.implementation.PointingSystemServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pointing-system")
public class PointingSystemController {

    private final PointingSystemServiceImp pointingSystemService;

    public PointingSystemController(PointingSystemServiceImp pointingSystemService) {
        this.pointingSystemService = pointingSystemService;
    }

    @PostMapping
    public ResponseEntity<PointingSystem> createPointingSystem(@RequestBody PointingSystem pointingSystem) {
        PointingSystem saved = pointingSystemService.save(pointingSystem);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointingSystem> updatePointingSystem(@PathVariable Long id, @RequestBody PointingSystem pointingSystem) {
        pointingSystem.setId(id);
        PointingSystem updated = pointingSystemService.save(pointingSystem);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointingSystem> getPointingSystemById(@PathVariable Long id) {
        return ResponseEntity.ok(pointingSystemService.getPointingSystemById(id));
    }

    @GetMapping
    public ResponseEntity<List<PointingSystem>> getAllPointingSystems() {
        return ResponseEntity.ok(pointingSystemService.getAllPointingSystems());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePointingSystem(@PathVariable Long id) {
        pointingSystemService.deletePointingSystem(id);
        return ResponseEntity.noContent().build();
    }
}
