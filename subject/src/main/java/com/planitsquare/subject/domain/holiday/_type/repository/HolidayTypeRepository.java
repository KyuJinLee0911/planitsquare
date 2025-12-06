package com.planitsquare.subject.domain.holiday._type.repository;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday._type.entity.HolidayType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface HolidayTypeRepository extends JpaRepository<HolidayType, Long> {
    List<HolidayType> findAllByHoliday_IdIn(List<Long> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                DELETE FROM HolidayType ht
                WHERE ht.holiday.country = :country
                AND ht.holiday.date BETWEEN :start AND :end
            """)
    void deleteByCountryAndDateBetween(Country country, LocalDate start, LocalDate end);
}
