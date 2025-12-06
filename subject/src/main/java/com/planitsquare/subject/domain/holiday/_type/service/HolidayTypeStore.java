package com.planitsquare.subject.domain.holiday._type.service;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday._type.entity.HolidayType;
import com.planitsquare.subject.domain.holiday._type.repository.HolidayTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayTypeStore {
    private final HolidayTypeRepository repository;

    public List<HolidayType> saveAll(List<HolidayType> holidayTypes) {
        return repository.saveAll(holidayTypes);
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
