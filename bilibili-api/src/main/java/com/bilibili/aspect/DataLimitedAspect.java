package com.bilibili.aspect;

import com.bilibili.api.support.UserSupport;
import com.bilibili.domain.UserMoment;
import com.bilibili.domain.annotation.ApiLimitedRole;
import com.bilibili.domain.annotation.DataLimited;
import com.bilibili.domain.auth.UserRole;
import com.bilibili.domain.constant.AuthRoleConstants;
import com.bilibili.domain.exception.ConditionException;
import com.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Order(1)
@Component
public class DataLimitedAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.bilibili.domain.annotation.DataLimited)")
    public void check() {
    }

    @Before(value = "check()")
    public void doBefore(JoinPoint joinPoint) {
        Long userId = userSupport.getCurrentUserId();

        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserMoment) {
                UserMoment userMoment = (UserMoment) arg;
                String type = userMoment.getType();
                if (roleCodeSet.contains(AuthRoleConstants.ROLE_LV0) && !"0".equals(type)) { //TODO
                    throw new ConditionException("参数异常 !");
                }
            }
        }

    }
}
