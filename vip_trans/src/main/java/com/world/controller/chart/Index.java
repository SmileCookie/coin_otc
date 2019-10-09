package com.world.controller.chart;
import com.tenstar.HTTPTcp;
import com.tenstar.Message;
import com.world.dish.DishDataCacheService;
import com.world.web.Page;
import com.world.web.Pages;

import java.math.BigDecimal;

public class Index extends Pages {

//    @Page(Viewer = "/cn/chart/index.jsp")
    public void index() {
        try {

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }
//    @Page(Viewer = "/cn/chart/simple.jsp")
    public void s() {
        try {

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }
//    @Page(Viewer = "/cn/u/index.jsp")
    public void trans() {
        try {

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }


    @Page(Viewer = ".json" )
    public void GetData(){
        try{
            String jsoncallback=request.getParameter("jsoncallback");
            long lastTime=Long.parseLong(request.getParameter("lastTime"));
            long length=Long.parseLong(request.getParameter("length"));
            if(length!=5&&length!=10&&length!=20&&length!=50)
            {
                Response.append(jsoncallback+"([{\"lastTime\":"+lastTime+"}])");
                return;
            }
            Object obj=DishDataCacheService.getDishDepthLastTime("default");
            if(obj==null||lastTime==Long.parseLong(obj.toString())){
                Response.append(jsoncallback+"([{\"lastTime\":"+lastTime+"}])");
            }else{
                String data = DishDataCacheService.getDishDepthData("default", (int)length);
                Response.append(jsoncallback+data);
            }
        }
        catch(Exception ex){
        }
    }


    /**
     * 委托的标准例子
     */
//    @Page(Viewer = ".xml" )
    public void entrust(){
        try{
            int userid=105190;//userId();

            double unitPrice=Double.parseDouble(request.getParameter("unitPrice"));
            double number=Double.parseDouble(request.getParameter("number"));
            int isBuy=Integer.parseInt(request.getParameter("isBuy"));

            int webid=8;
            Message myObj = new Message();
            myObj.setUserId(userid);
            myObj.setWebId(webid);
            myObj.setNumbers(BigDecimal.valueOf(number));
            myObj.setTypes(isBuy);
            myObj.setUnitPrice(BigDecimal.valueOf(unitPrice));
            myObj.setStatus(0);
            String param=HTTPTcp.ObjectToString(myObj);
            String rtn=HTTPTcp.DoRequest2(true,"127.0.0.1",800,"/entrust",param);


            Message rtn2 =(Message)HTTPTcp.StringToObject(rtn);

            Write(rtn2.getMessage(),true,rtn2.getStatus()+"");

        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
    }
    /**
     * 取消的标准例子
     */
//    @Page(Viewer = ".xml" )
    public void cancle(){
//		if(!IsLogin()){
//			   toLogin();
//			   return;
//			}
        try{
            int userid=105190;//userId();
            int webid=8;
            long entityId=Long.parseLong(request.getParameter("entrustId"));

            Message myObj = new Message();
            myObj.setUserId(userid);
            myObj.setWebId(webid);
            myObj.setTypes(5);//取消命令
            myObj.setNumbers(BigDecimal.valueOf(entityId));//
            myObj.setStatus(0);

            String param=HTTPTcp.ObjectToString(myObj);
            String rtn=HTTPTcp.DoRequest2(true,"127.0.0.1",800,"/entrust",param);



            Message rtn2 =(Message)HTTPTcp.StringToObject(rtn);

            Write(rtn2.getMessage(),true,rtn2.getStatus()+"");
        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
    }
    /**
     * 获取交易数据
     * @param name  名称，比如 btc  ltc  或者btq
     * @param level 数量级别 5 10 50
     * @param jsoncallback 返回参数
     * @return
     */
    public static String GetTrans(String name,int level,String jsoncallback,long lastTime,int webId,int userId){
        String data= DishDataCacheService.getDishDepthData(name, 60);
        return jsoncallback+data;
    }

}