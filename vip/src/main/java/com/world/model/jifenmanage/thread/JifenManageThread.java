package com.world.model.jifenmanage.thread;

import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.jifen.FuncJumpDao;
import com.world.model.dao.jifen.JifenDao;
import com.world.model.dao.jifen.JifenSignDao;
import com.world.model.dao.level.IntegralRuleDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.level.FuncJump;
import com.world.model.entity.level.IntegralRule;
import com.world.model.entity.level.Jifen;
import com.world.model.entity.level.JifenSign;
import com.world.model.entity.user.User;
import com.world.util.date.TimeUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xie on 2017/6/26.
 */
public class JifenManageThread extends Thread {

    private Logger log = Logger.getLogger(JifenManageThread.class.getName());

    private JifenSignDao jifenSignDao = new JifenSignDao();
    private IntegralRuleDao ruleDao = new IntegralRuleDao();
    private FuncJumpDao funcJumpDao = new FuncJumpDao();
//    private SysLogDao sysLogDao = new SysLogDao();
    private JifenDao jifenDao = new JifenDao();

    private String userId;      //用户ID
    private int seqNo;          //积分类型序号
    private BigDecimal amount;  //资金数量

    private String logId;  //项目名称
    private String batchId;  //项目名称
    private String systemName;  //项目名称
    private String modularName;       //日志编号
    private int modularType;       //日志编号
    private String logUserId;   //ID
    private String logUserName;   //Name

    public JifenManageThread(String userId, int seqNo, BigDecimal amount, String logId, String batchId, String systemName, String modularName, int modularType, String logUserId, String logUserName){
        this.userId = userId;
        this.seqNo = seqNo;
        this.amount = amount;

        this.logId = logId;
        this.batchId = batchId;
        this.systemName = systemName;
        this.modularName = modularName;
        this.modularType = modularType;
        this.logUserId = logUserId;
        this.logUserName = logUserName;
    }

    @Override
    public void run() {
        //开始处理积分
        addJifen();
    }

    /**
     * add by xwz 20170625
     * 增加积分
     */
    public void addJifen(){
        IntegralRule rule = ruleDao.getBySeqNo(seqNo);
        String logContent = "";
        String remark = "";
        try{
            UserDao userDao =  new UserDao();
            User user = userDao.getUserById(userId);

            logContent = logId + " 用户：" + userId + "获得：" + rule.getRule() + "积分处理中...";
//            sysLogDao.addSysLog(userId, logId, batchId, systemName, modularName,modularType, logContent, logUserId,logUserName,remark);

                //判断是否可以获得积分
            if(jifenSignDao.canGetJifen(userId,seqNo)){
                List<OneSql> addJifenSqls  = new ArrayList<>();
                //1.插入积分流水表:jifen
                BigDecimal totalScore = (amount == null ? BigDecimal.ONE : amount).multiply(new BigDecimal(rule.getScore()));//计算总积分
                totalScore = totalScore.setScale(0, BigDecimal.ROUND_DOWN); //对积分进行四舍五入
                if(totalScore.compareTo(BigDecimal.ONE) >= 0 ){
                    Jifen jifen = new Jifen(user.getId(), user.getUserName(), totalScore, rule.getMemo(), new Integer(rule.getSeqNo()), 0, TimeUtil.getNow());
                    addJifenSqls.add(jifenDao.getTransInsertSql(jifen));
                    if(Data.doTrans(addJifenSqls)){
                        //更新user表中的积分
                        userDao.updateUserJifen(userId, totalScore.doubleValue());
                        //2.组装积分标志对象
                        JifenSign jifenSign = jifenSignDao.getJifenSign(userId,rule.getSeqNo());
                        if(null != jifenSign){//更新
                            Timestamp ts = new Timestamp(System.currentTimeMillis());
                            jifenSign.setOperTime(ts);//完成时间
                            jifenSign.setCompFlag(1);  //完成标志：1，完成；0，未完成
                            jifenSignDao.updateJifenSign(jifenSign);
                        }else{//插入
                            jifenSign = new JifenSign(jifenSignDao.getDatastore());
                            Timestamp ts = new Timestamp(System.currentTimeMillis());
                            jifenSign.setOperTime(ts);		//完成时间
                            jifenSign.setCompFlag(1);		//完成标志：1，完成；0，未完成
                            jifenSign.setUserId(userId);
                            jifenSign.setJifenType(seqNo);	//积分类型
                            jifenSignDao.addJifenSign(jifenSign);
                        }
                        //处理成功，记录到sysLog
                        logContent = logId+ " 用户：" + userId + "获得：" + rule.getRule() + "积分,积分数量：" + rule.getScore();
//                        sysLogDao.addSysLog(userId, logId, batchId, systemName, modularName,modularType, logContent, logUserId,logUserName,remark);
                        log.info("用户：" + userId + "获得：" + rule.getRule() + "积分,积分数量：" + rule.getScore());
                    }
                    /*Start by guankaili 20181203 积分模块增加链接逻辑 */
                    //处理积分模块，增加链接
                    funcJump(rule.getIntegType());
                    /*end*/
                }else{
                    log.info("用户：" + userId + "获得积分数量为0，不能获得积分");
                }
            }else{
                //处理，不能获得积分，记录到sysLog
                logContent = logId + ": 用户：" + userId + "不能获得：" + rule.getRule() + "积分";
//                sysLogDao.addSysLog(userId, logId, batchId, systemName, modularName,modularType, logContent, logUserId,logUserName,remark);
            }
        }catch(Exception e){
            //sysLog记录积分处理失败
            log.error(logId + ": 用户：" + userId + "获得：" + rule.getRule() + "积分出错，错误信息：" + e.toString(), e);
            logContent = logId + ": 用户："+ userId + "获得：" + rule.getRule() + "积分出错，错误信息：" + e.toString();
//            sysLogDao.addSysLog(userId, logId, batchId, systemName, modularName,modularType, logContent, logUserId,logUserName,remark);
        }
    }

