package com.world.model.entity.pay;

import com.world.data.mysql.Bean;
import com.world.model.entity.user.User;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DownloadBean extends Bean{
	/**
	 * 主数据库中提现的实体
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String userId; 
	private String userName; 
	private BigDecimal amount; 
	private Timestamp submitTime; 
	private int status; //0提交   1失败  2成功  3取消  5已确认
	private int managerId; 
	private String manageName; 
	private Timestamp manageTime; 
	private String remark;
	private String fromAddress;
	private String toAddress;
	private long freezeId;
	private int isDel;
	private long commandId;
	private BigDecimal fees;//提现费率  每一笔保存一个值
	private BigDecimal realFee;//实际产生的手续费
	private boolean confirm;//是否已确认
	private BigDecimal payFee;//用户设置payFee
	private int hasFail;
	private BigDecimal balance;//打币后的钱包余额
	private String uuid;		//uuid
	/*add by xwz 20170707*/
	private String batchId;	//批次号
	private String txId;   //交易编号
	private String txIdN;  //交易编号+序号
	private Integer blockHeight;//区块高度
	/*end*/
	/*start by xzhang 20170831 币种展示辅助字段 */
	private String currency;//币种

	private long autoDownloadId = 0;//自动打币记录主键
	private String autoDownloadView = "";//打币类型显示

    private String memo; //提现记录标签，用户输入
    private String addressMemo; //提现地址标签

	public String getAutoDownloadView() {
//		if(this.autoDownloadId > 0 && this.commandId > 0){
//			return "自动";
//		}else if(this.autoDownloadId == 0 && this.commandId > 0){
//			return "人工";
//		}
		return "-";
	}


	private String commandIdView;//打币类型展示
	public String getCommandIdView() {
		//@TODO 创建常量类方便维护类型
//		if(1==this.commandId){
//			return "自动";
//		}else if (2==this.commandId){
//			return "免审";
//		}else if (0==this.commandId){
//			return "人工";
//		}
		return this.commandId+"";
	}

	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/*end*/
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getTxIdN() {
		return txIdN;
	}

	public void setTxIdN(String txIdN) {
		this.txIdN = txIdN;
	}

	public Integer getBlockHeight() {
		return blockHeight;
	}

	public void setBlockHeight(Integer blockHeight) {
		this.blockHeight = blockHeight;
	}

	public String getRemarkExHTML() {
		return remark.replace("<font color=\"red\">", "").replace("</font>", "");
	}
	
	public BigDecimal getPayFee() {
		return payFee;
	}

	public void setPayFee(BigDecimal payFee) {
		this.payFee = payFee;
	}

	public BigDecimal getFees() {
		return fees;
	}

	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}
	

	public BigDecimal getDoubleAmount(){
		return amount;
	}
	
	
	/**
	 * 获取收取费率后应该打比特币的数量
	 * @return
	 */
	public BigDecimal getAfterAmount(){
		return amount.subtract(fees);
	}
	
	private BigDecimal afterAmount;

	public void setAfterAmount(BigDecimal afterAmount) {
		this.afterAmount = afterAmount;
	}

	private String showStat;
	
	private User user;
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	private String statusT;
	public String getStatusT() {
//		switch (status) {
//			case 0:
//				if(commandId > 0){
//					statusT="已确认";
//				}else{
//					statusT="等待处理";
//				}
//				break;
//			case 1:
//				statusT="下载失败";
//				break;
//			case 2:
//				statusT="下载成功";
//				break;
//			case 3://05月21日24点
//				statusT="提现已取消";
//				break;
//
//			default:
//				break;
//		}
		return statusT;
	}
	
	public void setStatusT(String statusT) {
		this.statusT = statusT;
	}
	
	public String getShowStat() {
//		switch (status) {
//			case 0:
//				if(commandId > 0){
//					showStat="已确认";
//				}else{
//					showStat="待确认";
//				}
//				break;
//			case 1:
//				showStat="已失败";
//				break;
//			case 2:
//				showStat="已成功";
//				break;
//			case 3://05月21日24点
//				showStat="已取消";
//				break;
//			case 7:
//				showStat="发送中";
//				break;
//			default:
//				break;
//		}
		return showStat;
	}
	
	public void setShowStat(String showStat) {
		this.showStat = showStat;
	}

	public int getIsDel() {
		return isDel;
	}
	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}
	public long getFreezeId() {
		return freezeId;
	}
	public void setFreezeId(long freezeId) {
		this.freezeId = freezeId;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Timestamp getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Timestamp submitTime) {
		this.submitTime = submitTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getManagerId() {
		return managerId;
	}
	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}
	public String getManageName() {
		return manageName;
	}
	public void setManageName(String manageName) {
		this.manageName = manageName;
	}
	public Timestamp getManageTime() {
		return manageTime;
	}
	public void setManageTime(Timestamp manageTime) {
		this.manageTime = manageTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getCommandId() {
		return commandId;
	}

	public void setCommandId(long commandId) {
		this.commandId = commandId;
	}

	public BigDecimal getRealFee() {
		return realFee;
	}

	public void setRealFee(BigDecimal realFee) {
		this.realFee = realFee;
	}

	public boolean isConfirm() {
		return confirm;
	}

	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}

	public int getHasFail() {
		return hasFail;
	}

	public void setHasFail(int hasFail) {
		this.hasFail = hasFail;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getAutoDownloadId() {
		return autoDownloadId;
	}

	public void setAutoDownloadId(long autoDownloadId) {
		this.autoDownloadId = autoDownloadId;
	}

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getAddressMemo() {
        return addressMemo;
    }

    public void setAddressMemo(String addressMemo) {
        this.addressMemo = addressMemo;
    }
}
