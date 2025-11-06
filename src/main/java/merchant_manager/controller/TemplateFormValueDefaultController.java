package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import merchant_manager.models.DTO.AddValueRequest;
import merchant_manager.service.implementation.TemplateFormValueDefaultServiceImp;
import merchant_manager.service.implementation.TemplateFormValueServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/template-forms-value-default")
@Tag(name = "Template Form Value Default", description = "APIs for managing template form values")
@SecurityRequirement(name = "bearerAuth")
public class TemplateFormValueDefaultController {

    private final TemplateFormValueDefaultServiceImp templateFormValueServiceImp;

    public TemplateFormValueDefaultController(TemplateFormValueDefaultServiceImp templateFormValueServiceImp) {
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
    public ResponseEntity<Void> addValueToForm(
            @Parameter(description = "Menu ID for the template form", required = true)
            @RequestParam Long menuId,
            @Parameter(description = "Record ID (optional)", required = false)
            @RequestParam(required = false) Long recordId,
            @Parameter(description = "List of values to add to the form", required = true)
            @Valid @RequestBody List<AddValueRequest> addValueRequests
    ) {
        templateFormValueServiceImp.addDefaultValuesToForm(menuId, recordId, addValueRequests);
        return ResponseEntity.noContent().build();
    }

}
