package com.world.model.service;


import com.messi.user.feign.UserApi;
import com.messi.user.vo.UserSaveVo;
import feign.RequestLine;

/**
 * @author Elysion
 * @Description:
 * @date 2018/7/24上午9:25
 */
public interface UserApiService  extends UserApi {

    @RequestLine("register")
    String register(UserSaveVo userSaveVo);
}
