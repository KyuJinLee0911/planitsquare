package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.country.service.CountryReader;
import com.planitsquare.subject.domain.holiday._county.entity.HolidayCounty;
import com.planitsquare.subject.domain.holiday._county.service.HolidayCountyStore;
import com.planitsquare.subject.domain.holiday._type.entity.HolidayType;
import com.planitsquare.subject.domain.holiday._type.service.HolidayTypeStore;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import com.planitsquare.subject.domain.holiday.dto.HolidaySearchCondition;
import com.planitsquare.subject.domain.holiday.dto.request.UpdateHolidayRequest;
import com.planitsquare.subject.domain.holiday.dto.response.HolidayResponse;
import com.planitsquare.subject.domain.holiday.entity.Holiday;
import com.planitsquare.subject.global.common.utils.ExternalApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private final CountryReader countryReader;
    private final HolidayReader holidayReader;
    private final HolidayStore holidayStore;
    private final HolidayCountyStore holidayCountyStore;
    private final HolidayTypeStore holidayTypeStore;
    private final ExternalApiClient externalApiClient;

    @Transactional
    public Page<HolidayResponse> searchHolidays(HolidaySearchCondition condition, Pageable pageable) {
        return holidayReader.search(condition, pageable);
    }

    @Transactional
    public int refresh(UpdateHolidayRequest request) {
        String countryCode = request.countryCode();
        int year = request.year();

        Country country = countryReader.getCountry(countryCode);

        List<HolidayDTO> dtoList = externalApiClient.getHolidays(year, countryCode);

        if (dtoList == null || dtoList.isEmpty()) {
            throw new IllegalStateException(String.format("공휴일 데이터를 외부에서 가져오지 못했습니다. 국가 코드 : %s, 년도 : %d", countryCode, year));
        }

        // 기존에 존재하는 데이터 삭제
        long count = holidayReader.countExistingDataBetween(year, year, countryCode);
        if (count > 0) {
            holidayTypeStore.removeByCountryAndYear(country, year);
            holidayCountyStore.removeByCountryAndYear(country, year);
            holidayStore.removeByCountryAndYear(country, year);
        }

        // 새로 가져온 데이터 중복 제거 후 Entity 생성해서 저장
        int size = saveAllEntities(dtoList, country);
        return size;
    }

    @Transactional
    public void deleteHolidays(UpdateHolidayRequest request) {
        int year = request.year();
        String countryCode = request.countryCode();
        Country country = countryReader.getCountry(countryCode);

        long count = holidayReader.countExistingDataBetween(year, year, countryCode);
        if (count > 0) {
            holidayTypeStore.removeByCountryAndYear(country, year);
            holidayCountyStore.removeByCountryAndYear(country, year);
            holidayStore.removeByCountryAndYear(country, year);
        }
    }

    public int saveAllEntities(List<HolidayDTO> holidays, Country country) {
        Map<String, Holiday> holidayMap = new HashMap<>();
        List<HolidayType> holidayTypes = new ArrayList<>();
        List<HolidayCounty> holidayCounties = new ArrayList<>();

        Map<Holiday, Set<String>> typeCodesByHoliday = new HashMap<>();
        Map<Holiday, Set<String>> countyCodesByHoliday = new HashMap<>();
        for (HolidayDTO dto : holidays) {
            String key = dto.countryCode() + "|" + dto.date() + "|" + dto.name();
            Holiday holiday = holidayMap.get(key);
            if (holiday == null) {
                holiday = Holiday.from(dto, country);
                holidayMap.put(key, holiday);
            }

            if (dto.types() != null) {
                Set<String> typeSet = typeCodesByHoliday
                        .computeIfAbsent(holiday, h -> new LinkedHashSet<>());
                for (String typeCode : dto.types()) {
                    if (typeSet.add(typeCode)) {
                        holidayTypes.add(HolidayType.of(holiday, typeCode));
                    }
                }
            }

            if (dto.counties() != null) {
                Set<String> countySet = countyCodesByHoliday
                        .computeIfAbsent(holiday, h -> new LinkedHashSet<>());
                for (String countyCode : dto.counties()) {
                    if (countySet.add(countyCode)) {
                        holidayCounties.add(HolidayCounty.of(holiday, countyCode));
                    }
                }
            }
        }
        List<Holiday> holidayEntities = new ArrayList<>(holidayMap.values());

        holidayStore.saveAll(holidayEntities);
        holidayCountyStore.saveAll(holidayCounties);
        holidayTypeStore.saveAll(holidayTypes);

        return holidayEntities.size();
    }
}
