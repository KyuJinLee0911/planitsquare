package com.planitsquare.subject.domain.holiday.dto.request;

public record RefreshHolidayRequest(
        Integer year,
        String countryCode
) {
}
