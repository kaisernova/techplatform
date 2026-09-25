package com.kaisernova.modelo.enums;

public enum CalendarRole {
    READER("reader"),
    WRITER("writer"),
    OWNER("owner"),
    FREE_BUSY_READER("freeBusyReader");

    private final String apiValue;

    CalendarRole(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }
}
