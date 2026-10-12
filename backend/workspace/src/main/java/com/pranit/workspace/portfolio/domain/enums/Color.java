package com.pranit.workspace.portfolio.domain.enums;

import lombok.Getter;

@Getter
public enum Color {
    RED("#FF0000"),
    GREEN("#008000"),
    BLUE("#0000FF"),
    YELLOW("#FFFF00"),
    ORANGE("#FFA500"),
    PURPLE("#800080"),
    PINK("#FFC0CB"),
    BLACK("#000000"),
    WHITE("#FFFFFF"),
    GRAY("#808080"),
    BROWN("#A52A2A"),
    CYAN("#00FFFF"),
    MAGENTA("#FF00FF"),
    LIME("#00FF00"),
    NAVY("#000080"),
    GOLD("#FFD700"),
    SILVER("#C0C0C0"),
    TEAL("#008080"),
    MAROON("#800000"),
    OLIVE("#808000");

    private final String hexCode;

    Color(String hexCode) {
        this.hexCode = hexCode;
    }
}
