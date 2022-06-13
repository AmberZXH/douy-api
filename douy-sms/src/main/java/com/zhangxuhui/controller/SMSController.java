package com.zhangxuhui.controller;

import com.zhangxuhui.constant.RedisPrefix;
import com.zhangxuhui.utils.SMSUtils;
import com.zhangxuhui.vo.param.MsgVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @Author: AmberZxh
 * @DateTime: 2022/6/12 21:45
 * @Description:
 */
@RestController
public class SMSController {
    private static final Logger log = LoggerFactory.getLogger(SMSController.class);

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    SMSUtils smsUtils;

    @PostMapping("captchas")
    public void captchas(@RequestBody MsgVO msgVO) {
        //1.获取接收到的手机号
        String phone = msgVO.getPhone();
        log.info("发送短信的手机号为: {}", phone);

        //2.每次发送验证码之前判断,是否存在timeout_132... timeout_176
        String timeoutKey = RedisPrefix.TIME_OUT + phone;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(timeoutKey))) {
            throw new RuntimeException("提示: 不允许重复发送!");
        }
        try {
            //3.生成4位随机字符
            String code = RandomStringUtils.randomNumeric(4);
            log.info("发送的验证码: {}", code);
            //4.根据接收手机号以及生成随机字符 发送验证码
            //smsUtils.sendMsg(phone, code);

            //5.将验证码放入redis   key: phone_132....   value:code
            String phoneKey = RedisPrefix.PHONE + phone;//给key加入一个前缀
            stringRedisTemplate.opsForValue().set(phoneKey, code, 10, TimeUnit.MINUTES);//10分钟验证有效

            //6.如果验证码在有效期内,不允许重新发送  //timeout_132... true
            stringRedisTemplate.opsForValue().set(timeoutKey, "true", 70, TimeUnit.SECONDS);//两次发送间隔时间
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("提示: 短信发送失败!");
        }
    }
}
