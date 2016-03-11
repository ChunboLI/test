package com.lightchat.ims.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lightchat.ims.dal.domain.UserDO;
import com.lightchat.ims.dal.persistence.UserDAO;

/**
 * 用户服务
 * @author chunbo
 *
 * @version $Id: UserService.java, v 0.1 2016年3月7日 下午3:03:33 chunbo Exp $
 */
@Service
public class UserService {
	@Autowired
	UserDAO				userDAO;
	
	public List<UserDO> queryForList(UserDO userDO){
		List<UserDO> userList = userDAO.selectForList(userDO);
		return userList;
	}
}
