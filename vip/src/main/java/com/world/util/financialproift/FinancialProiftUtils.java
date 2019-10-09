package com.world.util.financialproift;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import com.Lan;
import com.world.data.mysql.Data;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.financialproift.UserFinancialInfo;
import com.world.model.entity.user.User;

public class FinancialProiftUtils {
	private static Logger log = Logger.getLogger(FinancialProiftUtils.class);
	/**
     * @param matrixLevel 投资矩阵级别
     * @return
     */
    public static BigDecimal giveMatrixLevelProfitAmount(int matrixLevel) {
        BigDecimal profitAmount = BigDecimal.valueOf(188);
        if (6 == matrixLevel) {
            profitAmount = BigDecimal.valueOf(188);
        } else if (5 == matrixLevel) {
            profitAmount = BigDecimal.valueOf(88);
        } else if (4 == matrixLevel) {
            profitAmount = BigDecimal.valueOf(38);
        } else if (3 == matrixLevel) {
            profitAmount = BigDecimal.valueOf(18);
        } else if (2 == matrixLevel) {
            profitAmount = BigDecimal.valueOf(8);
        } else if (1 == matrixLevel) {
            profitAmount = BigDecimal.valueOf(2);
        } else if (0 == matrixLevel) {
            profitAmount = BigDecimal.valueOf(0);
        }
        return profitAmount;
    }

    /**
     * @param curLevel    当前矩阵级别
     * @param targetLevel 目标矩阵级别
     * @return
     */
    public static BigDecimal giveDifLevelAmount(int curLevel, int targetLevel) {
        BigDecimal difLevelAmount = BigDecimal.valueOf(188);
        BigDecimal curLevelAmount = giveMatrixLevelProfitAmount(curLevel);
        BigDecimal targetLevelAmount = giveMatrixLevelProfitAmount(targetLevel);
        difLevelAmount = targetLevelAmount.subtract(curLevelAmount);
        return difLevelAmount;
    }

    /**
     * @param matrixLevel 投资矩阵级别
     * @return
     */
    public static BigDecimal giveVIPWeight(int matrixLevel) {
        BigDecimal vipWeight = BigDecimal.ZERO;
        /**
         * 投资188 Vollar可以获得100分的权重；0.53
         * 投资 88 Vollar可以获得45分的权重；0.511
         * 投资 38 Vollar可以获得20分的权重；0.52
         * 投资 18 Vollar可以获得10分的权重；0.55
         * 投资  8 Vollar可以获得4.5分的权重；0.625
         * 投资  2 Vollar可以获得1分的权重；0.5
         */
        if (6 == matrixLevel) {
            vipWeight = BigDecimal.valueOf(100);
        } else if (5 == matrixLevel) {
            vipWeight = BigDecimal.valueOf(46);
        } else if (4 == matrixLevel) {
            vipWeight = BigDecimal.valueOf(19.8);
        } else if (3 == matrixLevel) {
            vipWeight = BigDecimal.valueOf(9.3);
        } else if (2 == matrixLevel) {
            vipWeight = BigDecimal.valueOf(4.1);
        } else if (1 == matrixLevel) {
            vipWeight = BigDecimal.valueOf(1);
        } else if (0 == matrixLevel) {
            vipWeight = BigDecimal.ZERO;
        }
        return vipWeight;
    }
    
    public static BigDecimal giveVIPInsuranceWeight(BigDecimal insureInvestAmount) {
        BigDecimal vipWeight = BigDecimal.ZERO;
        /*除以2取整*/
        vipWeight = insureInvestAmount.divide(BigDecimal.valueOf(2), 0, BigDecimal.ROUND_DOWN);
        
        return vipWeight;
    }

    public static int giveHierarchyBuildFloor(int invitationActiveUser) {
        int returnHierarchyBuildFloor = 0;
        if (invitationActiveUser >= 10) {
            returnHierarchyBuildFloor = 10;
        } else {
            returnHierarchyBuildFloor = invitationActiveUser;
        }
        
        return returnHierarchyBuildFloor;
    }

