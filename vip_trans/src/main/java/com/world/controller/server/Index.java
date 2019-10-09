package com.world.controller.server;


import com.match.entrust.MemEntrustMatchProcessor;
import com.match.entrust.MemEntrustSwitchService;
import com.tenstar.HTTPTcp;
import com.tenstar.Info;
import com.tenstar.InfoEntrust;
import com.tenstar.Message;
import com.tenstar.MessageCancle;
import com.tenstar.RecordMessage;
import com.tenstar.SystemStatus;
import com.tenstar.UserConfig;
import com.tenstar.robotConfig;
import com.tenstar.timer.auto.AutoBrushAmount.AutoBrushAmountManager;
import com.tenstar.timer.chart.ChartData;
import com.tenstar.timer.entrust.Interface;
import com.tenstar.timer.wisdom.ChartArray;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Index extends UserAction  {
    public static Logger log = Logger.getLogger(Index.class);
    //public static Market market=Market.InitMarket(GlobalConfig.getValue("market")+"market");
    public Index(){
        //log.info(GlobalConfig.getValue("market"));
    }

    @Page(Viewer = ".json" , Cache = 60)
    public void index(){
        Response.append("[ok]");
    }

    /**
     * 进行委托
     */
    @Page
    public void Entrust(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            Message msg=(Message)HTTPTcp.StringToObject(post);
            if(msg.getTypes()==1||msg.getTypes()==0){
                //进行委托
                //log.info("here2");
                Market m = Market.getMarket(msg.getMarket());
                InfoEntrust in = Interface.doEntrust(msg.getTypes(), msg.getUserId(),msg.getNumbers(), msg.getUnitPrice(),msg.getWebId(), msg.getWebId(),msg.getStatus(), msg.getMt(), msg.getMessage(),m);
                if(in.rs != null && in.rs.getMsg() != null){
                    JSONObject json = JSONObject.fromObject(in.rs.getMsg());
                    in.in.setMessage(json.getString("des"));
                    msg.setCode(in.rs.getCode());
                }
                //log.info("here3"+"处理结果："+in.in.getMessage());

                String smsg = in.in.getMessage();
                if(in.in.equals(Info.DoEntrustFaildPromError)){
                    smsg = L("系统繁忙，请重新委托！");
                }

                msg.setMessage(smsg);
                msg.setStatus(in.in.getNum());
                msg.setNumbers(BigDecimal.valueOf(in.entrustId));
                if(in.equals(Info.DoEntrustSuccess)){//如果没成功就返回状态信息
                    //触发交割功能
                    //SystemStatus.exchangeNewWork=true;
                    SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
                }
                String rtn=HTTPTcp.ObjectToString(msg);
                if(rtn==null)
                    rtn=" ";
                response.getWriter().write( rtn);
            } else{
                msg.setMessage(Info.DoEntrustFaildUnKnowType.getMessage());
                msg.setStatus(Info.DoEntrustFaildUnKnowType.getNum());
                msg.setNumbers(BigDecimal.ZERO);
                String rtn=HTTPTcp.ObjectToString(msg);
                response.getWriter().write( rtn);
            }

        }catch(Exception ex){
            log.error(ex.toString(), ex);
            Message msg=new Message();
            msg.setMessage(L("系统繁忙，请重新委托！"));
            msg.setStatus(Info.DoEntrustFaildPromError.getNum());
            msg.setNumbers(BigDecimal.ZERO);
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 取消单项
     */
    @Page
    public void cancle(){
        try{
            String post=request.getParameter("messageBody");
            MessageCancle msg=(MessageCancle)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            Info in=Interface.doCancle(msg.getUserId(), msg.getEntrustId(),m);

            if(in==Info.DoCancleSuccess){
                //SystemStatus.exchangeNewWork=true;
                SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
            }
            msg.setMessage(in.getMessage());
            msg.setStatus(in.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            if(rtn==null)
                rtn=" ";
            response.getWriter().write( rtn);

        }catch(Exception ex){
            log.error(ex.toString(), ex);
            MessageCancle msg=new  MessageCancle();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 取消多项
     */
    @Page
    public void cancelmore(){
        try{
            String post=request.getParameter("messageBody");
            MessageCancle msg=(MessageCancle)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            int count=Interface.doCancle(msg.getWebId(),msg.getUserId(),msg.getPriceLow(),msg.getPriceHigh(),msg.getType(),m);
            if(count>0)
                //SystemStatus.exchangeNewWork=true;
                SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
            msg.setStatus(count);
            String rtn=HTTPTcp.ObjectToString(msg);
            if(rtn==null)
                rtn=" ";
            response.getWriter().write(rtn);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            MessageCancle msg=new MessageCancle();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 取消多项，刷量使用
     */
    @Page
    public void cancelmoreForBrush(){
        try{
            String post=request.getParameter("messageBody");
            MessageCancle msg=(MessageCancle)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            int count=Interface.doCancleForBrush(msg.getWebId(),msg.getUserId(),msg.getPriceLow(),msg.getPriceHigh(),msg.getType(),m);
            if(count>0)
                //SystemStatus.exchangeNewWork=true;
                SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
            msg.setStatus(count);
            String rtn=HTTPTcp.ObjectToString(msg);
            if(rtn==null)
                rtn=" ";
            response.getWriter().write(rtn);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            MessageCancle msg=new MessageCancle();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    @Page
    public void cancelByIdsForBrush() {
        try {
            String post = request.getParameter("messageBody");
            Message msg = (Message) HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());

            List<Long> ids = new ArrayList<>();
            String strIds = msg.getMessage();
            for (String strId : strIds.split(",")) {
                ids.add(Long.parseLong(strId));
            }
            int count = Interface.doCancleByIdsForBrush(msg.getUserId(), ids, m);
            if (count > 0) {
                SystemStatus.setSystemStatus(m.market + "_" + SystemStatus.exchangeNewWork, true);
            }
            msg.setStatus(count);
            String rtn = HTTPTcp.ObjectToString(msg);
            if (rtn == null)
                rtn = " ";
            response.getWriter().write(rtn);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            Message msg = new Message();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage() + ":" + ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn = HTTPTcp.ObjectToString(msg);
            try {
                response.getWriter().write(rtn);
            } catch (Exception ex2) {
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 取消市场全部订单
     */
    public void cancleAllServer(){
        MessageCancle msg = new MessageCancle();
        try{
            String post=request.getParameter("messageBody");
            Market market=(Market)HTTPTcp.StringToObject(post);
            int count=Interface.doCancleAll(market);
            if(count>0) {
                SystemStatus.setSystemStatus(market.market + "_" + SystemStatus.exchangeNewWork, true);
            }
            msg.setStatus(count);
            String rtn=HTTPTcp.ObjectToString(msg);
            if(rtn==null)
                rtn=" ";
            response.getWriter().write(rtn);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }
    /**
     * 取消多项
     */
    @Page
    public void entrustmore(){
        try{
            String post=request.getParameter("messageBody");
            MessageCancle msg=(MessageCancle)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            String rtn=Interface.doEntrustMore(msg.getWebId(),msg.getUserId(),msg.getPriceLow(),msg.getPriceHigh(),msg.getNumbers(),msg.getType(),m);
            if(rtn!=null)
                // SystemStatus.exchangeNewWork=true;
                SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
            // log.info(rtn);
            msg.setMessage(rtn);
            msg.setStatus(Info.DoEntrustSuccess.getNum());
            String rtn2=HTTPTcp.ObjectToString(msg);
            if(rtn2==null)
                rtn2=" ";
            response.getWriter().write(rtn2);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            MessageCancle msg=new MessageCancle();
            msg.setMessage("0:0:0");
            msg.setStatus(Info.DoEntrustFaildPromError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }


    @Page
    public void traderecord(){
        try{
            String post=request.getParameter("messageBody");
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            String rtn = "";
            if((msg.getPageindex() == 1 || msg.getPageindex() == 0) && msg.getDateTo()==0){
                rtn = MemEntrustMatchProcessor.da.getTraderecordFromMem(msg.getUserId(),m);
            }else{
                rtn = MemEntrustMatchProcessor.da.getTraderecord(msg.getUserId(), msg.getPageindex(), msg.getPageSize(), msg.getDateTo(),m);
            }
            msg.setMessage(rtn);
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write( r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取用户信息
     */
    @Page
    public void userrecord(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            // log.info(msg.getTypes());
            Market m = Market.getMarket(msg.getMarket());
            if(msg.getTypes()==101){//详情
                String rtn=MemEntrustMatchProcessor.da.getDetails(msg.getUserId(),Long.parseLong(msg.getMessage()),m);
                msg.setMessage(rtn);
            }else{//列表
                String rtn=MemEntrustMatchProcessor.da.getTop(msg.getWebId(),
                        msg.getUserId(),
                        msg.getPageindex(),
                        msg.getTypes(),
                        msg.getTimeFrom(),
                        msg.getTimeTo(),
                        msg.getNumberFrom(),
                        msg.getNumberTo(),
                        msg.getPriceFrom(),
                        msg.getPriceTo(),
                        msg.getPageSize(),
                        msg.getStatus(),
                        msg.getDateTo(),
                        m
                );
                msg.setMessage(rtn);
            }
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write( r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取用户信息，不走缓存，直接查库
     */
    @Page
    public void userrecordNoCache(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            // log.info(msg.getTypes());
            Market m = Market.getMarket(msg.getMarket());
            if(msg.getTypes()==101){//详情
                String rtn=MemEntrustMatchProcessor.da.getDetails(msg.getUserId(),Long.parseLong(msg.getMessage()),m);
                msg.setMessage(rtn);
            }else{//列表
                String rtn=MemEntrustMatchProcessor.da.getTopNoCache(msg.getWebId(),
                        msg.getUserId(),
                        msg.getPageindex(),
                        msg.getTypes(),
                        msg.getTimeFrom(),
                        msg.getTimeTo(),
                        msg.getNumberFrom(),
                        msg.getNumberTo(),
                        msg.getPriceFrom(),
                        msg.getPriceTo(),
                        msg.getPageSize(),
                        msg.getStatus(),
                        msg.getDateTo(),
                        m
                );
                msg.setMessage(rtn);
            }
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write( r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取用户历史委托信息，查询 entrust_all 表
     */
    @Page
    public void userRecordHistory(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            // log.info(msg.getTypes());
            Market m = Market.getMarket(msg.getMarket());
            String rtn=MemEntrustMatchProcessor.da.getUserRecordHistory(msg.getWebId(),
                    msg.getUserId(),
                    msg.getPageindex(),
                    msg.getTypes(),
                    msg.getTimeFrom(),
                    msg.getTimeTo(),
                    msg.getNumberFrom(),
                    msg.getNumberTo(),
                    msg.getPriceFrom(),
                    msg.getPriceTo(),
                    msg.getPageSize(),
                    msg.getStatus(),
                    msg.getDateTo(),
                    m
            );
            msg.setMessage(rtn);
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write( r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取用户计划单信息
     */
    @Page
    public void userplanrecord(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            // log.info(msg.getTypes());
            Market m = Market.getMarket(msg.getMarket());

            String rtn=MemEntrustMatchProcessor.da.getTopPlan(msg.getWebId(),
                    msg.getUserId(),
                    msg.getPageindex(),
                    msg.getTypes(),
                    msg.getTimeFrom(),
                    msg.getTimeTo(),
                    msg.getNumberFrom(),
                    msg.getNumberTo(),
                    msg.getPriceFrom(),
                    msg.getPriceTo(),
                    msg.getPageSize(),
                    msg.getStatus(),
                    msg.getDateTo(),
                    m
            );
            msg.setMessage(rtn);

            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write( r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取用户交易记录
     */
    @Page
    public void getTradeRecord(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            // log.info(msg.getTypes());
            Market m = Market.getMarket(msg.getMarket());
            String rtn = MemEntrustMatchProcessor.da.getUserTransRecord(msg.getUserId(), Long.parseLong(msg.getMessage()), msg.getPageSize(),m );
            msg.setMessage(rtn);
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write( r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取用户交易记录
     */
    @Page
    public void getOrderTradeRecord(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            // log.info(msg.getTypes());
            Market m = Market.getMarket(msg.getMarket());
            String rtn = MemEntrustMatchProcessor.da.getOrderTransRecord(msg.getUserId(), Long.parseLong(msg.getMessage()),m);
            msg.setMessage(rtn);
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write( r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取用户信息
     */
    @Page
    public void getOrders(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            log.info("getOrders:types:" + msg.getTypes() + ",userId:" + msg.getUserId() + ",status:" + msg.getStatus() + ",msg:" + msg.getMessage() + ",ip:" + ip());
            if(msg.getTypes()==101 && StringUtils.isNotEmpty(msg.getMessage())){//详情
                String rtn=MemEntrustMatchProcessor.da.getOrderByEntrustId(msg.getUserId(), Long.parseLong(msg.getMessage()),m);
                msg.setMessage(rtn);
            }else{//列表
                String rtn=MemEntrustMatchProcessor.da.getTop(msg.getWebId(),
                        msg.getUserId(),
                        msg.getPageindex(),
                        msg.getTypes(),
                        msg.getTimeFrom(),
                        msg.getTimeTo(),
                        msg.getNumberFrom(),
                        msg.getNumberTo(),
                        msg.getPriceFrom(),
                        msg.getPriceTo(),
                        msg.getPageSize(),
                        msg.getStatus(),
                        m
                );
                msg.setMessage(rtn);
            }
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write( r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取统计数据
     */
    @Page(Viewer = "")
    public void getStatisticsRecord(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            String rtn=MemEntrustMatchProcessor.da.getStatisticsRecord(msg.getWebId(),
                    msg.getUserId(),
                    msg.getTypes(),
                    msg.getTimeFrom(),
                    msg.getTimeTo(),
                    msg.getNumberFrom(),
                    msg.getNumberTo(),
                    msg.getPriceFrom(),
                    msg.getPriceTo(),
                    msg.getStatus(),
                    m
            );
            msg.setMessage(rtn);
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write(r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取最新交易信息
     * 参数pagesize代表档位  5 10 20 50
     */
    @Page
    public void getTrade(){
        try{
            String post=request.getParameter("messageBody");

            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            log.info("获取trade"+msg.getPageSize());
            //String rtn=MemEntrustMatchProcessor.da.SetTrade(msg.getPageSize(),m);//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
            String rtn = "";
            msg.setMessage(rtn);
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write(r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }
    /**
     * 获取最新交易信息
     * 参数pagesize代表档位  5 10 20 50
     */
    @Page
    public void getticker(){
        try{
            String post=request.getParameter("messageBody");
            log.info("ip:" + ip());
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            String rtn=ChartData.GetTiker(m);//.da.GetDepth50();//.SetTrade(msg.getPageSize());//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
            if(rtn==null)
                rtn=" ";
            msg.setMessage(rtn);

            String r=HTTPTcp.ObjectToString(msg);
            response.getWriter().write(r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取最新交易信息
     * 参数pagesize代表档位  5 10 20 50
     */
    @Page
    public void getdepth(){
        try{
            String post=request.getParameter("messageBody");
            // log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            String rtn = "[]";


            if(msg.getPageSize() <= 50){
                rtn=MemEntrustMatchProcessor.da.GetDepth50(m);//.GetDepth200();//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
            }else{
                if(msg.getPageSize() == 100 || msg.getPageSize() == 200 || msg.getPageSize() == 300 || msg.getPageSize() == 400 || msg.getPageSize() == 500){
                    //rtn=MemEntrustMatchProcessor.da.GetDepthN(msg.getPageSize(),m);//.GetDepth200();//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
                }
            }
            //log.info(rtn.length());
            msg.setMessage(rtn);
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write(r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }
    /**
     * 获取最新交易信息
     * 参数pagesize代表档位  5 10 20 50
     */
    @Page
    public void getTradeOuter(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            String rtn=MemEntrustMatchProcessor.da.GetTradeListOuter(msg.getPageindex(),m);//MemEntrustMatchProcessor.da.SetTrade(msg.getPageSize());//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
            msg.setMessage(rtn);
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write(r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 同步数据
     * 参数pagesize代表档位  5 10 20 50
     */
    @Page
    public void syndata(){
        try{
            String post=request.getParameter("messageBody");
            // log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
		   /* SystemStatus.exchangeRuning=false;
		    SystemStatus.chartDataRuning=false;
		    	//MemEntrustMatchProcessor.da.ReInit();//.SetTrade(msg.getPageSize());//.getCurrentPrice();//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
		    	SystemStatus.exchangeRuning=true;
		    	  SystemStatus.chartDataRuning=true;*/

            msg.setMessage("true");
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write(r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    @Page
    public void data(){
        try{
            String post=request.getParameter("messageBody");
            Message msg=(Message)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());

            String rtn=ChartData.caMap.get(m.market).Get(msg.getTypes(),m);//"


            msg.setMessage("ok");
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write(r);
        }catch(Exception ex){
            Message msg=new Message();
            msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    @Page
    public void wisdom(){
        try{
            String post=request.getParameter("messageBody");
            Message msg=(Message)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            ChartArray ca = new ChartArray(m.market,360,1000,3600);
            String rtn = ca.Get(msg.getTypes(),m);//"

            msg.setMessage("ok");
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write(r);
        }catch(Exception ex){
            Message msg=new Message();
            msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取用户信息
     * 没有参数
     */
    @Page
    public void getCurrentPrice(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            RecordMessage msg=(RecordMessage)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            String rtn=MemEntrustMatchProcessor.da.getCurrentPrice(m);//.getDetails(msg.getUserId(), Long.parseLong(msg.getMessage()));
            msg.setMessage(rtn);
            String r=HTTPTcp.ObjectToString(msg);
            if(r==null)
                r=" ";
            response.getWriter().write(r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msg=new RecordMessage();
            msg.setMessage(Info.GetMessageError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }


    /**
     * 设置系统自动项
     */
    @Page
    public void setSystemAuto(){
        try{
            String post=request.getParameter("messageBody");
            robotConfig msg=(robotConfig)HTTPTcp.StringToObject(post);
            //log.info("保存");
            String market =msg.getMarket();
            Market m =Market.getMarket(market);

            Interface.setSystemAuto(msg,m);
            response.getWriter().write("ok");
        }catch(Exception ex){
            try{
                response.getWriter().write(ex.toString());
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 设置系统自动项
     */
    @Page
    public void setSystemEntrustAuto(){
        try{
            String post=request.getParameter("messageBody");
            com.tenstar.RobotEntrustConfig msg= (com.tenstar.RobotEntrustConfig)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            // log.info("保存");
            Interface.setSystemEnstrustAuto(msg,m);
            response.getWriter().write("ok");
        }catch(Exception ex){
            try{
                response.getWriter().write(ex.toString());
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 设置系统自动项
     */
    @Page
    public void saveAutoShualiangParams(){
        try{
            String post=request.getParameter("messageBody");
            com.tenstar.RobotShualiangConfig msg= (com.tenstar.RobotShualiangConfig)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            // log.info("保存");
            Interface.saveAutoShualiangParams(msg,m);
            response.getWriter().write("ok");
        }catch(Exception ex){
            try{
                response.getWriter().write(ex.toString());
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    @Page
    public void setUserAuto(){
        try{
            String post=request.getParameter("messageBody");
            UserConfig msg=(UserConfig)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            log.info("保存");
            Interface.setUserAuto(msg,m);
            response.getWriter().write("ok");
        }catch(Exception ex){
            try{
                response.getWriter().write(ex.toString());
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }


    /**
     * 进行委托
     */
    @Page
    public void vitureEntrust(){
        try{
            String post=request.getParameter("messageBody");
            Message msg=(Message)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            //MemEntrustMatchProcessor.da.updateEntrustSystem2((int)msg.getUnitPrice(),msg.getNumbers());
            //MemEntrustMatchProcessor.da.SetTrade(m);//整体更新一次数据
            response.getWriter().write("success");
        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
    }

    @Page
    public void cancelVitureEntrust(){
        try{
			/* String post=request.getParameter("messageBody");
			 MessageCancle msg=(MessageCancle)HTTPTcp.StringToObject(post);
			 Market m = Market.getMarket(msg.getMarket());
			 long priceLow = msg.getPriceLow();
			 long priceHigh = msg.getPriceHigh();
			 for (int i = (int) priceLow; i<=priceHigh; i++) {
				 MemEntrustMatchProcessor.da.data[i][1]=0;
			 }
			 MemEntrustMatchProcessor.da.SetTrade(m);//整体更新一次数据
			 response.getWriter().write("success");
			 */
        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
    }

    @Page
    public void getVitureEntrust(){
		/* String result = "";
		 for (int i = 1000; i<1000000; i++) {
			 if(MemEntrustMatchProcessor.da.data[i][1] != 0){
			//	 result += "[" + Market.formatMoney(i) + "," + Math.abs(Market.formatNumber(MemEntrustMatchProcessor.da.data[i][1])) + "],";
			 }
		 }
		 if(StringUtils.isNotEmpty(result))
			 result = result.substring(0, result.length() - 1);
		 result = "[" + result + "]";
	 	try {
	 		response.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
		*/
    }


    /**
     * 获取市场深度
     */
    @Page
    public void getDeepth() {
        try {
            String post = request.getParameter("messageBody");
            RecordMessage msg = (RecordMessage) HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            //String rtn = MemEntrustMatchProcessor.da.getDeepth(msg.getPageindex(), msg.getDateTo(), msg.getPageSize(),m);
            String rtn = "";
            msg.setMessage(rtn);
            String r = HTTPTcp.ObjectToString(msg);
            if (r == null)
                r = " ";
            response.getWriter().write(r);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            RecordMessage msg = new RecordMessage();
            msg.setMessage(Info.GetMessageError.getMessage() + ":" + ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn = HTTPTcp.ObjectToString(msg);
            try {
                response.getWriter().write(rtn);
            } catch (Exception ex2) {
                log.error(ex2.toString(), ex2);
            }
        }
    }


    /**
     * 进行计划委托
     */
    @Page
    public void planEntrust(){
        try{
            String post=request.getParameter("messageBody");
            //log.info(post);
            Message msg=(Message)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            if(msg.getTypes()==1||msg.getTypes()==0){
                //进行委托
                //log.info("here2");
                InfoEntrust in=Interface.doPlanEntrust(msg.getTypes(), msg.getUserId(),msg.getNumbers(), msg.getUnitPrice(),msg.getWebId(),
                        msg.getWebId(),msg.getStatus(), msg.getMt(), msg.getMessage(),msg.getTriggerPrice(),msg.getUnitPriceProfit(),msg.getTriggerPriceProfit(),msg.getTotalMoney(),m);
                if(in.rs != null && in.rs.getMsg() != null){
                    JSONObject json = JSONObject.fromObject(in.rs.getMsg());
                    in.in.setMessage(json.getString("des"));
                    msg.setCode(in.rs.getCode());
                }
                //log.info("here3"+"处理结果："+in.in.getMessage());

                String smsg = in.in.getMessage();
                if(in.in.equals(Info.DoEntrustFaildPromError)){
                    smsg =L("系统繁忙，请重新委托！");
                }

                msg.setMessage(smsg);
                msg.setStatus(in.in.getNum());
                msg.setNumbers(BigDecimal.valueOf(in.entrustId));
                if(in.equals(Info.DoEntrustSuccess)){//如果没成功就返回状态信息
                    //触发交割功能
                    //SystemStatus.exchangeNewWork=true;
                    SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
                }
                String rtn=HTTPTcp.ObjectToString(msg);
                if(rtn==null)
                    rtn=" ";
                response.getWriter().write( rtn);
            } else{
                msg.setMessage(Info.DoEntrustFaildUnKnowType.getMessage());
                msg.setStatus(Info.DoEntrustFaildUnKnowType.getNum());
                msg.setNumbers(BigDecimal.ZERO);
                String rtn=HTTPTcp.ObjectToString(msg);
                response.getWriter().write( rtn);
            }


        }catch(Exception ex){
            log.error(ex.toString(), ex);
            Message msg=new Message();
            msg.setMessage(L("系统繁忙，请重新委托！"));
            msg.setStatus(Info.DoEntrustFaildPromError.getNum());
            msg.setNumbers(BigDecimal.ZERO);
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 取消单项计划委托
     */
    @Page
    public void canclePlanEntrust(){
        try{
            String post=request.getParameter("messageBody");
            MessageCancle msg=(MessageCancle)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            Info in=Interface.doCanclePlanEntrust(msg.getUserId(), msg.getEntrustId(),m);

            if(in==Info.DoCancleSuccess){
                //SystemStatus.exchangeNewWork=true;
                SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
            }
            msg.setMessage(in.getMessage());
            msg.setStatus(in.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            if(rtn==null)
                rtn=" ";
            response.getWriter().write( rtn);

        }catch(Exception ex){
            log.error(ex.toString(), ex);
            MessageCancle msg=new  MessageCancle();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 批量取消计划委托
     */
    @Page
    public void cancelmorePlanEntrust(){
        try{
            int count = 0;
            String post=request.getParameter("messageBody");
            MessageCancle msg=(MessageCancle)HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            if(msg.getMessage()!=null && msg.getMessage().equals("ALL")){
                count+=	Interface.doCancle(msg.getWebId(),msg.getUserId(),msg.getPriceLow(),msg.getPriceHigh(),msg.getType(),m);
            }

            count+=Interface.doCancleMorePlanEntrust(msg.getWebId(),msg.getUserId(),msg.getPriceLow(),msg.getPriceHigh(),msg.getType(),m);
            if(count>0)
                // SystemStatus.exchangeNewWork=true;
                SystemStatus.setSystemStatus(m.market+"_"+SystemStatus.exchangeNewWork,true);
            msg.setStatus(count);
            String rtn=HTTPTcp.ObjectToString(msg);
            if(rtn==null)
                rtn=" ";
            response.getWriter().write(rtn);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            MessageCancle msg=new MessageCancle();
            msg.setMessage(Info.DoCancleFailPriceError.getMessage()+":"+ex.toString());
            msg.setStatus(Info.DoCancleFailPriceError.getNum());
            String rtn=HTTPTcp.ObjectToString(msg);
            try{
                response.getWriter().write( rtn);
            }catch(Exception ex2){
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 刷量
     *
     * renfei
     */
    @Page
    public void brushAmount(){
        try{
            String amount = request.getParameter("amount");
            String coinType = request.getParameter("coinType");

            String marketName = "ltc_btc";
            if("ltc_btc".equalsIgnoreCase(coinType)){
                marketName = "ltc_btc";
            }else if("etc_btc".equalsIgnoreCase(coinType)){
                marketName = "etc_btc";
            }

            Market m = Market.getMarket(marketName);

            BigDecimal amountBD = new BigDecimal(amount);
            AutoBrushAmountManager.INSTANCE.brushAmount(amountBD, m);


            JSONObject json = new JSONObject();
            json.put("status", 0);
            json.put("message", "success");
            response.getWriter().write(json.toString());

        }catch(Exception ex){
            log.error("异常:", ex);

            JSONObject json = new JSONObject();
            json.put("status", -1);
            json.put("message", "系统繁忙");
            try{
                response.getWriter().write(json.toString());
            }catch(Exception ex2){
                log.error("异常2:", ex2);
            }

        }
    }

    /**
     * 同步交易数据
     *
     * renfei
     */
    @Page
    public void syncTrades(){
        try{
            String amount = request.getParameter("amount");
            String price = request.getParameter("price");
            String type = request.getParameter("type");
            String coinType = request.getParameter("coinType");

            String buyOne = request.getParameter("buy");
            String sellOne = request.getParameter("sell");

            String marketName = StringUtils.lowerCase(coinType);

            Market m = Market.getMarket(marketName);

            BigDecimal amountBD = new BigDecimal(amount);
            BigDecimal priceBD = new BigDecimal(price);
            BigDecimal buyOneBD = new BigDecimal(buyOne);
            BigDecimal sellOneBD = new BigDecimal(sellOne);
            AutoBrushAmountManager.INSTANCE.syncTrades(buyOneBD, sellOneBD, priceBD, amountBD, type, m);


            JSONObject json = new JSONObject();
            json.put("status", 0);
            json.put("message", "success");
            response.getWriter().write(json.toString());

        }catch(Exception ex){
            log.error("异常:", ex);

            JSONObject json = new JSONObject();
            json.put("status", -1);
            json.put("message", "系统繁忙");
            try{
                response.getWriter().write(json.toString());
            }catch(Exception ex2){
                log.error("异常2:", ex2);
            }

        }
    }


    /**
     * 获取买一卖一价格
     *
     * renfei
     */
    @Page(Viewer = JSON)
    public void buyOneSellOne(){
        try{
            String coinType = request.getParameter("coinType");

//			String marketName = StringUtils.lowerCase(coinType);

//			Market m = Market.getMarket(marketName);

//			BigDecimal[] buysell = ChartManager.getLbuyOneAndSellOne(m);

//			{
//				"ticker":{
//						"high":"0.078894",
//						"low":"0.071943",
//						"buy":"0.075001",
//						"sell":"0.075009",
//						"last":"0.075005000",
//						"vol":"106.4359",
//						"riseRate":-2.20999999999999996447286321199499070644378662109375,
//						"weekRiseRate":0,
//						"monthRiseRate":0,
//						"totalBtcNum":7.893202,
//						"hour6RiseRate":0.076702000,
//						"month3RiseRate":0,
//						"month6RiseRate":0
//				}
//			}
            //([{"lastTime":1502157862325,"currentPrice":0.015970000,"high":0.01597,"low":0.013929,"currentIsBuy":true,"dayNumber":380.301,"totalBtc":380.301,"listUp":[[0.01597,0.553],[0.016299,2.17],[0.016308,2.95],[0.016317,3.4],[0.016328,4.25]],"listDown":[[0.013416,0.64],[0.013414,0.77],[0.013413,1.49],[0.01341,0.61],[0.013408,0.29]],"transction":[]}])
            String tickerJsonStr = DishDataCacheService.getDishDepthData(coinType, 60);

//            log.info(">>>>>> 量化获取市场买一买一价格: " + tickerJsonStr);
            if (StringUtil.exist(tickerJsonStr) && tickerJsonStr.length() > 6) {
                com.alibaba.fastjson.JSONObject tickerJson = com.alibaba.fastjson.JSONObject.parseObject(tickerJsonStr);

                BigDecimal buy = tickerJson.getJSONArray("listDown").getJSONArray(0).getBigDecimal(0);
                BigDecimal sell = tickerJson.getJSONArray("listUp").getJSONArray(0).getBigDecimal(0);
                BigDecimal price = tickerJson.getBigDecimal("currentPrice");

                JSONObject json = new JSONObject();
                json.put("status", 0);

                JSONArray jsonArray = new JSONArray();
                jsonArray.add(buy);
                jsonArray.add(sell);
                jsonArray.add(price);
                json.put("message", jsonArray);
                response.getWriter().write(json.toString());
                return;
            }


            log.error("外部接口获取 " + coinType + " ticker信息为空");

            JSONObject json = new JSONObject();
            json.put("status", -1);
            json.put("message", "buy one and sell one un init.");
            response.getWriter().write(json.toString());

            return;

        }catch(Exception ex){
            log.error("异常:", ex);

            JSONObject json = new JSONObject();
            json.put("status", -1);
            json.put("message", "系统繁忙");
            try{
                response.getWriter().write(json.toString());
            }catch(Exception ex2){
                log.error("异常2:", ex2);
            }

        }
    }


    /**
     * 获取用户历史委托（已成交和已取消）
     */
    @Page
    public void getUserEntrustHistory() {
        try {
            String post = request.getParameter("messageBody");
            RecordMessage msg = (RecordMessage) HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());

            String table = "";
            if ("0".equals(msg.getMessage())) {
                table = "entrust";
            }else{
                table = "entrust_all";
            }

            String rtn = MemEntrustMatchProcessor.da.getUserEntrustHistory(msg.getUserId(),
                    msg.getTypes(),
                    msg.getStatus(),
                    msg.getPageindex(),
                    msg.getPageSize(),
                    m,
                    table
            );
            msg.setMessage(rtn);

            String r = HTTPTcp.ObjectToString(msg);
            if (r == null) {
                r = " ";
            }
            response.getWriter().write(r);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            RecordMessage msg = new RecordMessage();
            msg.setMessage(Info.GetMessageError.getMessage() + ":" + ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn = HTTPTcp.ObjectToString(msg);
            try {
                response.getWriter().write(rtn);
            } catch (Exception ex2) {
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取用户成交记录
     */
    @Page
    public void getUserTransRecordHistory() {
        try {
            String post = request.getParameter("messageBody");
            RecordMessage msg = (RecordMessage) HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());

            String table = "";
            if ("0".equals(msg.getMessage())) {
                table = "transrecord";
            }else{
                table = "transrecord_all";
            }
            String rtn = MemEntrustMatchProcessor.da.getUserTransRecordHistory(msg.getUserId(),
                    msg.getTypes(),
                    msg.getPageindex(),
                    msg.getPageSize(),
                    m,
                    table
            );
            msg.setMessage(rtn);
            String r = HTTPTcp.ObjectToString(msg);
            if (r == null) {
                r = " ";
            }
            response.getWriter().write(r);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            RecordMessage msg = new RecordMessage();
            msg.setMessage(Info.GetMessageError.getMessage() + ":" + ex.toString());
            msg.setStatus(Info.GetMessageError.getNum());
            String rtn = HTTPTcp.ObjectToString(msg);
            try {
                response.getWriter().write(rtn);
            } catch (Exception ex2) {
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 获取用户历史委托（已成交和已取消）
     */
    @Page
    public void getUserEntrustHistoryFor24() {
        try {
            String post = request.getParameter("messageBody");
            RecordMessage msg = (RecordMessage) HTTPTcp.StringToObject(post);
            Market m = Market.getMarket(msg.getMarket());
            String table = "entrust";
            String rtn = MemEntrustMatchProcessor.da.getUserEntrustHistoryFor24(msg.getUserId(),
                    msg.getTypes(),
                    msg.getStatus(),
                    msg.getPageindex(),
                    msg.getPageSize(),
                    m,
                    table
            );
            msg.setMessage(rtn);
            String r = HTTPTcp.ObjectToString(msg);
            if (r == null) {
                r = " ";
            }
            response.getWriter().write(r);
        }catch(Exception ex){
            log.error(ex.toString(), ex);
            RecordMessage msgError = new RecordMessage();
            msgError.setMessage(Info.GetMessageError.getMessage() + ":" + ex.toString());
            msgError.setStatus(Info.GetMessageError.getNum());
            String rtn = HTTPTcp.ObjectToString(msgError);
            try {
                response.getWriter().write(rtn);
            } catch (Exception ex2) {
                log.error(ex2.toString(), ex2);
            }
        }
    }

    /**
     * 切换撮合引擎开关
     */
    @Page
    public void switchMatchOpen() {
        boolean result = false;
        try {
            String post = request.getParameter("messageBody");
            Message msg = (Message) HTTPTcp.StringToObject(post);
            try {
                if (msg.getTypes() == 0) {
                    MemEntrustSwitchService.closeMatchSwitch(msg.getMarket());
                } else {
                    MemEntrustSwitchService.openMatchSwitch(msg.getMarket());
                }
                result = MemEntrustSwitchService.getMatchSwitch(msg.getMarket());
            } catch (Exception ex) {
                log.error(ex.toString(), ex);
            }
            String r = HTTPTcp.ObjectToString(result);
            if (r == null) {
                r = " ";
            }
            response.getWriter().write(r);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            try {
                String r = HTTPTcp.ObjectToString(result);
                response.getWriter().write(r);
            } catch (Exception ex2) {
                log.error(ex2.toString(), ex2);
            }
        }
    }

    public static void main(String[] args) {
        String tickerJsonStr = "([{\"lastTime\":1502157862325,\"currentPrice\":0.015970000,\"high\":0.01597,\"low\":0.013929,\"currentIsBuy\":true,\"dayNumber\":380.301,\"totalBtc\":380.301,\"listUp\":[[0.01597,0.553],[0.016299,2.17],[0.016308,2.95],[0.016317,3.4],[0.016328,4.25]],\"listDown\":[[0.013416,0.64],[0.013414,0.77],[0.013413,1.49],[0.01341,0.61],[0.013408,0.29]],\"transction\":[]}])";
        tickerJsonStr = tickerJsonStr.substring(2, tickerJsonStr.length()-2);

        System.out.println(tickerJsonStr);
        com.alibaba.fastjson.JSONObject tickerJson = com.alibaba.fastjson.JSONObject.parseObject(tickerJsonStr);

        BigDecimal buy = tickerJson.getJSONArray("listDown").getJSONArray(0).getBigDecimal(0);
        BigDecimal sell = tickerJson.getJSONArray("listUp").getJSONArray(0).getBigDecimal(0);
        BigDecimal price = tickerJson.getBigDecimal("currentPrice");


        System.out.println(buy + "," + sell + "," + price);
    }

}