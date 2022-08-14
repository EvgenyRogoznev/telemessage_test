package tests;

import api.CheckNumberApi;
import dto.ResponseDto;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import utils.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static api.CheckNumberApi.getValidXSignature;
import static api.CheckNumberApi.getXDate;
import static utils.NumberGenerator.getRandomPhone;


public class CheckNumberTests {

    @ParameterizedTest
    @MethodSource("numbersResponses")
    public void checkNumbers(String mdn, String statusInfo, String type, Integer currentStatus){
       var response = new CheckNumberApi().checkNumbers(List.of(mdn));

       ResponseDto responseDto =response.extract().jsonPath().getList("", ResponseDto.class).get(0);
        Assertions.assertAll(
                ()->Assertions.assertEquals(200, response.extract().statusCode()),
                ()->Assertions.assertEquals(statusInfo, responseDto.getStatusInfo()),
                ()->Assertions.assertEquals(type, responseDto.getType()),
                ()->Assertions.assertEquals(currentStatus, responseDto.getCurrentStatus()),
                ()->Assertions.assertEquals(mdn, responseDto.getMdn())
        );

    }

    @Test
    public void checkWithoutXData(){
        Map<String, String> headers = new HashMap<>();
        headers.put("x-Signature", getValidXSignature());
        ValidatableResponse response = new CheckNumberApi().checkNumbers(List.of("12345678901"),headers);
        Assertions.assertAll(
                ()->Assertions.assertEquals(401, response.extract().statusCode()),
                ()->Assertions.assertEquals(("Date header not present"),
                        response.extract().jsonPath().get("statusInfo").toString()),
                ()->Assertions.assertEquals(("ERROR"),
                        response.extract().jsonPath().get("status").toString())
        );
    }

    @Test
    public void checkWithoutXSignature(){
        Map<String, String> headers = new HashMap<>();
        headers.put("x-date", getXDate());
        ValidatableResponse response = new CheckNumberApi().checkNumbers(List.of("12345678901"),headers);
        Assertions.assertAll(
                ()->Assertions.assertEquals(401, response.extract().statusCode()),
                ()->Assertions.assertEquals(("Signature header not present"),
                        response.extract().jsonPath().get("statusInfo").toString()),
                ()->Assertions.assertEquals(("ERROR"),
                        response.extract().jsonPath().get("status").toString())
        );
    }

    @Test
    public void checkExtraLargeRequest(){
        List<String> numbers= new ArrayList<>();
        while (numbers.size()<11) numbers.add(getRandomPhone(11));

        ValidatableResponse response = new CheckNumberApi().checkNumbers(numbers);
        Assertions.assertAll(
                ()->Assertions.assertEquals(400, response.extract().statusCode()),
                ()->Assertions.assertEquals(("request exceeds maximum limit of numbers allowed max allowed 10"),
                        response.extract().jsonPath().get("statusInfo").toString())
        );
    }

    private static Stream<Arguments> numbersResponses() {
        return Stream.of(
                Arguments.of("12345678901", Constants.FULL_STATUS_INFO, Constants.TYPE_FULL, 2),
                Arguments.of("2345678901", Constants.NO_COUNTRY_CODE_STATUS_INFO, Constants.TYPE_NO_COUNTRY_CODE, 4),
                Arguments.of("345678901", Constants.NOT_US_STATUS_INFO, Constants.TYPE_NOT_US, 4),
                Arguments.of("123456789012", Constants.NOT_US_STATUS_INFO, Constants.TYPE_NOT_US, 4),
                Arguments.of("1234567 901", Constants.NOT_NUMBER_STATUS_INFO, Constants.TYPE_NOT_NUMBER, -1),
                Arguments.of("1234567890", Constants.NOT_FULL_STATUS_INFO, Constants.TYPE_NOT_FULL, 4)
        );
    }
}
