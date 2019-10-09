package com.world.controller.manage.loan.deductcoupon;

import java.math.BigDecimal;

import net.sf.json.JSONObject;

import com.world.model.loan.dao.DeductCouponDao;
import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.entity.DeductCoupon;
import com.world.model.loan.entity.P2pUser;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;

public class Index extends UserAction {

	private static final long serialVersionUID = 1L;

	DeductCouponDao dao = new DeductCouponDao();

	/**
	 * 输入抵扣券秘钥，获取抵扣券
	 *
	 * Close By suxinjie 一期屏蔽该功能
	 */
//	@Page(Viewer = JSON)
	public void obtainKey() {
		try {
			String secretkey = param("secretkey");
			DeductCoupon obj = dao.findBySecret(secretkey);
			if (obj != null) {
				if (obj.getUseState() != 0) {
					json(L("该抵扣券密钥已激活，不能再次使用!"), false, "");
					return;
				}

				String uuid = obj.getSecretkey();
				if (!uuid.equals(secretkey)) {
					json(L("抵扣券密钥输入不正确!"), false, "");
					return;
				}
				if (obj.getEndTime().getTime() < System.currentTimeMillis()) {
					json(L("该抵扣券已过期，不能激活!"), false, "");
					return;
				}
				String userId = userIdStr();
				P2pUser pUser = (P2pUser) new P2pUserDao().getById(userId);
				if (pUser == null) {
					json(L("未知异常，操作失败"), false, "");
					return;
				}
				if (dao.updateSecretkey(userId, userName(),TimeUtil.getNow(), 1, secretkey) > 0) {
					json(L("恭喜，您已成功获取了一个抵扣券，可用额度为：฿ %%，有效期至%%。", obj.getAmountDeg().setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString(), obj.getEndFormatTime()), true, "");
					return;
				} else {
					json(L("未知异常，操作失败"), false, "");
					return;
				}
			} else {
				json(L("请输入一个正确的抵扣券密钥!"), false, "");
				return;
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
			json(L("未知异常，操作失败"), false, "");
		}
	}

	/**
	 * @see 系统奖励抵扣券，用户自己激活。
	 * @author chenruidogn
	 */
	@Page(Viewer = JSON)
	public void enableKeys() {
		try {
			int id = intParam("id");
			DeductCouponDao dao = new DeductCouponDao();
			DeductCoupon obj = dao.findIdKey(id);
			if (obj == null) {
				json(L("找不到该抵扣券!"), false, "");
				return;
			}
			if (!obj.getUserId().equals(userIdStr())) {
				json(L("抵扣券不属于该用户!"), false, "");
				return;
			}
			if (obj.getUseState() != 0) {
				json(L("该抵扣券已经激活过了，不能再次激活!"), false, "");
				return;
			}
			obj.setUseState(1);
			dao.update(obj);
			json(L("抵扣券已经成功激活，您可以在P2P借款选择该抵扣券!"), true, JSONObject.fromObject(obj).toString());
		} catch (Exception ex) {
			log.error("内部异常", ex);
			json(L("内部错误"), false, "");
		}
	}

};
