package org.me.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.me.main.dto.req.TemplateReq;
import org.me.main.service._interface.ITemplateService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final ITemplateService templateService;

    @PostMapping
    private ResponseEntity<?> templateOptions(@Valid @RequestBody TemplateReq req) {
        return templateService.templateOptions(req);
    }

    @GetMapping
    private ResponseEntity<?> getTemplates(Pageable pageable,
                                           @RequestParam(required = false) String search) {
        return templateService.getTemplates(pageable,search);
    }

}
