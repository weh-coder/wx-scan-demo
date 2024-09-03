package com.weh.util;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * 微信公众号：weh程序猿
 *
 * @author weh-coder
 * @des 这是一个类
 * @date 2024/9/3
 */
public class SHA1Util {

    private static String token="your_token";

    // 验证信息是否来自微信服务器
    public static String getValidateStr(HttpServletRequest req) {
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String arr[] = { token, timestamp, nonce };
        Arrays.sort(arr);
        String arrStr = String.join("", arr);
        String sign = sha1_encode(arrStr);
        return sign;
    }

    // SHA-1加密方法【安全散列算法1】
    public static String sha1_encode(String str) {
        char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(str.getBytes());
            byte[] digest = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            int len = digest.length;
            for (int j = 0; j < len; j++) {
                sb.append(CHARS[(digest[j] >> 4) & 15]);
                sb.append(CHARS[digest[j] & 15]);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
