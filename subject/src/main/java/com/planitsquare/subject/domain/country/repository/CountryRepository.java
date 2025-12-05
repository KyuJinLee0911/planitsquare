package com.planitsquare.subject.domain.country.repository;

import com.planitsquare.subject.domain.country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
