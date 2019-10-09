package com.world.controller.entrust;

import com.world.web.Page;
import com.world.web.action.BaseAction;

import java.io.IOException;

public class Index extends BaseAction {
	
	private static final long serialVersionUID = 1L;
	
	/***
	 * 委托记录
	 */
	@Page
	public void index() {

        //TODO jsp页面跳转，之后下掉jsp
        try {
            response.sendRedirect(VIP_DOMAIN);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

	}

    /***
     * 新版委托记录
     */
    @Page
    public void list() {
        //TODO jsp页面跳转，之后下掉jsp
        try {
            response.sendRedirect(VIP_DOMAIN);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        log.info("entrust_new.jsp");
    }

    /***
     * 新版成交记录
     */
    @Page
    public void transrecord() {
        //TODO jsp页面跳转，之后下掉jsp
        try {
            response.sendRedirect(VIP_DOMAIN);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        log.info("transrecord.jsp");
    }
}

