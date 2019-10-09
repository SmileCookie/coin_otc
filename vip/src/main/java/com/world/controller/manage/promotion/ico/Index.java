package com.world.controller.manage.promotion.ico;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.pay.FundsDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.coin.CoinProps;
import com.world.model.ico.dao.ICOConfigDao;
import com.world.model.ico.dao.ICOExchangeNumConfigDao;
import com.world.model.ico.dao.ICOUserExchangeNumDao;
import com.world.model.ico.entity.ICOConfig;
import com.world.model.ico.entity.ICOExchangeNumConfig;
import com.world.model.ico.entity.ICOUserExchangeNum;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by xie on 2017/7/12.
 */

public class Index extends BaseAction {

    private static final long serialVersionUID = 1L;
    private ICOConfigDao configDao = new ICOConfigDao();
    private ICOUserExchangeNumDao userUserExchangeDao = new ICOUserExchangeNumDao();
    private ICOExchangeNumConfigDao exchangeNumConfigDao = new ICOExchangeNumConfigDao();

//    @Page(Viewer = JSON)
    public void showMenu() {
        try{
            List<ICOConfig> configList = configDao.getAllICOConfig();
            Map<String, Integer> paramMap = new HashMap<>();
            for(ICOConfig config : configList) {
                if(config.getSaleType() == 1){
                    paramMap.put("ration", config.getDisPlayFlag());
                }else if(config.getSaleType() == 2) {
                    paramMap.put("apply", config.getDisPlayFlag());
                }
            }
            String json = com.alibaba.fastjson.JSON.toJSONString(paramMap);
            json("获取配置信息成功", true, json);
        }catch (Exception e){
            json("获取配置信息失败", false, "");
        }
    }

    /*
    * 获取当前ICO状态（判断是配售还是申购）
    * */
//    @Page(Viewer="/cn/manage/subscription/index.jsp")
    public void index() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        String userId = userIdStr();
        if(StringUtils.isEmpty(userId)){
            json("您还没有登录，请先登录。", false, "");
            return;
        }

        //兑换比例
        List<Map<String, Object>> coinList = getICOExchange(currentTime);
        super.setAttr("exchangeRate", coinList);

        //下拉列表框币种余额设置
        List<Map<String, Object>> coinAndBalance = getCoinPropAndBalanceMaps(userId);
        super.setAttr("coinAndBalance", coinAndBalance);
        super.setAttr("btcBalance", coinAndBalance.get(0).get("balance"));

        //获取可兑换数量／已经兑换数量／兑换框里的提示信息
        ICOConfig icoConfig = configDao.getICOConfigByTime(currentTime);
        Map<String, Object> exchangeNumInfo = getUserExchangeNumInfo(userId, icoConfig);
        super.setAttr("exchangeNumInfo",exchangeNumInfo);

        int saleType = icoConfig.getSaleType();
        super.setAttr("saleType", saleType);//1：配售，2：申购

