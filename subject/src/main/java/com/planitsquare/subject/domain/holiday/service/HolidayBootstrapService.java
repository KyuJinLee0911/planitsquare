package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.country.dto.CountryDTO;
import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.country.service.CountryStore;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import com.planitsquare.subject.global.common.utils.ExternalApiClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidayBootstrapService {
    private final ExternalApiClient externalApiClient;
    private final HolidayReader holidayReader;
    private final CountryStore countryStore;
    private final HolidayService holidayService;

    @Transactional
    public void bootstrap(int from, int to) {
        log.info("[HolidayBootstrap] {}년부터 {}까지 공휴일 데이터 적재 시작", from, to);

        List<CountryDTO> countries = externalApiClient.getCountries();
        List<Country> countryEntities = countries.stream()
                .map(dto -> Country.from(dto)).toList();
        countryStore.saveAll(countryEntities);
        log.info("[HolidayBootstrap] 국가 목록 받아오기 완료 : {}개 국가", countries.size());

        int totalInserted = 0;
        List<String> failedTargets = new ArrayList<>();

        for (Country country : countryEntities) {

            for (int year = from; year <= to; year++) {
                try {
                    int inserted = getAndSavePerCountryYear(year, country);
                    totalInserted += inserted;

                    Thread.sleep(150L);
                } catch (InterruptedException e) {
                    log.error("[HolidayBootstrap] {} 국가의 {}년도 공휴일 정보를 받아오는 데 오류가 발생했습니다. error = {}"
                            , country.getName(), year, e.getMessage());
                    failedTargets.add(country.getCountryCode() + "-" + year);
                }
            }
        }

        log.info("[HolidayBootstrap] 공휴일 데이터 받아오기 완료. {}개의 데이터가 추가되었습니다.", totalInserted);
        if (!failedTargets.isEmpty()) {
            log.warn("[HolidayBootstrap] 불러오기에 실패한 목록 : {}", failedTargets);
        }
    }

    private int getAndSavePerCountryYear(int year, Country country) {
        long existingCount = holidayReader.countExistingDataBetween(year, year, country.getCountryCode());
        if (existingCount > 0) {
            log.info("[HolidayBootstrap] {} 국가의 {}년도의 데이터가 이미 존재하여 생략합니다. ({})",
                    country.getName(), year, existingCount);
            return 0;
        }

        List<HolidayDTO> holidays = retryGetHolidays(country.getCountryCode(), year, 3);
        if (holidays.isEmpty()) {
            log.warn("[HolidayBootstrap] {}국가의 {}년도 공휴일 목록이 비어있습니다.",
                    country.getName(), year);
            return 0;
        }

        int size = holidayService.saveAllEntities(holidays, country);

        log.info("[HolidayBootstrap] {}국가의 {}년도 {}개의 공휴일 데이터가 저장되었습니다.", country.getName(), year, size);
        return size;
    }


    private List<HolidayDTO> retryGetHolidays(String countryCode, int year, int maxAttempt) {
        for (int attempt = 1; attempt <= maxAttempt; attempt++) {
            List<HolidayDTO> result = externalApiClient.getHolidays(year, countryCode);
            if (!result.isEmpty()) {
                return result;
            }

            log.warn("[HolidayBootstrap] {}국가의 {}년도 공휴일 목록이 비어있습니다. {}/{}",
                    countryCode, year, attempt, maxAttempt);

            try {
                Thread.sleep(300L * attempt);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return List.of();
            }
        }
        return List.of();
    }
}
