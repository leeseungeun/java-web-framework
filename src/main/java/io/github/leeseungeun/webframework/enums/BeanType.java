package io.github.leeseungeun.webframework.enums;

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
		}
	};
	
	public abstract void process(Object object); 
}
