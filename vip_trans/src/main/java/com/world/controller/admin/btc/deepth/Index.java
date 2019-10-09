package com.world.controller.admin.btc.deepth;

import com.tenstar.HTTPTcp;
import com.tenstar.RecordMessage;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/btc/" , des = "委托深度")
public class Index extends AdminAction {

    @Page(Viewer = "/admins/btc/index.jsp")
    public void index() {
        try {
            String tab = param("tab");
            if (tab.length() == 0)
                tab = "btc_cny";

            Market m = Market.getMarkeByName(tab);
            setAttr("params", m);
            setAttr("tab", tab);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }

    /**
     * 获取委托和交易历史记录数据
     */
    @Page(Viewer = ".json" )
    public void getDeepth() {
        try {
            String jsoncallback = request.getParameter("jsoncallback");
            Market m = Market.getMarkeByName(GetPrama(0));
            if (m == null) {
                Response.append(jsoncallback + "({error market})");
                return;
            }
            long lastTime = Long.parseLong(request.getParameter("lastTime"));
            int buy = Integer.parseInt(request.getParameter("buy"));
            int sell = Integer.parseInt(request.getParameter("sell"));
            int trade = Integer.parseInt(request.getParameter("trade"));
            Object obj = DishDataCacheService.getDishDepthLastTime(m.market);

            if (obj == null || lastTime != Long.parseLong(obj.toString())) {
                RecordMessage myObj = new RecordMessage();
                myObj.setPageindex(buy);
                myObj.setDateTo(sell);
                myObj.setPageSize(trade);
                try {
                    String param = HTTPTcp.ObjectToString(myObj);
                    log.info("[请求转发] 请求转发到业务处理服务器 " + m.ip + ":" + m.port);
                    String rtn = HTTPTcp.Post(m.ip, m.port, "/server/getDeepth", param);
                    RecordMessage rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
                    Response.append(jsoncallback + rtn2.getMessage());
                } catch (Exception ex2) {
                    Response.append(jsoncallback + "({\"lastTime\":0})");
                }
            } else{
                Response.append(jsoncallback + "({\"lastTime\":" + lastTime + "})");
            }
        } catch (Exception ex) {
        }
    }

}

