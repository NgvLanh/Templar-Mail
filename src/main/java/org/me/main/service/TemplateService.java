package org.me.main.service;

import lombok.RequiredArgsConstructor;
import org.me.main.dto.req.TemplateReq;
import org.me.main.dto.res.ApiRes;
import org.me.main.mapper.TemplateMapper;
import org.me.main.model.EmailTemplate;
import org.me.main.repo.TemplateRepo;
import org.me.main.service._interface.ITemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateService implements ITemplateService {
    private final TemplateRepo templateRepo;
    private final TemplateMapper templateMapper;

    @Override
    public ResponseEntity<?> templateOptions(TemplateReq req) {
        String method = req.getMethod();
        try {
            switch (method) {
                case "1" -> {
                    EmailTemplate template = new EmailTemplate();
                    template.setName(req.getName());
                    template.setSubject(req.getSubject());
                    template.setBody(req.getBody());
                    templateRepo.save(template);
                }
                case "0" -> {
                    EmailTemplate template = templateRepo.findById(req.getId())
                            .orElseThrow(() -> new RuntimeException("Template không tồn tại"));
                    template.setName(req.getName());
                    template.setSubject(req.getSubject());
                    template.setBody(req.getBody());
                    templateRepo.save(template);
                }
                case "-1" -> {
                    if (!templateRepo.existsById(req.getId())) {
                        throw new RuntimeException("Template không tồn tại");
                    }
                    templateRepo.deleteById(req.getId());
                }
                default -> throw new RuntimeException("Method không hợp lệ");
            }

            return ResponseEntity.ok(ApiRes.success(req, "Thao tác thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiRes.error(e.getMessage(), ApiRes.ErrorCodes.INTERNAL_ERROR));
        }
    }

    @Override
    public ResponseEntity<?> getTemplates(Pageable pageable, String search) {
        Page<EmailTemplate> page;
        if (search != null && !search.isEmpty()) {
            page = templateRepo.findByNameContainingIgnoreCaseOrderByIdDesc(pageable, search);
        } else {
            page = templateRepo.findAllByOrderByIdDesc(pageable);
        }
        var result = Map.of(
                "content", page.getContent().stream().map(templateMapper::toRes).toList(),
                "pageNumber", page.getNumber(),
                "pageSize", page.getSize(),
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements()
        );
        return ResponseEntity.ok(ApiRes.success(result, "Tải dữ liệu thành công"));
    }
}
