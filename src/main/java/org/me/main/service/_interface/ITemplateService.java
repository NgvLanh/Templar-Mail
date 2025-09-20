package org.me.main.service._interface;

import org.me.main.dto.req.TemplateReq;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface ITemplateService {
    ResponseEntity<?> templateOptions(TemplateReq req);

    ResponseEntity<?> getTemplates(Pageable pageable, String search);

}
