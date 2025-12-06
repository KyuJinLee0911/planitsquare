package com.planitsquare.subject.domain.holiday.dto.request;

public record UpdateHolidayRequest(
        Integer year,
        String countryCode
) {
}
