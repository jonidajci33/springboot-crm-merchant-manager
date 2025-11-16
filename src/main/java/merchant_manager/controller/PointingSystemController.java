package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import merchant_manager.models.PointingSystem;
import merchant_manager.service.implementation.PointingSystemServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pointing-system")
@Tag(name = "Pointing System", description = "Manage merchant miles pointing systems and configurations")
public class PointingSystemController {

    private final PointingSystemServiceImp pointingSystemService;

    public PointingSystemController(PointingSystemServiceImp pointingSystemService) {
        this.pointingSystemService = pointingSystemService;
    }

    @PostMapping
    @Operation(summary = "Create pointing system", description = "Create a new merchant miles pointing system configuration")
    public ResponseEntity<PointingSystem> createPointingSystem(@RequestBody PointingSystem pointingSystem) {
        PointingSystem saved = pointingSystemService.save(pointingSystem);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pointing system", description = "Update an existing pointing system configuration")
    public ResponseEntity<PointingSystem> updatePointingSystem(@PathVariable Long id, @RequestBody PointingSystem pointingSystem) {
        pointingSystem.setId(id);
        PointingSystem updated = pointingSystemService.save(pointingSystem);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pointing system by ID", description = "Retrieve a specific pointing system by its ID")
    public ResponseEntity<PointingSystem> getPointingSystemById(@PathVariable Long id) {
        return ResponseEntity.ok(pointingSystemService.getPointingSystemById(id));
    }

    @GetMapping
    @Operation(summary = "Get all pointing systems", description = "Retrieve all pointing system configurations")
    public ResponseEntity<List<PointingSystem>> getAllPointingSystems() {
        return ResponseEntity.ok(pointingSystemService.getAllPointingSystems());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pointing system", description = "Delete a pointing system configuration by ID")
    public ResponseEntity<Void> deletePointingSystem(@PathVariable Long id) {
        pointingSystemService.deletePointingSystem(id);
        return ResponseEntity.noContent().build();
    }
}
