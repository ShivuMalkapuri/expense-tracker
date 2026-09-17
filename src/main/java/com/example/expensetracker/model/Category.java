package com.example.expensetracker.model;

public enum Category {
    FOOD("Food & Dining", "badge-food", "#ff6384"),
    TRANSPORTATION("Transportation", "badge-transport", "#36a2eb"),
    HOUSING("Housing & Rent", "badge-housing", "#cc65fe"),
    UTILITIES("Bills & Utilities", "badge-utilities", "#ffce56"),
    ENTERTAINMENT("Entertainment", "badge-entertainment", "#4bc0c0"),
    SHOPPING("Shopping", "badge-shopping", "#9966ff"),
    HEALTHCARE("Healthcare", "badge-healthcare", "#ff9f40"),
    EDUCATION("Education", "badge-education", "#c9cbcf"),
    SAVINGS("Savings & Investments", "badge-savings", "#2ecc71"),
    OTHER("Other Expenses", "badge-other", "#e74c3c");

    private final String displayName;
    private final String cssClass;
    private final String colorHex;

    Category(String displayName, String cssClass, String colorHex) {
        this.displayName = displayName;
        this.cssClass = cssClass;
        this.colorHex = colorHex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getColorHex() {
        return colorHex;
    }
}
