package com.stephen.login.util;

import java.util.Random;

public class StringUtil {
	
    private static Random random = new Random();
    
    public static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder salt = new StringBuilder();
        while (salt.length() < 4) { // length of the random string.
            int index = (random.nextInt() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }
	
}
