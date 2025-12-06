package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.holiday.utils.HolidayBootstrapRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayBootstrapRunnerTest {
    @Mock
    HolidayBootstrapService holidayBootstrapService;
    @Mock
    HolidayReader holidayReader;

    HolidayBootstrapRunner runner;

    @BeforeEach
    void setUp() throws Exception {
        runner = new HolidayBootstrapRunner(holidayBootstrapService, holidayReader);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = HolidayBootstrapRunner.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(runner, value);
    }

    @Test
    void bootstrapEnabled_false면_아무것도_하지않는다() throws Exception {
        // given
        setField("bootstrapEnabled", false);

        // when
        runner.run();

        // then
        verifyNoInteractions(holidayReader, holidayBootstrapService);
    }

    @Test
    void 기존_데이터가_있으면_bootstrap을_실행하지_않는다() throws Exception {
        // given
        setField("bootstrapEnabled", true);
        setField("fromYear", 2020);
        setField("toYear", 2025);

        when(holidayReader.countEveryData())
                .thenReturn(100L);

        // when
        runner.run();

        // then
        verify(holidayReader).countEveryData();
        verifyNoMoreInteractions(holidayBootstrapService);
    }

    @Test
    void 기존_데이터가_없고_enabled_true면_bootstrap을_호출한다() throws Exception {
        // given
        setField("bootstrapEnabled", true);
        setField("fromYear", 2020);
        setField("toYear", 2025);

        when(holidayReader.countEveryData())
                .thenReturn(0L);

        // when
        runner.run();

        // then
        verify(holidayReader).countEveryData();
        verify(holidayBootstrapService).bootstrap(2020, 2025);
    }
}
