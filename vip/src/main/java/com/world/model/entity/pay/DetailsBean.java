package com.world.model.entity.pay;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.world.data.mysql.Bean;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.user.User;
import com.world.util.WebUtil;
import com.world.util.date.TimeUtil;
//////
/////12     3

public class DetailsBean extends Bean {
	private static final long serialVersionUID = 1L;
	private long detailsId;
	private int type; // 1 充值
	private int status;//0等待确认 1 失败 2已经确认 3取消
	private String fromAddr;
	private String toAddr;
	private String addHash;
	private BigDecimal amount;
	private Timestamp sendTime;
	private Timestamp configTime;
	private String remark;
	private String userId;
	private String userName;
	private BigDecimal banlance;
	private long entrustId;// 委托号
	private BigDecimal price;// 单价
	private BigDecimal fees;// 手续费
	private String wallet;// 记录充值到哪个钱包
	private BigDecimal sumBtc;// 交易的比特币数量
	private int confirmTimes;
	private int adminId;
	private int sucConfirm;// 是否已成功确认,录入账务信息
	private AdminUser aUser;
	private long merchantsSyncId; // 商户平台同步ID
	private int isDelete; // 删除标识
	
	/*start by flym 20170606 添加新字段*/
	/*交易编号,暂时没有用保存到了addHash中*/
    private String txId;
    /*区块高度*/
    private int blockHeight;
    /*是否已核算*/
    private int isFinaAccount;
    /*结算编号*/
    private int finId;
    /*end*/

    /**strat by kinghao 20190111 地址标签*/
	private String addressTag;
	/** end  */

	/*start by xzhang 20170831 币种展示辅助字段 */
	private String currency;//币种
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/*end*/

	public AdminUser getaUser() {
		return aUser;
	}

	public void setaUser(AdminUser aUser) {
		this.aUser = aUser;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public int getConfirmTimes() {
		return confirmTimes;
	}

	public void setConfirmTimes(int confirmTimes) {
		this.confirmTimes = confirmTimes;
	}

	private String showStatu;

	public void setShowStatu(String showStatu) {
		this.showStatu = showStatu;
	}

	public String getShowStatu() {
		if (type == 1) {
			switch (status) {
			case 0:
				return "确认中";
			case 1:
				return "失败";
			case 2:
				return "成功";

			default:
				return "确认中";
			}
		} else {
			return "-";
		}
	}

	public long getEntrustId() {
		return entrustId;
	}

	public void setEntrustId(long entrustId) {
		this.entrustId = entrustId;
	}

	public String getShowBalance() {
		return WebUtil.saveFourShow(banlance);
	}

	private String inType;

	public void setInType(String inType) {
		this.inType = inType;
	}

	// 0 提现 1 充值 2 买入 3 卖出 4 获赠 5手续费 6推荐人提成 7系统充值,8购买比特权,9卖出比特权 10.买入比特权剩余
	public String getInType() {
		switch (type) {
		case 1:
			return "充值";
		case 2:
			return "买入";
		case 3:
			return "卖出";
		case 4:
			return "获赠";
		case 5:
			return "手续费";
		case 6:
			return "推荐人提成";
		case 7:
			return "系统充值";
		case 8:
			return "购买比特权";
		case 9:
			return "卖出比特权";
		case 10:
			return "买入比特权剩余";
		case 11:
			return "系统扣除";
		case 12:
			return "分红";
		default:
			break;
		}
		return null;
	}

	public String getShowType() {
		DetailsType billType = (DetailsType) EnumUtils.getEnumByKey(type, DetailsType.class);
		return billType != null ? billType.getValue() : null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	private User user;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public String getShowConfigTime() {
		if (configTime.compareTo(TimeUtil.getZero()) == 0) {
			return "—";
		} else {
			return sdf.format(configTime);
		}
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getConfigTime() {
		return configTime;
	}

	public void setConfigTime(Timestamp configTime) {
		this.configTime = configTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigDecimal getBanlance() {
		return banlance;
	}

	public void setBanlance(BigDecimal banlance) {
		this.banlance = banlance;
	}

	public int getSucConfirm() {
		return sucConfirm;
	}

	public void setSucConfirm(int sucConfirm) {
		this.sucConfirm = sucConfirm;
	}

	public String getWallet() {
		return wallet;
	}

	public void setWallet(String wallet) {
		this.wallet = wallet;
	}

	public long getDetailsId() {
		return detailsId;
	}

	public void setDetailsId(long detailsId) {
		this.detailsId = detailsId;
	}

	public String getFromAddr() {
		return fromAddr;
	}

	public void setFromAddr(String fromAddr) {
		this.fromAddr = fromAddr;
	}

	public String getToAddr() {
		return toAddr;
	}

	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}

	public String getAddHash() {
		return addHash;
	}

	public void setAddHash(String addHash) {
		this.addHash = addHash;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Timestamp getSendTime() {
		return sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getFees() {
		return fees;
	}

	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}

	public BigDecimal getSumBtc() {
		return sumBtc;
	}

	public void setSumBtc(BigDecimal sumBtc) {
		this.sumBtc = sumBtc;
	}

	public long getMerchantsSyncId() {
		return merchantsSyncId;
	}

	public void setMerchantsSyncId(long merchantsSyncId) {
		this.merchantsSyncId = merchantsSyncId;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public int getBlockHeight() {
		return blockHeight;
	}

	public void setBlockHeight(int blockHeight) {
		this.blockHeight = blockHeight;
	}

	public int getIsFinaAccount() {
		return isFinaAccount;
	}

	public void setIsFinaAccount(int isFinaAccount) {
		this.isFinaAccount = isFinaAccount;
	}

	public int getFinId() {
		return finId;
	}

	public void setFinId(int finId) {
		this.finId = finId;
	}

	public String getAddressTag() {
		return addressTag;
	}

	public void setAddressTag(String addressTag) {
		this.addressTag = addressTag;
	}
}
