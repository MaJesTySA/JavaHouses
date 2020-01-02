package com.inside.house.common.utils;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

public class HashUtils {
    private static final HashFunction FUNCTION = Hashing.md5();
    private static final String SALT = "majestysa";

    public static String encryptPassword(String password) {
        HashCode hashCode = FUNCTION.hashString(password + SALT, Charset.forName("UTF-8"));
        return hashCode.toString();
    }
}
