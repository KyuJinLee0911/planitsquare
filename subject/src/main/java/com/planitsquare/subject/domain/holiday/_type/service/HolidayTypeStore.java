package com.planitsquare.subject.domain.holiday._type.service;

import com.planitsquare.subject.domain.holiday._type.entity.HolidayType;
import com.planitsquare.subject.domain.holiday._type.repository.HolidayTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayTypeStore {
    private final HolidayTypeRepository repository;

    public List<HolidayType> saveAll(List<HolidayType> holidayTypes) {
        return repository.saveAll(holidayTypes);
    }
}
