package com.github.fabito.gaemeleon.api;

public class Property {

	private String key;
	private Object value;

	@SuppressWarnings("unused")
	private Property() {
		super();
	}
	
	public Property(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}


	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Property [key=" + key + ", value=" + value + "]";
	}

}