package org.me.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.me.main.dto.res.TemplateRes;
import org.me.main.model.EmailTemplate;

@Mapper(componentModel = "spring")
public interface TemplateMapper {
    @Mapping(target = "numUses",
            expression = "java(template.getSchedules() != null ? template.getSchedules().size() : 0)")
    TemplateRes toRes(EmailTemplate template);
}
