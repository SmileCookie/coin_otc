package com.world.controller.admin.btc.downloadexception;


import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.Query;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DownloadBean;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@FunctionAction(jspPath = "/admins/btc/downloadexception/", des = "提现异常处理")
public class Index extends FinanAction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DownloadDao bdDao  = new DownloadDao();
    @Page(Viewer = DEFAULT_INDEX)
    public void index() {

        int currentPage = intParam("currentPage");//资金类型
        int pageSize = 20;
        String fundsType = param("coint");//资金类型
        String uuid = param("uuid");//提现编号
        if(StringUtils.isBlank(fundsType) || fundsType.equals(0)){
            fundsType = "2";
        }
        setAttr("ft", DatabasesUtil.getCoinPropMaps());

        bdDao.setCoint(coint);
        Query<DownloadBean> query = bdDao.getQuery();

        query.setSql("select * from "+bdDao.getTableName());
        query.setCls(DownloadBean.class);

        query.append(" status = 1 ");

        if(StringUtils.isNotEmpty(uuid)){
            query.append(" and uuid = '" + uuid + "'");
        }
        List<DownloadBean> dataList = new ArrayList<>();

        int total = query.count();
        if (total > 0) {
            query.append(" ORDER BY submitTime");
            //分页查询
            dataList = bdDao.findPage(currentPage, PAGE_SIZE);
            request.setAttribute("dataList", dataList);
        }
        setPaging(total, currentPage, pageSize);

    }


    @Page(Viewer=DEFAULT_AJAX)
    public void ajax(){
        index();
    }

    @Page(Viewer = DEFAULT_AORU,des="新增/编辑")
    public void aoru() {
        String param = param("id");//id
        String uuid = param.substring(0,param.indexOf("_"));
        String conit = param.substring(param.indexOf("_")+1);
        if(StringUtils.isEmpty(uuid)){
            return;
        }
        conit = conit.toLowerCase();//币种
        DownloadBean download = (DownloadBean) Data.GetOne("SELECT * FROM " + conit+ "download where uuid = ?", new Object[] {uuid}, DownloadBean.class);
        setAttr("download", download);
        setAttr("conit", conit);
    }


    @Page(Viewer = ".xml",des="处理提现异常")
    public void doAoru() {
        try {
            String uuid = param("uuid");//uuid
            String conit = param("conit").toLowerCase();//币种
            String txId = param("txId");        //交易编号
            // TODO: 2017/7/26
            String txIdN = txId + param("txIdN");      //交易编号序号
            Integer blockHeight = intParam("blockHeight");  //区块高度
            BigDecimal realFee = decimalParam("realFee");
            if(StringUtils.isEmpty(uuid) || StringUtils.isEmpty(conit) || StringUtils.isEmpty(txId) || StringUtils.isEmpty(txIdN)
                    || blockHeight == 0 ||  realFee.compareTo(BigDecimal.ZERO) < 0){
                WriteError("操作失败");
                return;
            }

            DownloadBean download = (DownloadBean) Data.GetOne("SELECT * FROM " + conit+ "download where uuid = ?", new Object[] {uuid}, DownloadBean.class);
            if (download != null) {
                log.info("存在提现异常记录，正在处理，uuid:" + uuid);
                try {
                    String updateDownloadSql = "update " + conit + "download set txId=?,txIdN=?,blockHeight=?,realFee = ?,status=? where uuid=?";
                    Data.Update(updateDownloadSql, new Object[]{txId, txIdN, blockHeight, realFee, 2, uuid});

                    CoinProps coinProps = DatabasesUtil.coinProps(conit);
                    String updateDownloadSummarySql = "update downloadsummary set txId=?,txIdN=?,blockHeight=?,realFee = ?,status=? where fundsType=? and uuid=?";
                    Data.Update(updateDownloadSummarySql, new Object[]{txId, txIdN, blockHeight, realFee, 2, coinProps.getFundsType(), uuid});
                    WriteRight(L("操作成功"));
                    return;
                } catch (Exception e) {
                    log.error("处理提现异常失败，uuid=" + uuid + "失败信息：" + e.toString());
                }
            } else {
                WriteError("该提现异常记录已经被处理");
                log.info("该提现异常记录已经被处理，uuid:" + uuid);
                return;
            }
            WriteError("操作失败");
        }catch (Exception e){
            WriteError("操作失败");
        }
    }
}