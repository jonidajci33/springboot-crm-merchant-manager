package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import merchant_manager.models.Menu;
import merchant_manager.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@Tag(name = "Menu", description = "APIs for managing menus")
@SecurityRequirement(name = "bearerAuth")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Get all menus
     * Accessible by all authenticated users (ROLE_USER and ROLE_SUPERUSER)
     * GET /api/menus
     */
    @GetMapping
    @Operation(
            summary = "Get all menus",
            description = "Retrieves all available menus from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved menus"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<Menu>> getAllMenus() {
        List<Menu> menus = menuService.getMenus();
        return ResponseEntity.ok(menus);
    }
}