        /*查询兑换记录*/
        List<Map<String, Object>> exchangeRecordList = getExchangeRecordList();
        super.setAttr("exchangeRecordList", exchangeRecordList);
    }

    /**
     * 先判断兑换类型，配售时检查是否前N,名获取可兑换数量，和剩余兑换数量
     * @param userId
     * @param config
     * @return
     */
    public Map<String, Object> getUserExchangeNumInfo(String userId, ICOConfig config){
        //销售类型
        int saleType = config.getSaleType();
        Map<String, Object> paramMap = new HashMap<>();
        ICOUserExchangeNum icoUserExchangeNum = null;
        String des = "抱歉！您未获得原始配售额，感谢您的关注与参与。";
        if(saleType == 1){ //属于配售时间
            int beforeN = config.getBeforeN();
            if(isBeforeN(beforeN, userId)){//属于前N名，应获得配售权限
                icoUserExchangeNum = userUserExchangeDao.getICOUserExchangeNum(userId, saleType);
                if(icoUserExchangeNum == null){//没有获得配售权限
                    //分配配售权限
                    userUserExchangeDao.insertICOUserExchangeNum(userId, saleType, config.getEachUserConfigNum());
                    icoUserExchangeNum = userUserExchangeDao.getICOUserExchangeNum(userId, saleType);
                }
                des = "恭喜！您已获得GBC原始配售额度，已申购GBC，请及时充值申购。";
            }

        }else if(saleType == 2){//属于申购时间
            icoUserExchangeNum = userUserExchangeDao.getICOUserExchangeNum(userId, saleType);
            if(icoUserExchangeNum == null){//没有获得申购权限
                //分配申购权限
                userUserExchangeDao.insertICOUserExchangeNum(userId, saleType, config.getEachUserConfigNum());
                icoUserExchangeNum = userUserExchangeDao.getICOUserExchangeNum(userId, saleType);
            }
            des = "您的申购额度为GBC，已申购GBC。";
        }
        /*加强判断防止配置参数有误,配售阶段N名之后*/
        if(icoUserExchangeNum == null) {
            paramMap.put("totalNum", 0);
            paramMap.put("exchangeNum", 0);
        }else{
            paramMap.put("totalNum", icoUserExchangeNum.getTotalNum());
            paramMap.put("exchangeNum", icoUserExchangeNum.getExchangeNum());
        }
        paramMap.put("des",L(des, paramMap.get("totalNum").toString(), paramMap.get("exchangeNum").toString()));
        return paramMap;
    }

    /**
     * 币种下拉框和余额
     * */
    public List<Map<String, Object>> getCoinPropAndBalanceMaps(String userId){
        List<Map<String, Object>> list = new ArrayList<>();
        PayUserDao payUserDao = new PayUserDao();
        Map<String,BigDecimal> balanceMap = payUserDao.getBalanceMap(userId);
        Map<String, CoinProps> map = DatabasesUtil.getCoinPropMaps();
        for(Map.Entry<String, CoinProps> entry : map.entrySet()){
            Map<String, Object> tmpMap = new HashMap<>();
            CoinProps coin = entry.getValue();
            String coinName = coin.getDatabaseKey();
            if(coinName.equals("gbc")){
                continue;
            }
            tmpMap.put("key", coinName);
            tmpMap.put("propTag", coin.getPropTag());
            tmpMap.put("propEnName", coin.getPropEnName());
            BigDecimal balance = balanceMap.get(coinName);
            String balanceStr = "0";
            if(balance.compareTo(BigDecimal.ZERO) >= 0){
                balanceStr = balance.setScale(6, BigDecimal.ROUND_UP).stripTrailingZeros().toPlainString();
            }
            tmpMap.put("balance", balanceStr);
            list.add(tmpMap);
        }
        return list;
    }

    /**
     * 查询兑换记录
     */
    public List<Map<String, Object>> getExchangeRecordList() {
        String userId = userIdStr();
        List<Map<String, Object>> paramList = new ArrayList<>();
        String formatStr = "%Y-%m-%d %H:%i:%S";//格式化str
        if(lan.equals("en")){
            formatStr = "%m-%d-%Y %H:%i:%S";
        }
        //查询Bill表获取兑换记录
        String selBillSql = "select DATE_FORMAT(sendTime,'" + L(formatStr) + "')sendTime, amount, remark, status,fundsType from bill where userId = ? and type in (62,64) and status = 2 order by sendTime desc";
        List<List<Object>> list =  Data.Query(selBillSql, new Object[]{userId});
        for(List<Object> listObj : list) {
            Map<String, Object> tmpMap = new HashMap<>();
            tmpMap.put("sendTime", listObj.get(0));
            BigDecimal amount = new BigDecimal(listObj.get(1).toString());
            String amountStr = "0";
            if(amount.compareTo(BigDecimal.ZERO) >= 0){
                amountStr = amount.setScale(6, BigDecimal.ROUND_UP).stripTrailingZeros().toPlainString();
            }

            tmpMap.put("amount", amountStr);
            tmpMap.put("remark", listObj.get(2));
            tmpMap.put("status", listObj.get(3));
            tmpMap.put("coinName", DatabasesUtil.coinProps(new Integer(listObj.get(4).toString())).getPropTag());
            paramList.add(tmpMap);
        }
        return paramList;
    }

