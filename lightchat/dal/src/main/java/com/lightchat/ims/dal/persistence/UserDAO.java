package com.lightchat.ims.dal.persistence;

import java.util.List;

import com.lightchat.ims.dal.domain.UserDO;

public interface UserDAO {
	
	public Integer insert(UserDO userDO);

	public Integer update(UserDO userDO);

	public List<UserDO> selectForList(UserDO userDO);

	public UserDO selectForObject(UserDO userDO);

	public UserDO selectForObjectByPk(String userId);
	
	public Integer selectForCount(UserDO userDO);
	
	public void delete(String userId);
}
