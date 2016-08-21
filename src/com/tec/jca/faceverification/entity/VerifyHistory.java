package com.tec.jca.faceverification.entity;

public class VerifyHistory {
	//_id integer PRIMARY KEY AUTOINCREMENT
	
	public String getCard_code() {
		return card_code;
	}
	public void setCard_code(String card_code) {
		this.card_code = card_code;
	}
	public String getPhotoPath() {
		return photoPath;
	}
	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}
	public String getVerifyWhen() {
		return verifyWhen;
	}
	public void setVerifyWhen(String verifyWhen) {
		this.verifyWhen = verifyWhen;
	}
	public int get_id() {
		return _id;
	}
	private int _id;
	private String name;
	private String card_code;
	private String photoPath;
	private String similarity;
	private String verifyWhen;
	

	@Override
	public String toString() {
		return "VerifyHistory [_id=" + _id + ", name=" + name + ", card_code="
				+ card_code + ", photoPath=" + photoPath + ", similarity="
				+ similarity + ", verifyWhen=" + verifyWhen + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _id;
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
		VerifyHistory other = (VerifyHistory) obj;
		if (_id != other._id)
			return false;
		if (card_code == null) {
			if (other.card_code != null)
				return false;
		} else if (!card_code.equals(other.card_code))
			return false;
		return true;
	}
	public VerifyHistory(String name,String card_code, String photoPath, String similarity,String verifyWhen) {
		super();
		this.name = name;
		this.card_code = card_code;
		this.photoPath = photoPath;
		this.similarity=similarity;
		this.verifyWhen = verifyWhen;
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSimilarity() {
		return similarity;
	}
	public void setSimilarity(String similarity) {
		this.similarity = similarity;
	}
	
}
