package com.planitsquare.subject.domain.country.service;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.country.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryStore {
    private final CountryRepository countryRepository;

    public List<Country> saveAll(List<Country> countries) {
        return countryRepository.saveAll(countries);
    }
}
