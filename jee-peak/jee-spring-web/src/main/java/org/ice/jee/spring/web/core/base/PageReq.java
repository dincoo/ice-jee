package org.ice.jee.spring.web.core.base;

public class PageReq {

	private int size = 0;
	
	private int current = 1;
	
	private String orderByField = "id_";
	
	public PageReq() {super();}
	
	public PageReq(int current, int size) {
		super();
		this.size = size;
		this.current = current;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getOrderByField() {
		return orderByField;
	}

	public void setOrderByField(String orderByField) {
		this.orderByField = orderByField;
	}
	
	
}
