package com.world.model.dao.event;

import com.alibaba.fastjson.JSONObject;
import com.atlas.BizException;
import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.event.EventInfo;
import com.world.model.entity.lucky.LuckyEvent;
import com.world.model.entity.lucky.LuckyRule;
import com.world.util.date.TimeUtil;
import com.world.util.string.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * @Title: 活动主体信息表
 * @Description: 主要处理活动主体信息增删改查功能
 * @Company: atlas
 * @author: xzhang
 */
public class EventInfoDao extends DataDaoSupport {

    /**
     * @describe 应用于后台管理平台查询功能
     * @param title 活动标题
     * @param status
     * @param lan
     * @return List<EventInfo>
     */
    public List<EventInfo> getEventList(String title, String status,String lan){
        List<EventInfo> eventList = new ArrayList<EventInfo>();
        if (!StringUtil.exist(lan)) {
            lan = "cn";
        }
        StringBuilder where = new StringBuilder();
        List<EventInfo> retEventInfo = new ArrayList<EventInfo>();
        try {
            where.append("  where eventType = '01' and status <> '05' ");
            if (StringUtil.exist(status)) {
                if("01".equals(status)){
                    where.append(" AND status <> '03' and now() <= startTime ");
                }else if ("02".equals(status)){
                    where.append(" AND status <> '03'  and now() BETWEEN startTime and endTime ");
                }else if("03".equals(status)){
                    where.append(" AND status = '03' and now() BETWEEN startTime and endTime ");
                }else if("04".equals(status)){
                    where.append(" and now() >= endTime");
                }
            }
            if (StringUtil.exist(title)) {
                where.append(" AND eventTitleJson like '%" + title + "%'");
            }
            where.append(" order by createTime desc ");
            String w = where.toString();
            String sql = "select eventId,eventTitleJson,eventContentJson,eventRuleJson,startTime,endTime,status,createUserName,eventType,updateTime,createTime  from eventinfo " + w;
            eventList = find(sql, null, EventInfo.class);
            if(!CollectionUtils.isEmpty(eventList)){
                for (EventInfo eventInfo : eventList) {
                    JSONObject jsonTitle = JSONObject.parseObject(eventInfo.getEventTitleJson());
                    JSONObject jsonContent = JSONObject.parseObject(eventInfo.getEventContentJson());
                    JSONObject jsonRule = JSONObject.parseObject(eventInfo.getEventRuleJson());
                    eventInfo.setEventTitleJson(jsonTitle.getString(lan));
                    eventInfo.setEventContentJson(jsonContent.getString(lan));
                    eventInfo.setEventRuleJson(jsonRule.getString(lan));
                    if("进行中".equals(eventInfo.getStatusView())){
                        sql = "select t.luckyId from luckyrule t where t.luckyId = (select f.luckyId from luckyevent f where f.eventId= '"+eventInfo.getEventId()+"' ) and t.jackpotSize>t.occurAmount";
                        List<LuckyRule> ruleList = find(sql, null, LuckyRule.class);
                        if(CollectionUtils.isEmpty(ruleList)){
                            eventInfo.setStatus(Const.EVENT_STATUS_OVER);
                        }
                    }
                }
                Collections.sort(eventList, new Comparator<EventInfo>(){
                    //重写排序规则
                    public int compare(EventInfo vo1, EventInfo vo2) {
                        if("进行中".equals(vo1.getStatusView())){
                            return -1;
                        }else{
                            if(vo1.getCreateTime().compareTo(vo2.getCreateTime()) != -1){
                                return -1;
                            }else{
                                return 1;
                            }
                        }
                    }
                });

                if("02".equals(status)){
                    for(EventInfo eventInfo : eventList){
                        if(!Const.EVENT_STATUS_OVER.equals(eventInfo.getStatus())){
                            retEventInfo.add(eventInfo);
                        }
                    }
                    eventList = retEventInfo;
                }
            }
            if("04".equals(status)){
                String str = "";
                if (StringUtil.exist(title)) {
                    str = " AND eventTitleJson like '%" + title + "%'";
                }
                sql = "select eventId,eventTitleJson,eventContentJson,eventRuleJson,startTime,endTime,status,createUserName,eventType,updateTime,createTime  from eventinfo  where eventType = '01' and status <> '05'  AND status <> '03' and now() BETWEEN startTime and endTime " + str;
                List<EventInfo>  eventListTmp = find(sql, null, EventInfo.class);
                if(!CollectionUtils.isEmpty(eventListTmp)){
                    for (EventInfo eventInfo : eventListTmp) {
                        if("进行中".equals(eventInfo.getStatusView())){
                            sql = "select t.luckyId from luckyrule t where t.luckyId = (select f.luckyId from luckyevent f where f.eventId= '"+eventInfo.getEventId()+"' ) and t.jackpotSize>t.occurAmount";
                            List<LuckyRule> ruleList = find(sql, null, LuckyRule.class);
                            if(CollectionUtils.isEmpty(ruleList)){
                                JSONObject jsonTitle = JSONObject.parseObject(eventInfo.getEventTitleJson());
                                JSONObject jsonContent = JSONObject.parseObject(eventInfo.getEventContentJson());
                                JSONObject jsonRule = JSONObject.parseObject(eventInfo.getEventRuleJson());
                                eventInfo.setEventTitleJson(jsonTitle.getString(lan));
                                eventInfo.setEventContentJson(jsonContent.getString(lan));
                                eventInfo.setEventRuleJson(jsonRule.getString(lan));
                                eventInfo.setStatus(Const.EVENT_STATUS_OVER);
                                retEventInfo.add(eventInfo);
                            }
                        }
                    }
                }
                eventList.addAll(retEventInfo);
                Collections.sort(eventList, new Comparator<EventInfo>(){
                    //重写排序规则
                    public int compare(EventInfo vo1, EventInfo vo2) {
                        if("进行中".equals(vo1.getStatusView())){
                            return -1;
                        }else{
                            if(vo1.getCreateTime().compareTo(vo2.getCreateTime()) != -1){
                                return -1;
                            }else{
                                return 1;
                            }
                        }
                    }
                });
            }

        } catch (Exception e) {
           log.error("【抽奖后台】查询活动列表异常",e);
           throw new BizException("系统异常");
        }
        return eventList;
    }

