package merchant_manager.controller;

import jakarta.validation.Valid;
import merchant_manager.models.TemplateForm;
import merchant_manager.service.implementation.TemplateFormServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/template-forms")
public class TemplateFormController {

    private final TemplateFormServiceImp templateFormService;

    public TemplateFormController(TemplateFormServiceImp templateFormService) {
        this.templateFormService = templateFormService;
    }

    /**
     * Add a new field to user's template
     * POST /api/template-forms/add?userId=1&menuId=2
     */
    @PostMapping("/add")
    public ResponseEntity<List<TemplateForm>> addFieldToTemplate(
            @RequestParam Long userId,
            @RequestParam Long menuId,
            @Valid @RequestBody List<TemplateForm> templateForm
    ) {
        List<TemplateForm> savedForm = templateFormService.addFieldToTemplate(userId, menuId, templateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
    }

    /**
     * Remove a field from user's template
     * DELETE /api/template-forms/remove?userId=1&menuId=2&fieldId=3
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFieldFromTemplate(
            @RequestBody List<String> keys
    ) {
        templateFormService.removeFieldFromTemplate(keys);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all fields for a user's template
     * GET /api/template-forms?userId=1&menuId=2
     */
    @GetMapping
    public ResponseEntity<List<TemplateForm>> getTemplateFields(
            @RequestParam Long userId,
            @RequestParam Long menuId
    ) {
        List<TemplateForm> fields = templateFormService.getTemplateFields(userId, menuId);
        return ResponseEntity.ok(fields);
    }

    /**
     * Update an existing field in user's template
     * PUT /api/template-forms/update?userId=1&menuId=2&fieldId=3
     */
    @PutMapping("/update")
    public ResponseEntity<TemplateForm> updateField(
            @RequestParam Long userId,
            @RequestParam Long menuId,
            @RequestParam Long fieldId,
            @Valid @RequestBody TemplateForm templateForm
    ) {
        TemplateForm updatedForm = templateFormService.updateField(userId, menuId, fieldId, templateForm);
        return ResponseEntity.ok(updatedForm);
    }
}
