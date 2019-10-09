package com.world.model.dao.lucky;

import com.Lan;
import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.bill.BillDistribution;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.event.EventInfo;
import com.world.model.entity.lucky.LuckyEvent;
import com.world.model.entity.lucky.LuckyQualify;
import com.world.util.date.TimeUtil;
import com.world.util.sign.EncryDigestUtil;
import com.world.util.string.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.world.web.action.Action.VIP_DOMAIN;

/**
 * @Title: 抽奖资格信息表
 * @Description: 主要处理抽奖资格信息增删改查功能
 * @Company: atlas
 * @author: xzhang
 */
public class LuckyQualifyDao extends DataDaoSupport {

    /**
     * @describe 查询当前活动下该用户活动的抽奖金额
     * @param eventId
     * @param userId
     * @return BigDecimal
     */
    public BigDecimal getUserLucky(String eventId,String userId){
        List<LuckyQualify> qualifyList = null;
        BigDecimal userAmount = new BigDecimal(0);
        try{
            String sql = "select sum(q.occurAmount) occurAmount from luckyqualify q left join luckyevent l on q.luckyId = l.luckyId where l.eventId = ?  and q.isReceive = '02' AND q.source <> '03' and q.userId = ? ";
            qualifyList = find(sql, new Object[]{eventId,userId}, LuckyQualify.class);
            if(!CollectionUtils.isEmpty(qualifyList)){
                if(null != qualifyList.get(0).getOccurAmount()){
                    userAmount = qualifyList.get(0).getOccurAmount();
                }
            }
        }catch (Exception e){
            log.error("【抽奖】根据活动ID："+eventId+"及用户Id："+userId+"查询当前活动下该用户活动的抽奖金额异常，异常信息为：",e);
            return userAmount;
        }
        return userAmount;
    }

    /**
     * @describe  查询用户具有抽奖权限
     * @param luckyId
     * @param userId
     * @return boolean
     */
    public boolean getUserQualify(String luckyId,String userId){
        List<LuckyQualify> qualifyList = null;
        LuckyQualify qualify = null;
        try{
            String sql = "select q.qId from luckyqualify q where q.userId = ? and q.luckyId = ? limit 1 ";
            qualifyList = find(sql, new Object[]{userId,luckyId}, LuckyQualify.class);
            if(!CollectionUtils.isEmpty(qualifyList)) {
                return true;
            }
        }catch (Exception e){
            log.error("【抽奖】根据抽奖ID："+luckyId+"及用户Id："+userId+"查询用户具有抽奖权限异常，异常信息为：",e);
            return false;
        }
        return false;
    }

    /**
     * @describe 分组查询用户的抽奖资格权限：
     *  分为：01：未领取
     *       02：已领取
     * @param luckyId
     * @param userId
     * @return List<LuckyQualify>
     */
    public List<LuckyQualify> getQualifyDetail(String luckyId,String userId){
        List<LuckyQualify> qualify = null;
        try{
            String curr = TimeUtil.getFormatCurrentDateTime20();
            String sql = "select q.isReceive, count(1) receiveCount from luckyqualify q where q.userId = ? and q.luckyId = ? and ? BETWEEN startTime AND endTime group by q.isReceive ";
            List<List<Object>> list =  Data.Query(sql,new Object[]{userId,luckyId,curr});
            if(!CollectionUtils.isEmpty(list)){
                qualify = new ArrayList<LuckyQualify>();
                for(List<Object> vo :list){
                    LuckyQualify luckyQualify = new LuckyQualify();
                    luckyQualify.setIsReceive(String.valueOf(vo.get(0)));
                    luckyQualify.setReceiveCount(String.valueOf(vo.get(1)));
                    qualify.add(luckyQualify);
                }
            }
        }catch (Exception e){
            log.error("【抽奖】根据抽奖ID："+luckyId+"及用户Id："+userId+"分组查询用户的抽奖资格权限异常，异常信息为：",e);
            return null;
        }
        return qualify;
    }

