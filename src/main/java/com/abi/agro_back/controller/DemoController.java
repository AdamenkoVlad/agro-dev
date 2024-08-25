package com.abi.agro_back.controller;

import com.abi.agro_back.collection.Agrarian;
import com.abi.agro_back.collection.DemoConfig;
import com.abi.agro_back.collection.SortField;
import com.abi.agro_back.collection.VillageCouncil;
import com.abi.agro_back.repository.DemoRepository;
import com.abi.agro_back.service.AgrarianService;
import com.abi.agro_back.service.DemoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/demo")
@Tag(name = "Demo", description = "the Demo Endpoint")
public class DemoController {

    @Autowired
    private AgrarianService agrarianService;
    @Autowired
    private DemoService demoService;
    @Autowired
    DemoRepository demoRepository;

    @PostMapping("/admin/set")
    public ResponseEntity<DemoConfig> setConfig(@RequestBody DemoConfig demoConfig) {
        demoRepository.deleteAll();
        return new ResponseEntity<>(demoRepository.save(demoConfig), HttpStatus.CREATED);
    }
    @GetMapping("/get")
    public ResponseEntity<DemoConfig> getConfig() {

        return new ResponseEntity<>(demoRepository.findAll().get(0), HttpStatus.CREATED);
    }

    @GetMapping("/agrarians")
    public Page<Agrarian> getAllAgrariansByRegion(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int sizePerPage,
                                                  @RequestParam(defaultValue = "START_DATE") SortField sortField,
                                                  @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection,
                                                  @RequestParam(required = false) String title,
                                                  @RequestParam(required = false) String services,
                                                  @RequestParam(required = false) String sells,
                                                  @RequestParam(required = false) String head) {
        return demoService.getAllAgrariansByRegion(PageRequest.of(page, sizePerPage, sortDirection, sortField.getDatabaseFieldName()), title, services, sells, head);
    }

    @GetMapping("/allAgrarians")
    public List<Agrarian> getAllDemoAgrarians() {
        return demoService.getAllDemoAgrarians();
    }

    @GetMapping("/excelAgrarians")
    public List<Agrarian> getExcelDemoAgrarians(@RequestParam("oblast") String oblast,
                                                @RequestParam("region") String region) {
        return demoService.getAllExcelAgrarians(oblast, region);
    }

    @GetMapping("/excelVillageCouncils")
    public List<VillageCouncil> getExcelDemoVillageCouncils(@RequestParam("oblast") String oblast,
                                                            @RequestParam("region") String region) {
        return demoService.getAllExcelVillageCouncils(oblast, region);
    }

    @GetMapping("/excelOblAgrarians")
    public List<Agrarian> getExcelOblAgrarians(@RequestParam("oblast") String oblast) {
        return demoService.getAllExcelOblAgrarians(oblast);
    }

    @GetMapping("/excelOblVillageCouncils")
    public List<VillageCouncil> getExcelOblVillageCouncils(@RequestParam("oblast") String oblast) {
        return demoService.getAllExcelOblVillageCouncils(oblast);
    }

    @GetMapping("/villageCouncils")
    public List<VillageCouncil> getAllVillageCouncilsByRegion(@RequestParam(required = false) String title,
                                                              @RequestParam(required = false) String services,
                                                              @RequestParam(required = false) String sells,
                                                              @RequestParam(required = false) String head) {
        return demoService.getAllVillageCouncilsByRegion(title, services, sells, head);
    }

    @GetMapping("/allVillageCouncils")
    public List<VillageCouncil> getAllDemoVillageCouncils() {
        return demoService.getAllDemoVillageCouncils();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
