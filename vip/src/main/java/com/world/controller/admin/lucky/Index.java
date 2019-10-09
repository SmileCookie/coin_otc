package com.world.controller.admin.lucky;

import com.alibaba.fastjson.JSONObject;
import com.atlas.BizException;
import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.admin.logs.DailyRecordDao;
import com.world.model.dao.event.EventInfoDao;
import com.world.model.dao.lucky.LuckyEventDao;
import com.world.model.dao.lucky.LuckyQualifyDao;
import com.world.model.dao.lucky.LuckyRuleDao;
import com.world.model.dao.vote.VoteDao;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.event.EventInfo;
import com.world.model.entity.lucky.LuckyEvent;
import com.world.model.entity.lucky.LuckyQualify;
import com.world.model.entity.lucky.LuckyRule;
import com.world.model.entity.vote.Activity;
import com.world.util.CommonUtil;
import com.world.util.date.TimeUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@FunctionAction(jspPath = "/admins/lucky/", des = "抽奖管理")
public class Index extends FinanAction {

    EventInfoDao eventInfoDao = new EventInfoDao();
    LuckyEventDao luckyEventDao = new LuckyEventDao();
    LuckyQualifyDao luckyQualifyDao = new LuckyQualifyDao();
    LuckyRuleDao luckyRuleDao = new LuckyRuleDao();
    VoteDao voteDao = new VoteDao();

    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        List<EventInfo> eventInfoList = null;
        try {
            String title = param("title");
            String status = param("status");
            eventInfoList = eventInfoDao.getEventList(title,status,null);
        } catch (BizException e) {
            log.error("【抽奖后台】查询活动发生受控异常，异常信息为:"+e.getMessage());
        }catch (Exception e) {
            log.error("【抽奖后台】查询活动发生非受控异常，异常信息为:", e);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put(Const.EVENT_STATUS_UNSTART, "未开始");
        map.put(Const.EVENT_STATUS_ING, "进行中");
        map.put(Const.EVENT_STATUS_SUSPEND, "暂停");
        map.put(Const.EVENT_STATUS_OVER, "结束");

        setAttr("statusMap",map);
        setAttr("dataList", eventInfoList);
    }

    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }


    @Page(Viewer = DEFAULT_AORU)
    public void aoru() {
        try {
            String eventId = param("eventId");
            List<Activity> activityList = voteDao.getList("0,1,2", "cn", "");
            Map<String,String> activityLinkMap = new LinkedHashMap<String,String>();
            if(!CollectionUtils.isEmpty(activityList)){
                for(Activity vo : activityList){
                    activityLinkMap.put(vo.getActivityId(),vo.getActivityNameJson());
                }
            }
            setAttr("activityLinkMap",activityLinkMap);
            if (StringUtil.exist(eventId)) {
                EventInfo eventInfo = null;
                eventInfo = eventInfoDao.getEventById(eventId);
                if(eventInfo != null){
                    JSONObject jsonTitle = JSONObject.parseObject(eventInfo.getEventTitleJson());
                    JSONObject jsonContent = JSONObject.parseObject(eventInfo.getEventContentJson());
                    JSONObject jsonRule = JSONObject.parseObject(eventInfo.getEventRuleJson());

                    eventInfo.setEventTitleCN(jsonTitle.getString("cn"));
                    eventInfo.setEventTitleHK(jsonTitle.getString("hk"));
                    eventInfo.setEventTitleEN(jsonTitle.getString("en"));

                    eventInfo.setEventContentCN(jsonContent.getString("cn"));
                    eventInfo.setEventContentHK(jsonContent.getString("hk"));
                    eventInfo.setEventContentEN(jsonContent.getString("en"));

                    eventInfo.setEventRuleCN(jsonRule.getString("cn"));
                    eventInfo.setEventRuleHK(jsonRule.getString("hk"));
                    eventInfo.setEventRuleEN(jsonRule.getString("en"));

                    LuckyEvent luckyEvent = null;
                    luckyEvent = luckyEventDao.findOneByEventId(eventId);
                    LuckyRule luckyRule = luckyRuleDao.getRuleInfo(luckyEvent.getLuckyId());
                    setAttr("luckyRule",luckyRule);
                    setAttr("luckyEvent",luckyEvent);
                    setAttr("eventInfo", eventInfo);
                }
            }
        } catch (Exception e) {
            log.error("【抽奖后台】展示详情异常，异常信息为：", e);
        }
    }

    @Page(Viewer = ".xml")
    public void insert() {
        try {
            List<OneSql> batchSQL = new ArrayList<>();
            String eventTitleCN = param("eventTitleCN");
            String eventTitleHK = param("eventTitleHK");
            String eventTitleEN = param("eventTitleEN");
            String eventContentCN = request.getParameter("eventContentCN").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String eventContentHK = request.getParameter("eventContentHK").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String eventContentEN = request.getParameter("eventContentEN").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String eventRuleCN = request.getParameter("eventRuleCN").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String eventRuleHK = request.getParameter("eventRuleHK").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String eventRuleEN = request.getParameter("eventRuleEN").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            Timestamp startTimeTmp = dateParam("startTime");
            Timestamp endTimeTmp = dateParam("endTime");
            List<EventInfo> eventInfos = eventInfoDao.getComplieLucky();
            boolean falg = false;
            if(!CollectionUtils.isEmpty(eventInfos)){
                for(EventInfo vo :eventInfos){
                    if(TimeUtil.isOverlap(startTimeTmp,endTimeTmp,vo.getStartTime(),vo.getEndTime())){
                        falg = true;
                        break;
                    }
                }
            }
            if(falg){
                Write("活动时间不得与其他活动重叠", false, "活动时间不得与其他活动重叠");
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String begin = format.format(startTimeTmp);
            String end = format.format(endTimeTmp);
            Date startTime = format.parse(begin);
            Date endTime = format.parse(end);
            if(startTime.compareTo(new Date())!= 1){
                Write("活动开始时间不能早于当前时间", false, "活动开始时间不能早于当前时间");
                return;
            }
            JSONObject eventTitleJson = new JSONObject();
            JSONObject eventContentJson = new JSONObject();
            JSONObject eventRuleJson = new JSONObject();
            eventTitleJson.put("en", eventTitleEN);
            eventTitleJson.put("cn", eventTitleCN);
            eventTitleJson.put("hk", eventTitleHK);
            eventContentJson.put("en", eventContentEN);
            eventContentJson.put("cn", eventContentCN);
            eventContentJson.put("hk", eventContentHK);
            eventRuleJson.put("en", eventRuleEN);
            eventRuleJson.put("cn", eventRuleCN);
            eventRuleJson.put("hk", eventRuleHK);
            EventInfo eventInfo = new EventInfo();
            eventInfo.setEventTitleJson(eventTitleJson.toString());
            eventInfo.setEventContentJson(eventContentJson.toString());
            eventInfo.setEventRuleJson(eventRuleJson.toString());
            eventInfo.setStartTime(startTime);
            eventInfo.setEndTime(endTime);
            eventInfo.setCreateUserName(adminName());
            eventInfo.setStatus(Const.EVENT_STATUS_UNSTART);
            eventInfo.setEventType(Const.EVENT_TYPE_LUCKY);
            String currTime = format.format(new Date());
            eventInfo.setUpdateTime(new Date());
            eventInfo.setCreateTime(new Date());
            eventInfo.setEventId(TimeUtil.getCurrentTime17ByMillis());
            String eventSQL = "insert into eventinfo(eventId, eventTitleJson, eventContentJson, eventRuleJson, startTime, endTime, "
                    + "status, createUserName, eventType, updateTime, createTime) "
                    + "values ('" + eventInfo.getEventId() + "', '" + eventInfo.getEventTitleJson() + "','" + eventInfo.getEventContentJson() + "','" + eventInfo.getEventRuleJson() +
                    "','" + begin + "','" + end + "','" + eventInfo.getStatus() + "','" + eventInfo.getCreateUserName() + "','" + eventInfo.getEventType() + "','" + currTime+ "','" +currTime+"')";
            batchSQL.add(new OneSql(eventSQL, 1, new Object[]{}));

            String cycleLimitType = param("cycleLimitType");
            String cycleLimitCount = param("cycleLimitCount");
            String limitCount = param("limitCount");
            String relateEventId = param("relateEventId");
            String isHighest = param("isHighest");
            String isDouble = param("isDouble");

            if(!StringUtil.exist(limitCount)){
                Write("用户抽奖上限不能为空", false, "用户抽奖上限不能为空");
                return;
            }
            if(!StringUtil.exist(cycleLimitType)){
                Write("抽奖限制不能为空", false, "抽奖限制不能为空");
                return;
            }else{
                if(cycleLimitType.equals("03")||cycleLimitType.equals("04")){
                   if(!StringUtil.exist(relateEventId)){
                       Write("关联活动不能为空", false, "关联活动不能为空");
                       return;
                   }
                }
            }
            if(!StringUtil.exist(isHighest)){
                isHighest = "01";
            }else{
                if("02".equals(isHighest)){
                    cycleLimitType ="04";
                }
            }
            if(!StringUtil.exist(isDouble)){
                isDouble = "01";
            }else{
                if("02".equals(isDouble)){
                    cycleLimitType ="04";
                }
            }
            LuckyEvent luckyEvent = new LuckyEvent();
            luckyEvent.setLuckyId(TimeUtil.getCurrentTime17ByMillis());
            String luckySQL = "INSERT INTO luckyevent(luckyId,eventId,cycleLimitType,cycleLimitCount,limitCount,relateEventId,isHighest,isDouble,updateTime,createTime) "
                    +"values ('" + luckyEvent.getLuckyId()  + "','" + eventInfo.getEventId()+ "','" + cycleLimitType +"','" + cycleLimitCount +"','" + limitCount +"','" + relateEventId +"','" + isHighest +"','" + isDouble  + "','" + currTime + "','" + currTime+"')";
            batchSQL.add(new OneSql(luckySQL, 1, new Object[]{}));

            String ruleType = param("ruleType");
            if(!StringUtil.exist(ruleType)){
                Write("抽奖规则不能为空", false, "抽奖规则不能为空");
                return;
            }
            int radixPoint = intParam("radixPoint");
            BigDecimal jackpotSize = decimalParam("jackpotSize");
            if(ruleType.equals("01")){
                if(jackpotSize.compareTo(new BigDecimal(0))!= 1){
                    Write("奖池应大于零", false, "奖池应大于零");
                    return;
                }
            }
            BigDecimal startSize = decimalParam("startSize");
            BigDecimal endSize = decimalParam("endSize");
            BigDecimal hitProbability = decimalParam("hitProbability");
            BigDecimal occurAmount = new BigDecimal(0);
            if(startSize.compareTo(endSize)!= -1){
                Write("奖金开始范围不能大于等于结束范围", false, "奖金开始范围不能大于等于结束范围");
                return;
            }
            String ruleSQL = "INSERT INTO luckyrule(luckyId,ruleType,radixPoint,jackpotSize,startSize,endSize,hitProbability,occurAmount,occurCount,isUse,updateTime,createTime) "
                    +"values ('" + luckyEvent.getLuckyId()  + "','" + ruleType+ "','" + radixPoint +"','" + jackpotSize +"','" + startSize+"','" + endSize +"','" + hitProbability+"','" + occurAmount+"','" + occurAmount +"','" + Const.LUCKY_RULE_USABLE+ "','" + currTime + "','" + currTime+"')";
            batchSQL.add(new OneSql(ruleSQL, 1, new Object[]{}));

            Boolean flag = Data.doTrans(batchSQL);
            if (flag) {
                Write("操作成功", true, eventInfo.getEventId()+",活动创建成功。");
            } else {
                Write("操作失败", false, "操作失败");
            }
        } catch (Exception e) {
            log.error("【抽奖后台】创建抽奖订单异常，异常原因：", e);
            Write("操作失败,", false, "操作失败");
        }
    }

    @Page(Viewer = ".xml")
    public void update() {
        try {
            List<OneSql> batchSQL = new ArrayList<>();
            String eventId = param("eventId");
            String eventTitleCN = param("eventTitleCN");
            String eventTitleHK = param("eventTitleHK");
            String eventTitleEN = param("eventTitleEN");
            String eventContentCN = request.getParameter("eventContentCN").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String eventContentHK = request.getParameter("eventContentHK").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String eventContentEN = request.getParameter("eventContentEN").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String eventRuleCN = request.getParameter("eventRuleCN").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String eventRuleHK = request.getParameter("eventRuleHK").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String eventRuleEN = request.getParameter("eventRuleEN").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            Timestamp startTimeTmp = dateParam("startTime");
            Timestamp endTimeTmp = dateParam("endTime");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String begin = format.format(startTimeTmp);
            String end = format.format(endTimeTmp);
            Date startTime = format.parse(begin);
            Date endTime = format.parse(end);
            List<EventInfo> eventInfos = eventInfoDao.getComplieLucky();
            boolean falg = false;
            if(!CollectionUtils.isEmpty(eventInfos)){
                for(EventInfo vo :eventInfos){
                    if(vo.getEventId().equals(eventId)){
                        continue;
                    }
                    if(TimeUtil.isOverlap(startTimeTmp,endTimeTmp,vo.getStartTime(),vo.getEndTime())){
                        falg = true;
                        break;
                    }
                }
            }
            if(falg){
                Write("活动时间不得与其他活动重叠", false, "活动时间不得与其他活动重叠");
                return;
            }
            JSONObject eventTitleJson = new JSONObject();
            JSONObject eventContentJson = new JSONObject();
            JSONObject eventRuleJson = new JSONObject();
            eventTitleJson.put("en", eventTitleEN);
            eventTitleJson.put("cn", eventTitleCN);
            eventTitleJson.put("hk", eventTitleHK);
            eventContentJson.put("en", eventContentEN);
            eventContentJson.put("cn", eventContentCN);
            eventContentJson.put("hk", eventContentHK);
            eventRuleJson.put("en", eventRuleEN);
            eventRuleJson.put("cn", eventRuleCN);
            eventRuleJson.put("hk", eventRuleHK);
            EventInfo eventInfo = new EventInfo();
            eventInfo.setEventTitleJson(eventTitleJson.toString());
            eventInfo.setEventContentJson(eventContentJson.toString());
            eventInfo.setEventRuleJson(eventRuleJson.toString());
            eventInfo.setStartTime(startTime);
            eventInfo.setEndTime(endTime);
            eventInfo.setCreateUserName(adminName());
            eventInfo.setStatus(Const.EVENT_STATUS_UNSTART);
            eventInfo.setEventType(Const.EVENT_TYPE_LUCKY);
            String currTime = format.format(new Date());
            eventInfo.setUpdateTime(new Date());
            eventInfo.setCreateTime(new Date());
            String statusStr = "";
            if(startTime.compareTo(new Date()) != -1){
                statusStr = ",status='01' ";
            }
            String eventSQL = "UPDATE eventinfo SET eventTitleJson = ?, eventContentJson = ?, eventRuleJson = ?, startTime  = ?, endTime = ?, "
                    + " updateTime = ? "+statusStr+" where eventId = ? ";
            batchSQL.add(new OneSql(eventSQL, 1, new Object[]{eventInfo.getEventTitleJson(),eventInfo.getEventContentJson(),eventInfo.getEventRuleJson(),begin,end,currTime,eventId}));

            String cycleLimitType = param("cycleLimitType");
            String cycleLimitCount = param("cycleLimitCount");
            String limitCount = param("limitCount");
            String relateEventId = param("relateEventId");
            String isHighest = param("isHighest");
            String isDouble = param("isDouble");

            if(!StringUtil.exist(limitCount)){
                Write("用户抽奖上限不能为空", false, "用户抽奖上限不能为空");
                return;
            }
            if(!StringUtil.exist(cycleLimitType)){
                Write("抽奖限制不能为空", false, "抽奖限制不能为空");
                return;
            }else{
                if(cycleLimitType.equals("03")||cycleLimitType.equals("04")){
                    if(!StringUtil.exist(relateEventId)){
                        Write("关联活动不能为空", false, "关联活动不能为空");
                        return;
                    }
                }
            }

            if(!StringUtil.exist(isHighest)){
                isHighest = "01";
            }else{
                if("02".equals(isHighest)){
                    cycleLimitType ="04";
                }
            }
            if(!StringUtil.exist(isDouble)){
                isDouble = "01";
            }else{
                if("02".equals(isDouble)){
                    cycleLimitType ="04";
                }
            }
            LuckyEvent luckyEvent = new LuckyEvent();
            luckyEvent.setLuckyId(TimeUtil.getCurrentTime17ByMillis());
            String luckySQL = "UPDATE luckyevent SET cycleLimitType  = ?,cycleLimitCount = ?,limitCount = ?,relateEventId = ?,isHighest = ?,isDouble = ?,updateTime = ?  where eventId = ?  ";
            batchSQL.add(new OneSql(luckySQL, 1, new Object[]{cycleLimitType,cycleLimitCount,limitCount,relateEventId,isHighest,isDouble,currTime,eventId}));


            String ruleType = param("ruleType");
            BigDecimal jackpotSize = decimalParam("jackpotSize");
            if("01".equals(ruleType)){
                if(jackpotSize.compareTo(new BigDecimal(0))!= 1){
                    Write("奖池应大于零", false, "奖池应大于零");
                    return;
                }
            }
            int radixPoint = intParam("radixPoint");
            BigDecimal startSize = decimalParam("startSize");
            BigDecimal endSize = decimalParam("endSize");
            BigDecimal hitProbability = decimalParam("hitProbability");

            BigDecimal occurAmount = new BigDecimal(0);
            if(startSize.compareTo(endSize)!= -1){
                Write("奖金开始范围不能大于等于结束范围", false, "奖金开始范围不能大于等于结束范围");
                return;
            }
            String ruleSQL = "UPDATE luckyrule SET radixPoint = ?,jackpotSize = ?,startSize = ?,endSize = ?,hitProbability = ?,updateTime = ?   where luckyId = (select t.luckyId from luckyevent t where t.eventId = ?)";
            batchSQL.add(new OneSql(ruleSQL, 1, new Object[]{radixPoint,jackpotSize,startSize,endSize,hitProbability,currTime,eventId}));
            Boolean flag = Data.doTrans(batchSQL);
            if (flag) {
                Write("操作成功,", true, eventId+",活动修改成功。");
            } else {
                Write("操作失败,", false, "操作失败");
            }
        } catch (Exception e) {
            log.error("【抽奖后台】创建抽奖订单异常，异常原因：", e);
            Write("操作失败,", false, "操作失败");
        }
    }


    /**
     * 活动状态修改
     * 01 开启（未开始--> 开始），02:进行中，03:暂停， 04:结束  05:删除
     */
    @Page(Viewer = ".xml")
    public void changeState() {
        String eventId = param("eventId");
        String status = param("status");
        String msg = "";
        try {
            EventInfo eventInfo = eventInfoDao.getEventById(eventId);
            if(eventInfo ==null){
                log.error("【抽奖后台】根据活动ID："+eventId+"查询活动信息为空");
                Write("操作失败", false, "操作失败");
            }
            String curr = TimeUtil.getFormatCurrentDateTime20();
            if ("01".equals(status)) {//开启
                List<EventInfo> eventInfos = eventInfoDao.getComplieLucky();
                boolean falg = false;
                if(!CollectionUtils.isEmpty(eventInfos)){
                    for(EventInfo vo :eventInfos){
                        if(vo.getEventId().equals(eventId)){
                            continue;
                        }
                        if(TimeUtil.isOverlap(new Timestamp(TimeUtil.parseDate(curr)),new Timestamp(eventInfo.getEndTime().getTime()),vo.getStartTime(),vo.getEndTime())){
                            falg = true;
                            break;
                        }
                    }
                }
                if(falg){
                    Write("活动时间不得与其他活动重叠", false, "活动时间不得与其他活动重叠");
                    return;
                }
                Data.Update("UPDATE eventinfo SET startTime=? WHERE eventId=? ", new Object[]{curr, eventId});
                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.modifyLuckyStatus;
                    msg = "修改活动状态:管理员："+String.valueOf(adminId())+"将抽奖活动："+eventId+"开启，开启前活动开始时间为："+
                            TimeUtil.parseDate(eventInfo.getStartTime().getTime())+"变更为"+curr;
                    new DailyRecordDao().insertOneRecord(type,msg ,String.valueOf(adminId()),ip(),now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            } else if ("05".equals(status)) {//删除
                Data.Update("UPDATE eventinfo SET status='05' WHERE eventId=? ", new Object[]{eventId});
                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.modifyLuckyStatus;
                    msg = "修改活动状态:管理员："+String.valueOf(adminId())+"将抽奖活动："+eventId+"删除，删除前活动开始时间为："+
                            TimeUtil.parseDate(eventInfo.getStartTime().getTime())+"结束时间为"+curr+"状态为："+eventInfo.getStatusView();
                    new DailyRecordDao().insertOneRecord(type,msg ,String.valueOf(adminId()),ip(),now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            } else if ("04".equals(status)) {
                List<EventInfo> eventInfos = eventInfoDao.getComplieLucky();
                boolean falg = false;
                if(!CollectionUtils.isEmpty(eventInfos)){
                    for(EventInfo vo :eventInfos){
                        if(vo.getEventId().equals(eventId)){
                            continue;
                        }
                        if(TimeUtil.isOverlap(new Timestamp(eventInfo.getStartTime().getTime()),new Timestamp(TimeUtil.parseDate(curr)),vo.getStartTime(),vo.getEndTime())){
                            falg = true;
                            break;
                        }
                    }
                }
                if(falg){
                    Write("活动时间不得与其他活动重叠", false, "活动时间不得与其他活动重叠");
                    return;
                }
                Data.Update("UPDATE eventinfo SET endTime=? WHERE eventId=? ", new Object[]{curr, eventId});
                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.modifyLuckyStatus;
                    msg = "修改活动状态:管理员："+String.valueOf(adminId())+"将抽奖活动："+eventId+"结束，结束前活动开始时间为："+
                            TimeUtil.parseDate(eventInfo.getStartTime().getTime())+"结束时间为"+curr+"状态为："+eventInfo.getStatusView();
                    new DailyRecordDao().insertOneRecord(type,msg ,String.valueOf(adminId()),ip(),now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            } else if ("03".equals(status)) {
                Data.Update("UPDATE eventinfo SET status='03' WHERE eventId=? ", new Object[]{eventId});
                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.modifyLuckyStatus;
                    msg = "修改活动状态:管理员："+String.valueOf(adminId())+"将抽奖活动："+eventId+"暂停，暂停前活动开始时间为："+
                            TimeUtil.parseDate(eventInfo.getStartTime().getTime())+"结束时间为"+curr+"状态为："+eventInfo.getStatusView();
                    new DailyRecordDao().insertOneRecord(type,msg ,String.valueOf(adminId()),ip(),now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            }else if ("02".equals(status)) {
                Data.Update("UPDATE eventinfo SET status='02' WHERE eventId=? ", new Object[]{eventId});
                try {
                    //插入一条管理员日志信息
                    DailyType type = DailyType.modifyLuckyStatus;
                    msg = "修改活动状态:管理员："+String.valueOf(adminId())+"将抽奖活动："+eventId+"启动，启动前活动开始时间为："+
                            TimeUtil.parseDate(eventInfo.getStartTime().getTime())+"结束时间为"+curr+"状态为："+eventInfo.getStatusView();
                    new DailyRecordDao().insertOneRecord(type,msg ,String.valueOf(adminId()),ip(),now());
                } catch (Exception e) {
                    log.error("添加管理员日志失败", e);
                }
            }
            log.info(msg);
            Write("操作成功", true, "操作成功");
        } catch (Exception e) {
            log.error("【抽奖后台】根据活动ID："+eventId+"更新抽奖活动状态失败", e);
            Write("操作失败", false, "操作失败");
        }
    }

    @Page(Viewer = "/admins/lucky/result.jsp")
    public void getResultInfo() {
        String eventId = param("eventId");
        String userId = param("userId");
        int currentPage = intParam("page");
        if(currentPage==0){
            currentPage = 1;
        }
        EventInfo eventInfo = null;
        eventInfo = eventInfoDao.getEventById(eventId);
        if(eventInfo != null){
            LuckyEvent luckyEvent = null;
            luckyEvent = luckyEventDao.findOneByEventId(eventId);
            LuckyRule luckyRule = luckyRuleDao.getRuleInfo(luckyEvent.getLuckyId());
            LuckyQualify count = luckyQualifyDao.getUserDetailCount(luckyEvent.getLuckyId(),userId);
            List<LuckyQualify> luckyQualifys = luckyQualifyDao.getUserDetail(luckyEvent.getLuckyId(),userId,currentPage,30);
            int total = 0;
            if(count != null){
                total = count.getUserCount().intValue();
            }
            setPaging(total, currentPage , 30);
            setAttr("luckyEvent",luckyEvent);
            setAttr("eventInfo", eventInfo);
            setAttr("luckyQualifys", luckyQualifys);
            setAttr("luckyRule", luckyRule);
            setAttr("startSize",CommonUtil.getAmountAddZERO(luckyRule.getStartSize(),luckyRule.getRadixPoint()));
            setAttr("endSize",CommonUtil.getAmountAddZERO(luckyRule.getEndSize(),luckyRule.getRadixPoint()));
            setAttr("occurCount",CommonUtil.getAmountAddZERO(luckyRule.getOccurCount(),0));
            List<BigDecimal> lists = luckyQualifyDao.getOccurAmountInfo(luckyEvent.getLuckyId());
            if(!CollectionUtils.isEmpty(lists)&&lists.size()>= 3){
                setAttr("occurAmount", CommonUtil.getAmountAddZERO(lists.get(0),luckyRule.getRadixPoint()));
                setAttr("factAmount", CommonUtil.getAmountAddZERO(lists.get(1),luckyRule.getRadixPoint()));
                setAttr("factUser", lists.get(2));
                setAttr("brush", CommonUtil.getAmountAddZERO(lists.get(0).subtract(lists.get(1)).subtract(lists.get(3)),luckyRule.getRadixPoint()));
                setAttr("doubleAmount", CommonUtil.getAmountAddZERO(lists.get(3),luckyRule.getRadixPoint()));
            }else{
                setAttr("occurAmount",new BigDecimal(0));
                setAttr("factAmount", new BigDecimal(0));
                setAttr("factUser", new BigDecimal(0));
                setAttr("brush", new BigDecimal(0));
                setAttr("doubleAmount",new BigDecimal(0));
            }
        }
    }
    @Page(Viewer = ".xml")
    public void brush(){
        String luckyIds = param("luckyIds");
        String ruleId = param("ruleId");
        String brushAmount = param("brushAmount");
        List<OneSql> batchSQL = new ArrayList<>();
        String curr = TimeUtil.getFormatCurrentDateTime20();
        BigDecimal amount = new BigDecimal(brushAmount);
        if(new BigDecimal(0).compareTo(amount) != -1){
            Write("操作失败,数字应大于零", false, "");
            return;
        }
        String[] args =  brushAmount.split("\\.");
        String tmp = "0";
        if(args!= null&&args.length==2){
            tmp = args[1];
        }
        LuckyRule luckyRule=  luckyRuleDao.getRuleInfo(luckyIds);
        if(tmp.length() > luckyRule.getRadixPoint()){
            if(Integer.parseInt(tmp.substring(luckyRule.getRadixPoint(),tmp.length()))>0){
                Write("操作失败,请确认刷量小数位不能大于"+luckyRule.getRadixPoint()+"位", false, "");
                return;
            }
        }
        //新增资格信息
        String insertQualify = "insert into luckyqualify (luckyId,ruleId,userId,startTime,endTime,occurAmount,isReceive,source,updateTime,createTime)values(?,?,?,?,?,?,?,?,?,?)";
        batchSQL.add(new OneSql(insertQualify, 1, new Object[]{luckyIds,ruleId,adminId(),curr,curr,brushAmount,"02","02",curr,curr}));
        //更新规则
        String updateRule = "update luckyrule t set t.occurAmount = t.occurAmount + ? ,t.occurCount = t.occurCount+1,t.updateTime = ? where t.ruleId = ? and t.jackpotSize >= (t.occurAmount+?)";
        batchSQL.add(new OneSql(updateRule, 1, new Object[]{new BigDecimal(brushAmount),curr,ruleId,new BigDecimal(brushAmount)}));
        Boolean flag = Data.doTrans(batchSQL);
        if(flag){
            Write("操作成功", true, "");
            return;
        }
        Write("操作失败,请确认刷量是否超过奖池大小！", false, "");
    }


    @Page(Viewer = "/admins/lucky/lucky_draw.jsp")
    public void getView() {
        try {
            String eventId = param("eventId");
            eventId = eventId.split(",")[0];
            if (StringUtil.exist(eventId)) {
                EventInfo eventInfo = null;
                eventInfo = eventInfoDao.getEventById(eventId);
                if(eventInfo != null){
                    JSONObject jsonTitle = JSONObject.parseObject(eventInfo.getEventTitleJson());
                    JSONObject jsonContent = JSONObject.parseObject(eventInfo.getEventContentJson());
                    JSONObject jsonRule = JSONObject.parseObject(eventInfo.getEventRuleJson());

                    eventInfo.setEventTitleJson(jsonTitle.getString("cn"));
                    eventInfo.setEventContentJson(jsonContent.getString("cn"));
                    eventInfo.setEventRuleJson(jsonRule.getString("cn"));

                    LuckyEvent luckyEvent = null;
                    luckyEvent = luckyEventDao.findOneByEventId(eventId);
                    LuckyRule luckyRule = luckyRuleDao.getRuleInfo(luckyEvent.getLuckyId());
                    setAttr("luckyRule",luckyRule);
                    setAttr("luckyEvent",luckyEvent);
                    setAttr("eventInfo", eventInfo);
                }
            }
        } catch (Exception e) {
            log.error("【抽奖后台】展示详情异常，异常信息为：", e);
        }
    }

    @Page(Viewer  = "/admins/lucky/userDetail.jsp")
    public void showDetail() {
        try {
            String luckyId = param("luckyIdss");
            String userId = param("userId");
            List<LuckyQualify> luckyQualifys =  luckyQualifyDao.getUserDetails(luckyId,userId);
            setAttr("luckyQualifys",luckyQualifys);
        } catch (Exception e) {
            log.error("【抽奖后台】展示详情异常，异常信息为：", e);
        }
    }


}
