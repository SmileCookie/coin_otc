package com.world.model.dao.lucky;

import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.event.EventInfo;
import com.world.model.entity.lucky.LuckyEvent;
import com.world.model.entity.lucky.VLuckyEvent;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Title: 抽奖主体信息表
 * @Description: 主要处理抽奖主体信息增删改查功能
 * @Company: atlas
 * @author: xzhang
 */
public class LuckyEventDao extends DataDaoSupport {

    /**
     * @describe 查询未开始活动信息及规则
     * @param eventId 活动ID
     */
    public LuckyEvent getUnStartLucky(String eventId){
        List<LuckyEvent> luckyList = null;
        LuckyEvent luckyEvent = null;
        try{
            String sql = "select t.cycleLimitType,l.radixPoint,t.relateEventId from luckyrule l left join luckyevent t on l.luckyId = t.luckyId where t.eventId = '"+eventId+"'";
            luckyList = find(sql, null, LuckyEvent.class);
            if(!CollectionUtils.isEmpty(luckyList)) {
                luckyEvent =luckyList.get(0);
            }
        }catch (Exception e){
            log.error("【抽奖】根据活动ID："+eventId+"查询活动信息异常，异常信息为：",e);
            return null;
        }
        return luckyEvent;
    }


    /**
     * @describe 查询活动暂停或者已结束或进行中的活动的规则信息及发生额
     * @param eventId 活动ID
     * @return LuckyEvent
     */
    public LuckyEvent getEndAndINGLucky(String eventId){
        List<LuckyEvent> luckyList = null;
        LuckyEvent luckyEvent = null;
        try{
            String sql = "select t.luckyId,t.limitCount,t.cycleLimitType,l.radixPoint,t.relateEventId,l.occurAmount,l.jackpotSize from luckyrule l left join luckyevent t on l.luckyId = t.luckyId where t.eventId = '"+eventId+"'";
            luckyList = find(sql, null, LuckyEvent.class);
            if(!CollectionUtils.isEmpty(luckyList)) {
                luckyEvent = new LuckyEvent();
                luckyEvent.setOccurAmount(new BigDecimal(0));
                for(LuckyEvent vo:luckyList){
                    luckyEvent.setOccurAmount(luckyEvent.getOccurAmount().add(vo.getOccurAmount()));
                }
                luckyEvent.setCycleLimitType(luckyList.get(0).getCycleLimitType());
                luckyEvent.setRadixPoint(luckyList.get(0).getRadixPoint());
                luckyEvent.setRelateEventId(luckyList.get(0).getRelateEventId());
                luckyEvent.setLuckyId(luckyList.get(0).getLuckyId());
                luckyEvent.setLimitCount(luckyList.get(0).getLimitCount());
                luckyEvent.setJackpotSize(luckyList.get(0).getJackpotSize());
            }
        }catch (Exception e){
            log.error("【抽奖】根据活动ID："+eventId+"查询活动暂停、已结束或进行中异常，异常信息为：",e);
            return null;
        }
        return luckyEvent;
    }

    /**
     * @describe 查询进行中的活动信息
     * @return EventInfo
     */
    public EventInfo getEventING() {
        EventInfo returnEventInfo = null;
        List<EventInfo> eventList = null;
        try {
            String curr = TimeUtil.getFormatCurrentDateTime20();
            String sql = "select eventId,eventTitleJson,eventContentJson,eventRuleJson,startTime,endTime,status,createUserName,eventType,updateTime,createTime  from eventinfo " +
                    "where eventType = '01' and '"+curr+"' BETWEEN startTime AND endTime and status not in ('03','05') order by startTime desc";
            eventList = find(sql, null, EventInfo.class);
            if(!CollectionUtils.isEmpty(eventList)){
                returnEventInfo = eventList.get(0);
                returnEventInfo.setStatus(Const.EVENT_STATUS_ING);
            }
        } catch (Exception e) {
            log.error("【抽奖】查询进行中的活动信息异常，异常信息为：",e);
            return null;
        }
        return returnEventInfo;
    }

    /**
     * @describe 查询抽奖关联的投票信息
     * @return LuckyEvent
     */
    public LuckyEvent getEventAndActivity() {
        LuckyEvent returnLuckyEvent = null;
        List<LuckyEvent> eventList = null;
        try {
            String curr = TimeUtil.getFormatCurrentDateTime20();
            String sql = "select t.relateEventId,t.luckyId,e.startTime,e.endTime from luckyevent t left join eventinfo e on t.eventId = e.eventId where e.eventType = '01' and t.isHighSyn = '01' and t.isHighest = '02' and ? BETWEEN e.startTime AND e.endTime AND STATUS NOT IN ('03', '05') AND t.cycleLimitType = '04'";
            eventList = find(sql, new Object[]{curr}, LuckyEvent.class);
            if(!CollectionUtils.isEmpty(eventList)){
                returnLuckyEvent = eventList.get(0);
            }
        } catch (Exception e) {
            log.error("【抽奖】查询抽奖关联的投票信息，异常信息为：",e);
            return returnLuckyEvent;
        }
        return returnLuckyEvent;
    }

    /**
     * @describe 查询出单条记录
     * @param eventId
     * @return LuckyEvent
     */
    public LuckyEvent findOneByEventId(String eventId) {
        try {
            return (LuckyEvent) Data.GetOne("SELECT luckyId,eventId,cycleLimitType,cycleLimitCount,limitCount,relateEventId,isHighest,isDouble,updateTime,createTime from luckyevent WHERE eventId=? ", new Object[] { eventId }, LuckyEvent.class);
        } catch (Exception e) {
            log.error("【抽奖】根据活动ID"+eventId+"查询抽奖活动明细异常，异常信息为：",e);
            return null;
        }
    }

    /**
     * @describe 只查询抽奖活动已到结束时间的记录,同一时刻内只能查创建一个活动
     * @return LuckyEvent
     */
    public LuckyEvent getEventEnd() {
        LuckyEvent returnLuckyEvent = null;
        List<LuckyEvent> eventList = null;
        try {
            String curr = TimeUtil.getFormatCurrentDateTime20();
            String sql = "select t.relateEventId,t.luckyId,e.startTime,e.endTime,e.eventTitleJson from luckyevent t left join eventinfo e on t.eventId = e.eventId where e.eventType = '01' AND t.cycleLimitType = '04' and t.isDoubleSyn = '01' and t.isDouble = '02' and e.endTime < ? AND STATUS != '05' ";
            eventList = find(sql, new Object[]{curr}, LuckyEvent.class);
            if(!CollectionUtils.isEmpty(eventList)){
                returnLuckyEvent = eventList.get(0);
            }
        } catch (Exception e) {
            log.error("【抽奖】查询抽奖活动结束是否需要翻倍发生非受控异常，异常信息为：",e);
            return returnLuckyEvent;
        }
        return returnLuckyEvent;
    }
}
