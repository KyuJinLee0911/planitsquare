package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.country.dto.CountryDTO;
import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.country.service.CountryStore;
import com.planitsquare.subject.domain.holiday._county.service.HolidayCountyStore;
import com.planitsquare.subject.domain.holiday._type.service.HolidayTypeStore;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import com.planitsquare.subject.domain.holiday.entity.Holiday;
import com.planitsquare.subject.global.common.utils.ExternalApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayBootstrapServiceTest {
    @Mock
    ExternalApiClient externalApiClient;
    @Mock
    HolidayReader holidayReader;
    @Mock
    CountryStore countryStore;
    @Mock
    HolidayStore holidayStore;
    @Mock
    HolidayCountyStore holidayCountyStore;
    @Mock
    HolidayTypeStore holidayTypeStore;

    @InjectMocks
    HolidayBootstrapService holidayBootstrapService;

    @Test
    void 이미_데이터가_존재하면_해당_국가와_년도는_건너뛴다() {
        // given
        CountryDTO countryDTO = new CountryDTO("KR", "Korea");
        when(externalApiClient.getCountries()).thenReturn(List.of(countryDTO));

        Country country = Country.from(countryDTO);
        when(holidayReader.countExistingDataBetween(2020, 2020, "KR"))
                .thenReturn(10L);

        // when
        holidayBootstrapService.bootstrap(2020, 2020);

        // then
        verify(countryStore).saveAll(anyList());

        // 이미 존재해서 생략되므로 나머지 holiday관련 saveall은 안불리는게 맞음
        verify(holidayStore, never()).saveAll(anyList());
        verify(holidayTypeStore, never()).saveAll(anyList());
        verify(holidayCountyStore, never()).saveAll(anyList());
    }

    @Test
    void 공휴일_목록이_정상적으로_들어오면_엔티티를_저장한다() {
        // given
        CountryDTO countryDTO = new CountryDTO("KR", "Korea");
        when(externalApiClient.getCountries()).thenReturn(List.of(countryDTO));

        when(holidayReader.countExistingDataBetween(2020, 2020, "KR"))
                .thenReturn(0L); // 기존 저장된 데이터 없음

        HolidayDTO dto1 = new HolidayDTO(
                LocalDate.of(2020, 01, 01),
                "새해",
                "New Year's Day",
                "KR",
                false,
                true,
                null,
                null,
                List.of("Public")
        );

        when(externalApiClient.getHolidays(2020, "KR"))
                .thenReturn(List.of(dto1));

        // when
        holidayBootstrapService.bootstrap(2020, 2020);

        // then
        //공휴일 저장
        verify(holidayStore).saveAll(argThat(list -> {
            if (list.size() != 1) return false;
            Holiday h = list.get(0);
            return h.getDate().toString().equals("2020-01-01") &&
                    h.getCountry().getCountryCode().equals("KR") &&
                    h.getName().equals("New Year's Day");
        }));

        verify(holidayTypeStore).saveAll(argThat(list -> list.size() == 1));
        verify(holidayCountyStore).saveAll(argThat(list -> list.isEmpty()));
    }

    @Test
    void 같은_공휴일이_중복되어_들어와도_Holiday는_한번만_저장된다() {
        // given
        CountryDTO countryDTO = new CountryDTO("KR", "Korea");
        when(externalApiClient.getCountries()).thenReturn(List.of(countryDTO));

        when(holidayReader.countExistingDataBetween(2020, 2020, "KR"))
                .thenReturn(0L); // 기존 저장된 데이터 없음

        HolidayDTO dto1 = new HolidayDTO(
                LocalDate.of(2020, 01, 01),
                "새해",
                "New Year's Day",
                "KR",
                false,
                true,
                List.of("KR-11"),
                null,
                List.of("Public")
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

        when(externalApiClient.getHolidays(2020, "KR"))
                .thenReturn(List.of(dto1, dto2));

        // when
        holidayBootstrapService.bootstrap(2020, 2020);

        // then
        // holiday는 한 개만
        verify(holidayStore).saveAll(argThat(list -> list.size() == 1));
        // county는 두 개
        verify(holidayCountyStore).saveAll(argThat(list -> list.size() == 2));

        // type은 한 개
        verify(holidayTypeStore).saveAll(argThat(list -> list.size() == 1));
    }

    @Test
    void 공휴일_API가_빈_리스트만_주면_저장하지_않고_로그만_찍는다() {
        // given
        CountryDTO countryDTO = new CountryDTO("KR", "Korea");
        when(externalApiClient.getCountries()).thenReturn(List.of(countryDTO));

        when(holidayReader.countExistingDataBetween(2020, 2020, "KR"))
                .thenReturn(0L);

        when(externalApiClient.getHolidays(2020, "KR")).thenReturn(List.of());

        // when
        holidayBootstrapService.bootstrap(2020, 2020);

        // then
        verify(holidayStore, never()).saveAll(anyList());
        verify(holidayCountyStore, never()).saveAll(anyList());
        verify(holidayTypeStore, never()).saveAll(anyList());
    }

    @Test
    void retryGetHolidays_첫번째는_빈리스트_두번째에서_성공하면_정상저장된다() {
        // given
        CountryDTO countryDTO = new CountryDTO("KR", "Korea");
        when(externalApiClient.getCountries()).thenReturn(List.of(countryDTO));

        when(holidayReader.countExistingDataBetween(2020, 2020, "KR"))
                .thenReturn(0L); // 기존 저장된 데이터 없음

        HolidayDTO dto1 = new HolidayDTO(
                LocalDate.of(2020, 01, 01),
                "새해",
                "New Year's Day",
                "KR",
                false,
                true,
                List.of("KR-11"),
                null,
                List.of("Public")
        );

        when(externalApiClient.getHolidays(2020, "KR"))
                .thenReturn(List.of())
                .thenReturn(List.of(dto1));

        // when
        holidayBootstrapService.bootstrap(2020, 2020);

        // then
        verify(externalApiClient, atLeast(2)).getHolidays(2020, "KR");
        verify(holidayStore).saveAll(anyList());
    }
}
