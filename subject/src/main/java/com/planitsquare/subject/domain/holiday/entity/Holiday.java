package com.planitsquare.subject.domain.holiday.entity;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "holidays",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_holiday_country_date_name",
                        columnNames = {"country_code", "date", "name"}
                )
        }
)
@NoArgsConstructor
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("국가")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", nullable = false)
    private Country country;

    @Comment("날짜")
    @Column
    private LocalDate date;

    @Comment("공식 명칭")
    @Column
    private String name;

    @Comment("지역 명칭")
    @Column
    private String localName;

    @Comment("날짜 고정 여부")
    @Column
    private Boolean fixed;

    @Comment("국제 공휴일 여부")
    @Column
    private Boolean global;

    @Comment("도입 년도")
    @Column
    private Integer launch_year;

    private Holiday(Country country, LocalDate date, String name, String localName, Boolean fixed, Boolean global, Integer launch_year) {
        this.country = country;
        this.date = date;
        this.name = name;
        this.localName = localName;
        this.fixed = fixed;
        this.global = global;
        this.launch_year = launch_year;
    }

    public static Holiday from(HolidayDTO dto, Country country) {
        return new Holiday(country, dto.date(), dto.name(), dto.localName(), dto.fixed(), dto.global(), dto.launchYear());
    }


}
