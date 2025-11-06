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
import merchant_manager.models.TemplateForm;
import merchant_manager.service.implementation.TemplateFormServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/template-forms")
@Tag(name = "Template Form", description = "APIs for managing template forms")
@SecurityRequirement(name = "bearerAuth")
public class TemplateFormController {

    private final TemplateFormServiceImp templateFormService;

    public TemplateFormController(TemplateFormServiceImp templateFormService) {
        this.templateFormService = templateFormService;
    }

    @PostMapping("/add")
    @Operation(
            summary = "Add new fields to template",
            description = "Adds new fields to a user's template for a specific menu"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fields successfully added",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TemplateForm.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TemplateForm>> addFieldToTemplate(
            @Parameter(description = "Menu ID for the template", required = true)
            @RequestParam Long menuId,
            @Parameter(description = "List of template form fields to add", required = true)
            @Valid @RequestBody List<TemplateForm> templateForm
    ) {
        List<TemplateForm> savedForm = templateFormService.addFieldToTemplate(menuId, templateForm);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
    }

    @DeleteMapping("/remove")
    @Operation(
            summary = "Remove fields from template",
            description = "Removes specified fields from a user's template using their keys"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fields successfully removed"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Field not found")
    })
    public ResponseEntity<Void> removeFieldFromTemplate(
            @Parameter(description = "List of field keys to remove", required = true)
            @RequestBody List<String> keys
    ) {
        templateFormService.removeFieldFromTemplate(keys);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(
            summary = "Get template fields",
            description = "Retrieves all fields for a user's template for a specific menu"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved template fields",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TemplateForm.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<List<TemplateForm>> getTemplateFields(
            @Parameter(description = "Menu ID for the template", required = true)
            @RequestParam Long menuId
    ) {
        List<TemplateForm> fields = templateFormService.getTemplateFields(menuId);
        return ResponseEntity.ok(fields);
    }
}
