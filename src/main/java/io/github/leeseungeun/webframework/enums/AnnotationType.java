package io.github.leeseungeun.webframework.enums;

import java.lang.reflect.Field;
import java.util.Map;

import io.github.leeseungeun.webframework.beans.RequestHandler;
import io.github.leeseungeun.webframework.interfaces.Controller;

public enum AnnotationType {

	Bean {

		/**
		 * 빈 어노테이션이 있을 경우의 처리 주어진 클래스의 객체를 생성
		 */
		@Override
		public Object process(Object object) {

			Class targetClass = (Class) object;

			Object beanObject = null;
			try {
				beanObject = Class.forName(targetClass.getName()).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}

			return beanObject;
		}
	},
	Inject {
		/**
		 * Inject 어노테이션이 있을 경우 어노테이션이 선언된 필드의 자료형을 가져와 해당 빈을 찾아 넣어줌
		 */
		@Override
		public Object process(Object object) {

			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) object;

			Object classObject = data.get(INJECT_CLASS_DATA_NAME);
			Object targetObject = data.get(INJECT_BEAN_DATA_NAME);

			Field targetField = (Field) data.get(INJECT_FIELD_DATA_NAME);
			targetField.setAccessible(true);
			try {
				targetField.set(classObject, targetObject);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

			return classObject;
		}
	},
	RequestMapping {
		@Override
		public Object process(Object object) {

			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) object;

			RequestHandler requestHandler = (RequestHandler) data.get(REQUESTMAPPING_REQUESTHANDLER_DATA_NAME);
			Controller controller = (Controller) data.get(REQUESTMAPPING_CONTROLLER_DATA_NAME);
			String uri = String.valueOf(data.get(REQUESTMAPPING_URI_DATA_NAME));

			Map<String, Controller> mapper = requestHandler.getRequestMapper();
			mapper.put(uri, controller);

			return requestHandler;
		}
	};

	// 상수 선언
	public static final String INJECT_CLASS_DATA_NAME = "class";
	public static final String INJECT_BEAN_DATA_NAME = "bean";
	public static final String INJECT_FIELD_DATA_NAME = "field";
	public static final String REQUESTMAPPING_REQUESTHANDLER_DATA_NAME = "requestHandler";
	public static final String REQUESTMAPPING_CONTROLLER_DATA_NAME = "controller";
	public static final String REQUESTMAPPING_URI_DATA_NAME = "uri";

	public abstract Object process(Object object);

}
