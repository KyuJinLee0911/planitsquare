package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.country.dto.CountryDTO;
import com.planitsquare.subject.domain.holiday._county.service.HolidayCountyStore;
import com.planitsquare.subject.domain.holiday._type.service.HolidayTypeStore;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import com.planitsquare.subject.global.common.utils.ExternalApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class HolidayBootstrapServiceIntegrationTest {
    @Autowired
    HolidayBootstrapService holidayBootstrapService;

    @Autowired
    HolidayReader holidayReader;

    @Autowired
    HolidayStore holidayStore;
    @Autowired
    HolidayTypeStore holidayTypeStore;
    @Autowired
    HolidayCountyStore holidayCountyStore;

    @MockitoBean
    ExternalApiClient externalApiClient;

    @BeforeEach
    void cleanUp() {
        holidayStore.removeAll();
        holidayCountyStore.removeAll();
        holidayTypeStore.removeAll();
    }

    @Test
    void KR_2025년_통합_적재_테스트() {
        // given
        CountryDTO kr = new CountryDTO("KR", "Korea");
        when(externalApiClient.getCountries())
                .thenReturn(List.of(kr));

        HolidayDTO dto1 = new HolidayDTO(
                LocalDate.of(2025, 01, 01),
                "새해",
                "New Year's Day",
                "KR",
                false,
                true,
                null,
                null,
                List.of("Public")
        );

        HolidayDTO dto2 = new HolidayDTO(
                LocalDate.of(2025, 03, 01),
                "삼일절",
                "Independence Movement Day",
                "KR",
                false,
                true,
                null,
                null,
                List.of("Public")
        );
        when(externalApiClient.getHolidays(2025, "KR"))
                .thenReturn(List.of(dto1, dto2));

        // when
        holidayBootstrapService.bootstrap(2025, 2025);

        // then
        long count = holidayReader.countExistingDataBetween(2025, 2025, "KR");
        assertThat(count).isEqualTo(2L);

    }
}
