package com.telefon.ufanet;

public class ItemCalls {
	/**
	 * Заголовок
	 */
	String header;

	/**
	 * Подзаголовок
	 */
	String subHeader;

	String message;
	String name;







	/**
	 * Конструктор создает новый элемент в соответствии с передаваемыми
	 * параметрами:
	 * @param h - заголовок элемента
	 * @param s - подзаголовок
	 */
    public ItemCalls(String h, String s, String name, String m){
		this.header=h;
		this.subHeader=s;
		this.message = m;
		this.name = name;

	}
	
	//Всякие гетеры и сеттеры
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getSubHeader() {
		return subHeader;
	}
	public void setSubHeader(String subHeader) {
		this.subHeader = subHeader;
	}
	
}
