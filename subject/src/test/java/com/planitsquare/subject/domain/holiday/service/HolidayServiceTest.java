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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceTest {
    @Mock
    HolidayReader holidayReader;
    @Mock
    HolidayStore holidayStore;
    @Mock
    HolidayCountyStore holidayCountyStore;
    @Mock
    HolidayTypeStore holidayTypeStore;
    @Mock
    CountryReader countryReader;
    @Mock
    ExternalApiClient externalApiClient;

    @InjectMocks
    HolidayService holidayService;


    @Nested
    @DisplayName("중복 제거 후 저장")
    class dedupeAndSave {
        @Test
        void 같은_공휴일이_중복되어_들어와도_Holiday는_한번만_저장된다() {
            // given
            CountryDTO countryDTO = new CountryDTO("KR", "Korea");
            Country country = Country.from(countryDTO);

            HolidayDTO dto1 = new HolidayDTO(
                    LocalDate.of(2020, 01, 01),
                    "새해",
                    "New Year's Day",
                    "KR",
                    false,
                    true,
                    List.of("KR-11"),
                    null,
                    List.of("Public", "Bank")
            );

            HolidayDTO dto2 = new HolidayDTO(
                    LocalDate.of(2020, 01, 01),
                    "새해",
                    "New Year's Day",
                    "KR",
                    false,
                    true,
                    List.of("KR-12"),
                    null,
                    List.of("Public")
            );

            List<HolidayDTO> dtos = List.of(dto1, dto2);

            // when
            holidayService.saveAllEntities(dtos, country);

            // then
            verify(holidayStore).saveAll(argThat(list -> list.size() == 1));
            verify(holidayCountyStore).saveAll(argThat(list -> list.size() == 2));
            verify(holidayTypeStore).saveAll(argThat(list -> list.size() == 2));
        }
    }

    @Nested
    @DisplayName("데이터 재동기화 테스트")
    class refresh {
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

    @Nested
    @DisplayName("삭제 테스트")
    class delete {
        @Test
        void 특정연도와_특정국가의_데이터가_있으면_공휴일_데이터를_삭제한다() {
            // given
            UpdateHolidayRequest request = new UpdateHolidayRequest(2025, "KR");
            CountryDTO countryDTO = new CountryDTO("KR", "Korea");
            Country country = Country.from(countryDTO);

            when(countryReader.getCountry("KR")).thenReturn(country);
            when(holidayReader.countExistingDataBetween(2025, 2025, "KR")).thenReturn(1L);

            // when
            holidayService.deleteHolidays(request);

            // then
            verify(holidayReader).countExistingDataBetween(2025, 2025, "KR");

            verify(holidayTypeStore).removeByCountryAndYear(eq(country), eq(2025));
            verify(holidayCountyStore).removeByCountryAndYear(eq(country), eq(2025));
            verify(holidayStore).removeByCountryAndYear(eq(country), eq(2025));
        }

        @Test
        void 데이터가_없으면_삭제하지_않는다() {
            // given
            UpdateHolidayRequest request = new UpdateHolidayRequest(2025, "KR");
            CountryDTO countryDTO = new CountryDTO("KR", "Korea");
            Country country = Country.from(countryDTO);

            when(countryReader.getCountry("KR")).thenReturn(country);
            when(holidayReader.countExistingDataBetween(2025, 2025, "KR")).thenReturn(0L);

            // when
            holidayService.deleteHolidays(request);

            // then
            verify(holidayReader).countExistingDataBetween(2025, 2025, "KR");

            verify(holidayTypeStore, never()).removeByCountryAndYear(any(), anyInt());
            verify(holidayCountyStore, never()).removeByCountryAndYear(any(), anyInt());
            verify(holidayStore, never()).removeByCountryAndYear(any(), anyInt());
        }
    }
}
