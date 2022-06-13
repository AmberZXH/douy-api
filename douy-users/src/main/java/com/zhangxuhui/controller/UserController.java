package com.zhangxuhui.controller;

import com.zhangxuhui.constant.RedisPrefix;
import com.zhangxuhui.constant.UserDefaultConst;
import com.zhangxuhui.entity.User;
import com.zhangxuhui.service.UserService;
import com.zhangxuhui.vo.param.MsgVO;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: AmberZxh
 * @DateTime: 2022/6/12 23:34
 * @Description:
 */
@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserService userService;

    @PostMapping("token")
    public Map<String, Object> tokens(@RequestBody MsgVO params, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        String phone = params.getPhone();
        String captcha = params.getCaptcha();

        log.info("Phone : {} , captcha : {}", phone, captcha);

        String phoneKey = RedisPrefix.PHONE + phone;

        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(phoneKey))) {
            throw new RuntimeException("TIPS: The validation code has expired!");
        }

        String redisCaptcha = stringRedisTemplate.opsForValue().get(phoneKey);

        if (!StringUtils.equals(captcha, redisCaptcha)) {
            throw new RuntimeException("TIPS:  Wrong captcha code");
        }

        User user = userService.findByPhone(phone);
        if (ObjectUtils.isEmpty(user)) {
            user = new User();
            user.setPhone(phone);
            user.setName(UserDefaultConst.NAME);
            user.setAvatar(UserDefaultConst.AVATAR);
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            user.setIntro("");
            user.setPhoneLinked(true);
            user.setWechatLinked(false);
            user.setFollowersCount(0);
            user.setFollowingCount(0);
            userService.insert(user);
        }

        String token = request.getSession().getId();
        String tokenKey = RedisPrefix.SESSION + token;
        log.info("generate token is : {}", token);
        redisTemplate.opsForValue().set(tokenKey, user, 7, TimeUnit.DAYS);

        result.put("token" ,token);

        return result;
    }
}
