package io.github.leeseungeun.webframework.enums;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public enum JsonifiedObjectType {

	List("java.util.List") {

		@Override
		public boolean isInstance(Object data) {

			boolean result = false;

			try {

				Class clazz = Class.forName(getClassName());
				result = clazz.isInstance(data);

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		public String jsonify(Object data) {
			
			List dataList = (List) data;
			
			return listToJsonArray(dataList).toJSONString();
		}
	},
	Map("java.util.Map") {

		@Override
		public boolean isInstance(Object data) {
			
			boolean result = false;

			try {

				Class clazz = Class.forName(getClassName());
				result = clazz.isInstance(data);

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		public String jsonify(Object data) {
			
			JSONObject result = new JSONObject();
			
			Map<String, Object> mapData = (Map<String, Object>) data;
			for (String key : mapData.keySet()) {
				
				Object value = mapData.get(key);
				
				if(Map.isInstance(value)) {
					
					result.put(key, value);
				
				} else if (List.isInstance(value)) {
					
					List list = (List) value;
					result.put(key, listToJsonArray(list));
				
				} else if (DomainObject.isInstance(value)) {
					result.put(key, domainObjectToJsonObject(value));
				}
			}
			
			return result.toJSONString();
		}
	},
	DomainObject("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*(domain\\.).[a-zA-Z_$][a-zA-Z\\d_$]*") {

		@Override
		public boolean isInstance(Object data) {
			
			String className = data.getClass().getTypeName();
			
			return className.matches(getClassName());
		}

		@Override
		public String jsonify(Object data) {
			
			return domainObjectToJsonObject(data).toJSONString();
		}
	};

	// 인스턴스 변수
	private String className;

	// getter
	public String getClassName() {
		return className;
	}

	// 생성자
	JsonifiedObjectType(String className) {
		this.className = className;
	}

	public abstract String jsonify(Object data);

	public abstract boolean isInstance(Object data);
	
	/**
	 * 도메인 객체를 JSON 객체로 바꾸는 메소드
	 * 
	 * @param object 도메인 객체
	 * @return JSON으로 바꾼 도메인 객체
	 */
	@SuppressWarnings("unchecked")
	private static JSONObject domainObjectToJsonObject(Object object) {
		
		JSONObject result = new JSONObject();
		
		try {
		
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				result.put(field.getName(), field.get(object));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONArray listToJsonArray(List list) {
		
		JSONArray result = new JSONArray();
		
		for (Object object : list) {
			if (Map.isInstance(object)) {
			
				result.add(object);
			
			} else if (DomainObject.isInstance(object)) {
				
				result.add(domainObjectToJsonObject(object));
				
			}
		}
		
		return result;
	}
}
