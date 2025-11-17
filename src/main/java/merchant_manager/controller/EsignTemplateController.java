package merchant_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import merchant_manager.models.EsignTemplate;
import merchant_manager.service.implementation.EsignTemplateServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/esign-templates")
@Tag(name = "E-Signature Templates", description = "APIs for managing e-signature document templates")
public class EsignTemplateController {

    private final EsignTemplateServiceImp esignTemplateService;
    private final ObjectMapper objectMapper;

    public EsignTemplateController(EsignTemplateServiceImp esignTemplateService) {
        this.esignTemplateService = esignTemplateService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create template with file", description = "Create a new e-signature template with document file upload")
    public ResponseEntity<EsignTemplate> createTemplate(
            @RequestPart("file") MultipartFile file,
            @RequestPart("esignTemplate") EsignTemplate esignTemplate){

            EsignTemplate created = esignTemplateService.createTemplate(esignTemplate, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID", description = "Retrieve an e-signature template by its ID")
    public ResponseEntity<EsignTemplate> getTemplateById(@PathVariable Long id) {
        EsignTemplate template = esignTemplateService.findById(id);
        return ResponseEntity.ok(template);
    }

//    @PostMapping("/save")
//    @Operation(summary = "Save template", description = "Save or update an e-signature template without file upload")
//    public ResponseEntity<EsignTemplate> saveTemplate(@RequestBody EsignTemplate esignTemplate) {
//        EsignTemplate saved = esignTemplateService.save(esignTemplate);
//        return ResponseEntity.ok(saved);
//    }
}