    /**
     * @describe 新增资格信息
     * @param userId
     * @param activityId
     * @return Map<String,String>
     */
    public Map<String,String>  insertQualify(String userId,String activityId,String ip){
        Map<String,String> retMap = new HashMap<String,String>();
        List<LuckyEvent> eventList = null;
        try{
            String curr = TimeUtil.getFormatCurrentDateTime20();
            String endTime = TimeUtil.getFormatdayEndTime20(new Date());
            String sql = "select t.luckyId from luckyevent t where t.relateEventId =? and t.cycleLimitType in ('03','04')  and t.eventId = (" +
                    "select eventId from eventinfo where eventType = '01'  and '"+now()+"' BETWEEN startTime AND endTime and status <> '05' limit 1) limit 1";
            eventList = find(sql, new Object[]{activityId}, LuckyEvent.class);
            if(!CollectionUtils.isEmpty(eventList)){
                Data.Update("insert into luckyqualify (luckyId,ruleId,userId,startTime,endTime,occurAmount,isReceive,source,updateTime,createTime,ip)" +
                                    " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)",
                            new Object[]{eventList.get(0).getLuckyId(), "", userId,curr,endTime,new BigDecimal(0),"01","01",curr,curr,ip});
                retMap.put("isShow","1");
                retMap.put("url",VIP_DOMAIN + "/lottery");
            }else{
                retMap.put("isShow","0");
            }
        } catch(Exception e){
            log.error("【抽奖】根据投票ID："+activityId+"及用户Id："+userId+"新增资格信息异常，异常信息为：",e);
            retMap.put("isShow","0");
            return retMap;
        }
        return retMap;
    }



    public List<BigDecimal> getOccurAmountInfo(String luckyId){
        List<BigDecimal> amountList  = new ArrayList<BigDecimal>();
        //已领取
       LuckyQualify luckyQualify =  (LuckyQualify) Data.GetOne("select sum(t1.occurAmount) occurAmount from luckyqualify t1 where t1.luckyId = ? and t1.isReceive = '02' ", new Object[] { luckyId }, LuckyQualify.class);
        if(luckyQualify != null&&luckyQualify.getOccurAmount()!=null){
            amountList.add(luckyQualify.getOccurAmount());
        }else{
            amountList.add(new BigDecimal(0));
        }
        //实际领取
        luckyQualify =  (LuckyQualify) Data.GetOne("select sum(t1.occurAmount) occurAmount from luckyqualify t1 where t1.luckyId = ? and t1.isReceive = '02'  and t1.source = '01' ", new Object[] { luckyId }, LuckyQualify.class);
        if(luckyQualify != null&&luckyQualify.getOccurAmount()!=null){
            amountList.add(luckyQualify.getOccurAmount());
        }else{
            amountList.add(new BigDecimal(0));
        }
        //实际人数
        luckyQualify =  (LuckyQualify)Data.GetOne("select COUNT(DISTINCT(t1.userId)) userCount from luckyqualify t1 where t1.luckyId = ? and t1.isReceive = '02'  and t1.source = '01' ", new Object[] { luckyId }, LuckyQualify.class);
        if(luckyQualify != null&&luckyQualify.getUserCount()!=null){
            amountList.add(new BigDecimal(luckyQualify.getUserCount()));
        }else{
            amountList.add(new BigDecimal(0));
        }
        //已领取翻倍
        luckyQualify =  (LuckyQualify) Data.GetOne("select sum(t1.occurAmount) occurAmount from luckyqualify t1 where t1.luckyId = ? and t1.isReceive = '02' and t1.source = '03' ", new Object[] { luckyId }, LuckyQualify.class);
        if(luckyQualify != null&&luckyQualify.getOccurAmount()!=null){
            amountList.add(luckyQualify.getOccurAmount());
        }else{
            amountList.add(new BigDecimal(0));
        }
       return amountList;
    }

    public List<LuckyQualify> getUserDetail(String luckyId,String userId ,int currentPage,int pageSize){
        List<LuckyQualify> qualifys = null;
        try{
            String sql = " select userId,userCount,occurAmount from (select t.userId,count(t.userId) userCount,sum(t.occurAmount) occurAmount from luckyqualify t where t.luckyId = ? and t.isReceive = '02'  group by t.userId order by sum(t.occurAmount) desc) v limit ?,? ";
            if(StringUtil.exist(userId)){
                sql = "select userId,userCount,occurAmount from ( select t.userId,count(t.userId) userCount,sum(t.occurAmount) occurAmount from luckyqualify t where t.userId = "+userId+" and t.luckyId = ? and t.isReceive = '02' group by t.userId order by count(t.userId)) v limit ?,? ";
            }
            qualifys =  find(sql, new Object[]{luckyId,(currentPage-1)*pageSize,pageSize}, LuckyQualify.class);
        } catch(Exception e){
            return qualifys;
        }
        return qualifys;
    }

