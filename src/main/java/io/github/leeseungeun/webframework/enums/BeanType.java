package io.github.leeseungeun.webframework.enums;

import io.github.leeseungeun.webframework.beans.SqlSessionFactory;

public enum BeanType {
	
	Controller {
		@Override
		public void process(Object object) {
		}
	},
	RequestHandler {
		@Override
		public void process(Object object) {
		}
	},
	Service {
		@Override
		public void process(Object object) {
		}
	},
	Repository {
		@Override
		public void process(Object object) {
		}
	},
	SqlSessionFactory {
		@Override
		public void process(Object object) {
			SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) object.get();
			
		}
	};
	
	// 상수
	public static final String na = "";
	
	public abstract void process(Object object); 
}
