package com.world.model.dao.pay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.controller.api.util.SystemCode;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.logs.DailyRecordDao;
import com.world.model.dao.daily.MainDailyRecordDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.CointTable;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.Price;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.FreezType;
import com.world.model.entity.pay.FreezeBean;
import com.world.model.entity.pay.OrderNumberGeneration;
import com.world.model.entity.pay.PayUserWalletBean;
import com.world.model.entity.pay.ReceiveAddr;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.WithdrawAddressAuthenType;
import com.world.model.enums.CoinDownloadStatus;
import com.world.model.financial.dao.FinanEntryDao;
import com.world.model.financial.dao.FinanUseTypeDao;
import com.world.model.financial.entity.AccountType;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanUseType;
import com.world.util.MerchantsUtil;
import com.world.util.Message;
import com.world.util.UserUtil;
import com.world.util.date.TimeUtil;
import com.world.util.language.LanguageTag;
import com.world.web.Pages;
import com.world.web.response.DataResponse;
import com.world.web.sso.session.ClientSession;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public class DownloadDao extends DataDaoSupport<DownloadBean> {

	private static final long serialVersionUID = 1L;

	FreezDao freezDao = new FreezDao();
	DownloadSummaryDao downloadSummaryDao = new DownloadSummaryDao();

	public String getTableName() {
		return coint.getStag() + CointTable.download;
	}

	public long getId() {
		return OrderNumberGeneration.getNewNumber("btcdownload");
	}

	public DownloadBean getLast(String userId) {
		DownloadBean download = (DownloadBean) Data.GetOne("SELECT * FROM " + getTableName() + " where userId = ? ORDER BY submitTime DESC", new Object[] { userId }, DownloadBean.class);
		return download;
	}
	public DownloadBean findOne(Long did) {
		DownloadBean download = (DownloadBean) Data.GetOne("SELECT * FROM " + getTableName() + " where id = ?", new Object[] { did }, DownloadBean.class);
		return download;
	}

	public OneSql getUpdateDownloadSql(String cointName, int flag,long batchId){
		return new OneSql("update "+ cointName + "download set feeAccountFlag = ?  where batchId = ?" , -2 ,new Object[]{flag,batchId});
	}

	/**
	 * 获取今日已提现金额(不包含失败的和取消的)
	 * 
	 * @param userId
	 * @return
	 */
	public BigDecimal getTodayCash(String userId) {
		List list = (List) Data.GetOne(database, "select sum(amount) from " + getTableName() + " where userid=? and status<>1 and status<>3 and submitTime>?", new Object[] { userId, TimeUtil.getTodayFirst() });
		BigDecimal hasCash = list.get(0) == null ? BigDecimal.ZERO : (BigDecimal) list.get(0);
		return hasCash;
	}

	/**
	 * 获取今日已免审提现金额
	 * 
	 * @param userId
	 * @return
	 */
	public BigDecimal getTodayFreeCash(String userId) {
		List list = (List) Data.GetOne(database, "select sum(amount) from " + getTableName() + " where userid=? and status<>1 and submitTime>? and managerId=1", new Object[] { userId, TimeUtil.getTodayFirst() });
		BigDecimal hasCash = list.get(0) == null ? BigDecimal.ZERO : (BigDecimal) list.get(0);
		// double todayCash = hasCash.doubleValue();
		return hasCash;
	}

	/**
	 * 返回本次提现最多能提的金额
	 * 
	 * @param balance
	 * @param todayCash
	 * @param everyTimeCash
	 * @param dayCash
	 * @return
	 */
	public BigDecimal getThisTimeCouldCash(BigDecimal balance, BigDecimal todayCash, BigDecimal everyTimeCash, BigDecimal dayCash) {
        BigDecimal remains = dayCash.subtract(todayCash);
		BigDecimal thisCouldCash = remains.min(balance);
		thisCouldCash = thisCouldCash.min(everyTimeCash);
		return thisCouldCash;
    }

	/**
	 * 更新状态
	 * 
	 * @date 2015-12-7
	 * 
	 * @param ids
	 * @param status
	 * @return
	 */
	public OneSql updateStatusByIds(String ids, int status) {
		return new OneSql("update " + getTableName() + " set status = ? where id in (" + ids + ")", -2, new Object[] { status });
	}

	/**
	 * 更新打币的信息：手续费、余额等
	 * 
	 * @param ids
	 * @param status
	 * @param balance
	 * @return
	 */
	public OneSql updateDownloadByIds(String ids, int status, BigDecimal balance, String txid) {
		return new OneSql("update " + getTableName() + " set status = ?, addHash = ?, balance = ? where id in (" + ids + ") AND status = 5", -2, new Object[] { status, txid, balance });
	}

	/**
	 */
	public OneSql updateDownloadFirstId(long firstId, BigDecimal realFee) {
		return new OneSql("update " + getTableName() + " set realFee = ? where id = ?", -2, new Object[] { realFee, firstId });
	}

	/**
	 * 根据状态查询btcdownload记录
	 * 
	 * @param status
	 * @return
	 */
	public List<DownloadBean> findByStatus(int status, int pageSize) {
		return super.find("select * from " + getTableName() + " where status = ? AND commandId > 0 AND isMerchant = 0 GROUP BY toAddress LIMIT 0, ?", new Object[] { status, pageSize }, DownloadBean.class);
	}

	/**
	 * 提现地址是否属于本系统
	 * @param currency
	 * @param address
	 * @return
	 */
	public boolean isLocalAddress(String currency, String address){
		//查询该地址在本系统中记录条数
		List<Object> list= (List<Object>)Data.GetOne("select count(*) from " + currency + "key t where keyPre = '" + address + "'", new Object[]{});
		if(list != null && list.size() > 0 ){
			if ((long)list.get(0) > 0) {
				return true;
			}
		}
		return false;
	}

	public Message doBtcDownload(User user, BigDecimal account, String receiveAddr, BigDecimal fees, String payPass, String mobileCode, long googleCode,
                                 String ip, String liuyan, LanguageTag lanTag, boolean isApi, boolean isAppApi, String opUnique,String lan,String memo,String emailCode) {
		Message msg = new Message();
		try {
			PayUserWalletDao payUserDao = new PayUserWalletDao();
			UserDao userDao = new UserDao(lan);
			if (account.compareTo(BigDecimal.ZERO) <= 0) {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg(lan,"提交失败，请稍后重试");
				log.info("传入金额为：" + account);
				return msg;
			}

			if (receiveAddr == null || receiveAddr.length() == 0) {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg(lan, "您还没有添加提现地址。");
				return msg;
			}

			if (!UserUtil.checkAddress(coint.getStag(), receiveAddr)) {
				msg.setCode(SystemCode.code_1001.getKey());
//				msg.setMsg(lan, "%%提币地址错误。",coint.getTag());
				//2017.08.16 xzhang 提现地址错误提示  BITA-540
				msg.setMsg(lan, "提币地址不存在");
				return msg;
			}

			//站内地址可以互转
//			if (isLocalAddress(coint.getStag(), receiveAddr)) {
//				msg.setCode(SystemCode.code_1001.getKey());
//				msg.setMsg(lan, "%%提币地址不能为本系统地址。",coint.getTag());
//				return msg;
//			}

			if (user.isDeleted()) {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg(lan, "已删除用户，禁止提现");
				return msg;
			}
			if (user.isFreez()) {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg(lan,"该账户已冻结，暂时不能操作。");
				return msg;
			}

			BigDecimal minFees = coint.getMinFees();

			if (fees.compareTo(minFees) < 0) {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg(lan,"请选择正确的矿工费（最少%%）。", minFees.toPlainString());
				return msg;
			}

			String userId = user.getId();
			// 只有已认证的地址才可以使用api提现
			if (isApi) {
				ReceiveAddr reciveAddr = (ReceiveAddr) Data.GetOne("select * from " + coint.getStag() + CointTable.receiveaddr + " where userid=? and address=? and isdeleted = 0 order by createTime desc", new Object[] { userId, receiveAddr }, ReceiveAddr.class);
				if (null == reciveAddr) {
					// if (null == reciveAddr || reciveAddr.getAuth() == 0) {
					// modify by zhanglinbo 网站前台暂无验证需求
					msg.setMsg(lan,"您的提现地址未进行认证，请登录网站认证后重试。");
					return msg;
				}
				// api必须验证资金安全密码
				Message passMsg = userDao.safePwd(payPass, userId + "", lanTag);
				if (!passMsg.isSuc()) {
					return passMsg;
				}
			}

			if (account.compareTo(fees) <= 0) {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg(lan,"提现金额必须大于矿工费");
				return msg;
			}

            String addressMemo = "";
            //app端提现地址校验逻辑不执行，等app改版再校验
            if (!isAppApi) {
                //验证提现地址权限是否被锁定
                if (user.getWithdrawAddressAuthenSwitchStatus() == 2
                        && TimeUtil.getOriginDiffDay(now(), user.getWithdrawAddressAuthenModifyTime()) < 1) {
                    msg.setCode(SystemCode.code_1001.getKey());
                    msg.setMsg(lan, "您的帐户因切换模式被锁定，在此期间不能进行提现操作，请等待24小时后自动解锁。");
                    return msg;
                }

                //验证提现地址是否锁定
                ReceiveAddr bab = (ReceiveAddr) Data.GetOne("select * from " + coint.getStag() + CointTable.receiveaddr + " where userId=? and address=? and isDeleted=0", new Object[]{userId,receiveAddr}, ReceiveAddr.class);
                if (bab == null) {
                    msg.setCode(SystemCode.code_1001.getKey());
                    msg.setMsg(lan, "提币地址不存在");
                    return msg;
                }
                if (user.getWithdrawAddressAuthenType() == WithdrawAddressAuthenType.SECURITY.getKey()
                        && bab.getCreateTime().after(user.getWithdrawAddressAuthenModifyTime())
                        && TimeUtil.getOriginDiffDay(now(), bab.getCreateTime()) < 1) {
                    msg.setCode(SystemCode.code_1001.getKey());
                    msg.setMsg("提现地址已经锁定");
                    return msg;
                }
                addressMemo = bab.getMemo();
            }

			/**
			 * 如果设置每日提现额度，按设定额度。否则：不限额度或者100。
			 * 每次提现额度，如果设置，按设定额度。否则：贵宾版每次500，其他100。
			 */
			PayUserWalletBean payUser = payUserDao.getById(Integer.parseInt(user.getId()), coint.getFundsType());
			/**
			 * 如果设置每日提现额度，按设定额度。否则：不限额度或者100。
			 * 每次提现额度，如果设置，按设定额度。否则：贵宾版每次500，其他100。
			 */
			BigDecimal dayCash = payUser.getDayCash();
			BigDecimal everyTimeCash = payUser.getTimesCash();

			BigDecimal minCash = coint.getMinCash();
			if (account.compareTo(minCash) <= 0) {
				msg.setMsg(lan,"提现金额必须大于%%",minCash.toPlainString());
				return msg;
			}

			if (everyTimeCash.compareTo(BigDecimal.ZERO) > 0 && account.compareTo(everyTimeCash) > 0) {
				msg.setMsg(lan,"每笔提现不能超过%%个。",everyTimeCash.toString());
				return msg;
			}

			if (payUser.getBalance().compareTo(account) < 0) {// 小于用于余额
				msg.setMsg(lan,"您的余额已不足于你本次提现的额度。");
				return msg;
			}

            //兼容手机端，重新获取一遍校验
            if (isAppApi || isApi) {
                BigDecimal todayCash = BigDecimal.ZERO;
                //获取币种对btc价格
                BigDecimal price = Price.getCoinBtcPrice(coint.getStag());

                //如果该币种有市场，根据总额度计算提现数据
                if (price.compareTo(BigDecimal.ZERO) > 0) {
                    //获取用户总提现额度，跟实名认证关联，btc
                    Map<String, Object> userDownloadLimit = downloadSummaryDao.getDownloadLimit(userId);
                    BigDecimal downloadLimit = (BigDecimal) userDownloadLimit.get("downloadLimit");

                    dayCash = downloadLimit.divide(price, 8, BigDecimal.ROUND_DOWN);
                    //获取今天已经提现数量，btc
                    BigDecimal todayCashBTC = downloadSummaryDao.getTodayBtcAmount(String.valueOf(userId));
                    //转换后的该币种的已经提现数量
                    todayCash = todayCashBTC.divide(price, 8, BigDecimal.ROUND_UP);
                } else {
                    todayCash = getTodayCash(userId);
                }
                BigDecimal todayCanCash = dayCash.subtract(todayCash);
                if (dayCash.compareTo(BigDecimal.ZERO) > 0 && todayCanCash.compareTo(account) < 0) {
                    msg.setMsg(lan, "您今日提现额度已达上限，本次最多只能提现%%个。", todayCanCash.toString());
                    return msg;
                }
            }

            // 验证资金安全密码
            Message passMsg = userDao.safePwd(payPass, userId + "", lanTag);
            if (!passMsg.isSuc() && !isAppApi) {// do not validate when the app
                // client invoke, because the
                // validation have processed
                // before
                passMsg.setCode(passMsg.getCode());
                return passMsg;
            }

            UserContact uc = user.getUserContact();
            //验证邮箱
			if (emailCode != null){
				checkCode(emailCode,1,8,user,ip);
			}

//			 //不是手机API，验证
//            if (!isAppApi && !isApi) {
//                boolean dynamicPass = false;
//				// 检查短信验证码
//                if (user.getSmsOpen() && mobileCode != null) {
//					checkCode(mobileCode,2,8,user,ip);
//                }
//				if (user.getGoogleOpen() && googleCode != 0) {
//					//谷歌验证
//					UserContact userContact = user.getUserContact();
//					String secret = userContact.getSecret();
//					Map<Boolean, String> flagMap = new HashMap<Boolean, String>();
//					String url = ApiConfig.getValue("usecenter.url");
//					FeignContainer container = new FeignContainer(url);
//					DownloadSubmitService downloadSubmitService = container.getFeignClient(DownloadSubmitService.class);
//					flagMap = downloadSubmitService.checkGoogleCode(String.valueOf(googleCode), secret, String.valueOf(userId));
//					for (Map.Entry<Boolean, String> map : flagMap.entrySet()) {
//						if (map.getKey()) {
//							msg.setMsg(lan, map.getValue());
//						} else {
//							msg.setMsg(lan, map.getValue());
//						}
//					}
//                    dynamicPass = true;
//                }
//                if (!dynamicPass) {
//                    msg.setMsg(lan,"您未开启短信和Google提现验证，请开启短信或Google提现验证后重试");
//                    return msg;
//                }
//            }

			BigDecimal todayFreeCash = getTodayFreeCash(userId);
			boolean autoConfirm = false;// 自动打币
			if (todayFreeCash.add(account).compareTo(payUser.getDayFreeCash()) <= 0) {
				autoConfirm = true;
			}

			List<OneSql> sqls = new ArrayList<OneSql>();

			int manageid = 0;
			String managerName = "";

			long downloadId = getId();
			freezDao.setCoint(coint);

            String userName = user.getUserName();
			String uuid = UUID.randomUUID().toString();
			//提现金额折算成btc价格，记录下来用于计算当天可用提现额度
            BigDecimal amountBtc = Price.getCoinBtcPrice(coint.getStag()).multiply(account).setScale(8, BigDecimal.ROUND_DOWN);

			sqls.add(new OneSql("INSERT INTO " + getTableName() + " (id, userId, userName, amount, submitTime, status, managerId, manageName, toAddress, remark, isDel, fees, opUnique, uuid, amountBtc, memo, addressMemo) " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", -2, new Object[] { downloadId, userId, userName, account, now(), 0,// 状态
					manageid, managerName, receiveAddr, liuyan, 0, fees, opUnique, uuid, amountBtc, memo, addressMemo}));

			//提现汇总表中增加记录
            sqls.add(new OneSql("INSERT INTO downloadsummary (downloadId, fundsType, userId, userName, amount, submitTime, status, managerId, manageName, toAddress, remark, isDel, fees, opUnique, uuid, amountBtc, memo, addressMemo) " + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", -2, new Object[] { downloadId, coint.getFundsType(), userId, userName, account, now(), 0,// 状态
                    manageid, managerName, receiveAddr, liuyan, 0, fees, opUnique, uuid, amountBtc, memo, addressMemo}));

			FreezeBean freez = new FreezeBean(userId, userName, coint.getPropTag() + "提现", FreezType.download.getKey(), account, 0, 0);
			freezDao.walletFreez(sqls, freez);

			if (Data.doTrans(sqls)) {
				UserCache.resetUserWalletFundsFromDatabase(String.valueOf(userId));
				new UserDao().clearMobileCode(user.getId());
				msg.setMsg(lan,"提交成功！");
				msg.setData(downloadId + "");
				msg.setSuc(true);
			} else {
				log.info("事务执行失败：");
				for (OneSql oneSql : sqls) {
					log.info(oneSql.getSql());
				}
				msg.setMsg(lan,"提交失败，请稍后重试。");
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			msg.setMsg(lan,"提交失败，请稍后重试。");
		}
		return msg;
	}


	public Message getAvaliable(String userId,BigDecimal account,int fundsType){
		Message msg = new Message();
		PayUserWalletDao payUserDao = new PayUserWalletDao();
		PayUserWalletBean payUser = payUserDao.getById(Integer.parseInt(userId), fundsType);
		BigDecimal todayCash = BigDecimal.ZERO;
		//获取币种对btc价格
		BigDecimal price = Price.getCoinBtcPrice(coint.getStag());
		BigDecimal dayCash = coint.getDayCash();
		//如果该币种有市场，根据总额度计算提现数据
		if (price.compareTo(BigDecimal.ZERO) > 0) {
			//获取用户总提现额度，跟实名认证关联，btc
			Map<String, Object> userDownloadLimit = downloadSummaryDao.getDownloadLimit(userId);
			BigDecimal downloadLimit = (BigDecimal) userDownloadLimit.get("downloadLimit");

				dayCash = downloadLimit.divide(price, 8, BigDecimal.ROUND_DOWN);
			//获取今天已经提现数量，btc
			BigDecimal todayCashBTC = downloadSummaryDao.getTodayBtcAmount(String.valueOf(userId));
			//转换后的该币种的已经提现数量
			todayCash = todayCashBTC.divide(price, 8, BigDecimal.ROUND_UP);
		} else {
			todayCash = getTodayCash(userId);
		}
		BigDecimal todayCanCash = dayCash.subtract(todayCash);
		log.info("------当前可用额度"+todayCanCash.toString());
		if (dayCash.compareTo(BigDecimal.ZERO) > 0 && todayCanCash.compareTo(account) < 0) {
			log.info("------超过额度");
			msg.setMsg(lan, "超过当日总提现额度");
		}else{
			msg.setSuc(true);
		}
		return msg;
	}

    /**
     * 系统发起提现，跳过提现额度的验证。目前适用于回购GBC定时提现
     */
    public List<OneSql> systemDownload(String userId, String userName, BigDecimal amount, Timestamp withdrawTime, String receiveAddr) {
        List<OneSql> sqls = new ArrayList<>();

        long downloadId = getId();
        freezDao.setCoint(coint);

        String uuid = UUID.randomUUID().toString();
        String opUnique = userId + "_";
        sqls.add(new OneSql("INSERT INTO " + getTableName() + " (id, userId, userName, amount, submitTime, status, toAddress, opUnique, uuid) "
                + " VALUES (?,?,?,?,?,?,?,?,?)", -2, new Object[]{downloadId, userId, userName, amount, withdrawTime, 0, receiveAddr, opUnique, uuid}));
        sqls.add(new OneSql("INSERT INTO downloadsummary (downloadId, fundsType, userId, userName, amount, submitTime, status, toAddress, opUnique, uuid) "
                + " VALUES (?,?,?,?,?,?,?,?,?,?)", -2, new Object[]{downloadId, coint.getFundsType(), userId, userName, amount, withdrawTime, 0, receiveAddr, opUnique, uuid}));

        FreezeBean freeze = new FreezeBean(userId, userName, coint.getPropTag() + "提现", FreezType.download.getKey(), amount, 0, 0);
        freezDao.freez(sqls, freeze);

        return sqls;
    }

    /**
     * 获取某个用户累计提现金额
     *
     * @param userId
     * @return
     */
    public BigDecimal getTotalCash(String userId) {
        List list = (List) Data.GetOne(database, "select sum(amount) from " + getTableName() + " where userId=? and status<>1 and status<>3", new Object[] { userId });
        BigDecimal hasCash = list.get(0) == null ? BigDecimal.ZERO : (BigDecimal) list.get(0);
        return hasCash;
    }

    /**
     * 获取某个用户最近一次提现金额，不包括取消的
     *
     * @param userId
     * @return
     */
    public DownloadBean getUserLastDownload(String userId) {
        return (DownloadBean) Data.GetOne(database, "select * from " + getTableName() + " where userId=? and status<>3 order by submitTime desc limit 1", new Object[]{userId}, DownloadBean.class);
    }

	public Message doCancelCash(String userId, long did, String ip, CoinProps coint) {
		Message msg = new Message();
		try {
			if (did > 0) {
				DownloadBean bdlb = (DownloadBean) Data.GetOne("select * from " + coint.getStag() + CointTable.download + " where id=? and status<=0 and commandId <=0 and userId=?", new Object[] { did, userId }, DownloadBean.class);
				if (bdlb == null) {
					msg.setCode(SystemCode.code_1001.getKey());
					msg.setMsg("记录不存在");
					return msg;
				}
				List<OneSql> sqls = new ArrayList<OneSql>();
				bdlb.setRemark(coint.getCnname() + "提现取消");

				freezDao.setCoint(coint);
				// 解冻不扣除
				FreezeBean fbean = new FreezeBean(userId + "", bdlb.getUserName(), "取消下载", FreezType.cashUnFreez.getKey(), bdlb.getAmount(), 0, 0);
				freezDao.unFreezSqls(sqls, fbean, BillType.download, false);

				sqls.add(new OneSql("update " + coint.getStag() + CointTable.download + " set status=3,manageTime=? where id=? and userId=? and status<=0 and commandId <=0", 1, new Object[] { now(), did, userId }));// 状态提现记录改为取消
				//更新提现汇总表状态为取消
                sqls.add(new OneSql("update downloadsummary set status=3,manageTime=? where fundsType=? and downloadId=? and userId=? and status<=0 and commandId <=0", 1, new Object[] { now(), coint.getFundsType(), did, userId }));
				if (Data.doTrans(sqls)) {// /发送提现成功消息
					// 插入一条管理员日志信息
					DailyType type = DailyType.btcDownload;
					new DailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "取消下载", bdlb.getUserName(), did, coint.getPropTag()), String.valueOf(userId), ip, now());
					msg.setCode(SystemCode.code_1000.getKey());
					msg.setMsg("操作成功。");
					msg.setSuc(true);
					UserCache.resetUserWalletFundsFromDatabase(userId);
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			msg.setCode(SystemCode.code_1001.getKey());
			msg.setMsg("操作异常。");
		}
		return msg;
	}

	public DownloadBean getUnconfirm(long downloadId) {
		Query query = getQuery();

		query.setSql("select * from " + getTableName() + " where id = " + downloadId + " AND commandId=0 and status=" + CoinDownloadStatus.COMMITED.getKey());// 没有确认的
		query.setCls(DownloadBean.class);

		return (DownloadBean) query.getOne();
	}

	public void doWithdraw(long downloadId, boolean isAuto, Pages pages, String ip) {
		DownloadBean bdb = getUnconfirm(downloadId);

		if (bdb == null) {
			if (isAuto) {
				log.info("该记录已确认或者状态已发生改变");
			} else {
				pages.WriteError(pages.L("该记录已确认或者状态已发生改变"));
			}
			return;
		}

		PayUserWalletDao payDao = new PayUserWalletDao();
		PayUserWalletBean payUser = payDao.getById(Integer.parseInt(bdb.getUserId()), coint.getFundsType());
		if (payUser.getFreez().compareTo(bdb.getAmount()) < 0) {
			pages.WriteError(pages.L("提币失败，冻结的币小于提现的币"));
			return;
		}

		BigDecimal fees = coint.getMinFees();
		BigDecimal userFees = bdb.getFees();

		if (userFees.compareTo(fees) > 0) {
			fees = userFees;
		}

		BigDecimal account = bdb.getAmount();
		if (fees.compareTo(BigDecimal.ZERO) > 0) {// 网站补贴费率
			account = account.subtract(fees);
		}

		// 调用商户系统
		DownloadBean download = new DownloadBean();
		download.setId(bdb.getId());
		download.setUserId(bdb.getUserId());
		download.setUserName(bdb.getUserName());
		download.setSubmitTime(bdb.getSubmitTime());
		download.setStatus(bdb.getStatus());
		download.setManagerId(bdb.getManagerId());
		download.setManageName(bdb.getManageName());
		download.setManageTime(bdb.getManageTime());
		download.setFreezeId(bdb.getFreezeId());
		download.setFees(fees);
		download.setToAddress(bdb.getToAddress());
		download.setRemark(bdb.getRemark());
		JSONObject json = MerchantsUtil.doBtcDownload(download, bdb.getAmount(), fees, coint.getStag());
		if (null == json) {
			if (isAuto) {
				log.debug("提交商户系统出错，请稍后重试");
			} else {
				pages.Write(pages.L("提交商户系统出错，请稍后重试"), false, "");
			}
			return;
		}
		if (json.containsKey("code") && (json.getIntValue("code") == 1000 || json.getIntValue("code") == 5004)) {
			String merchantOrderNo = "";
			String walletId = "";
			if (json.containsKey("data")) {
				com.alibaba.fastjson.JSONObject data = json.getJSONObject("data");
				if (data.containsKey("orderNo")) {
					merchantOrderNo = data.getString("orderNo");
				}
				if (data.containsKey("commandId")) {
					walletId = data.getString("commandId");
				}
			}

			List<OneSql> workSqls = new ArrayList<OneSql>();
			workSqls.add(new OneSql("update " + getTableName() + " set commandId=?,manageTime=?,isMerchant=1,merchantOrderNo=? where status=? and id=?", 1, new Object[] { walletId, TimeUtil.getNow(), merchantOrderNo, CoinDownloadStatus.COMMITED.getKey(), downloadId }));// 已同步到打币库中（不再人工处理）更新commandId
																																																																				// =(钱包id)代表自动
            workSqls.add(new OneSql("update downloadsummary set commandId=?,manageTime=?,isMerchant=1,merchantOrderNo=? where status=? and fundsType=? and downloadId=?", -2, new Object[] { walletId, TimeUtil.getNow(), merchantOrderNo, CoinDownloadStatus.COMMITED.getKey(), coint.getFundsType(), downloadId }));

			if (Data.doTrans(workSqls)) {
				if (isAuto) {
					log.debug(coint.getTag() + "提现记录已同步到自动库中，无需人工处理。");
				} else {
					pages.WriteRight(coint.getTag() + pages.L("提现记录已同步到自动库中，无需人工处理。"));
				}

				try {
					DailyType type = DailyType.btcDownload;
					String remark = null;
					if (isAuto) {
						remark = "用户" + bdb.getUserName() + "提" + bdb.getAmount() + coint.getTag() + "，在免审额度内，主触发自动动打币程序。提现编号：" + downloadId;
					} else {
						User u = new UserDao().getUserById(String.valueOf(bdb.getUserId()));
						remark = DailyType.getMemoByType(type, "下载确认", u.getUserName(), account, downloadId, coint.getPropCnName());
					}

					new MainDailyRecordDao().insertOneRecord(type, remark, "", ip, TimeUtil.getNow(), Integer.parseInt(bdb.getUserId()), bdb.getAmount());
				} catch (Exception e) {
					log.error("用户提现主动发起自动打币程序日志添加失败", e);
					pages.WriteRight(coint.getTag() + pages.L("同步失败。"));
				}
			}
		} else {
			if (isAuto) {
				log.debug(json.getString("message"));
			} else {
				pages.Write(json.getString("message"), false, "");
			}
			return;
		}
	}

	/**
	 * 获取 用户的充值记录
	 * 
	 * @param userId
	 *            用户ID
	 * @param pageIndex
	 *            页码
	 * @param pageSize
	 *            每页条数
	 * @return JSON
	 */
	public JSONObject getRecord(String userId, int pageIndex, int pageSize) {
		JSONObject json = new JSONObject();
		JSONArray jarry = new JSONArray();
		String sql = "select * from btcdownload ";
		Query query = this.getQuery();
		query.setSql(sql);
		query.append(" userId='" + userId + "' and isDel=0 ");
		query.setCls(DownloadBean.class);

		int total = query.count();
//		if (total > 0) {
			query.append("order by id desc");
			// 分页查询
			List<DownloadBean> list = this.findPage(pageIndex, pageSize);
			if (null != list && list.size() > 0) {
				for (DownloadBean bb : list) {
					JSONObject jo = new JSONObject();
					jo.put("id", bb.getId());
					jo.put("amount", bb.getAmount());
					jo.put("fees", bb.getFees());
					jo.put("toAddress", bb.getToAddress());
					jo.put("submitTime", bb.getSubmitTime());
					jo.put("manageTime", bb.getManageTime());
					jo.put("status", bb.getStatus());
					jarry.add(jo);
				}
			}
//		}
		json.put("list", jarry);
		json.put("totalCount", total);
		json.put("pageIndex", pageIndex);
		json.put("pageSize", pageSize);
		json.put("totalPage", getTotalPage(total, pageSize));
		return json;
	}

	private int getTotalPage(int total, int pageSize) {
		int size = total / pageSize;// 总条数/每页显示的条数=总页数
		int mod = total % pageSize;// 最后一页的条数
		if (mod != 0)
			size++;
		return total == 0 ? 1 : size;
	}

	/**
	 * 自动打币程序
	 * @param coint
	 * @param userId
	 * @param userName
	 * @param receiveAddr
	 * @param downloadId
	 * @param amount
	 * @return
	 */
	public DataResponse autoCashToUser(CoinProps coint, String userId, String userName, int manageId, String manageName, String receiveAddr, long downloadId, BigDecimal amount, BigDecimal fees, BigDecimal payFees, int bankAccountId, int adminId, String ip,long time){
		DataResponse res = new DataResponse();
		
		String otherSql = "AND isDefault = true";
		if(bankAccountId > 0){
			otherSql = "AND id = " + bankAccountId;
		}
		FinanAccount account = (FinanAccount)Data.GetOne("SELECT * FROM finanaccount WHERE isDel = false AND fundType = ? AND type = ? "+otherSql+" ORDER BY createTime", new Object[]{coint.getFundsType(), AccountType.withdraw.getKey()}, FinanAccount.class);
		if(account == null){
			log.info("自动打币的账户不存在。");
			res.setSuc(false);
			res.setDes("自动打币的账户不存在。");
			return res;
		}
		
		BigDecimal realAmount = amount;
		if(fees.compareTo(BigDecimal.ZERO) > 0){//网站补贴费率
			//用户设置网络手续费较高  取用户的
			if(payFees.compareTo(fees) < 0){
				payFees = fees;
			}
			realAmount = realAmount.subtract(fees);
		}
		
		List<OneSql> sqls = new ArrayList<OneSql>();

		sqls.add(new OneSql("update "+getTableName()+" set commandId = ?, managerId=?, manageName=?,manageTime=?,batchId=?,status=0 where status=7 and id="+downloadId , 1 ,
				new Object[]{bankAccountId, manageId, manageName, now(),time}));//已同步到打币库中（不再人工处理）更新commandId =(钱包id)代表自动
        sqls.add(new OneSql("update downloadsummary set commandId = ?, managerId=?, manageName=?,manageTime=?,batchId=?,status=0 where fundsType=? and downloadId="+downloadId , -2 ,
				new Object[]{bankAccountId, manageId, manageName, now(),time, coint.getFundsType()}));
		
		FinanUseTypeDao useTypeDao = new FinanUseTypeDao();
		FinanUseType usetype = useTypeDao.getByType(2);
		sqls.addAll(new FinanEntryDao().saveOneEntry(account.getId(), usetype.getId(), coint.getFundsType(), amount, fees, Integer.parseInt(userId), userName, "用户主动发起自动打币", ip, downloadId, adminId, false));
		
		if(Data.doTrans(sqls)){
			res.setSuc(true);
			res.setDes("记录已同步到自动库中，打币正在进行中。");
		}else{
			res.setSuc(false);
			res.setDes("同步记录失败。");
		}
		return res;
	}

	/**
	 * 获取符合条件的自动打币记录
	 * @return
     */
	public List<DownloadBean> getAutoDownloadlist(){
		List<DownloadBean> list = new ArrayList<>();
		try{
			String sql = "select * from " + getTableName() + " where status = 0 and commandId = 0 order by submitTime";
			list = super.find(sql, new Object[]{}, DownloadBean.class);
		}catch (Exception e){
			log.error("查询待打币记录出错，错误信息：" + e.toString());
		}
		return list;
	}



	public Message checkCode(String code,int type,int icodeType,User user,String userIp) {
		Message msg = new Message();
		String codeRecvAddr =null;
		int sendType = 1 ;
		if (StringUtils.isEmpty(code)) {
			msg.setMsg(lan,"验证码不能为空");
			return msg;
		}
		if(type == 1){//sendtype 1:邮箱
			codeRecvAddr = user.getUserContact().getSafeEmail();
			sendType = 1;
		}
		if(type == 2){//sendtype 2：手机短信
			codeRecvAddr = user.getUserContact().getSafeMobile();
			sendType = 2;
		}
		PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
		String codeType = postCodeType.getValue();
		ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, codeType, false);
		DataResponse dr = null;
		if(sendType == 1){
			dr = clientSession.checkCodeMail(code);// 邮箱
		}else {
			dr = clientSession.checkCode(code);// 手机
		}
		if (!dr.isSuc()) {
			msg.setMsg(lan, dr.getDes());
			return msg;
		} else {
			msg.setMsg(lan, "");
			msg.setSuc(true);
		}
		return msg;

	}
}
