package com.planitsquare.subject.domain.holiday._county.entity;

import com.planitsquare.subject.domain.holiday.entity.Holiday;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "holiday_counties",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_holiday_county",
                        columnNames = {"county_code", "holiday_id"}
                )
        }
)
@NoArgsConstructor
public class HolidayCounty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "county_code", nullable = false)
    private String countyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id")
    private Holiday holiday;

    private HolidayCounty(Holiday holiday, String countryCode) {
        this.holiday = holiday;
        this.countyCode = countryCode;
    }

    public static HolidayCounty of(Holiday holiday, String countryCode) {
        return new HolidayCounty(holiday, countryCode);
    }
}
