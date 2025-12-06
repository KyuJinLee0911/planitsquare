package com.planitsquare.subject.domain.country.repository;

import com.planitsquare.subject.domain.country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByCountryCode(String countryCode);
}
