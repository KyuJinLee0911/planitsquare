package com.planitsquare.subject.domain.holiday._county.service;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday._county.entity.HolidayCounty;
import com.planitsquare.subject.domain.holiday._county.repository.HolidayCountyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayCountyStore {
    private final HolidayCountyRepository repository;

    public List<HolidayCounty> saveAll(List<HolidayCounty> holidayCounties) {
        return repository.saveAll(holidayCounties);
    }

    public void removeAll() {
        repository.deleteAll();
    }

    public void removeAllInBatch() {
        repository.deleteAllInBatch();
    }

    public void removeByCountryAndYear(Country country, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        repository.deleteByCountryAndDateBetween(country, start, end);
    }
}
