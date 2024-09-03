package com.weh.util;

import java.util.Random;

/**
 * 微信公众号：weh程序猿
 *
 * @author weh-coder
 * @des 这是一个类
 * @date 2024/9/2
 */
public class CommonUtil {
    /*
     * @Description: 随机生成6位数的验证码
     * @returns: str.toString()
     * @Author: weh
     * @Date: 2024/3/24 16:53
     */
    public static String getRandomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(1+random.nextInt(9));
        }
        return str.toString();
    }
}
