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

    @GetMapping("/by-menu-and-record")
    @Operation(
            summary = "Get default form values by menu ID and record ID",
            description = "Retrieves all default form values for a specific menu and record combination"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved form values"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No values found")
    })
    public ResponseEntity<?> getValuesByMenuIdAndRecordId(
            @Parameter(description = "Menu ID", required = true)
            @RequestParam Long menuId,
            @Parameter(description = "Record ID", required = true)
            @RequestParam Long recordId
    ) {
        return ResponseEntity.ok(templateFormValueServiceImp.findByMenuIdAndRecordId(menuId, recordId));
    }

}
