package com.world.controller.terms;

import com.world.web.Page;
import com.world.web.action.UserAction;
import org.apache.commons.lang.StringUtils;

/**
 * Created by xie on 2017/6/27.
 */
public class Index extends UserAction {

    @Page()
    public void termsContactUs() {
        String url = "/cn/terms/contact_us";
        try{
            if(StringUtils.isNotBlank(lan)) {
                url = url + "_" +  lan + ".jsp";
            }else{
                url = url + "_cn.jsp";
            }
            request.getRequestDispatcher(url).forward(request,response);
            return;
        }catch(Exception e) {
        }
    }

    @Page()
    public void termsExpenseTable() {
        String url = "/cn/terms/expense_table";
        try{
            if(StringUtils.isNotBlank(lan)) {
                url = url + "_" +  lan + ".jsp";
            }else{
                url = url + "_cn.jsp";
            }
            request.getRequestDispatcher(url).forward(request,response);
            return;
        }catch(Exception e) {
        }
    }

    @Page()
    public void termsPrivacy() {
        String url = "/cn/terms/privacy";
        try{
            if(StringUtils.isNotBlank(lan)) {
                url = url + "_" +  lan + ".jsp";
            }else{
                url = url + "_cn.jsp";
            }
            request.getRequestDispatcher(url).forward(request,response);
            return;
        }catch(Exception e) {
        }
    }

    @Page()
    public void termsRiskStatement() {
        String url = "/cn/terms/risk_statement";
        try{
            if(StringUtils.isNotBlank(lan)) {
                url = url + "_" +  lan + ".jsp";
            }else{
                url = url + "_cn.jsp";
            }
            request.getRequestDispatcher(url).forward(request,response);
            return;
        }catch(Exception e) {
        }
    }

    @Page()
    public void termsNotice() {
        String url = "/cn/terms/notice";
        try{
            if(StringUtils.isNotBlank(lan)) {
                url = url + "_" +  lan + ".jsp";
            }else{
                url = url + "_cn.jsp";
            }
            request.getRequestDispatcher(url).forward(request,response);
            return;
        }catch(Exception e) {
        }
    }


    @Page()
    public void service() {
        String url = "/cn/terms/service";
        try{
            if(StringUtils.isNotBlank(lan)) {
                url = url + "_" +  lan + ".jsp";
            }else{
                url = url + "_cn.jsp";
            }
            request.getRequestDispatcher(url).forward(request,response);
            return;
        }catch(Exception e) {
        }
    }

    @Page()
    public void relief() {
        String url = "/cn/terms/relief";
        try{
            if(StringUtils.isNotBlank(lan)) {
                url = url + "_" +  lan + ".jsp";
            }else{
                url = url + "_cn.jsp";
            }
            request.getRequestDispatcher(url).forward(request,response);
            return;
        }catch(Exception e) {
        }
    }

    @Page()
    public void termsBtNotice() {
        String url = "/cn/terms/bt_notice";
        try{
            if(StringUtils.isNotBlank(lan)) {
                url = url + "_" +  lan + ".jsp";
            }else{
                url = url + "_cn.jsp";
            }
            request.getRequestDispatcher(url).forward(request,response);
            return;
        }catch(Exception e) {
        }
    }


}
