package com.bfit.recommand.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class JsonUtils {

    private static ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
    }

    public static String toStringWithNullKey(Object obj){
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JsonUtils toStringWithNullKey",e);
        }
        return StringUtils.EMPTY;
    }

    public static  <T>T toObj(String src, Class<T> clazz){
        try {
            return MAPPER.convertValue((Object) src, clazz);
        }catch (Exception ex){
            log.error("JsonUtils toStringWithNullKey",ex);
        }
        return null;
    }

    public static  <T>T toList(String src, TypeReference<T> toValueTypeRef){
        try {
            return MAPPER.convertValue(src, toValueTypeRef);
        }catch (Exception ex){
            log.error("JsonUtils toStringWithNullKey",ex);
        }
        return null;
    }

}
