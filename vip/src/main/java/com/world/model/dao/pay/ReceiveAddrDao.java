package com.world.model.dao.pay;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Lan;
import com.world.cache.Cache;
import com.world.data.database.DatabasesUtil;
import com.world.model.LimitType;
import com.world.model.entity.user.WithdrawAddressAuthenType;
import com.world.util.CommonUtil;
import com.world.util.MsgToastKey;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.controller.api.util.SystemCode;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.CointTable;
import com.world.model.entity.pay.ReceiveAddr;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.util.Message;
import com.world.util.UserUtil;
import com.world.util.language.LanguageTag;
import com.world.web.response.DataResponse;
import com.world.web.sso.session.ClientSession;

public class ReceiveAddrDao extends DataDaoSupport<ReceiveAddr>{
	private static final long serialVersionUID = 1L;
	
	public String getTableName(){
		return coint.getStag()+CointTable.receiveaddr;
	}
	/**
	 * 添加一条地址记录
	 * @param addr
	 * @return
	 */
	public boolean save(ReceiveAddr addr){
		List<OneSql> sqls = new ArrayList<OneSql>();
		sqls.add(saveAddr(addr));
		
		return Data.doTrans(sqls);
	}

	public OneSql saveAddr(ReceiveAddr addr){
		return new OneSql("INSERT INTO "+getTableName()+" (address, userId, userName, totalAmount, createTime, isChecked, memo,addressTag,agreement) values (?,?,?,?,?,?,?,?,?)", 1,
				new Object[]{addr.getAddress(), addr.getUserId(), addr.getUserName(), addr.getTotalAmount(), addr.getCreateTime(), addr.getIsChecked(), addr.getMemo(),addr.getAddressTag(),addr.getAgreement()});
	}
	
	/**
	 * 修改新地址时删除之前的地址，重新保存一个地址，可做日志查询
	 * @param id
	 * @param userId
	 * @return
	 */
	public OneSql deleteOne(long id, String userId){
		return new OneSql("UPDATE "+getTableName()+" SET isDeleted = 1 WHERE userId = ? AND id = ?", 1, new Object[]{userId, id});
	}
	
	public ReceiveAddr getById(long id){
		return (ReceiveAddr)super.get("SELECT * FROM "+getTableName()+" WHERE id = ? AND isDeleted = 0", new Object[]{id}, ReceiveAddr.class);
	}

	public ReceiveAddr getByUserId(String userId){
		return getByAddr(userId, null);
	}

	public ReceiveAddr getByAddr(String userId, String addr){
		String otherSql = "";
		if(!StringUtils.isEmpty(addr)){
			otherSql = " AND address = '"+addr+"'";
		}
		return (ReceiveAddr)super.get("SELECT * FROM "+getTableName()+" WHERE userId = ? AND isDeleted = 0 "+otherSql, new Object[]{userId}, ReceiveAddr.class);
	}
	
	public Map<String, ReceiveAddr> getAddrMap(String userIds) {
		List<Bean> list = getAddrs(userIds);
		
		Map<String, ReceiveAddr> maps = new HashMap<String, ReceiveAddr>();
		if (list != null && list.size() > 0) {
			for (Bean b : list) {
				ReceiveAddr ra = (ReceiveAddr) b;
				String uid = ra.getUserId();
				if (maps.get(uid) == null) {
					maps.put(uid, ra);
				}
			}
		}
		return maps;
	}
	
	public List<Bean> getAddrs(String userIds) {
		if(userIds.length() > 0){
			List<Bean> list = Data.Query("select * from "+getTableName()+" where userId in (" + userIds + ") AND isAuto = 1 AND isDeleted = 0", new Object[] {}, ReceiveAddr.class);
			return list;
		}
		return null;
	}
	
	public List<ReceiveAddr> findAddr(String userId){
		List<ReceiveAddr> list = Data.QueryT("select * from "+getTableName()+" where userid=? and isdeleted = 0 order by createTime desc limit 0,50", new Object[] { userId }, ReceiveAddr.class);
		return list;
	}

    public int findAddrCount(String userId){
        // 查询记录条数
        List l2 = (List) Data.Query("select count(1) from "+getTableName()+" where userid=? and isdeleted = 0", new Object[] { userId });
        int count = 0;
        if(l2.size() > 0){
            count = Integer.parseInt(((List) l2.get(0)).get(0).toString());
        }
        return count;
    }

