package org.me.main.model._enum;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("Hoạt động"),
    INACTIVE("Ngưng hoạt động");

    private final String display;

    Status(String display) {
        this.display = display;
    }
}
