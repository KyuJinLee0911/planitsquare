package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday.dto.HolidaySearchCondition;
import com.planitsquare.subject.domain.holiday.dto.response.HolidayResponse;
import com.planitsquare.subject.domain.holiday.entity.Holiday;
import com.planitsquare.subject.domain.holiday.exception.HolidayNotFoundException;
import com.planitsquare.subject.domain.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayReader {
    private final HolidayRepository holidayRepository;

    public long countExistingDataBetween(int from, int to, String countryCode) {
        LocalDate start = LocalDate.of(from, 1, 1);
        LocalDate end = LocalDate.of(to, 12, 31);
        return holidayRepository.countByCountry_CountryCodeAndDateBetween(countryCode, start, end);
    }

    public long countEveryData() {
        return holidayRepository.count();
    }

    public Page<HolidayResponse> search(HolidaySearchCondition condition, Pageable pageable) {
        Page<HolidayResponse> searchResult = holidayRepository.search(condition, pageable);
        if (searchResult.isEmpty()) {
            throw new HolidayNotFoundException();
        }
        return searchResult;
    }

    public List<Holiday> getByCountryAndYear(Country country, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<Holiday> holidays = holidayRepository.findByCountryAndDateBetween(country, start, end);
        if (holidays == null || holidays.isEmpty()) {
            throw new HolidayNotFoundException();
        }
        return holidays;
    }
}
