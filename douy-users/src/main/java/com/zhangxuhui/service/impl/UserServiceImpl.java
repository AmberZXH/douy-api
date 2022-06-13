package com.zhangxuhui.service.impl;

/**
 * @Author: AmberZxh
 * @DateTime: 2022/6/13 00:06
 * @Description:
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhangxuhui.entity.User;
import com.zhangxuhui.mapper.UserMapper;
import com.zhangxuhui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public User findByPhone(String phoneNumber) {

        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phoneNumber));

    }

    @Override
    public User insert(User user) {
        int effect = userMapper.insert(user);
        if (effect < 1) {
            throw new RuntimeException("插入失败！");
        }
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, user.getPhone()));
    }
}
