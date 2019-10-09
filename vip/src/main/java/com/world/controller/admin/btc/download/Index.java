package com.world.controller.admin.btc.download;

import com.alibaba.fastjson.JSONArray;
import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.DowoloadApiService;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.autofactory.AutoDownloadRecordDao;
import com.world.model.dao.daily.MainDailyRecordDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.pay.FreezDao;
import com.world.model.dao.pay.FundsDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.dao.wallet.WalletDetailsDao;
import com.world.model.entity.CointTable;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.FreezType;
import com.world.model.entity.pay.FreezeBean;
import com.world.model.entity.user.User;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.dao.FinanEntryDao;
import com.world.model.financial.dao.FinanUseTypeDao;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanUseType;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FunctionAction(jspPath = "/admins/btc/download/", des = "提现记录")
public class Index extends FinanAction {
	private FreezDao freezDao = new FreezDao();
	private FundsDao fundDao = new FundsDao();
    UserDao uDao = new UserDao();
    DownloadDao bdDao = new DownloadDao();
    WalletDetailsDao walletDetailsDao = new WalletDetailsDao();

    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        //查询条件
        int currentPage = intParam("page");
        int pageSize = 50;
        String currentTab = param("tab");
        Timestamp startTime = dateParam("startDate");
        Timestamp endTime = dateParam("endDate");
        String toAddress = param("toAddress").trim();
        String userName = param("userName").trim();

        String userId = param("userId");

        Timestamp confirmStartDate = dateParam("confirmStartDate");//确认时间(区间)
        Timestamp confirmEndDate = dateParam("confirmEndDate");//确认时间(区间)

        double moneyMin = doubleParam("moneyMin");
        double moneyMax = doubleParam("moneyMax");
        int commandId = intParam("commandId");

        bdDao.setCoint(coint);
        Query<DownloadBean> query = bdDao.getQuery();
        
        query.setSql("select * from "+bdDao.getTableName());
        query.setCls(DownloadBean.class);

        if (currentTab.length() == 0)
            currentTab = "wait";

        String order = "desc";
        if (currentTab.equals("wait")) {
            order = "asc";
            query.append(" AND  (status=0 and commandId = 0)");
        } else if (currentTab.equals("confirm")) {
            query.append(" AND  (status=0 and commandId > 0)");
        } else if (currentTab.equals("success")) {
            query.append(" AND  (status=2)");
        } else if (currentTab.equals("fail")) {
            query.append(" AND  (status=1)");
        } else if (currentTab.equals("cancel")) {
            query.append(" AND  (status=3)");
        } else if(currentTab.equals("sendding")) {
            query.append(" AND  (status=7)");
        }
        if (confirmStartDate != null) {
            query.append(" and manageTime>=cast('" + confirmStartDate + "' as datetime)");
        }
        if (confirmEndDate != null) {
            query.append(" and manageTime<=cast('" + confirmEndDate + "' as datetime)");
        }
        request.setAttribute("currentTab", currentTab);

        if (startTime != null) {
            query.append(" and submitTime>=cast('" + startTime + "' as datetime)");
        }
        if (endTime != null) {
            query.append(" and submitTime<=cast('" + endTime + "' as datetime)");
        }

        if (toAddress.length() > 0) {
            query.append(" and toAddress='" + toAddress + "'");
        }
        if (userName.length() > 0) {
            query.append(" and userName='" + userName + "'");
        }
        if (userId.length() > 0) {
            query.append(" and userId='" + userId + "'");
        }
        if(moneyMin > 0){
	    	query.append(" and amount >=" + moneyMin);
	    }
	    if(moneyMax > 0){
	    	query.append(" and amount <=" + moneyMax);
	    }
        if (commandId > 0) {
            query.append(" and commandId > 0");
        }

