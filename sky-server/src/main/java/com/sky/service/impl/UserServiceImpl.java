package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String USER_LOGIN="https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private JwtProperties jwtProperties;


    @Override
    public UserLoginVO userLogin(UserLoginDTO userLoginDTO) {
        //获取响应结果
        Map<String, String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",userLoginDTO.getCode());
        map.put("grant_type","authorization_code");
        String json=HttpClientUtil.doGet(USER_LOGIN,map);

        JSONObject jsonObject=JSON.parseObject(json);

        //获取openid
        String openId=jsonObject.getString("openid");

        if(openId==null||openId.isEmpty()){
            throw new LoginFailedException("未登陆");
        }
        User user=userMapper.getUserByopenId(openId);
        if(user==null){
            user= User.builder()
                    .openid(openId)
                    .createTime(LocalDateTime.now()).build();
            userMapper.addUser(user);
        }
        //获取jwt令牌
        Map<String,Object> claim=new HashMap<>();
        claim.put(JwtClaimsConstant.USER_ID,user.getId());
        String token=JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claim);
        UserLoginVO userLoginVO=UserLoginVO.builder().id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return userLoginVO;
    }
}
