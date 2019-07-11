package com.kpk.pinpin.demo.utils.http;


/**
 */

public interface ResponseCallback<T>{
	T onResponse(int resultCode, String resultJson);
}
