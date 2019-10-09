package com.world.controller;

import com.world.web.Page;
import com.world.web.action.BaseAction;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2019/3/6 10:12 AM
 */
public class CheckAlive extends BaseAction {
    @Page(Viewer = JSON)
    public void index() {
        Response.append("ok");
        return;
    }
}
