package com.example.blog.shiro;

import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.json.JSONUtil;
import com.example.blog.common.lang.Result;
import com.example.blog.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Component
public class JwtFilter extends AuthenticatingFilter {
    @Autowired
    JwtUtils jwtUtils;

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        String jwt=request.getHeader("Authorization");
        if(StringUtils.isEmpty(jwt)){
            return null;
        }
        return new JwtToken(jwt);
    }



    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        String jwt=request.getHeader("Authorization");
        if(StringUtils.isEmpty(jwt)){
            return true;
        }else{
            //校验jwt
            Claims claim=jwtUtils.getClaimByToken(jwt);
            if(claim == null || jwtUtils.isTokenExpired((claim.getExpiration()))){
                throw new ExpiredCredentialsException("Token已失效，请重新登录");
            }
            //执行登录
            return  executeLogin(servletRequest,servletResponse);
        }
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServerResponse httpServerResponse=(HttpServerResponse) response;
        Throwable throwable = e.getCause() == null ? e : e.getCause();
        Result result = Result.fail((throwable.getMessage()));
        String json = JSONUtil.toJsonStr(result);
        try {
            httpServerResponse.getWriter().print(json);
        } catch (Exception exception) {

        }
        return false;
    }
}
