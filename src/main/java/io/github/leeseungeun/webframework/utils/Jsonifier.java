package io.github.leeseungeun.webframework.utils;

import io.github.leeseungeun.webframework.enums.JsonifiedObjectType;

/**
 * 응답을 JSON으로 변환해주는 클래스
 * 
 * @author 이승은
 *
 */
public class Jsonifier {
	
	/**
	 * 응답하는 데이터가 Map, List, Domain Object 중 어느 것에 해당하는지 확인하는 메소드
	 * 
	 * @param data 응답 데이터
	 * @return Map, List, Domain Object에 일대일로 대응된 Enum 값
	 */
	public static JsonifiedObjectType classifyObject(Object data) {
		
		JsonifiedObjectType result = null;
		
		JsonifiedObjectType[] jsonfiedObjects = JsonifiedObjectType.class.getEnumConstants();
		
		for (JsonifiedObjectType jsonifiedObjectType : jsonfiedObjects) {
			if (jsonifiedObjectType.isInstance(data)) {
				result = jsonifiedObjectType;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * 주어진 객체를 JSON으로 변환하는 메소드
	 * 
	 * @param data JSON으로 변환할 객체
	 * @return JSON으로 변환한 값
	 */
	public static String jsonify(Object data) {
		
		JsonifiedObjectType objectType = classifyObject(data);
		assert objectType != null;
		
		return objectType.jsonify(data);
	}
}
