package com.planitsquare.subject.domain.holiday.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record HolidayResponse(
        @Schema(description = "공휴일 id", example = "1")
        Long holidayId,
        @Schema(description = "날짜", example = "2025-08-15")
        LocalDate date,
        @Schema(description = "공휴일 명칭", example = "Independent Day")
        String name,
        @Schema(description = "지역에서의 공휴일 명칭", example = "광복절")
        String localName,
        @Schema(description = "국가 코드", example = "KR")
        String countryCode,
        @Schema(description = "도입 년도", example = "1949")
        Integer launchYear,
        @Schema(description = "지역 목록", example = "[\"KR-11\"]")

        List<String> counties,
        @Schema(description = "공휴일 타입", example = "[\"Public\"]")
        List<String> types
) {
}
