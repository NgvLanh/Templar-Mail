package org.me.main.repo;

import org.me.main.model.EmailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepo extends JpaRepository<EmailTemplate, Long> {
    Page<EmailTemplate> findAllByOrderByIdDesc(Pageable pageable);
    Page<EmailTemplate> findByNameContainingIgnoreCaseOrderByIdDesc(Pageable pageable, String name);

}