    /**
     * @describe web页面展示活动信息
     * @param lan 语言环境
     * @return List<EventInfo>
     * 接口查询逻辑：
            查询最新在进行中的活动，即当前时间大于活动开始时间，且小于活动结束时间，且当前活动未暂停。按照开始时间倒序排序，获取List集合。
                 Y：则显示当前进行中活动信息。
                 N：查询最新在结束活动或暂停活动，即当前时间大于活动结束时间或活动状态为暂停。按照活动结束时间倒序排序，获取List集合。
                    Y：显示活动全部中出页面信息。
                    N：查询最新在未开始的活动，即当前时间小于活动开始时间，按照活动开始时间正序排列。获取List集合。
                        Y：显示活动未开始的页面信息。
                        N：无活动可参与。
     */
    public EventInfo getEventInfo(String lan) {
        EventInfo returnEventInfo = null;
        List<EventInfo> eventList = null;
        if(!StringUtil.exist(lan)){
            lan = "en";
        }
        try {
            //查询最新在进行中的活动
            String curr = TimeUtil.getFormatCurrentDateTime20();
            String sql = "select eventId,eventTitleJson,eventContentJson,eventRuleJson,startTime,endTime,status,createUserName,eventType,updateTime,createTime  from eventinfo " +
                    "where eventType = '01' and '"+curr+"' BETWEEN startTime AND endTime and status not in ('03','05') order by startTime desc limit 1";
            eventList = find(sql, null, EventInfo.class);
            if(!CollectionUtils.isEmpty(eventList)){
                returnEventInfo = eventList.get(0);
                returnEventInfo.setStatus(Const.EVENT_STATUS_ING);
            }else{
                //查询最新在结束活动或暂停活动
                sql = "select eventId,eventTitleJson,eventContentJson,eventRuleJson,startTime,endTime,status,createUserName,eventType,updateTime,createTime  from eventinfo " +
                        "where eventType = '01' and (endTime <  '"+curr+"' or status = '03' ) and status <> '05' order by endTime desc";
                eventList = find(sql, null, EventInfo.class);
                if(!CollectionUtils.isEmpty(eventList)){
                    returnEventInfo = eventList.get(0);
                    if(!Const.EVENT_STATUS_SUSPEND.equals(returnEventInfo.getStatus()) ){
                        returnEventInfo.setStatus(Const.EVENT_STATUS_OVER);
                    }
                }else{
                    //查询最新未开始的活动
                    sql = "select eventId,eventTitleJson,eventContentJson,eventRuleJson,startTime,endTime,status,createUserName,eventType,updateTime,createTime  from eventinfo " +
                            "where eventType = '01' and startTime > '"+curr+"'  and status <> '05' order by startTime asc";
                    eventList = find(sql, null, EventInfo.class);
                    if(!CollectionUtils.isEmpty(eventList)){
                        returnEventInfo = eventList.get(0);
                    }
                }
            }
            //国际化展示活动信息
            if(returnEventInfo != null){
                returnEventInfo.setTitleOriginal(returnEventInfo.getEventTitleJson());
                JSONObject jsonTitle = JSONObject.parseObject(returnEventInfo.getEventTitleJson());
                JSONObject jsonContent = JSONObject.parseObject(returnEventInfo.getEventContentJson());
                JSONObject jsonRule = JSONObject.parseObject(returnEventInfo.getEventRuleJson());
                returnEventInfo.setEventTitleJson(jsonTitle.getString(lan));
                returnEventInfo.setEventContentJson(jsonContent.getString(lan));
                returnEventInfo.setEventRuleJson(jsonRule.getString(lan));
            }
        } catch (Exception e) {
            log.error("【抽奖】查询活动信息异常",e);
            return returnEventInfo;
        }
        return returnEventInfo;
    }

    /**
     * @describe 查询出单条记录
     * @param eventId
     * @return
     */
    public EventInfo getEventById(String eventId) {
        try {
            return (EventInfo) Data.GetOne("SELECT eventId,eventTitleJson,eventContentJson,eventRuleJson,startTime,endTime,status,createUserName,eventType,updateTime,createTime from eventinfo WHERE eventId=? ", new Object[] { eventId }, EventInfo.class);
        } catch (Exception e) {
            log.error("【抽奖】根据活动ID"+eventId+"查询活动明细异常，异常信息为：",e);
            return null;
        }
    }


    /**
     * @describe 比较抽奖信息
     */
    public List<EventInfo> getComplieLucky(){
        List<EventInfo> luckyList = null;
        try{
            String sql = "select t.eventId,t.startTime,t.endTime from eventinfo t where t.`status` IN ('01', '02', '03') and now() BETWEEN t.startTime AND t.endTime";
            luckyList = find(sql, null, EventInfo.class);
        }catch (Exception e){
            log.error("【抽奖】查询比较抽奖信息，异常信息为：",e);
            return luckyList;
        }
        return luckyList;
    }


}
