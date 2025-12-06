package com.planitsquare.subject.domain.country.exception;

import com.planitsquare.subject.global.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class CountryNotFoundException extends ApiException {
    private static final String MESSAGE = "존재하지 않는 국가코드입니다.";

    public CountryNotFoundException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE, "400");
    }
}
