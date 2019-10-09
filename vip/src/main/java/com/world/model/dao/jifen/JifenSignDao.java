package com.world.model.dao.jifen;

import com.google.code.morphia.Datastore;
import com.world.data.mongo.MongoDao;
import com.world.model.dao.level.IntegralRuleDao;
import com.world.model.entity.level.IntegralRule;
import com.world.model.entity.level.JifenSign;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.entity.level.JifenType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xie on 2017/6/24.
 */
public class JifenSignDao extends MongoDao<JifenSign, String> {

    IntegralRuleDao ruleDao = new IntegralRuleDao();
    Logger logger = Logger.getLogger(JifenSignDao.class);
    /**
     * 根据userId和JifenType取积分标志对象
     * @param userId
     * @param type
     * @return
     */
    public JifenSign getJifenSign(String userId, int seqNo){
        Query<JifenSign> q=null;
        q = getQuery(JifenSign.class).filter("userId =", userId).filter("jifenType = ", seqNo);
        return super.findOne(q);
    }

    /**
     * 是否有机会获得积分
     * @param userId
     * @param type
     * @return
     */
    public boolean canGetJifen(String userId, int seqNo){
        JifenSign jifenSign = getJifenSign(userId, seqNo);
        IntegralRule rule = ruleDao.getBySeqNo(seqNo);
        int integType = rule.getIntegType();
        if(null == jifenSign){
            return true;
        }else{
            if(integType == 1) { //一次性
                if(jifenSign.getCompFlag() == 0){
                    return true;
                }
            }else if(integType == 2){//周期性
                //周期性默认是天，如果时间间隔没有
                if(null == rule.getPeriod() || rule.getPeriod().equals("d")){
                    Timestamp time = jifenSign.getOperTime();
                    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String ds1 = sdf.format(time);
                    String ds2 = sdf.format(currentTime);
                    if(!ds1.equals(ds2) && currentTime.after(time)){
                        return true;
                    }
                }
            }else if(integType == 3){//重复性
                return true;
            }
        }

        return false;
    }




    //添加积分标志信息sql
    public String addJifenSign(JifenSign jifenSign){
        String nid = super.save(jifenSign).getId().toString();
        logger.info("成功添加一条新数据，主键："+nid);
        return nid;
    }

    //更新积分标志信息
    public UpdateResults<JifenSign> updateJifenSign(JifenSign jifenSign){
        Datastore ds = super.getDatastore();
        Query<JifenSign> q=null;
        q = getQuery(JifenSign.class).filter("_id", jifenSign.getId());
        UpdateOperations<JifenSign> ops = ds.createUpdateOperations(JifenSign.class);
        ops.set("operTime", jifenSign.getOperTime());
        UpdateResults<JifenSign> ur = super.update(q, ops);
        return ur;
    }





}
