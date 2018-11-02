package io.github.leeseungeun.webframework.utils;

public enum AnnotationType {
	
	Bean {
        @Override
        public void process(Object object) {
        	
        }
    },
    Inject {
        @Override
        public void process(Object object) {
        	
        }
    },
    RequestMapping {
    	@Override
    	public void process(Object object) {
    		
    	}
    };
	
	public abstract void process(Object object);

}
