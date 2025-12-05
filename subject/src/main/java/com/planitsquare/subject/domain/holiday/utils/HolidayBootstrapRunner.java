package com.planitsquare.subject.domain.holiday.utils;

import com.planitsquare.subject.domain.holiday.service.HolidayBootstrapService;
import com.planitsquare.subject.domain.holiday.service.HolidayReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class HolidayBootstrapRunner implements CommandLineRunner {
    private final HolidayBootstrapService holidayBootstrapService;
    private final HolidayReader holidayReader;

    @Value("${app.holiday.bootstrap.enabled:false}")
    private boolean bootstrapEnabled;

    @Value("${app.holiday.years.from:2020}")
    private int fromYear;

    @Value("${app.holiday.years.to:2025}")
    private int toYear;

    @Override
    public void run(String... args) {
        if (!bootstrapEnabled) {
            log.info("[HolidayBootstrapRunner] Bootstrap이 비활성화 되어있습니다. 건너뜁니다.");
            return;
        }

        long count = holidayReader.countEveryData();
        if (count > 0) {
            log.info("[HolidayBootstrapRunner] 이미 {}개의 데이터가 존재합니다. 건너뜁니다.", count);
            return;
        }

        log.info("[HolidayBootstrapRunner] 초기 적재를 시작합니다...");
        holidayBootstrapService.bootstrap(fromYear, toYear);
        log.info("[HolidayBootstrapRunner] 초기 적재가 끝았습니다.");
    }
}
