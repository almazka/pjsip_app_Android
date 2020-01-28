package com.telefon.ufanet;

public class ItemContactsWorkers {
	/**
	 * Заголовок
	 */
	String header;

	/**
	 * Подзаголовок
	 */
	String subHeader;
	String mail;
	String name;


	/**
	 * Конструктор создает новый элемент в соответствии с передаваемыми
	 * параметрами:
	 * @param h - заголовок элемента
	 * @param s - подзаголовок
	 */
    public ItemContactsWorkers(String h, String s, String name, String m){
		this.header=h;
		this.subHeader=s;
		this.mail = m;
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
	public String getnum() {
		return mail;
	}
	public void setNum(String num) {
		this.mail = num;
	}
}
