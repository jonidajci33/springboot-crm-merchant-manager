package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Template Form Value", description = "APIs for managing template form values")
@SecurityRequirement(name = "bearerAuth")
public class TemplateFormValueController {

    private final TemplateFormValueServiceImp templateFormValueServiceImp;

    public TemplateFormValueController(TemplateFormValueServiceImp templateFormValueServiceImp) {
        this.templateFormValueServiceImp = templateFormValueServiceImp;
    }

    @PostMapping("/add")
    @Operation(
            summary = "Add values to template form",
            description = "Adds multiple values to a template form for a specific menu and optional record"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Values successfully added"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Template form not found")
    })
    public ResponseEntity<Void> addFieldToTemplate(
            @Parameter(description = "Menu ID for the template form", required = true)
            @RequestParam Long menuId,
            @Parameter(description = "Record ID (optional)", required = false)
            @RequestParam(required = false) Long recordId,
            @Parameter(description = "List of values to add to the form", required = true)
            @Valid @RequestBody List<AddValueRequest> addValueRequests
    ) {
        templateFormValueServiceImp.addValuesToForm(menuId, recordId, addValueRequests);
        return ResponseEntity.noContent().build();
    }
}

