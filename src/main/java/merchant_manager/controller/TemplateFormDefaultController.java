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
import lombok.RequiredArgsConstructor;
import merchant_manager.models.DTO.ConfigDTO;
import merchant_manager.models.TemplateFormDefault;
import merchant_manager.service.implementation.TemplateFormDefaultServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/template-forms-default")
@Tag(name = "Template Form Default", description = "APIs for managing default template form columns (grid headers)")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class TemplateFormDefaultController {

    private final TemplateFormDefaultServiceImp templateFormDefaultService;

    @GetMapping("/menu/{menuId}/{companyId}")
    @Operation(
            summary = "Get column definitions for template",
            description = "Retrieves all column definitions (table headers) for a specific template. " +
                         "These columns define the structure of the grid/table. Use this once and cache on frontend."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Columns successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TemplateFormDefault.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<List<TemplateFormDefault>> getColumnsByMenuId(
            @Parameter(description = "Menu ID", required = true)
            @PathVariable Long menuId,
            @Parameter(description = "Company ID", required = true)
            @PathVariable Long companyId
    ) {
        List<TemplateFormDefault> columns = templateFormDefaultService.getColumnsByMenuIdAndCompanyId(menuId, companyId);
        return ResponseEntity.ok(columns);
    }

    @GetMapping("/column/{key}")
    @Operation(
            summary = "Get column by key",
            description = "Retrieves a specific column definition by its unique key"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Column successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TemplateFormDefault.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Column not found")
    })
    public ResponseEntity<TemplateFormDefault> getColumnByKey(
            @Parameter(description = "Column key", required = true)
            @PathVariable String key
    ) {
        TemplateFormDefault column = templateFormDefaultService.getByKey(key);
        return ResponseEntity.ok(column);
    }

    @PostMapping("/add")
    @Operation(
            summary = "Add fields to default template",
            description = "Adds new column definitions to a default template for a specific menu. " +
                         "This operation is restricted to SUPERUSER role only. " +
                         "These columns will define the default structure for all users accessing this menu."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fields successfully added to default template",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TemplateFormDefault.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not a SUPERUSER"),
            @ApiResponse(responseCode = "404", description = "Template not found for the given menu")
    })
    public ResponseEntity<List<TemplateFormDefault>> addFieldToDefaultTemplate(
            @Parameter(description = "Menu ID for the default template", required = true)
            @RequestParam Long menuId,
            @Parameter(description = "Company ID for the default template", required = true)
            @RequestParam Long companyId,
            @Parameter(description = "List of column definitions to add to the default template", required = true)
            @Valid @RequestBody List<TemplateFormDefault> templateFormDefaults
    ) {
        List<TemplateFormDefault> savedForms = templateFormDefaultService.addFieldToDefaultTemplate(menuId, companyId,templateFormDefaults);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedForms);
    }

    @DeleteMapping("/remove")
    @Operation(
            summary = "Remove fields from default template",
            description = "Removes column definitions from a default template by their keys. " +
                         "This operation is restricted to SUPERUSER role only. " +
                         "The fields will be permanently deleted from the default template structure."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fields successfully removed from default template"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not a SUPERUSER"),
            @ApiResponse(responseCode = "404", description = "One or more fields not found with the provided keys")
    })
    public ResponseEntity<Void> removeFieldFromTemplate(
            @Parameter(description = "List of column keys to remove from the default template", required = true)
            @RequestBody List<String> keys
    ) {
        templateFormDefaultService.removeFieldFromTemplate(keys);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/config")
    @Operation(
            summary = "Get Config"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Columns successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TemplateFormDefault.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<ConfigDTO> getConfig(
            @Parameter(description = "Company ID", required = true)
            @RequestParam Long companyId
    ) {
        ConfigDTO dto = templateFormDefaultService.getConfig(companyId);
        return ResponseEntity.ok(dto);
    }

}
