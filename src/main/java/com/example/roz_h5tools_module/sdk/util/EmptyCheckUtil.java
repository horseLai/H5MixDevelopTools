package com.example.roz_h5tools_module.sdk.util;

import java.util.Collection;
import java.util.Map;
/**
 * @Author horseLai
 * CreatedAt  
 * Desc:
 * Update:  
 */
public class EmptyCheckUtil {

    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }


    public static boolean isEmpty(CharSequence strLeft) {
        return strLeft == null || strLeft.length() == 0;
    }

    public static boolean isEmpty(Object[] params) {
        return params == null || params.length == 0;
    }

    public static boolean isEmpty(Map  map) {
        return map == null || map.isEmpty();
    }
}
