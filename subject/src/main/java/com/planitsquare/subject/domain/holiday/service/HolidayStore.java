package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.holiday.entity.Holiday;
import com.planitsquare.subject.domain.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayStore {
    private final HolidayRepository holidayRepository;

    public List<Holiday> saveAll(List<Holiday> holidayEntities) {
        return holidayRepository.saveAll(holidayEntities);
    }

    public void removeAll() {
        holidayRepository.deleteAll();
    }
}
