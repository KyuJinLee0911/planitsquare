package com.planitsquare.subject.domain.holiday.repository;

import com.planitsquare.subject.domain.holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    public long countByDateBetweenAndCountry_CountryCode(LocalDate start, LocalDate end, String countryCode);

}