    public List<ReceiveAddr> findAddrPage(String userId, int offset, int size){
        List<ReceiveAddr> list = Data.QueryT("select * from "+getTableName()+" where userid=? and isdeleted = 0 order by createTime desc limit ?,?", new Object[] { userId,offset,size }, ReceiveAddr.class);
        return list;
    }
	public List<ReceiveAddr> findAddrPageForApp(String userId, int offset, int size){
		List<ReceiveAddr> list = Data.QueryT("select * from "+getTableName()+" where userid=? and isdeleted = 0 and agreement is null order by createTime desc limit ?,?", new Object[] { userId,offset,size }, ReceiveAddr.class);
		return list;
	}
	public List<ReceiveAddr> findAddrPageForUsdte(String userId, int offset, int size){
		List<ReceiveAddr> list = Data.QueryT("select * from "+getTableName()+" where userid=? and isdeleted = 0 and agreement = 102 order by createTime desc limit ?,?", new Object[] { userId,offset,size }, ReceiveAddr.class);
		return list;
	}


	/**
	 * 添加提现地址的json方法，废弃，app改造需要重写
	 * @param safePwd
	 * @param mCode
	 * @param gCode
	 * @param memo
	 * @param receiveId
	 * @param address
	 * @return
	 */
	public Message doAddReceiveAddr(String lan, User user, String safePwd, String mCode, int gCode, String memo, int receiveId, String address, String ip, LanguageTag lanTag) {
		Message msg = new Message();
		try {
			if (address == null || !UserUtil.checkAddress(coint.getStag(), address)) {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg("地址填写有误，请检查后重新填写");
				return msg;
			}
			String userId = user.getId();
			String userName = user.getUserName();
			UserDao userDao = new UserDao();
			msg = userDao.safePwd(safePwd, userId, lanTag);
			if(!msg.isSuc()){
				return msg;
			}
			msg.setSuc(false);
			UserContact uc = user.getUserContact();
			if (uc.getGoogleAu() == 2) {
				String savedSecret = uc.getSecret();
				userDao.setLan(lan);
				msg = userDao.isCorrect(user, savedSecret, gCode);
				if (!msg.isSuc()) {
					return msg;
				}
				msg.setSuc(false);
			}
			if (uc.getMobileStatu() == 2) {
				String codeRecvAddr = user.getUserContact().getSafeMobile();
				if (StringUtils.isBlank(codeRecvAddr)) {
					codeRecvAddr = user.getUserContact().getSafeEmail();
				}
				ClientSession clientSession = new ClientSession(ip, codeRecvAddr, lanTag.getValue(), PostCodeType.addAddr.getValue(), false);
				DataResponse dr = clientSession.checkCode(mCode);
				if(!dr.isSuc()){
					msg.setMsg(dr.getDes());
					return msg;
				}
			}

			address = address.trim();

			if (receiveId > 0) {
				int count = Data.Update("UPDATE "+coint.getStag()+CointTable.receiveaddr+" SET address = ? Where userId = ? AND id = ?", new Object[] { address, userId, receiveId });
				if (count > 0) {
					msg.setCode(SystemCode.code_1000.getKey());
					msg.setMsg("保存成功");
					msg.setSuc(true);
				} else {
					msg.setCode(SystemCode.code_1001.getKey());
					msg.setMsg("操作失败，请稍后重试");
				}
			} else {
				ReceiveAddr receive = getByAddr(userId, address);
				if(receive != null){
					msg.setCode(SystemCode.code_1001.getKey());
					msg.setMsg("您已添加过该地址，不能重复添加");
				}
				receive = new ReceiveAddr(address, userId, userName, BigDecimal.ZERO, now(), 0, memo);
				List<OneSql> sqls = new ArrayList<OneSql>();
				sqls.add(saveAddr(receive));
				if (Data.doTrans(sqls)) {
					msg.setSuc(true);
					msg.setCode(SystemCode.code_1000.getKey());
					msg.setMsg(coint.getTag()+"地址添加成功");
				} else {
					msg.setCode(SystemCode.code_1001.getKey());
					msg.setMsg("操作失败，请稍后重试");
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			msg.setCode(SystemCode.code_1002.getKey());
			msg.setMsg("操作异常");
		}
		return msg;
	}

    /**
     * 添加提现地址，目前前端使用的 add by buxianguan
     * @param lan
     * @param user
     * @param mobileCode
     * @param memo
     * @param address
     * @param ip
     * @param lanTag
     * @return
     */
    public Message addReceiveAddrNew(String lan, User user, String mobileCode, String memo, String address, String ip, LanguageTag lanTag,String errorMsg,String addressTag,Integer agreement) {
        Message msg = new Message();
        try {
            String userId = user.getId();
            String userName = user.getUserName();
			String propTag = coint.getPropTag();
			if(agreement != null){
				propTag = DatabasesUtil.getUsdtAggrement(agreement).getPropTag();
			}
			if (StringUtils.isEmpty(address) || !UserUtil.checkAddress(propTag, address)) {
				errorMsg = CommonUtil.mapToJsonStr(errorMsg,"address", Lan.Language(lan,  "提币地址不存在"));
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg(errorMsg);
				return msg;
			}

			address = address.trim();
			ReceiveAddr receive = getByAddr(userId, address);
			if (receive != null) {
				errorMsg = CommonUtil.mapToJsonStr(errorMsg,"address", Lan.Language(lan,  "提币地址已添加"));
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg(errorMsg);
				return msg;
			}
            //如果开启了提现地址校验安全模式，增加地址需要验证手机或者邮箱验证码
            if (user.getWithdrawAddressAuthenType() == WithdrawAddressAuthenType.SECURITY.getKey()) {
                /*UserContact uc = user.getUserContact();
                String codeRecvAddr = uc.getSafeMobile();
                if (StringUtils.isBlank(codeRecvAddr)) {
                    codeRecvAddr = uc.getSafeEmail();
                }*/
//                ClientSession clientSession = new ClientSession(ip, userName, lanTag.getValue(), PostCodeType.addAddr.getValue(), false);
//                DataResponse dr = clientSession.checkCode(mobileCode);
				ClientSession clientSession = new ClientSession(ip, userName, lan,  PostCodeType.addAddr.getValue(), false);
				DataResponse dr = clientSession.checkCodeMailNew(user.getId(),mobileCode,LimitType.addAddress, MsgToastKey.ADD_WITHDRAWAL_ADDRESS,MsgToastKey.LOCK_24_HOUR);
                if (!dr.isSuc()) {
//					if(dr.getDes().equals(Lan.LanguageFormat(lan, "验证码输入错误次数过多,已被锁定,请24小时后重试。", ""))){
//						dr.setDes(Lan.LanguageFormat(lan,"验证码输入错误次数超出限制，锁定该帐户24小时，不得使用新增提现地址功能", ""));
////						if(StringUtils.isEmpty(Cache.Get(LimitType.addAddress.name() + "_" + userId))){
////							Cache.Set(LimitType.addAddress.name()+"_"+userId,"1",24 * 60 * 60);
////						}
//					}
					errorMsg = CommonUtil.mapToJsonStr(errorMsg,"emailcode", Lan.Language(lan, dr.getDes()));
					msg.setCode(SystemCode.code_1001.getKey());
					msg.setMsg(errorMsg);
					return msg;
                }
            }


//			if(StringUtils.isNotEmpty(errorMsg)){
//				msg.setCode(SystemCode.code_1001.getKey());
//				msg.setMsg(errorMsg);
//				return msg;
//			}
            receive = new ReceiveAddr(address, userId, userName, BigDecimal.ZERO, now(), 0, memo,addressTag,agreement);
            List<OneSql> sqls = new ArrayList<OneSql>();
            sqls.add(saveAddr(receive));
            if (Data.doTrans(sqls)) {
                msg.setSuc(true);
                msg.setCode(SystemCode.code_1000.getKey());
                msg.setMsg(lan, "创建成功");

            } else {
                msg.setCode(SystemCode.code_1001.getKey());
                msg.setMsg(lan, "操作失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            msg.setCode(SystemCode.code_1002.getKey());
            msg.setMsg(lan, "操作失败");
        }
        return msg;
    }
	/**
	 * 添加提现地址，目前前端使用的 add by buxianguan
	 * @param lan
	 * @param user
	 * @param mobileCode
	 * @param memo
	 * @param address
	 * @param ip
	 * @return
	 */
	public Message addReceiveAddrForApp(String lan, User user, String mobileCode, String memo, String address, String ip, String addressTag,Integer agreement) {
		Message msg = new Message();
		try {
			String userId = user.getId();
			String userName = user.getUserName();
			String propTag = coint.getPropTag();
			if(agreement != null){
				propTag = DatabasesUtil.getUsdtAggrement(agreement).getPropTag();
			}
			//如果开启了提现地址校验安全模式，增加地址需要验证手机或者邮箱验证码
			if (user.getWithdrawAddressAuthenType() == WithdrawAddressAuthenType.SECURITY.getKey()) {

				ClientSession clientSession = new ClientSession(ip, userName, lan,  PostCodeType.addAddr.getValue(), false);
				DataResponse dr = clientSession.checkCodeMailApp(user.getId(),mobileCode,LimitType.addAddress, MsgToastKey.ADD_WITHDRAWAL_ADDRESS,MsgToastKey.LOCK_24_HOUR);
				if (!dr.isSuc()) {
					msg.setCode(SystemCode.code_1001.getKey());
					msg.setMsg(Lan.Language(lan, dr.getDes()));
					return msg;
				}
			}

			if (address == null || !UserUtil.checkAddress(propTag, address)) {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg( Lan.Language(lan,  "提币地址不存在"));
				return msg;
			}

			address = address.trim();
			ReceiveAddr receive = getByAddr(userId, address);
			if (receive != null) {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg( Lan.Language(lan,  "提币地址已添加"));
				return msg;
			}
			receive = new ReceiveAddr(address, userId, userName, BigDecimal.ZERO, now(), 0, memo,addressTag, agreement);
			List<OneSql> sqls = new ArrayList<OneSql>();
			sqls.add(saveAddr(receive));
			if (Data.doTrans(sqls)) {
				msg.setSuc(true);
				msg.setCode(SystemCode.code_1000.getKey());
				msg.setMsg(lan, "创建成功");
				return msg;

			} else {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg(lan, "操作失败，请稍后重试");
				return msg;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			msg.setCode(SystemCode.code_1002.getKey());
			msg.setMsg(lan, "操作失败");
		}
		return msg;
	}
	/**
	 * 添加提现地址的json方法
	 * @param safePwd
	 * @param mCode
	 * @param gCode
	 * @param memo
	 * @param receiveId
	 * @param address
	 * @return
	 */
	public Message doAddReceiveAddr(User user, String safePwd, String mCode, int gCode, String memo, int receiveId, String address, String ip, LanguageTag lanTag) {
		Message msg = new Message();
		try {
			if (address == null || !UserUtil.checkAddress(coint.getStag(), address)) {
				msg.setCode(SystemCode.code_1001.getKey());
				msg.setMsg("地址填写有误，请检查后重新填写");
				return msg;
			}
			String userId = user.getId();
			String userName = user.getUserName();
			UserDao userDao = new UserDao();
			msg = userDao.safePwd(safePwd, userId, lanTag);
			if(!msg.isSuc()){
				return msg;
			}
			msg.setSuc(false);
			UserContact uc = user.getUserContact();
			if (uc.getGoogleAu() == 2) {
				String savedSecret = uc.getSecret();
				userDao.setLan(lan);
				msg = userDao.isCorrect(user, savedSecret, gCode);
				if (!msg.isSuc()) {
					return msg;
				}
				msg.setSuc(false);
			}
			if (uc.getMobileStatu() == 2) {
				String codeRecvAddr = user.getUserContact().getSafeMobile();
				if (StringUtils.isBlank(codeRecvAddr)) {
					codeRecvAddr = user.getUserContact().getSafeEmail();
				}
				ClientSession clientSession = new ClientSession(ip, codeRecvAddr, lanTag.getValue(), PostCodeType.addAddr.getValue(), false);
				DataResponse dr = clientSession.checkCode(mCode);
				if(!dr.isSuc()){
					msg.setMsg(dr.getDes());
					return msg;
				}
			}

			address = address.trim();

			if (receiveId > 0) {
				int count = Data.Update("UPDATE "+coint.getStag()+CointTable.receiveaddr+" SET address = ? Where userId = ? AND id = ?", new Object[] { address, userId, receiveId });
				if (count > 0) {
					msg.setCode(SystemCode.code_1000.getKey());
					msg.setMsg("保存成功");
					msg.setSuc(true);
				} else {
					msg.setCode(SystemCode.code_1001.getKey());
					msg.setMsg("操作失败，请稍后重试");
				}
			} else {
				ReceiveAddr receive = getByAddr(userId, address);
				if(receive != null){
					msg.setCode(SystemCode.code_1001.getKey());
					msg.setMsg("您已添加过该地址，不能重复添加");
				}
				receive = new ReceiveAddr(address, userId, userName, BigDecimal.ZERO, now(), 0, memo);
				List<OneSql> sqls = new ArrayList<OneSql>();
				sqls.add(saveAddr(receive));
				if (Data.doTrans(sqls)) {
					msg.setSuc(true);
					msg.setCode(SystemCode.code_1000.getKey());
					msg.setMsg(coint.getTag()+"地址添加成功");
				} else {
					msg.setCode(SystemCode.code_1001.getKey());
					msg.setMsg("操作失败，请稍后重试");
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			msg.setCode(SystemCode.code_1002.getKey());
			msg.setMsg("操作异常");
		}
		return msg;
	}
	
	
	/**
	 *  获取用户所有已认证的接收地址
	 * @param userId 用户ID
	 * @return JSONArray
	 */
	public JSONArray findUserAuthAddrs(String userId){
		List<ReceiveAddr> list = Data.QueryT("select * from "+getTableName()+" where userid=? and isdeleted = 0 and auth = 1 ", new Object[] { userId }, ReceiveAddr.class);
		JSONArray jarray = new JSONArray();
		if(list!=null && !list.isEmpty()){
			for(ReceiveAddr addrBean:list){
				JSONObject json = new JSONObject();
				json.put("address", addrBean.getAddress());
				json.put("memo", addrBean.getMemo());
				jarray.add(json);
			}
			
		}
		return jarray;
	}
	
}
