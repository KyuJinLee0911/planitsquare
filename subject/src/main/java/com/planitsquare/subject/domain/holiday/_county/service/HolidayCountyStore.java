package com.planitsquare.subject.domain.holiday._county.service;

import com.planitsquare.subject.domain.holiday._county.entity.HolidayCounty;
import com.planitsquare.subject.domain.holiday._county.repository.HolidayCountyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
