package com.planitsquare.subject.domain.country.entity;

import com.planitsquare.subject.domain.country.dto.CountryDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Table(name = "countries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Country {
    @Id
    @Comment("국가 코드")
    private String countryCode;

    @Comment("국가명")
    private String name;

    private Country(String countryCode, String name) {
        this.countryCode = countryCode;
        this.name = name;
    }

    public static Country from(CountryDTO dto) {
        return new Country(dto.countryCode(), dto.name());
    }
}
