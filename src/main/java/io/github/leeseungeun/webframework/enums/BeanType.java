package io.github.leeseungeun.webframework.enums;

import java.util.Map;

import io.github.leeseungeun.webframework.beans.SqlSessionFactory;
import oracle.net.aso.s;

public enum BeanType {
	
	Controller {
		@Override
		public Object process(Object object) {
			return object;
		}
	},
	RequestHandler {
		@Override
		public Object process(Object object) {
			return object;
		}
	},
	Service {
		@Override
		public Object process(Object object) {
			return object;
		}
	},
	Repository {
		@Override
		public Object process(Object object) {
			return object;
		}
	},
	SqlSessionFactory {
		@Override
		public Object process(Object object) {
			
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) object;
			
			SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) data.get(SQLSESSIONFACTORY_SQLSESSIONFACTORY_DATA_KEY);
			String configLocation = String.valueOf(data.get(SQLSESSIONFACTORY_CONFIGLOCATION_DATA_KEY));
			String environment = String.valueOf(data.get(SQLSESSIONFACTORY_ENVIRONMENT_DATA_KEY));
			
			sqlSessionFactory.setConfigLocation(configLocation);
			sqlSessionFactory.setEnvironment(environment);
			
			return sqlSessionFactory.build();
		}
	};
	
	// 상수
	public static final String SQLSESSIONFACTORY_SQLSESSIONFACTORY_DATA_KEY = "sqlSessionFactory";
	public static final String SQLSESSIONFACTORY_CONFIGLOCATION_DATA_KEY = "configLocation";
	public static final String SQLSESSIONFACTORY_ENVIRONMENT_DATA_KEY = "environment";
	
	public abstract Object process(Object object); 
}
