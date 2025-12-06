package com.planitsquare.subject.domain.country.service;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.country.exception.CountryNotFoundException;
import com.planitsquare.subject.domain.country.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryReader {
    private final CountryRepository countryRepository;

    public Country getCountry(String countryCode) {
        Country country = countryRepository.findByCountryCode(countryCode).orElseThrow(CountryNotFoundException::new);
        return country;
    }
}
