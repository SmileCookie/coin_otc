package com.world.controller.admin.loan.deductcoupon;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.api.user.UserManager;
import com.world.controller.api.common.util.VerifiUtil;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.user.MobileDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.loan.dao.DeductCouponDao;
import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.entity.DeductCoupon;
import com.world.model.loan.entity.P2pUser;
import com.world.util.date.TimeUtil;
import com.world.util.sign.EncryDigestUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

import net.sf.json.JSONArray;
/***
 * 抵扣券管理
 * @author chenruidong
 */
@FunctionAction(jspPath = "/admins/loan/deductcoupon/", des = "抵扣券管理")
public class Index extends AdminAction {
	private static final long serialVersionUID = 1L;

	DeductCouponDao dao = new DeductCouponDao();
	DeductCoupon dEntity = new DeductCoupon();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		setAttr("fundlist", DatabasesUtil.getCoinPropMaps());
		// 获取参数
		String tab = param("tab");
		int pageNo = intParam("page");
		String userId = param("userId");
		String userName = param("userName");
		String title = param("title");// 标题
		int couponType = intParam("couponType");// 抵扣券类型（1抵扣券、2打折券、3限额抵扣券、4限额打折券）
		int fundsType = intParam("fundsType");// 抵扣币种
		int getWay = intParam("getWay");// 获取途径（1活动发放、2系统赠送）
		String endTime = param("endTime");// 过期时间

		Query query = dao.getQuery();
		query.setSql("SELECT * FROM deductcoupon ");
		query.setCls(DeductCoupon.class);
//		int pageSize = 10;

