package com.planitsquare.subject.domain.holiday._type.entity;

import com.planitsquare.subject.domain.holiday.entity.Holiday;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "holiday_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_holiday_types",
                        columnNames = {"type_code", "holiday_id"}
                )
        }
)
@NoArgsConstructor
public class HolidayType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_code", nullable = false, length = 50)
    private String typeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id")
    private Holiday holiday;

    private HolidayType(Holiday holiday, String typeCode) {
        this.holiday = holiday;
        this.typeCode = typeCode;
    }

    public static HolidayType of(Holiday holiday, String typeCode) {
        return new HolidayType(holiday, typeCode);
    }
}