    public LuckyQualify getUserDetailCount(String luckyId,String userId ){
        LuckyQualify qualifys = null;
        try{
            String sql = " select count(1) userCount from (select t.userId,count(t.userId) userCount,sum(t.occurAmount) occurAmount from luckyqualify t where t.luckyId = ? and t.isReceive = '02'  group by t.userId ) v ";
            if(StringUtil.exist(userId)){
                sql = "select count(1) userCount from ( select t.userId,count(t.userId) userCount,sum(t.occurAmount) occurAmount from luckyqualify t where t.userId = "+userId+" and t.luckyId = ? and t.isReceive = '02' and source = '01' group by t.userId ) v ";
            }
            qualifys =  (LuckyQualify)Data.GetOne(sql, new Object[]{luckyId}, LuckyQualify.class);
        } catch(Exception e){
            return qualifys;
        }
        return qualifys;
    }

    /**
     * 获取用户活动资格信息
     * @param luckyId
     * @param userId
     * @return
     */
    public List<LuckyQualify> getUserDetails(String luckyId,String userId){
        List<LuckyQualify> qualifys = null;
        try{
            String sql = " select t.userId,t.updateTime,t.occurAmount from luckyqualify t where t.userId = ? and t.luckyId = ? and t.isReceive = '02' order by t.updateTime desc ";
            qualifys =  find(sql, new Object[]{userId,luckyId}, LuckyQualify.class);
        } catch(Exception e){
            log.error("【抽奖】根据抽奖活动ID："+luckyId+"及用户Id："+userId+"获取用户活动资格信息异常，异常信息为：",e);
            return qualifys;
        }
        return qualifys;
    }

    /**
     * 获取用户最新资格记录
     * @param luckyId
     * @param userId
     * @return
     */
    public List<LuckyQualify> getNewest(String luckyId,String userId){
        List<LuckyQualify> qualifys = null;
        try{
            String sql = " select t.endTime,t.createTime,t.isReceive from luckyqualify t where t.userId = ? and t.luckyId = ? order by t.createTime desc limit 1 ";
            qualifys =  find(sql, new Object[]{userId,luckyId}, LuckyQualify.class);
        } catch(Exception e){
            log.error("【抽奖】根据抽奖活动ID："+luckyId+"及用户Id："+userId+"获取用户最新资格记录异常，异常信息为：",e);
            return qualifys;
        }
        return qualifys;
    }

    /**
     *获取用户的已领取的金额
     * @param luckyId
     * @param userId
     */
    public BigDecimal getReceived(String luckyId,String userId){
        LuckyQualify luckyQualify = null;
        BigDecimal occurAmount =new BigDecimal(0);
        try{
            String sql = " select sum(t.occurAmount) occurAmount from luckyqualify t where t.userId = ? and t.luckyId = ? order by t.createTime desc limit 1 ";
            luckyQualify =  (LuckyQualify) Data.GetOne(sql, new Object[] {userId,luckyId }, LuckyQualify.class);
            if(luckyQualify != null&&luckyQualify.getOccurAmount()!=null){
                occurAmount = luckyQualify.getOccurAmount();
            }
        } catch(Exception e){
            log.error("【抽奖】根据抽奖活动ID："+luckyId+"及用户Id："+userId+"获取用户的已领取的金额异常，异常信息为：",e);
            return occurAmount;
        }
        return occurAmount;
    }

    /**
     *获取用户需要提醒奖金翻倍的记录
     * @param userId
     */
    public String getUnShowInfo(String userId){
        List<LuckyQualify> luckyQualifys = null;
        String occurAmount = "";
        try{
            String sql = " select e.radixPoint radixPoint,sum(t.occurAmount) occurAmount from luckyqualify t left join luckyrule e on t.luckyId = e.luckyId  where t.userId = ? and isShow = '02'  group by e.radixPoint order by e.radixPoint desc ";
            log.info("sql="+sql+"userId="+userId);
            luckyQualifys =  find(sql, new Object[]{userId}, LuckyQualify.class);
            if(luckyQualifys != null&&luckyQualifys.size()>0){
                BigDecimal amount = new BigDecimal(0);
                for(LuckyQualify vo:luckyQualifys){
                    amount = amount.add(vo.getOccurAmount());
                }
                int radixPoint = luckyQualifys.get(0).getRadixPoint();
                Data.Update("update luckyqualify set isShow='03' where userId=?", new Object[]{userId});
                occurAmount = amount.setScale(radixPoint, BigDecimal.ROUND_DOWN).toPlainString();
            }
        } catch(Exception e){
            log.error("【抽奖】根据用户Id："+userId+"获取用户需要提醒奖金翻倍的记录发生异常，异常信息为：",e);
            return occurAmount;
        }
        return occurAmount;
    }


