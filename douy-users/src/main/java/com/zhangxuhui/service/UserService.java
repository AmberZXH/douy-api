package com.zhangxuhui.service;

import com.zhangxuhui.entity.User;

/**
 * @Author: AmberZxh
 * @DateTime: 2022/6/13 00:05
 * @Description:
 */
public interface UserService {
    User findByPhone(String phoneNumber);

    User insert(User user);
}
