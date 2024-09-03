package com.weh.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weh.service.RedisService;
import com.weh.util.CommonUtil;
import com.weh.util.ParseXml;
import com.weh.util.ParseXmlForWx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
public class WxAuthController {

    @Autowired
    private RedisService redisService;

    long CODE_EXPIRE_TIME = 5 * 60;

    private final String WX_CODE_KEY = "weixin:code:";

    // 记录所有登录人的状态
    Map<String, Boolean> loginObj = new HashMap<>();

    @PostMapping("/conn")
    public JSONObject conn(HttpServletRequest req){
        //获取一个版本3(基于名称)根据指定的字节数组的UUID。
        byte[] nbyte = {10, 20, 30};
        UUID uuidFromBytes = UUID.randomUUID();
        String uuid = uuidFromBytes.toString().replaceAll("-","");
        this.loginObj.put(uuid,false);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", uuid);
        jsonObject.put("qrurl", "https://itweh.cn/weixin-qcoder.png");
        return jsonObject;
    }

    @PostMapping("/checkCode")
    public JSONObject getQrCode(HttpServletRequest req){
        System.out.println("----------------对方发了验证码过来");
        String myid = req.getParameter("myid");
        String code = req.getParameter("code");
        JSONObject jsonObj = new JSONObject();
        if(redisService.hasKey(WX_CODE_KEY + code)){
            this.loginObj.put(myid,true);
            jsonObj.put("statu", true);
            jsonObj.put("username", redisService.get(WX_CODE_KEY + code));
            redisService.del(WX_CODE_KEY + code);
        }else{
            jsonObj.put("statu", false);
        }
        return jsonObj;
    }

    // 当用户用手机扫码时，微信服务器会通过post方法给我们传递数据
    @PostMapping("/weh/callback")
    public String awaitData(@RequestBody ParseXmlForWx px){
        System.out.println("接收到的XML为："+px);
        String FromUserName = px.getFromUserName();
        String ToUserName = px.getToUserName();
        String MsgType = px.getMsgType();
        String Event = px.getEvent();
        String fromContent = px.getContent();
        Long now = new Date().getTime();

        // 回复信息给 微信服务器
        String content = "";
        if(MsgType.equals("text") ){
            if(fromContent.equals("验证码")){
                String code = CommonUtil.getRandomCode();
                //   验证码存储到redis中
                redisService.set(WX_CODE_KEY + code, FromUserName, CODE_EXPIRE_TIME);
                content = "验证码："+code;
            } else {
                content = "谢谢！";
            }
        }else if(MsgType.equals("event")){
            content = "event事件";
            if(Event.equals("SCAN")){
                content = "好家伙，手机扫码";
            } else if(Event.equals("subscribe")){
                content = "感谢您的关注！查看更多信息，您可以访问菜鸟程序猿：https://noob.itweh.cn";

            }
            if(Event.equals("unsubscribe")){
                content = "好家伙，你居然敢取消关注？";
            }
        } else{
            content = "其他信息来源！";
        }

        // 根据来时的信息格式，重组返回。(注意中间不能有空格)
        String msgStr = "<xml>"
                +"<ToUserName><![CDATA["+FromUserName+"]]></ToUserName>"
                +"<FromUserName><![CDATA["+ToUserName+"]]></FromUserName>"
                +"<CreateTime>"+now+"</CreateTime>"
                +"<MsgType><![CDATA[text]]></MsgType>"
                +"<Content><![CDATA["+content+"]]></Content>"
                +"</xml>";
        return msgStr;
    }

    @PostMapping("parseXml")
    public ParseXml parseXml(@RequestBody ParseXml px){
        System.out.println("ParseXml: "+px);
        return px;
    }
    @PostMapping("parseXmlForWx")
    public ParseXmlForWx parseXml(@RequestBody ParseXmlForWx px){
        System.out.println("ParseXmlForWx: "+px);
        return px;
    }

    @GetMapping("/login")
    public JSONObject Login(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String myid = req.getParameter("myid");
        System.out.println("有人来问"+myid);
        System.out.println(this.loginObj.get(myid));
        if(!this.loginObj.containsKey(myid)){
            jsonObject.put("isexit",false);
        }else{
            jsonObject.put("isexit",true);
            if(this.loginObj.get(myid)){
                jsonObject.put("login",true);
                System.out.println(myid+"已登录");
            }else {
                jsonObject.put("login",false);
                System.out.println(myid+"未登录");
            }
        }
        return jsonObject;
    }

    @GetMapping("Logout")
    public JSONObject Logout(@RequestParam String myid){
        System.out.println("退出登录"+myid);
        if(this.loginObj.containsKey(myid)){
            this.loginObj.remove(myid);
            String jsonStr = "{\"login\":\"true\"}";
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            return jsonObject;
        } else {
            String jsonStr = "{\"login\":\"false\"}";
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            return jsonObject;
        }
    }
}
