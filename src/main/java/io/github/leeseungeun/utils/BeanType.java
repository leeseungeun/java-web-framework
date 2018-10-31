package io.github.leeseungeun.utils;

public enum BeanType {
	
	CONTROLLER {
        @Override
        public void process() {
        	
        }
    },
    SERVICE {
        @Override
        public void process() {
        	
        }
    },
    DAO {
    	@Override
    	public void process() {
    		
    	}
    },
    SQLSESSIONFACTORY {
    	@Override
    	public void process() {
    		
    	}
    };
	
	public abstract void process();

}
