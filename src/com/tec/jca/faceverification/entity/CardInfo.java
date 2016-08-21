package com.tec.jca.faceverification.entity;

import java.io.Serializable;

public class CardInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9098092177215293207L;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getNation() {
		return nation;
	}
	public void setNation(int nation) {
		this.nation = nation;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCard_code() {
		return card_code;
	}
	public void setCard_code(String card_code) {
		this.card_code = card_code;
	}
	public String getFzjg() {
		return fzjg;
	}
	public void setFzjg(String fzjg) {
		this.fzjg = fzjg;
	}
	public String getYrqx_begin() {
		return yrqx_begin;
	}
	public void setYrqx_begin(String yrqx_begin) {
		this.yrqx_begin = yrqx_begin;
	}
	public String getYrqx_end() {
		return yrqx_end;
	}
	public void setYrqx_end(String yrqx_end) {
		this.yrqx_end = yrqx_end;
	}
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	private String name;
	private int sex;
	private  int  nation;//名族
	private String birthday;
	private String address;
	private String card_code;
	private String fzjg;//发证机关
	private String yrqx_begin;//有效期限
	private String yrqx_end;
	private String picPath;
	@Override
	public String toString() {
		return "CardInfo [name=" + name + ", sex=" + sex + ", nation=" + nation
				+ ", birthday=" + birthday + ", address=" + address
				+ ", card_code=" + card_code + ", fzjg=" + fzjg
				+ ", yrqx_begin=" + yrqx_begin + ", yrqx_end=" + yrqx_end
				+ ", picPath=" + picPath + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((card_code == null) ? 0 : card_code.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CardInfo other = (CardInfo) obj;
		if (card_code == null) {
			if (other.card_code != null)
				return false;
		} else if (!card_code.equals(other.card_code))
			return false;
		return true;
	}
	public CardInfo(String name, int sex, int nation, String birthday,
			String address, String card_code, String fzjg, String yrqx_begin,
			String yrqx_end, String picPath) {
		super();
		this.name = name;
		this.sex = sex;
		this.nation = nation;
		this.birthday = birthday;
		this.address = address;
		this.card_code = card_code;
		this.fzjg = fzjg;
		this.yrqx_begin = yrqx_begin;
		this.yrqx_end = yrqx_end;
		this.picPath = picPath;
	}
	public CardInfo(String name, int sex, int nation, String birthday,
			String address, String card_code, String fzjg, String yrqx_begin,
			String yrqx_end) {
		this( name,  sex,  nation,  birthday,
				 address,  card_code,  fzjg,  yrqx_begin,
				 yrqx_end,null);
		
	}
	public CardInfo() {
	}
}	

