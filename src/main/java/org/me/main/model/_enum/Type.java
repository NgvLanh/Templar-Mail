package org.me.main.model._enum;

import lombok.Getter;

@Getter
public enum Type {
    DAILY("Hàng ngày"),
    WEEKLY("Hàng tuần"),
    MONTHLY("Hàng tháng"),
    CRON("Biểu thức CRON tùy chỉnh");

    private final String display;

    Type(String display) {
        this.display = display;
    }
}
