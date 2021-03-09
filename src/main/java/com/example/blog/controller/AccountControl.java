package com.example.blog.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blog.common.dto.LoginDto;
import com.example.blog.common.lang.Result;
import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import com.example.blog.util.JwtUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountControl {
    @Autowired
    UserService userService;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/Login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServerResponse httpServerResponse){
        User user=userService.getOne(new QueryWrapper<User>().eq("username",loginDto.getUsername()));
        Assert.notNull(user,"用户不存在");
        if(user.getPassword() .equals(SecureUtil.md5(loginDto.getPassword()) )){
            return Result.fail("密码不正确");
        }
        String jwt = jwtUtils.generateToken((user.getId()));
        httpServerResponse.setHeader("Authorization",jwt);
        httpServerResponse.setHeader("Access-control-Expose-Headers","Authorization");
        return Result.succ(MapUtil.builder()
                .put("id",user.getId())
                .put("username",user.getId())
                .put("avatar",user.getAvatar())
                .put("email",user.getEmail())
                .map()
        );
    }
    @RequiresAuthentication
    @PostMapping("/Logout")
    public Result logout(){
        SecurityUtils.getSubject().logout();
        return Result.succ(null);
    }

}
