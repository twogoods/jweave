package com.github.twogoods.jweave.plugin.weave.advice;

import com.alibaba.fastjson2.JSONB;

/**
 * @author twogoods
 * @since 2024/11/27
 */
public class SerializationUtils {
    public static byte[] serialize(Object object) {
        return JSONB.toBytes(object);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSONB.parseObject(bytes, clazz);
    }

}
