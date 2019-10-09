package com.world.controller.manage.account;


import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.CapitalTransferApiService;
import com.messi.user.feign.DownloadRecordApiService;
import com.messi.user.feign.RechargeRecordApiService;
import com.messi.user.vo.DownloadRecordBo;
import com.messi.user.vo.DownloadRecordVo;
import com.messi.user.vo.RechargeRecordBo;
import com.messi.user.vo.RechargeRecordVo;
import com.messi.user.vo.TransRecordBo;
import com.messi.user.vo.TransRecordVo;
import com.world.data.database.DatabasesUtil;
import com.world.model.dao.TransferLog.FundTransferLogDao;
import com.world.model.dao.pay.DetailsSummaryDao;
import com.world.model.dao.pay.DownloadSummaryDao;
import com.world.model.entity.coin.CoinProps;
import com.world.web.Page;
import com.world.web.action.UserAction;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ChargeDownHistory extends UserAction {
    DetailsSummaryDao detailsSummaryDao = new DetailsSummaryDao();
    DownloadSummaryDao downloadSummaryDao = new DownloadSummaryDao();
    FundTransferLogDao fundTransferLogDao = new FundTransferLogDao();

    @Page
    public void index() {
        //TODO jsp页面跳转，之后下掉jsp
        try {
            response.sendRedirect(VIP_DOMAIN);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Page(Viewer = JSON)
    public void getCoins() {
        List<String> coints = new ArrayList<>();
        Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
        for (Map.Entry<String, CoinProps> entry : coinMap.entrySet()) {
            CoinProps coin = entry.getValue();
            if (coin.isDisplay()) {
                coints.add(coin.getPropTag());
            }
        }
        json("", true, com.alibaba.fastjson.JSONObject.toJSON(coints).toString());
    }

    //充值记录
    @Page(Viewer = JSON)
    public void getChargeRecordList() {
        try {
            String userId = userIdStr();
            int TimeTab = intParam("timeTab");
            int pageIndex = intParam("pageIndex");
            int pageSize = intParam("pageSize");
            FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/rechargeRecord");
            RechargeRecordApiService rechargeRecordApiService = container.getFeignClient(RechargeRecordApiService.class);
            RechargeRecordVo rechargeRecordVo = new RechargeRecordVo();
            rechargeRecordVo.setUserId(userId);
            rechargeRecordVo.setTimeTab(TimeTab);
            rechargeRecordVo.setPageIndex(pageIndex);
            rechargeRecordVo.setPageSize(pageSize);
            String currency = param("coint");
            CoinProps coinProps = null;
            if (StringUtils.isNotBlank(currency)) {
                coinProps = DatabasesUtil.coinProps(currency);
                if(coinProps != null){
                    rechargeRecordVo.setFundsType(coinProps.getFundsType());
                }
            }
            RechargeRecordBo rechargeRecordBo = rechargeRecordApiService.getRecord(rechargeRecordVo);
//            // 时间选项  0：全部    1：7天内    2：15天内    3：30天内
//
//            if (pageIndex <= 0) {
//                pageSize = 1;
//            }
//
//            if (pageSize <= 0) {
//                pageSize = 10;
//            }
//
//
//            //获取现在时间
//            String startDate = null;
//            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date now = new Date();
//            if (TimeTab != 0){
//                if (TimeTab == 1){
//                    startDate = sd.format(DateUtils.addDays(now, -7));
//                }else if (TimeTab == 2){
//                    startDate = sd.format(DateUtils.addDays(now, -15));
//                }else if (TimeTab == 3) {
//                    startDate = sd.format(DateUtils.addDays(now, -30));
//                }
//            }
//
//            Query<DetailsSummaryBean> query = detailsSummaryDao.getQuery();
//            query.setSql("select * from detailssummary ");
//            query.setCls(DetailsSummaryBean.class);
//
//            query.append(" userId='" + userId + "' ");
//
//            if(startDate != null)
//                query.append(" and sendTime <= '" + sd.format(now) + "' ");
//
//            if(startDate != null)
//                query.append(" and sendTime >= '" + startDate + "' ");
//
//            CoinProps coinProps = null;
//            String currency = param("coint");
//            if (StringUtils.isNotBlank(currency)) {
//                coinProps = DatabasesUtil.coinProps(currency);
//                query.append(" and fundsType=" + coinProps.getFundsType());
//            }
//            query.append(" and type=1 ");
//
//            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//            int total = query.count();
//            if (total > 0) {
//                query.append("order by sendTime desc");
//                //分页查询
//                List<DetailsSummaryBean> details = detailsSummaryDao.findPage(pageIndex, pageSize);
//                for (DetailsSummaryBean detail : details) {
//                    Map<String, Object> downloadMap = new HashMap<String, Object>();
//                    if (StringUtils.isBlank(currency)) {
//                        coinProps = DatabasesUtil.getCoinPropsByFundsType(detail.getFundsType());
//                    }
//                    downloadMap.put("id", detail.getDetailsId());
//                    downloadMap.put("submitTime", detail.getSendTime());
//                    downloadMap.put("coinName", coinProps.getPropTag());
//                    downloadMap.put("amount", detail.getAmount());
//                    downloadMap.put("showStatus", L(detail.getShowStatu()));
//                    downloadMap.put("confirmTimes", detail.getConfirmTimes());
//                    downloadMap.put("toAddress", detail.getToAddr());
//                    String txId = detail.getAddHash().split("_")[0];
//                    downloadMap.put("txId", txId);
//                    downloadMap.put("webUrl", coinProps.getWeb() + txId);
//                    downloadMap.put("totalConfirmTimes", coinProps.getInConfirmTimes());
//                    downloadMap.put("status", detail.getStatus());
//                    if (detail.getConfigTime() != null) {
//                        downloadMap.put("confirmTime", detail.getConfigTime());
//                    }
//                    list.add(downloadMap);
//                }
//            }
//
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", pageIndex);
            page.put("totalCount", rechargeRecordBo.getTotalCount());
            page.put("list", rechargeRecordBo.getList());
            json("", true, JSONObject.fromObject(page).toString());
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    //提现记录
    @Page(Viewer = JSON)
    public void getDownloadRecordList() {
        try {
            String userId = userIdStr();
            int TimeTab = intParam("timeTab");
            int pageIndex = intParam("pageIndex");
            int pageSize = intParam("pageSize");
            FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/downloadRecord");
            DownloadRecordApiService downloadRecordApiService = container.getFeignClient(DownloadRecordApiService.class);
            DownloadRecordVo downloadRecordVo = new DownloadRecordVo();
            downloadRecordVo.setUserId(userId);
            downloadRecordVo.setTimeTab(TimeTab);
            downloadRecordVo.setPageIndex(pageIndex);
            downloadRecordVo.setPageSize(pageSize);
            String currency = param("coint");
            CoinProps coinProps = null;
            if (StringUtils.isNotBlank(currency)) {
                coinProps = DatabasesUtil.coinProps(currency);
                if(coinProps != null){
                    downloadRecordVo.setFundsType(coinProps.getFundsType());
                }
            }
            DownloadRecordBo downloadRecordBo = downloadRecordApiService.getList(downloadRecordVo);
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", pageIndex);
            page.put("totalCount", downloadRecordBo.getTotalCount());
            page.put("list", downloadRecordBo.getList());
            json("", true, JSONObject.fromObject(page).toString());



//            int pageIndex = intParam("pageIndex");
//            int pageSize = intParam("pageSize");
//            // 时间选项  0：全部    1：7天内    2：15天内    3：30天内
//            int TimeTab = intParam("timeTab");
//
//
//            //获取现在时间
//            String startDate = null;
//            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date now = new Date();
//            if (TimeTab != 0){
//                if (TimeTab == 1){
//                    startDate = sd.format(DateUtils.addDays(now, -7));
//                }else if (TimeTab == 2){
//                    startDate = sd.format(DateUtils.addDays(now, -15));
//                }else if (TimeTab == 3) {
//                    startDate = sd.format(DateUtils.addDays(now, -30));
//                }
//            }
//
//            Query<DownloadSummaryBean> query = downloadSummaryDao.getQuery();
//            query.setSql("select * from downloadsummary ");
//            query.setCls(DownloadSummaryBean.class);
//
//            query.append(" userId='" + userId + "' ");
//
//            //拼接查询条件
//            if(startDate != null)
//                query.append(" and submitTime <= '" + now + "' ");
//
//            if(startDate != null)
//                query.append(" and submitTime >= '" + startDate + "' ");
//
//            CoinProps coinProps = null;
//            String currency = param("coint");
//            if (StringUtils.isNotBlank(currency)) {
//                coinProps = DatabasesUtil.coinProps(currency);
//                query.append(" and fundsType=" + coinProps.getFundsType());
//            }
//            query.append(" and isDel=0 ");
//
//            int total = query.count();
//            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//            if (total > 0) {
//                query.append("order by submitTime desc");
//                //分页查询
//                List<DownloadSummaryBean> downloads = downloadSummaryDao.findPage(pageIndex, pageSize);
//                for (DownloadSummaryBean download : downloads) {
//                    Map<String, Object> downloadMap = new HashMap<String, Object>();
//                    if (StringUtils.isBlank(currency)) {
//                        coinProps = DatabasesUtil.getCoinPropsByFundsType(download.getFundsType());
//                    }
//                    downloadMap.put("id", download.getDownloadId());
//                    downloadMap.put("submitTime", download.getSubmitTime());
//                    downloadMap.put("coinName", coinProps.getPropTag());
//                    downloadMap.put("amount", download.getAmount());
//                    downloadMap.put("status", download.getStatus());
//                    downloadMap.put("confirmTime", download.getManageTime());
//                    downloadMap.put("toAddress", download.getToAddress());
//                    downloadMap.put("afterAmount", download.getAfterAmount());
//                    downloadMap.put("commandId", download.getCommandId());
//                    downloadMap.put("txId", download.getTxId());
//                    downloadMap.put("webUrl", coinProps.getWeb() + download.getTxId());
//                    downloadMap.put("memo", download.getMemo());
//                    downloadMap.put("addressMemo", download.getAddressMemo());
//
//                    list.add(downloadMap);
//                }
//            }
//
//            Map<String, Object> page = new HashMap<String, Object>();
//            page.put("pageIndex", pageIndex);
//            page.put("totalCount", total);
//            page.put("list", list);
//            json("", true, JSONObject.fromObject(page).toString());

        } catch (Exception e) {
            log.error("内部异常", e);
            json("fail", false, "");
        }
    }


    //划转记录
    @Page(Viewer = JSON)
    public void getFundTrandferLogList() {
        try {
            int pageIndex = intParam("pageIndex");
            int pageSize = intParam("pageSize");
            //划出账户 0 我的钱包 1 币币交易 2 法币交易（otc） 全部 -1
            int src = intParam("src");
            //划入账户 0 我的钱包 1 币币交易 2 法币交易（otc） 全部 -1
            int dst = intParam("dst");
            // 时间选项  0：全部    1：7天内    2：15天内    3：30天内
            int timeTab = intParam("timeTab");
            int fundsType = intParam("fundsType");



            FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/capitalTransfer");
            CapitalTransferApiService capitalTransferApiService = container.getFeignClient(CapitalTransferApiService.class);
            TransRecordVo transRecordVo = new TransRecordVo();
            transRecordVo.setFrom(src);
            transRecordVo.setTo(dst);
            transRecordVo.setFundsType(fundsType);
            transRecordVo.setTimeTab(timeTab);
            transRecordVo.setPageIndex(pageIndex);
            transRecordVo.setPageSize(pageSize);
            transRecordVo.setUserId(userIdStr());
            transRecordVo.setSendPoint("0");
            TransRecordBo transRecordBo = capitalTransferApiService.getRecord(transRecordVo);

//            //获取现在时间
//            String startDate = null;
//            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date now = new Date();
//            if (TimeTab != 0){
//                if (TimeTab == 1){
//                    startDate = sd.format(DateUtils.addDays(now, -7));
//                }else if (TimeTab == 2){
//                    startDate = sd.format(DateUtils.addDays(now, -15));
//                }else if (TimeTab == 3) {
//                    startDate = sd.format(DateUtils.addDays(now, -30));
//                }
//            }
//
//            Query<FundTransferLogBean> query = fundTransferLogDao.getQuery();
//            query.setSql("select * from fund_transfer_log ");
//            query.setCls(FundTransferLogBean.class);
//
//            //拼接查询条件
//            if(startDate != null)
//                query.append(" time <= '" + now + "' ");
//
//            if(startDate != null)
//                query.append(" and time >= '" + startDate + "' ");
//
//            if(src != 0)
//                query.append(" and src ='" + src + "' ");
//
//            if(dst != 0)
//                query.append(" and dst ='" + dst + "' ");
//
//            int total = query.count();
//            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//            if (total > 0) {
//                query.append("order by time desc");
//                //分页查询
//                List<FundTransferLogBean> transferLogBeans = fundTransferLogDao.findPage(pageIndex, pageSize);
//                for (FundTransferLogBean trans : transferLogBeans) {
//                    Map<String, Object> transferLogMap = new HashMap<String, Object>();
//                    CoinProps coinProps = DatabasesUtil.getCoinPropsByFundsType(trans.getFundType());
//                    transferLogMap.put("id", trans.getId());
//                    transferLogMap.put("time", trans.getTime());
//                    transferLogMap.put("coinName", coinProps.getPropTag());
//                    transferLogMap.put("amount", trans.getAmount());
//                    transferLogMap.put("uid", trans.getUid());
//                    transferLogMap.put("src", trans.getSrc());
//                    transferLogMap.put("det", trans.getDst());
//                    transferLogMap.put("srcName", trans.getSrcName());
//                    transferLogMap.put("dstName", trans.getDstName());
//                    transferLogMap.put("fundType", trans.getFundType());
//                    list.add(transferLogMap);
//                }
//            }
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", pageIndex);
            page.put("totalCount", transRecordBo.getTotalCount());
            page.put("list", transRecordBo.getList());
            json("", true, JSONObject.fromObject(page).toString());
        } catch (Exception e) {
            log.error("内部异常", e);
            json("fail", false, "");
        }
    }



    //划转记录
    @Page(Viewer = JSON)
    public void getFundTrandferLog() {
        try {
            int pageIndex = intParam("pageIndex");
            int pageSize = intParam("pageSize");
            int src = intParam("src");//划出账户 1 我的钱包 2 币币交易 3 法币交易（otc）4 期货 5 理财 全部 -1
            int dst = intParam("dst");//划入账户 1 我的钱包 2 币币交易 3 法币交易（otc）4 期货 5 理财 全部 -1
            // 时间选项  0：全部    1：7天内    2：15天内    3：30天内
            int timeTab = intParam("timeTab");
            int fundsType = intParam("fundsType");



            FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/capitalTransfer");
            CapitalTransferApiService capitalTransferApiService = container.getFeignClient(CapitalTransferApiService.class);
            TransRecordVo transRecordVo = new TransRecordVo();
            transRecordVo.setFrom(src);
            transRecordVo.setTo(Integer.valueOf(dst));
            transRecordVo.setFundsType(fundsType);
            transRecordVo.setTimeTab(timeTab);
            transRecordVo.setPageIndex(pageIndex);
            transRecordVo.setPageSize(pageSize);
            transRecordVo.setUserId(userIdStr());
            transRecordVo.setSendPoint("0");
            TransRecordBo transRecordBo = capitalTransferApiService.getRecordList(transRecordVo);

//            //获取现在时间
//            String startDate = null;
//            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date now = new Date();
//            if (TimeTab != 0){
//                if (TimeTab == 1){
//                    startDate = sd.format(DateUtils.addDays(now, -7));
//                }else if (TimeTab == 2){
//                    startDate = sd.format(DateUtils.addDays(now, -15));
//                }else if (TimeTab == 3) {
//                    startDate = sd.format(DateUtils.addDays(now, -30));
//                }
//            }
//
//            Query<FundTransferLogBean> query = fundTransferLogDao.getQuery();
//            query.setSql("select * from fund_transfer_log ");
//            query.setCls(FundTransferLogBean.class);
//
//            //拼接查询条件
//            if(startDate != null)
//                query.append(" time <= '" + now + "' ");
//
//            if(startDate != null)
//                query.append(" and time >= '" + startDate + "' ");
//
//            if(src != 0)
//                query.append(" and src ='" + src + "' ");
//
//            if(dst != 0)
//                query.append(" and dst ='" + dst + "' ");
//
//            int total = query.count();
//            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//            if (total > 0) {
//                query.append("order by time desc");
//                //分页查询
//                List<FundTransferLogBean> transferLogBeans = fundTransferLogDao.findPage(pageIndex, pageSize);
//                for (FundTransferLogBean trans : transferLogBeans) {
//                    Map<String, Object> transferLogMap = new HashMap<String, Object>();
//                    CoinProps coinProps = DatabasesUtil.getCoinPropsByFundsType(trans.getFundType());
//                    transferLogMap.put("id", trans.getId());
//                    transferLogMap.put("time", trans.getTime());
//                    transferLogMap.put("coinName", coinProps.getPropTag());
//                    transferLogMap.put("amount", trans.getAmount());
//                    transferLogMap.put("uid", trans.getUid());
//                    transferLogMap.put("src", trans.getSrc());
//                    transferLogMap.put("det", trans.getDst());
//                    transferLogMap.put("srcName", trans.getSrcName());
//                    transferLogMap.put("dstName", trans.getDstName());
//                    transferLogMap.put("fundType", trans.getFundType());
//                    list.add(transferLogMap);
//                }
//            }
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", pageIndex);
            page.put("totalCount", transRecordBo.getTotalCount());
            page.put("list", transRecordBo.getList());
            json("", true, JSONObject.fromObject(page).toString());
        } catch (Exception e) {
            log.error("内部异常", e);
            json("fail", false, "");
        }
    }













}

