package com.lightchat.ims.dal.domain;

public class UserDO extends PageDO{
	private String			UserId;
	private String 			UserName;
	private String 			Password;
	private String 			UserHead;
	private String 			UserSign;
	private String 			Gender;
	private Integer 		Age;
	private Integer 		ValidateType;
	private Integer 		isDeleted;
	private Long 			Updated;
	private Long 			Created;
    private Long            startDate;
    private Long            endDate;
	
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public String getUserHead() {
		return UserHead;
	}
	public void setUserHead(String userHead) {
		UserHead = userHead;
	}
	public String getUserSign() {
		return UserSign;
	}
	public void setUserSign(String userSign) {
		UserSign = userSign;
	}
	public String getGender() {
		return Gender;
	}
	public void setGender(String gender) {
		Gender = gender;
	}
	public Integer getAge() {
		return Age;
	}
	public void setAge(Integer age) {
		Age = age;
	}
	public Integer getValidateType() {
		return ValidateType;
	}
	public void setValidateType(Integer validateType) {
		ValidateType = validateType;
	}
	public Integer getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Long getUpdated() {
		return Updated;
	}
	public void setUpdated(Long updated) {
		Updated = updated;
	}
	public Long getCreated() {
		return Created;
	}
	public void setCreated(Long created) {
		Created = created;
	}
	public Long getStartDate() {
		return startDate;
	}
	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}
	public Long getEndDate() {
		return endDate;
	}
	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}
	
}
