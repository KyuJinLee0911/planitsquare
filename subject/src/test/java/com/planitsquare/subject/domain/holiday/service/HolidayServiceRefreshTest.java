package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.country.dto.CountryDTO;
import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.country.service.CountryReader;
import com.planitsquare.subject.domain.holiday._county.service.HolidayCountyStore;
import com.planitsquare.subject.domain.holiday._type.service.HolidayTypeStore;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import com.planitsquare.subject.domain.holiday.dto.request.UpdateHolidayRequest;
import com.planitsquare.subject.domain.holiday.entity.Holiday;
import com.planitsquare.subject.global.common.utils.ExternalApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceRefreshTest {
    @Mock
    CountryReader countryReader;

    @Mock
    HolidayReader holidayReader;

    @Mock
    HolidayStore holidayStore;

    @Mock
    HolidayCountyStore holidayCountyStore;

    @Mock
    HolidayTypeStore holidayTypeStore;

    @Mock
    ExternalApiClient externalApiClient;

    @InjectMocks
    HolidayService holidayService;

    @Test
    void refresh_외부_API_결과_없으면_예외처리한다() {
        // given
        UpdateHolidayRequest request = new UpdateHolidayRequest(2025, "KR");
        CountryDTO countryDTO = new CountryDTO("KR", "Korea");
        Country country = Country.from(countryDTO);

        when(countryReader.getCountry("KR")).thenReturn(country);
        when(externalApiClient.getHolidays(2025, "KR")).thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> holidayService.refresh(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("공휴일 데이터를 외부에서 가져오지 못했습니다. 국가 코드 : %s, 년도 : %d", "KR", 2025));
    }

    @Test
    void refresh_기존_데이터_있으면_삭제후_saveAllEntities_호출한다() {
        // given
        UpdateHolidayRequest request = new UpdateHolidayRequest(2025, "KR");
        CountryDTO countryDTO = new CountryDTO("KR", "Korea");
        Country country = Country.from(countryDTO);
        HolidayDTO dto = new HolidayDTO(
                LocalDate.of(2025, 1, 1),
                "새해",
                "New Year's Day",
                "KR",
                true,
                true,
                List.of("KR-11"),
                null,
                List.of("Public")
        );
        List<HolidayDTO> apiResponse = List.of(dto);

        Holiday existing = Holiday.from(dto, country);

        when(countryReader.getCountry("KR")).thenReturn(country);
        when(externalApiClient.getHolidays(2025, "KR")).thenReturn(apiResponse);
        when(holidayReader.countExistingDataBetween(2025, 2025, "KR")).thenReturn(1L);

        HolidayService spyService = Mockito.spy(holidayService);
        doReturn(1).when(spyService).saveAllEntities(apiResponse, country);

        // when
        int result = spyService.refresh(request);

        // then
        verify(holidayReader).countExistingDataBetween(2025, 2025, "KR");

        verify(holidayTypeStore).removeByCountryAndYear(country, 2025);
        verify(holidayCountyStore).removeByCountryAndYear(country, 2025);
        verify(holidayStore).removeByCountryAndYear(country, 2025);

        verify(spyService).saveAllEntities(apiResponse, country);
        assertThat(result).isEqualTo(1);
    }

    @Test
    void refresh_기존_데이터_없으면_삭제없이_saveAllEntities만_호출한다() {
        // given
        UpdateHolidayRequest request = new UpdateHolidayRequest(2025, "KR");
        CountryDTO countryDTO = new CountryDTO("KR", "Korea");
        Country country = Country.from(countryDTO);
        HolidayDTO dto = new HolidayDTO(
                LocalDate.of(2025, 1, 1),
                "새해",
                "New Year's Day",
                "KR",
                true,
                true,
                null,
                null,
                List.of("Public")
        );
        List<HolidayDTO> apiResponse = List.of(dto);

        when(countryReader.getCountry("KR")).thenReturn(country);
        when(externalApiClient.getHolidays(2025, "KR")).thenReturn(apiResponse);

        HolidayService spyService = Mockito.spy(holidayService);
        doReturn(1).when(spyService).saveAllEntities(apiResponse, country);

        // when
        int result = spyService.refresh(request);

        // then
        verify(holidayReader).countExistingDataBetween(2025, 2025, "KR");

        verify(holidayTypeStore, never()).removeByCountryAndYear(any(), anyInt());
        verify(holidayCountyStore, never()).removeByCountryAndYear(any(), anyInt());
        verify(holidayStore, never()).removeByCountryAndYear(any(), anyInt());

        verify(spyService).saveAllEntities(apiResponse, country);
        assertThat(result).isEqualTo(1);
    }
}
