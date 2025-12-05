package com.planitsquare.subject.global.common.utils;

import com.planitsquare.subject.domain.country.dto.CountryDTO;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExternalApiClient {
    private final WebClient webClient;

    public List<HolidayDTO> getHolidays(int year, String countryCode) {
        log.info("[Nager] 공휴일 받아오는 중 : 년도 = {}, 국가 = {}", year, countryCode);
        return webClient.get()
                .uri("/PublicHolidays/{year}/{country}", year, countryCode)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(HolidayDTO.class)
                .collectList()
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(ex -> {
                    log.error("[Nager] 공휴일을 받아오는 도중 문제가 생겼습니다 {}/{} : {}"
                            , countryCode, year, ex.getMessage());
                    return Mono.empty();
                })
                .block();
    }

    public List<CountryDTO> getCountries() {
        log.info("[Nager] 국가 목록 받아오는 중...");
        return webClient.get()
                .uri("/AvailableCountries")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(CountryDTO.class)
                .collectList()
                .timeout(Duration.ofSeconds(10))
                .block();
    }

}
