package com.planitsquare.subject.domain.holiday.controller;

import com.planitsquare.subject.domain.holiday.dto.HolidaySearchCondition;
import com.planitsquare.subject.domain.holiday.dto.request.UpdateHolidayRequest;
import com.planitsquare.subject.domain.holiday.dto.response.HolidayResponse;
import com.planitsquare.subject.domain.holiday.service.HolidayService;
import com.planitsquare.subject.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/holidays")
public class HolidayController {
    private final HolidayService holidayService;

    @GetMapping
    @Operation(summary = "조건별 공휴일 데이터 검색",
            description = "년도, 국가, from-to, 타입 별 조건을 걸어 조건에 맞는 공휴일 데이터를 검색합니다.")
    public ResponseEntity<ApiResponse<Page<HolidayResponse>>> search(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) String type,
            Pageable pageable
    ) {
        HolidaySearchCondition condition = new HolidaySearchCondition(year, countryCode, from, to, type);
        Page<HolidayResponse> responses = holidayService.searchHolidays(condition, pageable);
        return ApiResponse.ok(responses);
    }

    @PutMapping
    @Operation(summary = "공휴일 데이터 재동기화",
            description = "특정 연도, 특정 국가의 공휴일 데이터를 재동기화 합니다. 기존 데이터가 존재한다면 삭제하고 다시 받아와 저장합니다.")
    public ResponseEntity<ApiResponse<Integer>> refresh(
            @RequestBody UpdateHolidayRequest request
    ) {
        int response = holidayService.refresh(request);
        return ApiResponse.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "공휴일 데이터 삭제",
            description = "특정 연도, 특정 국가의 공휴일 데이터를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestBody UpdateHolidayRequest request
    ) {
        holidayService.deleteHolidays(request);
        return ApiResponse.ok();
    }
}
