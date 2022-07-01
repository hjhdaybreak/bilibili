package com.bilibili.api;


import com.bilibili.api.support.UserSupport;
import com.bilibili.domain.JsonResponse;
import com.bilibili.domain.UserMoment;
import com.bilibili.domain.annotation.ApiLimitedRole;
import com.bilibili.domain.annotation.DataLimited;
import com.bilibili.domain.constant.AuthRoleConstants;
import com.bilibili.service.UserMomentsService;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserMomentsApi {

    @Autowired
    private UserMomentsService userMomentsService;


    @Autowired
    private UserSupport userSupport;

    /**
     * 动态
     *
     * @param userMoment
     * @return
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     * @throws MQClientException
     */
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstants.ROLE_LV0})
    @DataLimited
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }

    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubScribedMoments() {
        Long userId = userSupport.getCurrentUserId();

        List<UserMoment> list = userMomentsService.getUserSubScribedMoments(userId);
        return new JsonResponse<>(list);
    }


}
