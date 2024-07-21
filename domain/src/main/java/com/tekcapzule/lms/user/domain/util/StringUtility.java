package com.tekcapzule.lms.user.domain.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtility {
    /**
     * Search/replace of multiple strings. Case Sensitive!
     */
    public static String replace(String targetString, Map<String, String> replacements) {
        return replaceKeys(targetString, replacements);
    }

    public static String replaceKeys(String target, Map<String, String> replacements) {
        if(target == null || "".equals(target) || replacements == null || replacements.size() == 0)
            return target;

        StringBuilder patternString = new StringBuilder();
        patternString.append('(');
        boolean first = true;
        for(String key : replacements.keySet()) {
            if(first)
                first = false;
            else
                patternString.append('|');

            patternString.append(Pattern.quote(key));
        }
        patternString.append(')');

        Pattern pattern = Pattern.compile(patternString.toString());
        Matcher matcher = pattern.matcher(target);

        StringBuffer res = new StringBuffer();
        while(matcher.find()) {
            String match = matcher.group(1);
            matcher.appendReplacement(res, replacements.get(match));
        }
        matcher.appendTail(res);

        return res.toString();
    }
}
