package com.weh.controller;

import com.weh.util.SHA1Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信公众号：weh程序猿
 *
 * @author weh-coder
 * @des 验证 token
 * @date 2024/9/3
 */
@Slf4j
@RestController
public class WxTokenController {
    //  验证 token
    @GetMapping("/weh/callback")
    public String echoStr(HttpServletRequest req){
        String signature = req.getParameter("signature");
        String echostr = req.getParameter("echostr");
        String str = SHA1Util.getValidateStr(req);
        if (str.equals(signature)) {
            log.info("token验证通过");
            return echostr;
        } else {
            log.info("验证失败");
            return "fail";
        }
    }
}
