package com.zhangxuhui.vo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: AmberZxh
 * @DateTime: 2022/6/12 21:48
 * @Description:
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MsgVO implements Serializable {

    private static final long serialVersionUID = 3195904100875695614L;

    private String phone;  //用来接收手机号

    private String captcha; //接收验证码
}
