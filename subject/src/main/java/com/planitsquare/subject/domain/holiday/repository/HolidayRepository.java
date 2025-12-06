package com.planitsquare.subject.domain.holiday.repository;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {

    long countByCountry_CountryCodeAndDateBetween(String countryCode, LocalDate start, LocalDate end);

    List<Holiday> findByCountryAndDateBetween(Country country, LocalDate start, LocalDate end);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE from Holiday h
            WHERE h.country = :country
            and h.date between :start and :end
            """
    )
    void deleteByCountryAndYear(
            @Param("country") Country country,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
