package com.github.twogoods.jweave.agent.core.util;

import java.util.Collection;

public class StringUtils {

    public static final String EMPTY = "";

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static String join(Collection<?> collection, String separator) {
        if (collection == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        Object[] objects = collection.toArray();

        for (int i = 0; i < collection.size(); i++) {
            if (objects[i] != null) {
                stringBuilder.append(objects[i]);
                if (i != collection.size() - 1 && separator != null) {
                    stringBuilder.append(separator);
                }
            }
        }

        return stringBuilder.toString();
    }
}
