package com.planitsquare.subject.domain.holiday.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateHolidayRequest(
        @Schema(description = "년도", example = "2025")
        Integer year,
        @Schema(description = "국가 코드", example = "KR")
        String countryCode
) {
}