//    @Page(Viewer = JSON)
    public void getExchangeRecordListJson() {
        List<Map<String, Object>> paramList = getExchangeRecordList();
        json("success", true, com.alibaba.fastjson.JSON.toJSONString(paramList));
    }

    /**
     * 获取当前ICO兑换的数据结构
     */
    public List<Map<String, Object>> getICOExchange(Timestamp currentTime){
        List<Map<String, Object>> coinList = new ArrayList<>();
        try{
            List<ICOExchangeNumConfig> list = exchangeNumConfigDao.getCurrentICOExchangeNumList(currentTime);
            if(null == list || list.size() == 0){
                list = exchangeNumConfigDao.getSimpleICOConfig(1);
            }
            for(ICOExchangeNumConfig bean : list){
                Map<String, Object> tmpMap = new HashMap<>();
                String coinName = DatabasesUtil.coinProps(bean.getFundsType()).getPropTag();
                tmpMap.put("name", coinName);
                tmpMap.put("value", bean.getExchangeNum());
                coinList.add(tmpMap);
            }
            log.info("获取兑换比例成功");
        }catch (Exception e){
            log.error("获取兑换比例失败,错误信息" + e.toString());
        }
        return coinList;
    }

    /**
     * 判断是否在前N名之内
     * @param beforeN
     * @param userId
     * @return
     */
    public boolean isBeforeN(int beforeN, String userId) {
        try {
            int intUserId = Integer.parseInt(userId);
            if (intUserId <= beforeN) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }



    /*兑换接口*/
//    @Page(Viewer = JSON)
    public void userExchangeNum() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        //用户ID
        String userId = userIdStr();
        try{
            //销售类型
            int saleType = intParam("saleType");

            //兑换GBC数量
            BigDecimal exchangeNum = decimalParam("exchangeNum").setScale(0,BigDecimal.ROUND_DOWN);
            //币种类型
            String coinName = param("coinName").toLowerCase();
            //币种类型代码
            int fundsType = DatabasesUtil.coinProps(coinName).getFundsType();

            /*获取本币种余额start开始*/
            PayUserDao payUserDao = new PayUserDao();
            Map<String,BigDecimal> balanceMap = payUserDao.getBalanceMap(userId);
            BigDecimal userFundsBalance = balanceMap.get(coinName);
            /*end*/

    	    //判断活动类型
            if(1 != saleType && 2!= saleType) {
                json(L("活动类型参数传递不对。"), false, "");
                return;
            }

            /*存储当前活动信息,ICO主配置表*/
            List<ICOConfig> configList = null;
            configList = activityStatusBySaleType(saleType, currentTime);
            /*检查活动申购活动是否开始*/
            if(null == configList){
                json(L("申购将于北京时间7月28日开启，敬请期待。"), false, "");
                return;
            }
            if (null != configList && configList.size() != 1){
                json(L("申购将于北京时间7月28日开启，敬请期待。"), false, "");
                return;
            }else{
                if(saleType == 2){
                    BigDecimal totalNum = new BigDecimal(configList.get(0).getSaleTotalNum());      //配售总数量
                    BigDecimal importNum = new BigDecimal(configList.get(0).getImportNum());        //上轮到入数量
                    BigDecimal saleExchangeNum = new BigDecimal(configList.get(0).getSaleExchangeNum());//以兑换数量
                    BigDecimal suplusExchangeNum = totalNum.add(importNum).subtract(saleExchangeNum);//剩余未兑换数量
                    if(suplusExchangeNum.compareTo(exchangeNum) < 0) {
                        json(L("抱歉，市场剩余兑换额度不足。"), false, "");
                        return;
                    }
                }

            }

            /*获取ico兑换数量配置,icoExchangeNumConfig表*/
            List<ICOExchangeNumConfig> exchangeNumConfigList = null;
            exchangeNumConfigList = giveExchangeNumConfigBySaleType(saleType, fundsType, currentTime);
            if (null != exchangeNumConfigList && exchangeNumConfigList.size() != 1){
                json(L("没有获取到兑换比例。"), false, "");
                return;
            }

            /*获取用户兑换限额*/
            ICOUserExchangeNum icoUserExchangeNum = userUserExchangeDao.getICOUserExchangeNum(userId,saleType);
            if(null == icoUserExchangeNum){
                json(L("没有申购权限。"), false, "");
                return;
            }

            //申购数量
            BigDecimal totalNumConfig = new BigDecimal(icoUserExchangeNum.getTotalNum());
            BigDecimal exchangeNumConfig = new BigDecimal(icoUserExchangeNum.getExchangeNum());
            if(null == icoUserExchangeNum || exchangeNum.compareTo(BigDecimal.ZERO) <= 0) {
                json(L("申购数量有误，请重新输入。"), false, "");
                return;
            }

            //申购数量大于剩余申购额度
            if(totalNumConfig.subtract(exchangeNumConfig).compareTo(exchangeNum)  < 0){
                json(L("申购数量超出限额，请重新输入。"), false, "");
                return;
            }


    	    /*属于配售时间配售阶段*/
            if (1 == saleType) {
                //检查是否在前N名
                int beforeN = configList.get(0).getBeforeN();
                //不属于前N名
                if (!isBeforeN(beforeN, userId)) {
                    json(L("抱歉，您未获得原始配售额，请等待公开申购。"), true, "");
                    return;
                }
            }

    	    //获取当前兑换比例
            BigDecimal exchangePro = new BigDecimal(exchangeNumConfigList.get(0).getExchangeNum());
   	 	    /*真实的兑换消耗本币,BTC,LTC的数量*/
            BigDecimal sourceCurRealNum = null;
   	 	    /*兑换数量GBC/兑换比例,即从第6位UP，例如1.1234561显示为1.123457*/
            sourceCurRealNum = exchangeNum.divide(exchangePro, 6, RoundingMode.UP);
            log.info("userId = " + userId + ", saleType = " + saleType + ", fundsType = " + fundsType);
            log.info("exchangePro = " + exchangePro + ", exchangeNum = " + exchangeNum + ", sourceCurRealNum = " + sourceCurRealNum);
   	 	    //判断用户资金
            if (userFundsBalance.compareTo(sourceCurRealNum) < 0 || sourceCurRealNum.compareTo(BigDecimal.ZERO) <= 0) {
                json(L("抱歉，用户资金不足。"), false, "");
                return;
            } else {
   	 		    /*开始进行兑换操作*/
                boolean result = dealUserExchangeToDB(userId, saleType, fundsType, sourceCurRealNum, exchangeNum);
                if(result) {
                    json(L("兑换成功。"), true, "");
                }else{
                    json(L("兑换失败。"), false, "");
                }
                return;
            }
        }catch(Exception e){
            log.error("用户：" + userId + "兑换失败：" + e.toString());
            json(L("兑换失败。"), false, "");
        }

    }