		// 将参数保存为attribute
		try {
			if (userId.length() > 0) {
				query.append(" AND userId = '" + userId + "'");
			}
			if (userName.length() > 0) {
				query.append(" AND userName like '%" + userName + "%'");
			}
			if (title.length() > 0) {
				query.append(" AND title like '%" + title + "%'");
			}
			if (fundsType > 0) {
				query.append("AND fundsType=" + fundsType);
			}
			if (getWay > 0) {
				if (getWay == 3) {
					getWay = 1;
				}
				query.append("AND getWay=" + getWay);
			}
			if (endTime.length() > 0) {
				query.append(" AND endTime <= '" + endTime + "'");
			}
			if (couponType > 0) {
				if (couponType == 5) {
					couponType = 4;
				}
				query.append(" AND couponType = " + couponType + "");
			}
			if (tab.length() == 0) {
				tab = "allCol";
			}

			if (tab.equals("zeroCol")) {
				query.append(" AND (userId = '' or userId = null) AND useState = 0 ");
			} else if (tab.equals("oneCol")) {
				query.append(" AND (userId <> '' or userId <> null) AND useState = 1 ");
			} else if (tab.equals("towCol")) {
				query.append(" AND useState=2 ");
			} else if (tab.equals("threeCol")) {
				query.append(" AND (useState=3 or endTime < '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeUtil.getTodayFirst()) + "')");
			} else if (tab.equals("fourCol")) {
				query.append(" AND useState=4 ");
			} else if (tab.equals("fivesCol")) {
				query.append(" AND useState=5 ");
			}

			int total = query.count();
			if (total > 0) {
				query.append("ORDER BY startTime DESC");
				
				List<Bean> dataList = dao.findPage(pageNo, 10);
//				query.append("ORDER BY id DESC");
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setAttr("tab", tab);
			setPaging(total, pageNo, 10);

		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	// 加载显示
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	// 新建一批弹出框显示值
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			setAttr("fundlist", DatabasesUtil.getCoinPropMaps());
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	// 赠送用户
	@Page(Viewer = "/admins/loan/deductcoupon/rewardUser.jsp")
	public void rewardUser() {
		try {
			setAttr("fundlist", DatabasesUtil.getCoinPropMaps());
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	// 禁止使用
	@Page(Viewer = JSON)
	public void unjinZhi() {
		try {
			String secretkey = param("secretkey");
			dealMore(secretkey, "unjinZhi");
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	// 启动使用
	@Page(Viewer = JSON)
	public void startUp() {
		try {
			String secretkey = param("secretkey");
			dealMore(secretkey, "startUp");
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	// 删除
	@Page(Viewer = JSON)
	public void deClear() {
		try {
			String secretkey = param("secretkey");
			dealMore(secretkey, "deClear");
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	

	/**
	 * 禁用、启用、删除
	 * @param secretkey（秘钥）
	 * @param methodName（方法名）
	 */
	public void dealMore(String secretkey, String methodName) {
		try {
			String[] arr = secretkey.split(",");
			List<OneSql> list = new ArrayList<OneSql>();
			DeductCouponDao dao = new DeductCouponDao();
			for (String strArr : arr) {
				strArr = strArr.trim();
				if (StringUtils.isNotEmpty(strArr)) {
					
					DeductCoupon dCoupon = dao.findBySecret(strArr);
					
					if (methodName.equals("unjinZhi")) {//禁用
						if (dCoupon.getUseState() == 2) {
							json("已使用的抵扣券不能禁止！", false, "");
							return;
						}
						if (dCoupon.getUseState() == 3) {
							json("过期的抵扣券不能禁止！", false, "");
							return;
						}
						if (dCoupon.getUseState() == 5) {
							json("等待还款的抵扣券不能禁止！", false, "");
						}
						list.add(dao.getUpdateStatusSQL(strArr, 4));
					} else if (methodName.equals("startUp")) {//激活
						//这种状态不嫩使用：2已使用、3已过期、5已选择
						if (dCoupon.getUseState() == 2) {
							json("已使用的抵扣券不能重复激活！", false, "");
							return;
						}
						if (dCoupon.getUseState() == 3) {
							json("过期的抵扣券不能激活！", false, "");
							return;
						}
						if (dCoupon.getUseState() == 5) {
							json("等待还款的抵扣券不能重复激活！", false, "");
						}
						list.add(dao.getUpdateStatusSQL(strArr, 1));
					} else if (methodName.equals("deClear")) {//删除
						// 抵扣券已赠送用户，并且激活不能删除 ，赠送给用户的券，只能删除过期，过时的券
						if ((StringUtils.isBlank(dCoupon.getUserName())) || (StringUtils.isNotBlank(dCoupon.getUserName()) && dCoupon.getUseState() == 3 && dCoupon.getEndTime().before(TimeUtil.getNow()))) {
							list.add(dao.getDeleteSQL(strArr));
						} else {
							json("只能删除已过期的归属用户抵扣券！", false, "");
							return;
						}
					}
				}
			}
			if (Data.doTrans(list)) {
				json("操作成功!", true, "");
				return;
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
		json("未知异常，操作失败!", false, "");
	}

	/**
	 * ①生成一批抵扣券
	 * ②赠送抵扣券用户
	 * @author chenruidong
	 */
	@Page(Viewer = JSON)
	public void creates() {
		try {
			String userName = param("userName");// 拥有人
			String title = param("title");// 标题
			int getWay = intParam("getWay");// 获取途径
			BigDecimal amountDeg = decimalParam("amountDeg");// 额度
			int couponType = intParam("couponType");// 抵扣券类型
			int fundsType = intParam("fundsType");// 币种
			String useCondition = param("useCondition");// 使用条件
			int count = intParam("count");// 数量
			Timestamp endTime = dateParam("endTime");// 过期时间
			
			/* Start */
			P2pUser p2pUser = null;
			if (StringUtils.isNotEmpty(userName)) {
				// 此次是赠送给用户的
				p2pUser = new P2pUserDao().getByUserName(userName);
				if (p2pUser == null) {
					json("P2P中找不到该用户,无法赠送!", false, "");
					return;
				}
			}
			if (StringUtils.isBlank(title)) {
				json("请输入标题", false, "");
				return;
			}
			if (amountDeg.compareTo(BigDecimal.ZERO) <= 0) {
				json("请输入额度!", false, "");
				return;
			}
			if (amountDeg.compareTo(BigDecimal.ZERO) <= 0) {
				json("金额不能小于￥0！", false, "");
				return;
			}
//			if (amountDeg.compareTo(DigitalUtil.getBigDecimal("10")) <= 0) {
//				json("金额不能小于￥10！", false, "");
//				return;
//			}
			if (endTime == null) {
				json("请输入时间！", false, "");
				return;
			}
			if (endTime.before(TimeUtil.getNow())) {
				json("不能小于当前日期！", false, "");
				return;
			}
			if (getWay <= 0) {
				json("请选择获取途径!", false, "");
				return;
			}
			if (couponType <= 0) {
				json("请选择抵扣券类型!", false, "");
				return;
			}
			if (fundsType < 0) {
				json("请选择币种类型！", false, "");
				return;
			}
			if (count <= 0) {
				json("请选择生成数量!", false, "");
				return;
			}
//			if (StringUtils.isBlank(batchMark)) {
//				json("请选择本批标识!", false, "");
//				return;
//			}
			/* End */
			
			int batchMark = 0;// 标识
			DeductCoupon batchName= dao.batchName();
			if (batchName == null) {
				batchMark = 1;
			} else {
				if(batchName.getBatchMark().matches("\\d+")){
					batchMark = Integer.parseInt(batchName.getBatchMark());//数字
				}else{
					batchMark=batchName.getBatchMark().length();//字符串
				}
				batchMark ++;
			}

			List<DeductCoupon> list = new ArrayList<DeductCoupon>();
			List<OneSql> sqls = new ArrayList<OneSql>();
			
			for (int i = 0; i < count; i++) {
				DeductCoupon dc = new DeductCoupon();
				if (p2pUser != null) {
					dc.setUserId(p2pUser.getUserId());
					dc.setUserName(userName);
				} else {
					dc.setUserId("");
					dc.setUserName("");
				}
				dc.setStartTime(TimeUtil.getNow());
				dc.setTitle(title);
				dc.setGetWay(getWay);
				dc.setCouponType(couponType);
				String uuid = UUID.randomUUID().toString();
				dc.setSecretkey(EncryDigestUtil.digest(uuid));
				dc.setFundsType(0);
				dc.setAmountDeg(amountDeg);
				dc.setUseCondition(useCondition);
				if (StringUtils.isNotBlank(userName)) {
					log.info(TimeUtil.getNow());

					log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(TimeUtil.getNow()));
					dc.setUseState(1);
					dc.setActTime(TimeUtil.getNow());
				} else {
					dc.setUseState(0);
				}
				dc.setEndTime(endTime);
				dc.setBatchMark(String.valueOf(batchMark));
				list.add(dc);
				sqls.add(dao.getInsert(dc));
			}
			if (Data.doTrans(sqls)) {
				if (null != p2pUser) {
//					FundsType fT = FundsType.getFundsTypeByKey(fundsType);
//					fT.getValue();
					MobileDao mDao = new MobileDao();
					User user = VerifiUtil.getInstance().getUserById(p2pUser.getUserId());
					if(user != null){
						UserContact uc = user.getUserContact();
						if(StringUtils.isNotEmpty(uc.getSafeMobile())){
							mDao.sendSms(user, "", "", "尊敬的" + userName + "您好，恭喜您获得一张价值 " + amountDeg + " 元的利息抵扣券，详情请登录btcwinex.com查看", uc.getSafeMobile());
						}
					}
				}
				json("操作成功!", true, JSONArray.fromObject(list).toString());
				return;
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
		json("未知异常，操作失败!", false, "");
	}
	
	//快速赠送用户
	@Page(Viewer = "/admins/loan/deductcoupon/reUser.jsp")
	public void reUser() {
			try {
				String secretkey = param("secretkey");
				DeductCoupon sekey=dao.findBySecret(secretkey);
				CoinProps coin = DatabasesUtil.coinProps(sekey.getFundsType());
				// FundsType ftype = dao.getfType();
				setAttr("ftype", coin);
				setAttr("fundlist", sekey);
				setAttr("seid", "502"+sekey.getId());
			} catch (Exception e) {
				log.error("内部异常", e);
			}
		}
		
	@Page(Viewer = JSON)
	public void creates2() {

		String se_id = param("se_id");
		String userName = param("userName");
		if (StringUtils.isBlank(userName)) {
			json("用户名不可以为空！", false, "");
			return;
		}
		P2pUser str = new P2pUserDao().getByUserName(userName);
		if (str == null) {
			json("该用户不存在，请重新输入！", false, "");
			return;
		}
		int seid = Integer.parseInt(se_id.substring(3));//保护抵扣id
		DeductCoupon dc = new DeductCouponDao().findIdKey(seid);
		if (dc == null) {
			json("网页异常，请刷新重试！", false, "");
			return;
		}
		
		if (dao.updateUserName4(str.getUserId(), str.getUserName(), 1, TimeUtil.getNow(), seid) <= 0) {
			json("赠送失败！", false, "");
			return;
		} else {
			json("赠送成功！", true, "");
			return;
		}
	}
	
}
