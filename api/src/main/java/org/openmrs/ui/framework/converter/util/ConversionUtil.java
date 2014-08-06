package org.openmrs.ui.framework.converter.util;

import java.util.regex.Pattern;

public class ConversionUtil {

    private static Pattern onlyDigits = Pattern.compile("\\d+");

    public static boolean onlyDigits(String input) {
        return onlyDigits.matcher(input).matches();
    }

}
