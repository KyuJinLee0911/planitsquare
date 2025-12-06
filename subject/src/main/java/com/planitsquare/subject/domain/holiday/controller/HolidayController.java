package com.planitsquare.subject.domain.holiday.controller;

import com.planitsquare.subject.domain.holiday.dto.HolidaySearchCondition;
import com.planitsquare.subject.domain.holiday.dto.request.UpdateHolidayRequest;
import com.planitsquare.subject.domain.holiday.dto.response.HolidayResponse;
import com.planitsquare.subject.domain.holiday.service.HolidayService;
import com.planitsquare.subject.global.common.dto.ApiResponse;
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

    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> refresh(
            @RequestBody UpdateHolidayRequest request
    ) {
        int response = holidayService.refresh(request);
        return ApiResponse.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestBody UpdateHolidayRequest request
    ) {
        holidayService.deleteHolidays(request);
        return ApiResponse.ok();
    }
}