    private void funcJump(int integType) {
        //手机验证
        if(4 == seqNo){
            FuncJump funcJump = new FuncJump(funcJumpDao.getDatastore());
            funcJump.setSeqNo(seqNo);
            funcJump.setRegStatus(true);
            funcJump.setLoginState(true);
            funcJump.setMobileState(true);
            funcJump.setGoogleStatus(false);
            funcJump.setFstStatus(false);
            funcJump.setDayStatus(false);
            funcJump.setCoinStatus(false);
            funcJump.setSomeCoinStatus(false);
            funcJump.setJifenCategory(integType);
            funcJump.setUserId(userId);
            funcJumpDao.save(funcJump);
        }else if(5 == seqNo){//谷歌验证
            FuncJump funcJump = new FuncJump(funcJumpDao.getDatastore());
            funcJump.setSeqNo(seqNo);
            funcJump.setRegStatus(true);
            funcJump.setLoginState(true);
            funcJump.setMobileState(false);
            funcJump.setGoogleStatus(true);
            funcJump.setFstStatus(false);
            funcJump.setDayStatus(false);
            funcJump.setCoinStatus(false);
            funcJump.setSomeCoinStatus(false);
            funcJump.setJifenCategory(integType);
            funcJump.setUserId(userId);
            funcJumpDao.save(funcJump);
        }else if(6 == seqNo){//首次充值
            FuncJump funcJump = new FuncJump(funcJumpDao.getDatastore());
            funcJump.setSeqNo(seqNo);
            funcJump.setRegStatus(true);
            funcJump.setLoginState(true);
            funcJump.setMobileState(false);
            funcJump.setGoogleStatus(false);
            funcJump.setFstStatus(true);
            funcJump.setDayStatus(false);
            funcJump.setCoinStatus(false);
            funcJump.setSomeCoinStatus(false);
            funcJump.setJifenCategory(integType);
            funcJump.setUserId(userId);
            funcJumpDao.save(funcJump);
        }else if(8 == seqNo){//首次交易
            FuncJump funcJump = new FuncJump(funcJumpDao.getDatastore());
            funcJump.setSeqNo(seqNo);
            funcJump.setRegStatus(true);
            funcJump.setLoginState(true);
            funcJump.setMobileState(false);
            funcJump.setGoogleStatus(false);
            funcJump.setFstStatus(false);
            funcJump.setDayStatus(false);
            funcJump.setCoinStatus(true);
            funcJump.setSomeCoinStatus(false);
            funcJump.setJifenCategory(integType);
            funcJump.setUserId(userId);
            funcJumpDao.save(funcJump);
        }
    }
}