    public static int givePushGuidanceRatio(int invitationActiveUser) {
        int returnPushGuidanceRatio = 0;
        if (invitationActiveUser >= 10) {
            returnPushGuidanceRatio = 50;
        } else if (8 == invitationActiveUser || 9 == invitationActiveUser) {
            returnPushGuidanceRatio = 40;
        } else if (6 == invitationActiveUser || 7 == invitationActiveUser) {
            returnPushGuidanceRatio = 30;
        } else if (4 == invitationActiveUser || 5 == invitationActiveUser) {
            returnPushGuidanceRatio = 20;
        } else if (2 == invitationActiveUser || 3 == invitationActiveUser) {
            returnPushGuidanceRatio = 10;
        } else {
            returnPushGuidanceRatio = 0;
        }
        
        return returnPushGuidanceRatio;
    }
    
    public static boolean outUserJudge(String userId) {
    	boolean outUserFlag = false;
    	try {
    		/*查询SQL*/
        	String sql = "";
        	sql = "select userId, authPayFlag from fin_userfinancialinfo where userId = " + userId + " ";
    		log.info("sql = " + sql);
    		UserFinancialInfo userFinancialInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
    		int authPayFlag = 0;
        	if (null == userFinancialInfo) {
        		outUserFlag = false;
        	} else {
        		authPayFlag = userFinancialInfo.getAuthPayFlag();
        	}
        	if (3 == authPayFlag) {
        		outUserFlag = true;
        	}
    	} catch (Exception e) {
    		log.info("理财报警REWARDERROR:用户出局判断异常", e);
    	}
		
    	return outUserFlag;
    }
    
    public static void sendUserReturnCapitalMsg(String userId, BigDecimal investAmount, BigDecimal investAvergPrice, 
    		BigDecimal expectProfitUsdt, BigDecimal staticProfitSumUsdt, Date profitTime, Date resetProfitTime, Date dealTime,String remindDay) {
    	UserDao userDao = new UserDao();
	    User user = userDao.getUserById(userId);
	    String userName = user.getUserName();
	    
	    /*日期格式*/
	    SimpleDateFormat sdf = null;
	    sdf = new SimpleDateFormat("MM-dd-yyyy");
	    
	    /*获取国际化语言*/
	    String userLanguage = user.getLanguage();
	    
	    /**
	     * 资金小数点处理
	     */
	    investAmount = investAmount.setScale(0, BigDecimal.ROUND_DOWN);
	    /*本金估值USDT*/
	    BigDecimal investAmountUsdt = investAmount.multiply(investAvergPrice).setScale(4, BigDecimal.ROUND_DOWN);
	    expectProfitUsdt = expectProfitUsdt.setScale(4, BigDecimal.ROUND_DOWN);
	    /*回本发放 = 预期 - 历史累计发放*/
	    BigDecimal returnAmountUsdt = expectProfitUsdt.subtract(staticProfitSumUsdt).setScale(4, BigDecimal.ROUND_DOWN);
	    
	    String msgTitle = Lan.Language(userLanguage, "回本加成");
	    String msgContent = Lan.Language(userLanguage, "用户回本发送邮件内容");
	    
	    msgContent = msgContent.replace("aaa", investAmount + "");
	    msgContent = msgContent.replace("bbb", investAmountUsdt + "");
	    msgContent = msgContent.replace("ccc", expectProfitUsdt + "");
	    msgContent = msgContent.replace("ddd", returnAmountUsdt + "");
	    msgContent = msgContent.replace("zzz", remindDay);
	    
	    if (!"en".equals(userLanguage)) {
	    	sdf = new SimpleDateFormat("yyyy-MM-dd");
	    }
	    msgContent = msgContent.replace("xxx", sdf.format(profitTime));
	    msgContent = msgContent.replace("yyy", sdf.format(dealTime));
	    
	    log.info("msgContent = " + msgContent);
	    
	    EmailDao eDao = new EmailDao();
        
        String content = eDao.getOutUserJudgeEmailHtml(userLanguage, userName, msgContent);
        eDao.sendEmail(user.getLoginIp(), userId, userName, msgTitle, content, user.getUserContact().getSafeEmail());
    }

    public static void main(String[] args) {
        outUserJudge("1003762");
    }
}
