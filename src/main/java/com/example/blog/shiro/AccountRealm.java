package com.example.blog.shiro;

import cn.hutool.core.lang.Console;
import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import com.example.blog.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwtToken =(JwtToken) authenticationToken;
        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        Console.log(userId);

        User user = userService.getById(Long.valueOf(userId));

        if(user == null){
            throw new UnknownAccountException("账户不存在");
        }

        if(user.getStatus() == -1){
            throw new LockedAccountException("账户已被锁定");
        }

        AccountProfile accountProfile=new AccountProfile();
        BeanUtils.copyProperties(user,accountProfile);

        System.out.println("--------------------------");


        return new SimpleAuthenticationInfo(accountProfile,jwtToken.getCredentials(),getName());
    }
}
