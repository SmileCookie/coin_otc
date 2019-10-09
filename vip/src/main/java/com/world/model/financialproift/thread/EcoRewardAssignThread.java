package com.world.model.financialproift.thread;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.entity.financialproift.FinProfitAssignDetail;
import com.world.model.entity.financialproift.UserFinancialInfo;

public class EcoRewardAssignThread extends Thread {

    private static Logger log = Logger.getLogger(EcoRewardAssignThread.class.getName());

    public EcoRewardAssignThread(UserFinancialInfo userFinancialInfo, BigDecimal profitUdstPrice, CountDownLatch countDownLatch) {
        this.userFinancialInfo = userFinancialInfo;
        this.profitUdstPrice = profitUdstPrice;
        this.countDownLatch = countDownLatch;
    }

    private Integer ecoRewardAssignLayerCount = 12;
//    private static final Integer UNIT = 9;
    private static final Integer TIMEOUT_MILLS = 30000;
    private static final String CARACTERSET = "UTF-8";
    private UserFinancialInfo userFinancialInfo;
    private BigDecimal profitUdstPrice;
    private CountDownLatch countDownLatch;

    @Override
    public void run() {

        try {
        	TransactionObject txObj = new TransactionObject();
            List<OneSql> sqls = new ArrayList<>();
            long currentTime = System.currentTimeMillis();
            //获取Work中传入的该笔投资的信息
            int assignuserid = userFinancialInfo.getUserId();//分配人UserId
            String assignvid = userFinancialInfo.getUserVID();//分配人Vid
            BigDecimal investamount = userFinancialInfo.getInvestUsdtAmount();//投资金额
            int parentid = userFinancialInfo.getId();//关联投资表ID

            log.info("assignUserId = " + assignuserid + ", assignVid = " + assignvid
                    + ", investAmount = " + investamount + ", investId = " + parentid);

            /** 线程处理整体业务逻辑
             * 1、根据 investUserVid 调用钱包接口获取钢印关系（最高取12层用户，按照获取的VidList倒序获取）
             * 2、计算可分配、单个可分配和分配剩余金额
             * 算法：
             *      1）总奖励金额：投资金额*5/100
             *      2）每个用户可分配金额：总奖励金额/12
             *      3）剩余金额（9位精度向下取整）：
             *          被邀请数为0：总奖励金额
             *          0<被邀请数<12：总奖励金额 - (每个用户可分配金额)*被邀请VID人数
             *          被邀请数 = 12：总奖励金额 - （每个用户可分配金额）*11
             * 3、根据 investUserVid 和 循环得到的 beInvestUserVid 以及 投资ID判断是否已插入该记录
             * 4、若未插入，则生成钢印收益分配记录，初始记录为未结算状态0，生成VDS生态收益奖励分配信息-vip_financial
             * 5、将本次投资的分配标记 ecologySystemDealFlag 修改为1 已分配-vip_financial
             * 6、将剩余未分配金额转给平台理财专有账户，bill_financial/pay_user_financial-vip_main
             * 7、事务执行本次投资分配信息
             */

            /*1、获取钢印关系（通过调用新钱包架构的接口）,最多只保留12层*/
            List<String> ecoVids = null;

            /*2、计算本次可分配收益、单个用户可分配收益、收益折算成USDT个数，本次实际分配金额，分配后剩余金额 */
            /*总金额取9位小数*/
            BigDecimal avilableAssignAmount = investamount.multiply(new BigDecimal(0.05)).setScale(9, BigDecimal.ROUND_DOWN);
            /*用户分配取3位小数*/
            BigDecimal perAssignAmount = avilableAssignAmount.divide(new BigDecimal(ecoRewardAssignLayerCount), 3, BigDecimal.ROUND_DOWN);
            BigDecimal profitUdstAmount = perAssignAmount.multiply(profitUdstPrice).setScale(3, BigDecimal.ROUND_DOWN);
            BigDecimal actualAssignAmount = new BigDecimal(0);
            BigDecimal unAssignAmount = new BigDecimal(0);
            /*是否需要分配标志*/
            boolean distFlag = false;

            /*3、判断是否已经计入分配表,拼凑可执行的sql*/
            StringBuffer ecorewardassignSql = new StringBuffer("");
            int effectRows = 0;
            if (perAssignAmount.compareTo(BigDecimal.ZERO) > 0) {
            	/*金额大于等于0.001 可分配*/
            	distFlag = true;
            	ecoVids = getEcoVids(assignvid);
            	ecorewardassignSql = new StringBuffer("insert into " +
                        "fin_profit_assign_detail" +
                        "(assignUserid,assignVid,profitUserid,profitVid,profitAmount,usdtAmount,usdtPrice," +
                        "parentid,investAmount,profitType,flag,createTime,profitUserName) " +
                        "values ");
                for (int i = 0; i < ecoVids.size(); i++) {
                    String profitVid = ecoVids.get(i);
                    //根据 investUserVid 和 循环得到的 beInvestUserVid 以及 投资ID判断是否已插入该记录,已经插入则略过这条记录
                    String sql = "select * from fin_profit_assign_detail " +
                            "where assignVid = '" + assignvid + "' and profitVid = '" + profitVid + "' and parentid = " + parentid;
                    log.info("EcoRewardAssignThread sql = " + sql);
                    FinProfitAssignDetail profitAssignDetail = (FinProfitAssignDetail) Data.GetOne("vip_financial", sql, null, FinProfitAssignDetail.class);
                    log.info("profitAssignDetail = " + JSON.toJSONString(profitAssignDetail));
                    if (profitAssignDetail != null) {
                        continue;
                    }
                    
                    //根据Vid查询被分配人的信息(已支付)
                    sql = "select * from fin_userfinancialinfo where authPayFlag = 2 and userVID = '" + profitVid + "'";
                    log.info("EcoRewardAssignThread sql = " + sql);
                    UserFinancialInfo userInfo = (UserFinancialInfo) Data.GetOne("vip_financial", sql, null, UserFinancialInfo.class);
                    log.info("userInfo = " + JSON.toJSONString(userInfo));

                    //追加该 beinvitedVid 分配sql,将本次金额计入实际分配金额
                    if (userInfo != null) {
                        //有效分配用户+1
                        effectRows++;
                        ecorewardassignSql.append("(" + assignuserid + ", '" + assignvid + "', " + userInfo.getUserId() + ", '" + profitVid + "', "
                                + perAssignAmount + ", " + profitUdstAmount + "," + profitUdstPrice + ","
                                + parentid + ", " + investamount + ", 6, 0, now(), '"+ userInfo.getUserName()+"'),");
                        actualAssignAmount = actualAssignAmount.add(perAssignAmount);
                    }
                }
            }

            /*4、计算剩余金额*/
            if (ecoVids == null || ecoVids.size() <= 0) {
                unAssignAmount = avilableAssignAmount;
            } else {
                unAssignAmount = avilableAssignAmount.subtract(actualAssignAmount).setScale(9, BigDecimal.ROUND_DOWN);
            }
            /*UNIT = 9*/
//            unAssignAmount = unAssignAmount.setScale(9, BigDecimal.ROUND_DOWN);
//            String unAssignAmountStr = unAssignAmount.toPlainString();

            /*5、vip_financial - 插入分配信息表的sql */
            if (distFlag && StringUtils.isNotEmpty(ecorewardassignSql.toString())) {
                String substringSql = ecorewardassignSql.toString().substring(0, ecorewardassignSql.toString().length() - 1);
                if (effectRows != 0) {
                    sqls.add(new OneSql(substringSql, effectRows, null, "vip_financial"));
                }
            }
            
            /*6、vip_financial - 将本次投资的分配标记 ecologySystemDealFlag 修改为1 已分配*/
            String sql = "update fin_productinvest set ecologySystemDealFlag = 1 where id = " + parentid;
            sqls.add(new OneSql(sql, 1, null, "vip_financial"));

            /*7、vip_main - 将剩余未分配金额转给平台理财专有账户【用户ID：1216832 邮箱：lvwa1900@163.com】
             * pay_user_financial:balance = balance + unAssignAmount
             */
            sql = "update pay_user_financial set balance = balance + " + unAssignAmount + " where userid = 1216832 and fundstype = 51 ";
            log.info("理财报警:EcoRewardAssignThread sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));
            
            /*8、vip_main - bill_financial:插入一条type=5385 amount=unAssignAmount 的记录（type=5385，资金流向=1收入。公司理财账号专有，生态没有分配完的转入超级节点搭建）*/
            sql = "insert into bill_financial (userId, userName, type, amount, createTime, balance, fundsType, typeName, "
                    + "remark, vdsUsdtPrice, investProPeriod, matrixLevel) "
                    + "select 1216832, userName, 5385, " + unAssignAmount + ", "
                    + "" + currentTime + " , (balance + profit + insureInvestFreezeAmount), "
                    + "51, '生态体系参与奖励转入超级节点搭建', '转入帐号：1216832，投资ID：" + parentid + "', 0, 0, 0 "
                    + "from pay_user_financial where userid = 1216832 and fundstype = 51 for update";
            log.info("理财报警:EcoRewardAssignThread sql = " + sql);
            sqls.add(new OneSql(sql, 1, null, "vip_main"));

            /*9、事务执行本次投资分配信息*/
            txObj.excuteUpdateList(sqls);
            if (txObj.commit()) {
//                log.info("理财报警REWARDINFO:投资ID【" + parentid + "】" + assignvid + "分配成功");
            } else {
                log.info("理财报警REWARDERROR:投资ID【" + parentid + "】" + assignvid + "分配失败");
            }

        } catch (Exception e) {
            log.info("理财报警REWARDERROR:EcoRewardAssignThread", e);
        } finally {
            countDownLatch.countDown();
        }

    }

    /**
     * 根据传入的VID获取相关的生态
     *
     * @param vid
     * @return
     */
    private List<String> getEcoVids(String vid) {

        try {

            String walletUrl = ApiConfig.getValue("vidcheck.url");
            String urlPattern = walletUrl + "/vidBubbleAddress?address=" + vid;
            GetMethod request = new GetMethod(urlPattern);
            String result = doHttp(request, TIMEOUT_MILLS, CARACTERSET);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(result)) {
                JSONObject model = JSONObject.parseObject(result);
                if (model != null) {
                    Integer code = model.getInteger("code");
                    if (code == 200) {
                        JSONObject data = model.getJSONObject("data");
                        JSONArray addressList = data.getJSONArray("addressList");
                        Integer addressListSize = data.getInteger("addressListSize");
                        if (addressListSize <= ecoRewardAssignLayerCount) {
                            return addressList.toJavaList(String.class);
                        } else {
                            List<String> list = addressList.toJavaList(String.class);
                            list = list.subList(0, ecoRewardAssignLayerCount);
                            return list;
                        }
                    } else {
                        log.error("理财报警REWARDERROR: 获取钢印关系失败，" +
                                "错误码：" + code + "，提示信息：" + model.getString("msg"));
                    }

                } else {
                    log.error("理财报警REWARDWARN: 获取钢印关系失败,返回的JSON为空");
                }
            }

        } catch (Exception e) {
            log.error("理财报警REWARDERROR:同步超级节点信息,获取钢印关系失败", e);
        }
        return new ArrayList<String>();
    }

    /**
     * @param request
     * @param timeout
     * @param charset
     * @return
     */
    public static String doHttp(HttpMethod request, int timeout, String charset) {
        HttpClient client = new HttpClient();
        try {

            HttpConnectionManagerParams managerParams = client.getHttpConnectionManager().getParams();
            //连接超时
            managerParams.setConnectionTimeout(timeout);
            //等待结果超时
            managerParams.setSoTimeout(timeout);
            client.getHttpConnectionManager().setParams(managerParams);

            request.setRequestHeader(HttpHeaders.ACCEPT, "application/json");
            request.setRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36");

            int code = client.executeMethod(request);
            if (code == 200) {
                String result = request.getResponseBodyAsString();
                log.info("获取到的VIDS数据 " + result);
                return result;
            } else {
                log.info("理财报警REWARDINFO doHttp - code " + code);
            }

        } catch (HttpException e) {
            log.error("理财报警REWARDERROR: HttpException：", e);
        } catch (IOException e) {
            try {
                log.error("理财报警REWARDERROR: 接口url=" + request.getURI() + "连接超时 IOException：", e);
            } catch (URIException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            log.error("理财报警REWARDERROR: DoHttp Exception：", e);
        } finally {
            request.releaseConnection();
        }
        return null;
    }

}