//    public boolean dealUserExchangeToDB(String userId, int saleType, int fundsType, BigDecimal sourceCurRealNum, BigDecimal exchangeNum) {
//        String userName = userName();
//        FundsDao fundsDao = new FundsDao();
//    	/*开启事物*/
//        List<OneSql> paySqls  = new ArrayList<>();
//
//    	/*更新icoUserExchangeNum*/
//        String sql = "";
//        sql = "update icoUserExchangeNum set exchangeNum = exchangeNum + " + exchangeNum + " "
//                + "where userId = " + userId + " and saleType = " + saleType + " and totalNum >= exchangeNum + " + exchangeNum + "";
//        log.info("sql = " + sql);
//        paySqls.add(new OneSql(sql, 1, null));
//
//    	/*插入bill流水表*/
//        /**
//         * icoExchangePlacingIn(61, "ICO配售兑入", 1), icoExchangePlacingOut(62, "ICO配售兑出", 2),
//         * icoExchangeApplyIn(63, "ICO申购兑入", 1), icoExchangeApplyOut(64, "ICO申购兑出", 2),
//         */
//    	/*配售阶段*/
//        if (1 == saleType) {
//    		/*插入bill流水ICO配售兑入，GBC增加 */
//    		/*更新用户资金表中的兑币，增加*/
//    		/*次方法包含了sql = "updae pay_user set balance = balance + " + exchangeNum + " where userId = '" + userId + "' and fundsType = 9";*/
//            List<OneSql> sqlExchangeCur = fundsDao.addMoney(exchangeNum, userId, userName, sourceCurRealNum + "", 61, 9, BigDecimal.ZERO, "0", true);
//            paySqls.addAll(sqlExchangeCur);
//
//    		/*更新用户资金表中的本币，减少*/
//    		/*次方法包含了updae pay_user set balance = balance - " + sourceCurRealNum + " where userId = '" + userId + "' and fundsType = " + fundsType + " and balance >= " + sourceCurRealNum + ""*/
//            List<OneSql> sqlSourceCur = fundsDao.subtractMoney(sourceCurRealNum, userId, userName, exchangeNum + "", 62, fundsType, BigDecimal.ZERO, "0", true);
//            paySqls.addAll(sqlSourceCur);
//        } else {
//    		/*申购阶段*/
//    		/*插入bill流水ICO配售兑入，GBC增加 */
//    		/*更新用户资金表中的兑币，增加*/
//    		/*次方法包含了sql = "updae pay_user set balance = balance + " + exchangeNum + " where userId = '" + userId + "' and fundsType = 9";*/
//            List<OneSql> sqlExchangeCur = fundsDao.addMoney(exchangeNum, userId, userName, sourceCurRealNum + "", 63, 9, BigDecimal.ZERO, "0", true);
//            paySqls.addAll(sqlExchangeCur);
//
//    		/*更新用户资金表中的本币，减少*/
//    		/*次方法包含了updae pay_user set balance = balance - " + sourceCurRealNum + " where userId = '" + userId + "' and fundsType = " + fundsType + " and balance >= " + sourceCurRealNum + ""*/
//            List<OneSql> sqlSourceCur = fundsDao.subtractMoney(sourceCurRealNum, userId, userName, exchangeNum + "", 64, fundsType, BigDecimal.ZERO, "0", true);
//            paySqls.addAll(sqlSourceCur);
//
//            sql = "update ICOConfig set saleExchangeNum = saleExchangeNum + " + exchangeNum + " "
//                    + "where saleType = 2 and saleTotalNum + importNum >= saleExchangeNum + " + exchangeNum + "";
//            log.info("sql = " + sql);
//            paySqls.add(new OneSql(sql, 1, null));
//        }
//        if (Data.doTrans(paySqls)) {
//            log.info("【用户：" + userId + "】申购成功!【" + fundsType + ":" + sourceCurRealNum + ":" + exchangeNum + "】");
//            return true;
//        } else {
//            log.error("【用户：" + userId + "】申购失败!【" + fundsType + ":" + sourceCurRealNum + ":" + exchangeNum + "】");
//            return false;
//        }
//    }

    /**
     * 进行兑换处理
     * @param userId
     * @param saleType
     * @param fundsType
     * @param sourceCurRealNum 本币数量,BTC等
     * @param exchangeNum 兑币数量GBC
     */
    public boolean dealUserExchangeToDB(String userId, int saleType, int fundsType, BigDecimal sourceCurRealNum, BigDecimal exchangeNum) {
        String userName = userName();
        FundsDao fundsDao = new FundsDao();

        //sql集合
        List<OneSql> paySqls  = new ArrayList<>();
        //ICO兑入代码
        int icoInType;
        //ICO兑出代码
        int icoOutType;
        //ICO货币代码
        int icoCoinType = 9;

    	/*更新icoUserExchangeNum*/
        String updateICOUserSql = "update icoUserExchangeNum set exchangeNum = exchangeNum + ? "
                + "where userId = ? and saleType = ? and totalNum >= exchangeNum + ?";
        paySqls.add(new OneSql(updateICOUserSql, 1, new Object[]{exchangeNum,userId,saleType,exchangeNum}));

        if (1 == saleType) {//配售阶段
            //配售兑入
            icoInType = 61;
            //配售兑出
            icoOutType = 62;
            //更新ICOConfig
            String updateICOConfigSql = "update ICOConfig set saleExchangeNum = saleExchangeNum + ? "
                    + "where saleType = ? and saleTotalNum >= saleExchangeNum + ?";
            paySqls.add(new OneSql(updateICOConfigSql, 1, new Object[]{exchangeNum,saleType,exchangeNum}));
        } else {        //申购阶段
            //申购兑入
            icoInType = 63;
            //申购兑出
            icoOutType = 64;
            String updateICOConfigSql = "update ICOConfig set saleExchangeNum = saleExchangeNum + ? "
                    + "where saleType = ? and saleTotalNum + importNum >= saleExchangeNum + ?";
            paySqls.add(new OneSql(updateICOConfigSql, 1, new Object[]{exchangeNum,saleType,exchangeNum}));
        }
        //更新payUser表和插入Bill表（GBC增加）
        List<OneSql> sqlExchangeCur = fundsDao.addMoney(exchangeNum, userId, userName, sourceCurRealNum + "", icoInType, icoCoinType, BigDecimal.ZERO, "0", true);
        paySqls.addAll(sqlExchangeCur);

        //更新payUser表和插入Bill表（本币减少）
        List<OneSql> sqlSourceCur = fundsDao.subtractMoney(sourceCurRealNum, userId, userName, exchangeNum + "", icoOutType, fundsType, BigDecimal.ZERO, "0", true);
        paySqls.addAll(sqlSourceCur);



        if (Data.doTrans(paySqls)) {
            UserCache.resetUserFunds(userId);
            log.info("【用户：" + userId + "】申购成功!【" + fundsType + ":" + sourceCurRealNum + ":" + exchangeNum + "】");
            return true;
        } else {
            log.error("【用户：" + userId + "】申购失败!【" + fundsType + ":" + sourceCurRealNum + ":" + exchangeNum + "】");
            return false;
        }
    }


    /**
     * 根据发售类型，获取活动信息
     * @param saleType
     * @return
     */
    public List<ICOConfig> activityStatusBySaleType(int saleType,Timestamp currentTime) {
        String sql = "";
        sql = "select * from ICOConfig "
                + "where saleType = " + saleType + " and exchangeState = 1 "
                + "and exchangeStartTime <= ? and exchangeEndTime >= ?";
        log.info("sql = " + sql);
        /*存储当前活动信息,ICO主配置表*/
        List<ICOConfig> configList = configDao.find(sql, new Object[]{currentTime,currentTime}, ICOConfig.class);
        return configList;
    }

    /**
     * 根据发售类型，获取
     * @param saleType
     */
    public List<ICOExchangeNumConfig> giveExchangeNumConfigBySaleType(int saleType, int fundsType, Timestamp currentTime) {
        String sql = "";
        sql = "select * from icoExchangeNumConfig "
                + "where saleType = " + saleType + " and fundsType = " + fundsType + " and exchangeOpenFlag = 1 "
                + "and exchangeStartTime <= ? and exchangeEndTime >= ?";
        log.info("sql = " + sql);
        /*存储ico兑换数量配置*/
        List<ICOExchangeNumConfig> exchangeNumConfigList = exchangeNumConfigDao.find(sql, new Object[]{currentTime, currentTime}, ICOExchangeNumConfig.class);
        return exchangeNumConfigList;
    }


    /**
     * 获取兑换数量
     */
//    @Page(Viewer = JSON, Cache = 60)
//    public void getExchangeNum() {
//        try{
//            Map<String, Object> returnMap = new HashMap<>();
//            int saleType = intParam("saleType");
//            String sql = "select sum(saleExchangeNum) saleExchangeNum from ICOConfig";
//            if(saleType == 1){
//                sql += " where saleType = 1";
//            }else if(saleType == 2){
//                sql += " where saleType = 2";
//            }
//            List<BigDecimal> list = (List<BigDecimal>) Data.GetOne(sql,new Object[]{});
//            int exchangeNum = list.get(0).intValue();
//            returnMap.put("exchangeNum", exchangeNum);
//            json("success", true, com.alibaba.fastjson.JSON.toJSONString(returnMap));
//        }catch (Exception e){
//            log.error("获取兑换数量出错" + e.toString());
//            json("success", false, "");
//        }
//
//    }

}
