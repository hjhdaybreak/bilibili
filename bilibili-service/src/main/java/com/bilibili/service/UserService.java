package com.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.bilibili.dao.UserDao;
import com.bilibili.domain.PageResult;
import com.bilibili.domain.User;
import com.bilibili.domain.UserInfo;
import com.bilibili.domain.constant.UserConstant;
import com.bilibili.domain.exception.ConditionException;
import com.bilibili.service.util.MD5Util;
import com.bilibili.service.util.RSAUtil;
import com.bilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthService userAuthService;

    public void addUser(User user) {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("phone number can not be null !");
        }
        User dbUser = this.getUserByPhone(phone);
        if (dbUser != null) {
            throw new ConditionException("this phone number has be registered !");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassWord;
        try {
            rawPassWord = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("decrypt fail !");
        }
        String md5Password = MD5Util.sign(rawPassWord, salt, StandardCharsets.UTF_8.name());
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);

        //添加用户默认权限角色
        userAuthService.addUserDefaultRole(user.getId());
    }

    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }

    public String login(User user) throws Exception {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("phone number can not be null !");
        }
        User dbUser = this.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("user is not exist !");
        }
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("decrypt fail !");
        }
        String salt = dbUser.getSalt();
        String md5PassWod = MD5Util.sign(rawPassword, salt, StandardCharsets.UTF_8.name());
        if (!md5PassWod.equals(dbUser.getPassword())) {
            throw new ConditionException("secret is not correct !");
        }
        return TokenUtil.generateToken(dbUser.getId());
    }

    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    public void updateUsers(User user) throws Exception {
        Long id = user.getId();
        User dbUser = userDao.getUserById(id);
        if (dbUser == null) {
            throw new ConditionException("user not exist !");
        }
        if (!StringUtils.isNullOrEmpty(user.getPassword())) {
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }
        user.setUpdateTime(new Date());
        userDao.updateUsers(user);
    }

    public void updateUserInfos(UserInfo userInfo) {
        userInfo.setUpdateTime(new Date());
        userDao.updateUserInfos(userInfo);
    }

    public User getUserById(Long followingId) {
        return userDao.getUserById(followingId);
    }

    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.getUserInfoByUserIds(userIdList);
    }

    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = params.getInteger("no");
        Integer size = params.getInteger("size");
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        Integer total = userDao.pageCountUserInfos(params);
        List<UserInfo> list = new ArrayList<>();
        if (total > 0) {
            list = userDao.pageListUserInfos(params);
        }
        return new PageResult<>(total, list);
    }
}
