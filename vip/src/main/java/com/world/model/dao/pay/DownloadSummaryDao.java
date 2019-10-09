package com.world.model.dao.pay;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.pay.DownloadSummaryBean;
import com.world.model.entity.user.authen.Authentication;
import com.world.util.date.TimeUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class DownloadSummaryDao extends DataDaoSupport<DownloadSummaryBean> {

    private static final long serialVersionUID = 1L;

    AuthenticationDao authenticationDao = new AuthenticationDao();

    /**
     * 获取今日已提现金额(不包含失败的和取消的)，按照提现当时btc金额汇总
     *
     * @param userId
     * @return
     */
    public BigDecimal getTodayBtcAmount(String userId) {
        List list = (List) Data.GetOne(database, "select sum(amountBtc) from downloadsummary where userid=? and status<>1 and status<>3 and submitTime>?", new Object[] { userId, TimeUtil.getTodayFirst() });
        BigDecimal btcAmount = list.get(0) == null ? BigDecimal.ZERO : (BigDecimal) list.get(0);
        return btcAmount;
    }


    public DownloadSummaryBean getOne(long id){
        DownloadSummaryBean download = (DownloadSummaryBean) Data.GetOne("SELECT * FROM downloadsummary where id = ?", new Object[] { id }, DownloadSummaryBean.class);
        return download;
    }



    /**
     * 获取用户提现额度，跟身份认证相关联
     *
     * @param userId
     * @return
     */
    public Map<String, Object> getDownloadLimit(String userId) {
        //获取用户是否通过身份认证
        Authentication au = authenticationDao.getByUserId(userId);
        int authResult = 0;
        BigDecimal maxDownloadLimit = new BigDecimal("50");
        BigDecimal downloadLimit = new BigDecimal("5");
        if (null != au && au.getStatus() == AuditStatus.a1Pass.getKey()) {
            //认证通过
            downloadLimit = maxDownloadLimit;
            authResult = 1;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("authResult", authResult);
        result.put("downloadLimit", downloadLimit);
        result.put("maxDownload", maxDownloadLimit);

        return result;
    }
}