        int total = query.count();
        if (total > 0) {
            query.append(" ORDER BY submitTime " + order);
            //分页查询
            List<DownloadBean> btcDownloads = bdDao.findPage(currentPage, pageSize);

            List<String> userIds = new ArrayList<String>();
            List<String> uuids = new ArrayList<String>();
            for (DownloadBean bdb : btcDownloads) {
                userIds.add(bdb.getUserId() + "");//获取用户ID
                uuids.add(bdb.getUuid());       //获取uuid
            }

            if (userIds.size() > 0) {
                //查询用户ID
                Map<String, User> userMaps = new UserDao().getUserMapByIds(userIds);
                Map<String,Long> uuidMaps = new AutoDownloadRecordDao().getAutoDownloadRecordId(uuids);
                for (DownloadBean bdb : btcDownloads) {
                	bdb.setUser(userMaps.get(bdb.getUserId() + ""));
                    if(uuidMaps.containsKey(bdb.getUuid())){
                        bdb.setAutoDownloadId(bdb.getId());
                    }

                }
            }
            request.setAttribute("dataList", btcDownloads);
        }
        //页面顶部币种切换
        super.setAttr("coinMap", DatabasesUtil.getCoinPropMaps());
        setPaging(total, currentPage, pageSize);

    }

    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }

    @Page(Viewer = "/admins/btc/download/merchantsConfirm.jsp", des = "查看确认打币页面")
    public void merchantsConfirm() {
        long did = longParam("id");
        DownloadBean bdb = bdDao.getUnconfirm(did);
        setAttr("curData", bdb);
    }

    /**
     * 确认可自动提现
     */
    @Page(Viewer = ".xml", des = "确认打币")
    public void doMerchantsConfirm() {
        if (!codeCorrect(XML)) {
            return;
        }
        long did = longParam("did");
        if (did <= 0) {
            WriteError("该记录不存在或状态已变更，请刷新页面");
            return;
        }

        bdDao.setCoint(coint);
        bdDao.doWithdraw(did, false, this, ip());
    }

    @Deprecated
    @Page(Viewer = "/admins/btc/download/entry.jsp")
    public void aoru() {
        try {
            long connId = longParam("connId");
            int useTypeId = intParam("useTypeId");
            setAttr("connId", connId);
            setAttr("useTypeId", useTypeId);
            
            setAttr("fundType",super.coint.getFundsType());
            
            
            setAttr("accounts", new FinanAccountDao().findList(roleId() == 1 || roleId() == 6 ? 0 : adminId(), coint.getFundsType()));
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    /**
     * 人工打币成功，更新提现状态，解冻用户资金并扣除
     */
    @Page(Viewer = ".xml")
    public void confirmSuc() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }

            long did = longParam("connId");
            int accountId = intParam("accountId");
            int userId = 0;
            BigDecimal amount = BigDecimal.ZERO;
            String userName = "";
            if (did > 0) {
                FinanAccount fa = new FinanAccountDao().get(accountId);
                int walletId = fa.getBankAccountId();

                DownloadBean bdlb = (DownloadBean) Data.GetOne("select * from "+coint.getStag()+CointTable.download+" where id=?", new Object[]{did}, DownloadBean.class);

                BigDecimal fees = bdlb.getFees().subtract(bdlb.getRealFee());//DigitalUtil.sub(DigitalUtil.div(, fee.btcFee), DigitalUtil.div(bdlb.getRealFee(), fee.btcFee));
				fees = fees.compareTo(BigDecimal.ZERO) > 0 ? fees : BigDecimal.ZERO;
                
                bdlb.setRemark("比特币提现成功");
                List<OneSql> sqls = new ArrayList<OneSql>();
				
				//解冻并扣除的语句
				FreezeBean fbean = new FreezeBean(bdlb.getUserId(), bdlb.getUserName(), "提现成功", FreezType.cashUnFreez.getKey(), bdlb.getAmount(), 0, 0);
				
				freezDao.setDatabase(coint.getDatabasesName());
				freezDao.unFreezSqls(sqls, fbean, BillType.download, true);

                /*if (walletId > 0) {
                	BigDecimal fundsComm = decimalParam("fundsComm");//手续费
					BigDecimal add = bdlb.getAmount();
					BigDecimal realFee = fundsComm;
					
					sqls.add(new OneSql("update "+coint.getStag()+CointTable.wallet+" set btcs=btcs-?-? where walletId=?" , 1 , new Object[]{add, realFee , walletId}));
                }*/

                sqls.add(new OneSql("update "+coint.getStag()+CointTable.download+" set status=2,manageTime=? where id=" + did, 1, new Object[]{now()}));//状态提现记录改为成功
                sqls.add(new OneSql("update downloadsummary set status=2,manageTime=? where fundsType=? and downloadId=" + did, 1, new Object[]{now(), coint.getFundsType()}));//状态提现记录改为成功

                /**
                 * 财务录入事务语句=============================
                 */
                List<OneSql> paySqls = saveEntrySqls();
                if (paySqls == null) {
                    return;
                }
                sqls.addAll(paySqls);
                /**
                 * 财务录入操作结束=============================
                 */

                if (Data.doTrans(sqls)) {
                    UserCache.resetUserFundsFromDatabase(bdlb.getUserId());
                } else {
                    WriteError("确认失败。");
                    return;
                }
                userName = bdlb.getUserName();
            }

            //插入一条管理员日志信息
            DailyType type = DailyType.btcDownload;
            new MainDailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "确认成功", userName, did), String.valueOf(adminId()), ip(), now(), userId, amount);
        } catch (Exception e) {
            log.error("内部异常", e);
        }
        WriteRight("确认成功。");
    }

    /**
     * 取消提现，用户要求取消提现，更新提现状态为取消
     */
    @Page(Viewer = DEFAULT_AJAX)
    public void confirmCancel() {
        try {
            long did = longParam("did");

            Integer fundsType = intParam("fundsType");
            FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url") + "/download");
            DowoloadApiService dowoloadApiService = container.getFeignClient(DowoloadApiService.class);
            Boolean flag = dowoloadApiService.cancelDownload(userIdStr(),did,fundsType.intValue(),1);
            json("",flag,"");
            return;
//			List<OneSql> sqls=new ArrayList<OneSql>();
//			if(did > 0){
//				DownloadBean bdlb = (DownloadBean) Data.GetOne("select * from "+coint.getStag()+CointTable.download+" where id=?",	new Object[] { did }, DownloadBean.class);
//				bdlb.setRemark(coint.getPropCnName()+"提现取消");
//
//				FreezeBean fbean = new FreezeBean(bdlb.getUserId(), bdlb.getUserName(), "取消下载", FreezType.cashUnFreez.getKey(), bdlb.getAmount(), 0, 0);
//				freezDao.coint = coint;
//				freezDao.unFreezSqls(sqls, fbean, null, false);
//
//				sqls.add(new OneSql("update "+coint.getStag()+CointTable.download+" set status=3,manageTime=? where id=? and status<=0" , 1 , new Object[]{now(),did}));//状态提现记录改为取消
//				sqls.add(new OneSql("update downloadsummary set status=3,manageTime=? where fundsType=? and downloadId=? and status<=0" , 1 , new Object[]{now(),coint.getFundsType(),did}));//状态提现记录改为取消
//				if(Data.doTrans(sqls)){///发送提现成功消息
//					UserCache.resetUserFundsFromDatabase(bdlb.getUserId());
//					try {
//						//插入一条管理员日志信息
//						DailyType type = DailyType.btcDownload;
//						new MainDailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "取消下载", bdlb.getUserName(), did, coint.getPropTag()), String.valueOf(adminId()), ip(), now());
//					} catch (Exception e) {
//						log.error("添加管理员日志失败", e);
//					}
//
//					WriteRight(coint.getPropCnName()+"提现取消成功。");
//					return;
//				}
//			}
		}catch (Exception e) {
            log.error("内部异常", e);
		}
    }

    /**
     * 更新提现状态为失败，还没有打。地址错误，失败操作
     */
    @Page(Viewer = ".xml")
    public void confirmFail() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            
            long did = longParam("did");
			CoinProps coin = coinProps();
			List<OneSql> sqls=new ArrayList<OneSql>();
			if(did > 0){///失败处理
				DownloadBean bdlb = (DownloadBean) Data.GetOne("select * from "+coint.getStag()+CointTable.download+" where id=?",	new Object[] { did }, DownloadBean.class);
				
				FreezeBean fbean = new FreezeBean(bdlb.getUserId(), bdlb.getUserName(), "下载失败", FreezType.cashUnFreez.getKey(), bdlb.getAmount(), 0, 0);
				freezDao.coint = coint;
				freezDao.unFreezSqls(sqls, fbean, null, false);
				
				sqls.add(new OneSql("update "+coint.getStag()+CointTable.download+" set hasFail = 1 where id="+did , 1 , new Object[]{}));//状态提现记录改为失败
				if(Data.doTrans(sqls)){///发送提现成功消息
					try {
						UserCache.resetUserFundsFromDatabase(bdlb.getUserId());
						//插入一条管理员日志信息
						DailyType type = DailyType.btcDownload;
						new MainDailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "确认失败", bdlb.getUserName(), did, coint.getPropCnName()), String.valueOf(adminId()), ip(), now());
					} catch (Exception e) {
						log.error("添加管理员日志失败", e);
					}
					
					WriteRight("确认失败成功。");
					return;
				}
			}
            
        } catch (Exception e) {
            log.error("内部异常", e);
        }
        WriteError("确认失败出现错误。");
    }

    /**
     * 提现状态已更新成功，用户资金解冻并扣除后，发现提现记录有误，再确认失败，返还用户资金
     */
    @Page(Viewer = ".xml")
    public void sucFail() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            
            CoinProps coin = coinProps();
			long did = longParam("did");
			List<OneSql> sqls=new ArrayList<OneSql>();
			//设置区间判定
			if(did>0){
				DownloadBean bdlb = (DownloadBean) Data.GetOne("select * from "+coint.getStag()+CointTable.download+" where id=?",	new Object[] { did }, DownloadBean.class);
			
				sqls.addAll(fundDao.addMoney(bdlb.getAmount(), bdlb.getUserId(), bdlb.getUserName(), "提现错误扣除返还", BillType.errorDeductReturn.getKey(), coin.getFundsType(), BigDecimal.ZERO, adminId()+"", true));
				
				sqls.add(new OneSql("update "+coint.getStag()+CointTable.download+" set status=1,manageTime=?,confirm=0 where id="+did , 1 , new Object[]{now()} ));//状态提现记录改为成功
				sqls.add(new OneSql("update downloadsummary set status=1,manageTime=?,confirm=0 where fundsType=? and downloadId="+did , 1 , new Object[]{now(),coint.getFundsType()} ));//状态提现记录改为成功
				if(Data.doTrans(sqls)){///发送提现成功消息
					try {
						UserCache.resetUserFundsFromDatabase(bdlb.getUserId());
						//插入一条管理员日志信息
						DailyType type = DailyType.btcDownload;
						new MainDailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "成功后失败", bdlb.getUserName(), did, coin.getPropTag()), String.valueOf(adminId()), ip(), now());
					} catch (Exception e) {
						log.error("添加管理员日志失败", e);
					}
					WriteRight("确认失败成功。");
					return;
				}
			}
        } catch (Exception e) {
            log.error("内部异常", e);
        }
        Write("程序异常导致操作失败，请刷新页面后继续操作！", false, "");
    }

    @Page(Viewer = "/admins/btc/download/fees.jsp")
	public void fees() {
		try {
			long connId = longParam("connId");
			
			DownloadBean bdlb = (DownloadBean) Data.GetOne("select * from "+coint.getStag()+CointTable.download+" where id=?",	new Object[] { connId }, DownloadBean.class);
			
			setAttr("bdlb", bdlb);
			FinanAccount fa = (FinanAccount)Data.GetOne("SELECT * FROM finanaccount WHERE type = 3 AND isDefault = true AND fundType = ?", new Object[]{coint.getFundsType()}, FinanAccount.class);
			setAttr("fa", fa);
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
    
    @Page(Viewer=".xml")
	public void confirmFees(){
		if(!codeCorrect(XML)){
			return;
		}
		CoinProps coin = coinProps();
		long did = longParam("connId");
		
		FinanUseTypeDao useTypeDao = new FinanUseTypeDao();
		FinanUseType usetype = useTypeDao.getByType(3);
		int useTypeId = usetype.getId();//比特币其他或莱特币其他
		
		BigDecimal funds = decimalParam("funds");
		BigDecimal fundsComm = decimalParam("fundsComm");
		String memo = param("memo");
		int paramUserType = intParam("useTypeId");
		
		List<OneSql> sqls=new ArrayList<OneSql>();
		if(did > 0){
			DownloadBean bdlb = (DownloadBean) Data.GetOne("select * from "+coint.getStag()+CointTable.download+" where id=?",	new Object[] { did }, DownloadBean.class);
			FinanAccount fa = (FinanAccount)Data.GetOne("SELECT * FROM finanaccount WHERE bankAccountId = ? AND fundType = ?", new Object[]{bdlb.getCommandId(), coin.getFundsType()}, FinanAccount.class);
			
			boolean isAdd = true;
			if(paramUserType == 6){
				isAdd = false;
				fundsComm = BigDecimal.ZERO.subtract(fundsComm.abs());
			}
			sqls.addAll(new FinanEntryDao().saveOneEntry(fa.getId(), useTypeId, coin.getFundsType(), funds, fundsComm, Integer.parseInt(bdlb.getUserId()), bdlb.getUserName(), memo, ip(), bdlb.getId(), adminId(), isAdd));
			
			sqls.add(new OneSql("update "+coint.getStag()+CointTable.download+" set confirm = true where id="+did , 1 , new Object[]{}));//状态提现记录改为成功
			
			if(Data.doTrans(sqls)){//
			}else{
				WriteError("录入失败。");
				return;
			}
		}
	
		WriteRight("记录保存成功。");
	}

    @Page(Viewer = JSON)
    public void tongji() {
        try {
            //查询条件
            String currentTab = param("tab");
            Timestamp startTime = dateParam("startDate");
            Timestamp endTime = dateParam("endDate");
            String toAddress = param("toAddress").trim();
            String userName = param("userName").trim();
            Timestamp confirmStartDate = dateParam("confirmStartDate");//确认时间(区间)
            Timestamp confirmEndDate = dateParam("confirmEndDate");//确认时间(区间)
            double moneyMin = doubleParam("moneyMin");
            double moneyMax = doubleParam("moneyMax");
            int commandId = intParam("commandId");

            CoinProps coin = coinProps();
            bdDao.setCoint(coin);
            Query<DownloadBean> query = bdDao.getQuery();

            query.setSql("select * from "+bdDao.getTableName());
            query.setCls(DownloadBean.class);

            String ids = param("eIds");
            boolean isAll = booleanParam("isAll");

            if (isAll) {
                if (currentTab.length() == 0)
                    currentTab = "wait";

                if (currentTab.equals("wait")) {
                    query.append(" AND  (status=0 and commandId = 0)");
                } else if (currentTab.equals("confirm")) {
                    query.append(" AND  (status=0 and commandId > 0)");
                } else if (currentTab.equals("success")) {
                    query.append(" AND  (status=2)");
                } else if (currentTab.equals("fail")) {
                    query.append(" AND  (status=1)");
                } else if (currentTab.equals("cancel")) {
                    query.append(" AND  (status=3)");
                }
                request.setAttribute("currentTab", currentTab);

                if (startTime != null) {
                    query.append(" and submitTime>=cast('" + startTime + "' as datetime)");
                }
                if (endTime != null) {
                    query.append(" and submitTime<=cast('" + endTime + "' as datetime)");
                }
                if (confirmStartDate != null) {
                    query.append(" and manageTime>=cast('" + confirmStartDate + "' as datetime)");
                }
                if (confirmEndDate != null) {
                    query.append(" and manageTime<=cast('" + confirmEndDate + "' as datetime)");
                }
                if (toAddress.length() > 0) {
                    query.append(" and toAddress LIKE '%" + toAddress + "%'");
                }
                if (userName.length() > 0) {
                    query.append(" and userName LIKE '%" + userName + "%'");
                }

                if(moneyMin > 0){
        	    	query.append(" and amount >=" + moneyMin);
        	    }
        	    if(moneyMax > 0){
        	    	query.append(" and amount <=" + moneyMax);
        	    }
                if (commandId > 0) {
                    query.append(" and commandId > 0");
                }
            } else {
                if (ids.endsWith(",")) {
                    ids = ids.substring(0, ids.length() - 1);
                }
                query.append(" AND id IN (" + ids + ")");
            }

            List<DownloadBean> list = bdDao.find();

            String pattern = "0.000000##";//格式代码
            DecimalFormat df = new DecimalFormat();
            df.applyPattern(pattern);

            BigDecimal totalMoney = BigDecimal.ZERO;
            BigDecimal totalMoney2 = BigDecimal.ZERO;
            for (DownloadBean bdb : list) {
                totalMoney = totalMoney.add(bdb.getAmount());
                totalMoney2 = totalMoney2.add(bdb.getAfterAmount());
            }
            JSONArray array = new JSONArray();
            array.add(df.format(totalMoney));
            array.add(totalMoney2);

            json("", true, array.toString());

        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }
}
