package com.kpk.pinpin.demo.utils.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author youletter
 */
public abstract class JsonUtil {

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final ObjectMapper objMapper = new ObjectMapper();
	
	static{
		objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objMapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	public static String toJson(Object obj){
		String rst = null;
		if(obj == null || obj instanceof String){
			return (String) obj;
		}
		try {
			rst = objMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("object to json error!", e);
		}
		return rst;
	}
	
	public static <T> T fromJson(String json, Class<T> type){
		try {
			return objMapper.readValue(json, type);
		} catch (Exception e) {
			throw new RuntimeException("json to object error!",e);
		}
	}

	public static <T> T fromJson(String json, TypeReference<T> typeRef){
		try {
			return (T) objMapper.readValue(json, typeRef);
		} catch (Exception e) {
			throw new RuntimeException("json to object error!", e);
		}
	}

	public static <K, V> HashMap<K, V> toMap(String json, Class<K> k, Class<V> v) {
		try {
			return objMapper.readValue(json, getCollectionType(HashMap.class, k, v));
		} catch (Exception e) {
			throw new RuntimeException("json to object error!", e);
		}
	}

	public static <T> List<T> toList(String json, Class<T> t) {
		try {
			return objMapper.readValue(json, getCollectionType(ArrayList.class, t));
		} catch (Exception e) {
			throw new RuntimeException("json to object error!",e);
		}
	}
	
	public static <K, V> HashMap<K, V> convertMap(Object obj, Class<K> k, Class<V> v) {
		try {
			return objMapper.convertValue(obj, getCollectionType(HashMap.class, k, v));
		} catch (Exception e) {
			throw new RuntimeException("object convert map error!",e);
		}
	}
	
	public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);   
    }
	
}
