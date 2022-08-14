package utils;

import java.util.Random;

public class NumberGenerator {
    private static final int CORRECT_LENGTH = 11;

    public static String getRandomPhone(int length) {
        String s = "1234567890";
        StringBuffer phoneNumber = new StringBuffer();

        for (int i = 0; i < length; i++) {
            phoneNumber.append(s.charAt(new Random().nextInt(s.length())));
        }
        return phoneNumber.toString();
    }
}
