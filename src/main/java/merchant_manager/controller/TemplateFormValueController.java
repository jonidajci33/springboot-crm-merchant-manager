package merchant_manager.controller;

import jakarta.validation.Valid;
import merchant_manager.models.DTO.AddValueRequest;
import merchant_manager.models.TemplateForm;
import merchant_manager.service.implementation.TemplateFormServiceImp;
import merchant_manager.service.implementation.TemplateFormValueServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/template-forms-value")
public class TemplateFormValueController {

    private final TemplateFormValueServiceImp templateFormValueServiceImp;

    public TemplateFormValueController(TemplateFormValueServiceImp templateFormValueServiceImp) {
        this.templateFormValueServiceImp = templateFormValueServiceImp;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addFieldToTemplate(
            @RequestParam Long menuId,
            @RequestParam(required = false) Long recordId,
            @Valid @RequestBody List<AddValueRequest> addValueRequests
    ) {
        templateFormValueServiceImp.addValuesToForm(menuId, recordId, addValueRequests);
        return ResponseEntity.noContent().build();
    }
}

