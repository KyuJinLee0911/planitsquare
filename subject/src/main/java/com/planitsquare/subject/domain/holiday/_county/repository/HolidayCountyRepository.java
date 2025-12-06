package com.planitsquare.subject.domain.holiday._county.repository;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday._county.entity.HolidayCounty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface HolidayCountyRepository extends JpaRepository<HolidayCounty, Long> {
    List<HolidayCounty> findAllByHoliday_IdIn(List<Long> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                DELETE FROM HolidayCounty hc
                WHERE hc.holiday.country = :country
                AND hc.holiday.date BETWEEN :start AND :end
            """)
    void deleteByCountryAndDateBetween(Country country, LocalDate start, LocalDate end);
}
