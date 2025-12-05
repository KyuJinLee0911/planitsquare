package com.planitsquare.subject.domain.holiday.dto;

import java.time.LocalDate;
import java.util.List;

public record HolidayDTO(
        LocalDate date,
        String localName,
        String name,
        String countryCode,
        Boolean fixed,
        Boolean global,
        List<String> counties,
        Integer launchYear,
        List<String> types
) {
}
