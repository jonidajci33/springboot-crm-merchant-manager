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
import merchant_manager.dto.GenericQueryRequestDTO;
import merchant_manager.dto.GenericQueryResponseDTO;
import merchant_manager.service.implementation.GenericQueryServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generic-data")
@Tag(name = "Generic Data", description = "Dynamic query endpoint for all entities with automatic user filtering and pagination")
@SecurityRequirement(name = "bearerAuth")
public class GenericDataController {

    private final GenericQueryServiceImp genericQueryService;

    public GenericDataController(GenericQueryServiceImp genericQueryService) {
        this.genericQueryService = genericQueryService;
    }

    @PostMapping("/query")
    @Operation(
            summary = "Execute dynamic query",
            description = "Query any entity with dynamic filters, sorting, and pagination. " +
                         "Automatically filters results by the logged-in user's ID (createdBy field). " +
                         "Supports multiple filter operators: EQUALS, NOT_EQUALS, CONTAINS, STARTS_WITH, " +
                         "ENDS_WITH, GREATER_THAN, LESS_THAN, IN, IS_NULL, IS_NOT_NULL"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Query executed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericQueryResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    public ResponseEntity<GenericQueryResponseDTO<Object>> executeQuery(
            @Parameter(description = "Query request with entity name, filters, and pagination", required = true)
            @Valid @RequestBody GenericQueryRequestDTO request
    ) {
        GenericQueryResponseDTO<Object> response = genericQueryService.executeQuery(request);
        return ResponseEntity.ok(response);
    }
}
