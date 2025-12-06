package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday.entity.Holiday;
import com.planitsquare.subject.domain.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayStore {
    private final HolidayRepository holidayRepository;

    public Holiday save(Holiday holiday) {
        return holidayRepository.save(holiday);
    }

    public List<Holiday> saveAll(List<Holiday> holidayEntities) {
        return holidayRepository.saveAll(holidayEntities);
    }

    public void removeAll() {
        holidayRepository.deleteAll();
    }

    public void removeAllInBatch() {
        holidayRepository.deleteAllInBatch();
    }

    public void removeByCountryAndYear(Country country, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        holidayRepository.deleteByCountryAndYear(country, start, end);
    }
}