    /**
     * xzhang
     * 查询用户分叉币系统分发及抽奖获得
     * @param userId
     * @param type BillType中的sysDistribute，sysDistribute，为空默认查这两种
     * @return Long
     */
    public Long userDistributionCount(String userId,String type){
        Long count=0L;
        if(StringUtil.exist(type)){
            //合法性校验
            if(!type.equals(BillType.sysDistribute.getKey()+"")&&!type.equals(BillType.luckyIn.getKey()+","+BillType.luckyDouble.getKey())){
                log.error("【统计分发记录】当前用户："+userId+"发生非法请求，异常参数：type="+type);
                return count;
            }
        }else{
            type = BillType.sysDistribute.getKey()+","+BillType.luckyIn.getKey();
        }
        try{
            String sql = "select count(1) from billdistribution  WHERE type in ("+type+") and userId = "+userId;
            List<Long> list = (List<Long>) Data.GetOne(sql,null);
            if(list != null&&list.size()>0){
                count=list.get(0);
            }
        }catch (Exception e){
            log.error("【统计分发记录】当前用户："+userId+"请求type="+type+"发生非受控异常，异常信息为：",e);
        }
        return count;
    }


    /**
     * xzhang
     * 查询用户分叉币系统分发及抽奖获得
     * @param userId
     * @param type BillType中的sysDistribute，sysDistribute，为空默认查这两种
     * @return Long
     */
    public List<BillDistribution> userDistribution(String userId, String type, int pageIndex, int pageSize,String language){
        List<BillDistribution> billList = null;
        if(StringUtil.exist(type)){
            //合法性校验
            if(!type.equals(BillType.sysDistribute.getKey()+"")&&!type.equals(BillType.luckyIn.getKey()+"")&&!type.equals(BillType.luckyDouble.getKey()+"") && !type.equals(BillType.luckyIn.getKey()+","+BillType.luckyDouble.getKey())){
                log.error("【查询分发记录】当前用户："+userId+"发生非法请求，异常参数：type="+type);
                return billList;
            }
        }else{
            type = BillType.sysDistribute.getKey()+","+BillType.luckyIn.getKey()+","+BillType.luckyDouble.getKey();
        }
        try{
            String sql = "select sendTime,fundsType,amount,sourceRemark,type from billdistribution  WHERE type in ("+type+") and userId = "+userId +" order by sendTime desc limit ?,? ";
            billList =  find(sql, new Object[]{(pageIndex-1)*pageSize,pageSize},BillDistribution.class);
        }catch (Exception e){
            log.error("【查询分发记录】当前用户："+userId+"请求type="+type+"发生非受控异常，异常信息为：",e);
        }
        if(billList != null&&billList.size()>0){
            for(BillDistribution vo :billList){
                vo.setDataName("");//隐藏数据库信息
                vo.setDatabase("");//隐藏数据库信息
                CoinProps coinProps = DatabasesUtil.coinProps(vo.getFundsType());
                vo.setCoinView(coinProps.getPropTag());
                if(BillType.sysDistribute.getKey() == vo.getType()){
                    CoinProps coinPropsOrg = DatabasesUtil.coinProps(vo.getSourceRemark());
                    vo.setSourceRemark(coinPropsOrg.getPropTag()+" "+Lan.Language(language, "分发"));
                    vo.setTypeView(Lan.Language(language, "系统分发"));
                }else if(BillType.luckyIn.getKey() == vo.getType()){
                    vo.setTypeView(Lan.Language(language, "活动奖励"));
                    JSONObject jsonRemark = JSONObject.parseObject(vo.getSourceRemark());
                    if("en".equals(language)){
                        vo.setSourceRemark(Lan.Language(language, "活动奖金")+"-\""+jsonRemark.getString(language)+"\"");
                    }else{
                        vo.setSourceRemark(Lan.Language(language, "活动奖金")+"-\""+jsonRemark.getString(language)+"\"");
                    }
                }else{
                    vo.setTypeView(Lan.Language(language, "奖金翻倍"));
                    JSONObject jsonRemark = JSONObject.parseObject(vo.getSourceRemark());
                    if("en".equals(language)){
                        vo.setSourceRemark(Lan.Language(language, "奖金翻倍")+"-\""+jsonRemark.getString(language)+"\"");
                    }else{
                        vo.setSourceRemark(Lan.Language(language, "奖金翻倍")+"-\""+jsonRemark.getString(language)+"\"");
                    }
                }
            }
        }
        return billList;
    }
}
