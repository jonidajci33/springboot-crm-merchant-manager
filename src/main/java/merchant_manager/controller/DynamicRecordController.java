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
import merchant_manager.models.DTO.DynamicRecordsPageDTO;
import merchant_manager.models.DTO.DynamicRecordsRequestDTO;
import merchant_manager.models.DTO.DynamicRecordsSimplePageDTO;
import merchant_manager.service.implementation.DynamicRecordServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dynamic-records")
@Tag(name = "Dynamic Records", description = "APIs for retrieving dynamic records with filtering and pagination. " +
                                             "For better performance, use the separate endpoints: " +
                                             "GET /api/template-forms-default/template/{id} for columns, " +
                                             "POST /api/dynamic-records/query-simple for records.")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class DynamicRecordController {

    private final DynamicRecordServiceImp dynamicRecordService;

    @PostMapping("/query-simple")
    @Operation(
            summary = "Query dynamic records (without columns)",
            description = "Retrieves dynamic records for a template with optional filtering, sorting, and pagination. " +
                         "This API transforms EAV (Entity-Attribute-Value) data into a grid format suitable for display. " +
                         "Fetch column definitions separately using GET /api/template-forms-default/template/{id} for better performance."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DynamicRecordsSimplePageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<DynamicRecordsSimplePageDTO> getDynamicRecordsSimple(
            @Parameter(description = "Request containing template ID, filters, and pagination settings", required = true)
            @Valid @RequestBody DynamicRecordsRequestDTO request
    ) {
        DynamicRecordsSimplePageDTO response = dynamicRecordService.getDynamicRecordsSimple(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/menu/{menuId}")
    @Operation(
            summary = "Query dynamic records by template ID (GET) - without columns",
            description = "Retrieves dynamic records for a template with pagination. Simple version without filters. " +
                         "Fetch columns separately using GET /api/template-forms-default/template/{id}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DynamicRecordsSimplePageDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Template not found")
    })
    public ResponseEntity<DynamicRecordsSimplePageDTO> getDynamicRecordsByMenuId(
            @Parameter(description = "Template ID", required = true)
            @PathVariable Long menuId,
            @Parameter(description = "Page number (0-based)", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", required = false)
            @RequestParam(defaultValue = "10") int size
    ) {
        DynamicRecordsRequestDTO request = new DynamicRecordsRequestDTO();
        request.setMenuId(menuId);
        request.setPage(page);
        request.setSize(size);

        DynamicRecordsSimplePageDTO response = dynamicRecordService.getDynamicRecordsSimple(request);
        return ResponseEntity.ok(response);
    }
}
