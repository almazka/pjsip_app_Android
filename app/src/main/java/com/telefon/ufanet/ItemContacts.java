package com.telefon.ufanet;

public class ItemContacts {
	/**
	 * Заголовок
	 */
	String header;

	/**
	 * Подзаголовок
	 */
	String subHeader;




	
	/**
	 * Конструктор создает новый элемент в соответствии с передаваемыми 
	 * параметрами:
	 * @param h - заголовок элемента
	 * @param s - подзаголовок
	 */
    public ItemContacts(String h, String s){
		this.header=h;
		this.subHeader=s;
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
