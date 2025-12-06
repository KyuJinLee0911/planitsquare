package com.planitsquare.subject.domain.holiday.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planitsquare.subject.domain.country.dto.CountryDTO;
import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.country.service.CountryStore;
import com.planitsquare.subject.domain.holiday._county.entity.HolidayCounty;
import com.planitsquare.subject.domain.holiday._county.service.HolidayCountyReader;
import com.planitsquare.subject.domain.holiday._county.service.HolidayCountyStore;
import com.planitsquare.subject.domain.holiday._type.entity.HolidayType;
import com.planitsquare.subject.domain.holiday._type.service.HolidayTypeReader;
import com.planitsquare.subject.domain.holiday._type.service.HolidayTypeStore;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import com.planitsquare.subject.domain.holiday.dto.request.UpdateHolidayRequest;
import com.planitsquare.subject.domain.holiday.entity.Holiday;
import com.planitsquare.subject.domain.holiday.service.HolidayReader;
import com.planitsquare.subject.domain.holiday.service.HolidayStore;
import com.planitsquare.subject.global.common.utils.ExternalApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class HolidayControllerE2ETest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CountryStore countryStore;

    @Autowired
    HolidayStore holidayStore;
    @Autowired
    HolidayReader holidayReader;

    @Autowired
    HolidayTypeStore holidayTypeStore;
    @Autowired
    HolidayTypeReader holidayTypeReader;

    @Autowired
    HolidayCountyStore holidayCountyStore;
    @Autowired
    HolidayCountyReader holidayCountyReader;

    @MockitoBean
    ExternalApiClient externalApiClient;

    private Country country;

    @BeforeEach
    void setUp() {
        holidayCountyStore.removeAllInBatch();
        holidayTypeStore.removeAllInBatch();
        holidayStore.removeAllInBatch();

        CountryDTO krDTO = new CountryDTO("KR", "Korea");
        CountryDTO usDTO = new CountryDTO("US", "United States");
        Country kr = Country.from(krDTO);
        Country us = Country.from(usDTO);
        country = kr;
        countryStore.saveAll(List.of(kr, us));

        HolidayDTO krNewYearDTO = new HolidayDTO(
                LocalDate.of(2025, 1, 1),
                "새해",
                "New Year's Day",
                kr.getCountryCode(),
                true,
                true,
                List.of("KR-11"),
                null,
                List.of("Public", "Bank")
        );

        HolidayDTO usNewYearDTO = new HolidayDTO(
                LocalDate.of(2025, 1, 1),
                "New Year's Day",
                "New Year's Day",
                us.getCountryCode(),
                true,
                true,
                null,
                null,
                List.of("Public")
        );

        Holiday krNewYear = Holiday.from(krNewYearDTO, kr);
        Holiday usNewYear = Holiday.from(usNewYearDTO, us);

        holidayStore.saveAll(List.of(krNewYear, usNewYear));
        holidayTypeStore.saveAll(
                List.of(
                        HolidayType.of(krNewYear, "Public"),
                        HolidayType.of(krNewYear, "Bank"),
                        HolidayType.of(usNewYear, "Public")
                )
        );
        holidayCountyStore.saveAll(List.of(HolidayCounty.of(krNewYear, "KR-11")));
    }

    @Nested
    @DisplayName("공휴일 검색 API (/api/holidays)")
    class SearchApi {
        @Test
        void 연도와_국가로_HTTP_요청하면_JSON_페이지_응답을_받는다() throws Exception {
            mockMvc.perform(get("/api/holidays")
                            .param("year", "2025")
                            .param("countryCode", "KR")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(1)))
                    .andExpect(jsonPath("$.data.content[0].countryCode").value("KR"))
                    .andExpect(jsonPath("$.data.content[0].date").value("2025-01-01"))
                    .andExpect(jsonPath("$.data.content[0].types", containsInAnyOrder("Public", "Bank")))
                    .andExpect(jsonPath("$.data.content[0].counties", contains("KR-11")))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.number").value(0))
                    .andExpect(jsonPath("$.data.size").value(10));
        }

        @Test
        void type_파라미터로_public만_검색하면_public을_포함하는_공휴일만_반환된다() throws Exception {
            mockMvc.perform(get("/api/holidays")
                            .param("type", "Public")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(2)))
                    .andExpect(jsonPath("$.data.content[*].types", everyItem(hasItem("Public"))));
        }
    }

    @Nested
    @DisplayName("공휴일 재동기화 API Post(/api/holidays)")
    class RefreshApi {
        @Test
        void POST_api_holidays_기존_데이터_삭제후_외부데이터로_재동기화() throws Exception {
            UpdateHolidayRequest request = new UpdateHolidayRequest(2025, "KR");
            List<HolidayDTO> externalDtos = List.of(
                    new HolidayDTO(LocalDate.of(2025, 1, 1),
                            "새 새해",
                            "New New Year's Day",
                            "KR",
                            true,
                            true,
                            List.of("KR-11"),
                            null,
                            List.of("Public", "Bank")
                    ),
                    new HolidayDTO(LocalDate.of(2025, 1, 1),
                            "새 새해",
                            "New New Year's Day",
                            "KR",
                            true,
                            true,
                            List.of("KR-11"),
                            null,
                            List.of("Public")
                    )
            );
            when(externalApiClient.getHolidays(2025, "KR")).thenReturn(externalDtos);
            MvcResult result = mockMvc.perform(post("/api/holidays")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn();

            List<Holiday> holidays = holidayReader.getByCountryAndYear(country, 2025);
            assertThat(holidays).hasSize(1);
            Holiday newHoliday = holidays.get(0);
            assertThat(newHoliday.getName()).isEqualTo("New New Year's Day");

            List<HolidayType> types = holidayTypeReader.getAllTypes(List.of(newHoliday.getId()));
            assertThat(types).extracting(HolidayType::getTypeCode)
                    .containsExactlyInAnyOrder("Public", "Bank");

            List<HolidayCounty> counties = holidayCountyReader.getAllCounties(List.of(newHoliday.getId()));
            assertThat(counties).extracting(HolidayCounty::getCountyCode)
                    .containsExactly("KR-11");
        }
    }
}
