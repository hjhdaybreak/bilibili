package com.bilibili.service;


import com.bilibili.domain.auth.*;
import com.bilibili.domain.constant.AuthRoleConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAuthService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AuthRoleService authRoleService;


    public UserAuthorities getUserAuthorities(Long userId) {
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<Long> roleIdSet = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());

        //根据角色ID查询权限

        //1.按钮操作权限
        List<AuthRoleElementOperation> roleElementOperations = authRoleService.getRoleElementOperationsByRoleIds(roleIdSet);

        //2.Menu
        List<AuthRoleMenu> authRoleMenuList = authRoleService.getAuthRoleMenusByRoleIds(roleIdSet);

        UserAuthorities userAuthorities = new UserAuthorities();
        userAuthorities.setRoleElementOperationList(roleElementOperations);
        userAuthorities.setRoleMenuList(authRoleMenuList);

        return userAuthorities;
    }

    public void addUserDefaultRole(Long userId) {
        UserRole userRole = new UserRole();
        AuthRole role = authRoleService.getRoleByCode(AuthRoleConstants.ROLE_LV0);
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        userRoleService.addUserRole(userRole);
    }
}
