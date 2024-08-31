package com.Lnn.realm;

import com.Lnn.entity.User;
import com.Lnn.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRealm  extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    //获取授权信息
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = (User)principalCollection.getPrimaryPrincipal();
        //TODO  待完成 授权
        return null;
    }

    //获取认证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        //获取当前用户信息
        String username = authenticationToken.getPrincipal().toString();

        User user = userService.getUserByUsername(username);

        if(user == null){
            return null; //这里会抛出 用户不存在异常
        }

        return new SimpleAuthenticationInfo(
                authenticationToken.getPrincipal(),
                user.getPassword(),//真实的密码
                ByteSource.Util.bytes(user.getSalt()),//盐值
                this.getName() //realm名
        );

    }
}
