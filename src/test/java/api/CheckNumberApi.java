package api;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.codec.digest.DigestUtils;
import utils.Config;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.restassured.RestAssured.given;
import static utils.Config.getPort;

public class CheckNumberApi {

    public Map<String, String> defaultHeaders = new HashMap<>();
    public static String getXDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }
    public static String getValidXSignature () {
        String xDate = getXDate();
        System.out.println(xDate);
        StringBuilder s = new StringBuilder().append("\"TEST\" , \"POST\", \"").append(xDate).append("\"");
        return DigestUtils.sha512Hex(String.format(s.toString()));
    }

    public Map<String, String> getDefaultHeaders() {
        defaultHeaders.put("x-date", getXDate());
        defaultHeaders.put("x-Signature", getValidXSignature());
        return defaultHeaders;
    }

    public ValidatableResponse checkNumbers(List<String> numbers){
        return checkNumbers(numbers, getDefaultHeaders());
    }

    public ValidatableResponse checkNumbers(List<String> numbers, Map headers) {
        if (headers==null){
            headers=getDefaultHeaders();
        }
        return  given()
                .baseUri(Config.getConfig().get("ServerUrl"))
                .port(getPort())
                .body(numbers)
                .headers(headers)
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .post("/checkNumber")
                .then()
                .log().all();
    }
}




