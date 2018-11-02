package io.github.leeseungeun.webframework.enums;

import java.lang.reflect.Field;
import java.util.Map;

public enum AnnotationType {
	
	Bean {
		
        /** 
         * 빈 어노테이션이 있을 경우의 처리
         * 주어진 클래스의 객체를 생성
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
        @Override
        public Object process(Object object) {
        	
        	@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) object;
        	
        	Object classObject = data.get("class");
        	
        	Field targetField = (Field) data.get("filed");
        	String targetType = targetField.getType().getName();
        	
        	Object targetObject = null;
        	try {
        		
        		targetObject = Class.forName(targetType).newInstance();
        		targetField.setAccessible(true);
        		targetField.set(classObject, targetObject);
			
        	} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
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
    		
    		
    		return object;
    	}
    };
	
	public abstract Object process(Object object);

}
