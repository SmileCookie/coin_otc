package com.world.model.balaccount.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.model.entity.coin.CoinProps;

/**
 * <p>标题: 钱包每日对账实体类</p>
 * <p>描述: 钱包每日对账实体类</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version
 */
public class FinAccWalDailyAccBean extends Bean {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/*主键,自增长*/
	private long id;
	/*资金类型*/
	private int fundsType;
	/*充值账户期初余额*/
	private BigDecimal detailPerAmount;
	/*充值账户发生额*/
	private BigDecimal detailOcAmount;
	/*充值账户期末余额*/
	private BigDecimal detailCurAmount;
	/*充值账户累积金额*/
	private BigDecimal detailTotalAmount;
	/*际提现账户期初余额*/
	private BigDecimal downloadPerAmount;
	/*实际提现账户发生额*/
	private BigDecimal downloadOcAmount;
	/*实际提现账户期末余额*/
	private BigDecimal downloadCurAmount;
	/*实际提现账户累积金额*/
	private BigDecimal downloadTotalAmount;
	/*冷钱包期初余额*/
	private BigDecimal coldWalPerAmount;
	/*冷钱包发生额*/
	private BigDecimal coldWalOcAmount;
	/*冷钱包期末余额*/
	private BigDecimal coldWalCurAmount;
	/*冷钱包累积金额*/
	private BigDecimal coldWalTotalAmount;
	/*热冲钱包期初余额*/
	private BigDecimal hotDetailWalPerAmount;
	/*热冲钱包发生额*/
	private BigDecimal hotDetailWalOcAmount;
	/*热冲钱包期末余额*/
	private BigDecimal hotDetailWalCurAmount;
	/*热冲钱包累积金额*/
	private BigDecimal hotDetailWalTotalAmount;
	/*热提钱包期初余额*/
	private BigDecimal hotDownloadWalPerAmount;
	/*热提钱包发生额*/
	private BigDecimal hotDownloadWalOcAmount;
	/*热提钱包期末余额*/
	private BigDecimal hotDownloadWalCurAmount;
	/*热提钱包累积金额*/
	private BigDecimal hotDownloadWalTotalAmount;
	/*热冲到冷网络费期初余额*/
	private BigDecimal hotDetailToColdNetPerAmount;
	/*热冲到冷网络费发生额*/
	private BigDecimal hotDetailToColdNetOcAmount;
	/*热冲到冷网络费期末余额*/
	private BigDecimal hotDetailToColdNetCurAmount;
	/*热冲到冷网络费累积金额*/
	private BigDecimal hotDetailToColdNetTotalAmount;
	/*冷到热提网络费期初余额*/
	private BigDecimal coldTohotDownloadNetPerAmount;
	/*冷到热提网络费发生额*/
	private BigDecimal coldTohotDownloadNetOcAmount;
	/*冷到热提网络费期末余额*/
	private BigDecimal coldTohotDownloadNetCurAmount;
	/*冷到热提网络费累积金额*/
	private BigDecimal coldTohotDownloadNetTotalAmount;
	/*热提到用户网络费期初余额*/
	private BigDecimal hotDownloadToUserNetPerAmount;
	/*热提到用户网络费发生额*/
	private BigDecimal hotDownloadToUserNetOcAmount;
	/*热提到用户网络费期末余额*/
	private BigDecimal hotDownloadToUserNetCurAmount;
	/*热提到用户网络费累积金额*/
	private BigDecimal hotDownloadToUserNetTotalAmount;
	/*核对状态 1：平衡，2：不平衡*/
	private int checkState;
	private String strCheckState;
	/*区块高度*/
	private int blockHeight;
	/*创建时间*/
	private Timestamp createTime;
	/*资金类型btc ltc*/
	private String fundTypeName;

	/*dealType;//交易类型1:充值  2:提现（热提）   3:冷钱包到热钱包转账（冷）   4:热钱包到冷钱包转账(热冲).*/
	private BigDecimal amount1;
	private BigDecimal fee1;
	private BigDecimal amount2;
	private BigDecimal fee2;
	private BigDecimal amount3;
	private BigDecimal fee3;
	private BigDecimal amount4;
	private BigDecimal fee4;
	//xzhang 2017.08.21 新增字段
	/**5:表示未知账户往热提打钱（手续费是0，金额是正数）,
	 6:其他未知钱包到冷钱包（手续费是0，金额是正数）。
	 7：冷钱包到其他地址。
	 8：热提到其他地址。*/
	private BigDecimal amount5;
	private BigDecimal fee5;
	private BigDecimal amount6;
	private BigDecimal fee6;
	private BigDecimal amount7;
	private BigDecimal fee7;
	private BigDecimal amount8;
	private BigDecimal fee8;

	private int maxHeight;
	private int minHeight;


	/*start by xzhang 20170905 新报表辅助字段*/
	private BigDecimal colDeposit;//冷存入
	private BigDecimal colRollOut;//冷转出
	private BigDecimal colFee;//冷手续费
	private BigDecimal colBalance;//冷余额

	private BigDecimal hotDeposit;//热提存入
	private BigDecimal hotRollOut;//热提转出
	private BigDecimal hotFee;//热提手续费
	private BigDecimal hotBalance;//热提余额

	private BigDecimal hotPayBalance;//热充余额

	private BigDecimal sumSameFee37;	//重复手续费37
	private BigDecimal sumSameFee3;		//重复手续费3
	private BigDecimal sumSameFee7;		//重复手续费7


	private BigDecimal transactionPlatformAmount; //交易平台金额
	private BigDecimal internalAdjustmentPositive;
	private BigDecimal internalAdjustmentNegative;
	private BigDecimal externalAdjustmentPositive;
	private BigDecimal externalAdjustmentNegative;

	public BigDecimal getInternalAdjustmentPositive() {
		return internalAdjustmentPositive;
	}

	public void setInternalAdjustmentPositive(BigDecimal internalAdjustmentPositive) {
		this.internalAdjustmentPositive = internalAdjustmentPositive;
	}

	public BigDecimal getInternalAdjustmentNegative() {
		return internalAdjustmentNegative;
	}

	public void setInternalAdjustmentNegative(BigDecimal internalAdjustmentNegative) {
		this.internalAdjustmentNegative = internalAdjustmentNegative;
	}

	public BigDecimal getExternalAdjustmentPositive() {
		return externalAdjustmentPositive;
	}

	public void setExternalAdjustmentPositive(BigDecimal externalAdjustmentPositive) {
		this.externalAdjustmentPositive = externalAdjustmentPositive;
	}

	public BigDecimal getExternalAdjustmentNegative() {
		return externalAdjustmentNegative;
	}

	public void setExternalAdjustmentNegative(BigDecimal externalAdjustmentNegative) {
		this.externalAdjustmentNegative = externalAdjustmentNegative;
	}

	public BigDecimal getTransactionPlatformAmount() {
		return transactionPlatformAmount;
	}

	public void setTransactionPlatformAmount(BigDecimal transactionPlatformAmount) {
		this.transactionPlatformAmount = transactionPlatformAmount;
	}

	public void setColDeposit(BigDecimal colDeposit) {
		this.colDeposit = colDeposit;
	}

	public void setColRollOut(BigDecimal colRollOut) {
		this.colRollOut = colRollOut;
	}

	public void setColFee(BigDecimal colFee) {
		this.colFee = colFee;
	}

	public void setColBalance(BigDecimal colBalance) {
		this.colBalance = colBalance;
	}

	public void setHotDeposit(BigDecimal hotDeposit) {
		this.hotDeposit = hotDeposit;
	}

	public void setHotRollOut(BigDecimal hotRollOut) {
		this.hotRollOut = hotRollOut;
	}

	public void setHotFee(BigDecimal hotFee) {
		this.hotFee = hotFee;
	}

	public void setHotBalance(BigDecimal hotBalance) {
		this.hotBalance = hotBalance;
	}

	public void setHotPayBalance(BigDecimal hotPayBalance) {
		this.hotPayBalance = hotPayBalance;
	}

	public BigDecimal getColDeposit() {
		return colDeposit;
	}

	public BigDecimal getColRollOut() {
		return colRollOut;
	}

	public BigDecimal getColFee() {
		return colFee;
	}

	public BigDecimal getColBalance() {
		return colBalance;
	}

	public BigDecimal getHotDeposit() {
		return hotDeposit;
	}

	public BigDecimal getHotRollOut() {
		return hotRollOut;
	}

	public BigDecimal getHotFee() {
		return hotFee;
	}

	public BigDecimal getHotBalance() {
		return hotBalance;
	}

	public BigDecimal getHotPayBalance() {
		return hotPayBalance;
	}

	/**end**/
	//xzhang 2017.08.21 导出列展示处理
	private String heightRange;//区块高度范围
	public String getHeightRange() {
		return this.minHeight+"到"+this.maxHeight;
	}
	private String timeRange;//对账时间范围
	public String getTimeRange(){
		try {
			Date minDate = new Date(this.minTime.getTime());
			Date maxDate = new Date(this.maxTime.getTime());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.format(minDate) + "到" + formatter.format(maxDate);
		}catch(Exception e){
			return "";
		}
	}

	private Timestamp maxTime;
	private Timestamp minTime;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getFundsType() {
		return fundsType;
	}
	public void setFundsType(int fundsType) {
		this.fundsType = fundsType;
	}
	public BigDecimal getDetailPerAmount() {
		return detailPerAmount;
	}
	public void setDetailPerAmount(BigDecimal detailPerAmount) {
		this.detailPerAmount = detailPerAmount;
	}
	public BigDecimal getDetailOcAmount() {
		return detailOcAmount;
	}
	public void setDetailOcAmount(BigDecimal detailOcAmount) {
		this.detailOcAmount = detailOcAmount;
	}
	public BigDecimal getDetailCurAmount() {
		return detailCurAmount;
	}
	public void setDetailCurAmount(BigDecimal detailCurAmount) {
		this.detailCurAmount = detailCurAmount;
	}
	public BigDecimal getDetailTotalAmount() {
		return detailTotalAmount;
	}
	public void setDetailTotalAmount(BigDecimal detailTotalAmount) {
		this.detailTotalAmount = detailTotalAmount;
	}
	public BigDecimal getDownloadOcAmount() {
		return downloadOcAmount;
	}
	public void setDownloadOcAmount(BigDecimal downloadOcAmount) {
		this.downloadOcAmount = downloadOcAmount;
	}
	public BigDecimal getDownloadCurAmount() {
		return downloadCurAmount;
	}
	public void setDownloadCurAmount(BigDecimal downloadCurAmount) {
		this.downloadCurAmount = downloadCurAmount;
	}
	public BigDecimal getDownloadTotalAmount() {
		return downloadTotalAmount;
	}
	public void setDownloadTotalAmount(BigDecimal downloadTotalAmount) {
		this.downloadTotalAmount = downloadTotalAmount;
	}
	public BigDecimal getColdWalPerAmount() {
		return coldWalPerAmount;
	}
	public void setColdWalPerAmount(BigDecimal coldWalPerAmount) {
		this.coldWalPerAmount = coldWalPerAmount;
	}
	public BigDecimal getColdWalOcAmount() {
		return coldWalOcAmount;
	}
	public void setColdWalOcAmount(BigDecimal coldWalOcAmount) {
		this.coldWalOcAmount = coldWalOcAmount;
	}
	public BigDecimal getColdWalCurAmount() {
		return coldWalCurAmount;
	}
	public void setColdWalCurAmount(BigDecimal coldWalCurAmount) {
		this.coldWalCurAmount = coldWalCurAmount;
	}
	public BigDecimal getColdWalTotalAmount() {
		return coldWalTotalAmount;
	}
	public void setColdWalTotalAmount(BigDecimal coldWalTotalAmount) {
		this.coldWalTotalAmount = coldWalTotalAmount;
	}
	public BigDecimal getHotDetailWalPerAmount() {
		return hotDetailWalPerAmount;
	}
	public void setHotDetailWalPerAmount(BigDecimal hotDetailWalPerAmount) {
		this.hotDetailWalPerAmount = hotDetailWalPerAmount;
	}
	public BigDecimal getHotDetailWalOcAmount() {
		return hotDetailWalOcAmount;
	}
	public void setHotDetailWalOcAmount(BigDecimal hotDetailWalOcAmount) {
		this.hotDetailWalOcAmount = hotDetailWalOcAmount;
	}
	public BigDecimal getHotDetailWalCurAmount() {
		return hotDetailWalCurAmount;
	}
	public void setHotDetailWalCurAmount(BigDecimal hotDetailWalCurAmount) {
		this.hotDetailWalCurAmount = hotDetailWalCurAmount;
	}
	public BigDecimal getHotDetailWalTotalAmount() {
		return hotDetailWalTotalAmount;
	}
	public void setHotDetailWalTotalAmount(BigDecimal hotDetailWalTotalAmount) {
		this.hotDetailWalTotalAmount = hotDetailWalTotalAmount;
	}
	public BigDecimal getHotDownloadWalPerAmount() {
		return hotDownloadWalPerAmount;
	}
	public void setHotDownloadWalPerAmount(BigDecimal hotDownloadWalPerAmount) {
		this.hotDownloadWalPerAmount = hotDownloadWalPerAmount;
	}
	public BigDecimal getHotDownloadWalOcAmount() {
		return hotDownloadWalOcAmount;
	}
	public void setHotDownloadWalOcAmount(BigDecimal hotDownloadWalOcAmount) {
		this.hotDownloadWalOcAmount = hotDownloadWalOcAmount;
	}
	public BigDecimal getHotDownloadWalCurAmount() {
		return hotDownloadWalCurAmount;
	}
	public void setHotDownloadWalCurAmount(BigDecimal hotDownloadWalCurAmount) {
		this.hotDownloadWalCurAmount = hotDownloadWalCurAmount;
	}
	public BigDecimal getHotDownloadWalTotalAmount() {
		return hotDownloadWalTotalAmount;
	}
	public void setHotDownloadWalTotalAmount(BigDecimal hotDownloadWalTotalAmount) {
		this.hotDownloadWalTotalAmount = hotDownloadWalTotalAmount;
	}
	public BigDecimal getHotDetailToColdNetPerAmount() {
		return hotDetailToColdNetPerAmount;
	}
	public void setHotDetailToColdNetPerAmount(BigDecimal hotDetailToColdNetPerAmount) {
		this.hotDetailToColdNetPerAmount = hotDetailToColdNetPerAmount;
	}
	public BigDecimal getHotDetailToColdNetOcAmount() {
		return hotDetailToColdNetOcAmount;
	}
	public void setHotDetailToColdNetOcAmount(BigDecimal hotDetailToColdNetOcAmount) {
		this.hotDetailToColdNetOcAmount = hotDetailToColdNetOcAmount;
	}
	public BigDecimal getHotDetailToColdNetCurAmount() {
		return hotDetailToColdNetCurAmount;
	}
	public void setHotDetailToColdNetCurAmount(BigDecimal hotDetailToColdNetCurAmount) {
		this.hotDetailToColdNetCurAmount = hotDetailToColdNetCurAmount;
	}
	public BigDecimal getHotDetailToColdNetTotalAmount() {
		return hotDetailToColdNetTotalAmount;
	}
	public void setHotDetailToColdNetTotalAmount(BigDecimal hotDetailToColdNetTotalAmount) {
		this.hotDetailToColdNetTotalAmount = hotDetailToColdNetTotalAmount;
	}
	public BigDecimal getColdTohotDownloadNetPerAmount() {
		return coldTohotDownloadNetPerAmount;
	}
	public void setColdTohotDownloadNetPerAmount(BigDecimal coldTohotDownloadNetPerAmount) {
		this.coldTohotDownloadNetPerAmount = coldTohotDownloadNetPerAmount;
	}
	public BigDecimal getColdTohotDownloadNetOcAmount() {
		return coldTohotDownloadNetOcAmount;
	}
	public void setColdTohotDownloadNetOcAmount(BigDecimal coldTohotDownloadNetOcAmount) {
		this.coldTohotDownloadNetOcAmount = coldTohotDownloadNetOcAmount;
	}
	public BigDecimal getColdTohotDownloadNetCurAmount() {
		return coldTohotDownloadNetCurAmount;
	}
	public void setColdTohotDownloadNetCurAmount(BigDecimal coldTohotDownloadNetCurAmount) {
		this.coldTohotDownloadNetCurAmount = coldTohotDownloadNetCurAmount;
	}
	public BigDecimal getColdTohotDownloadNetTotalAmount() {
		return coldTohotDownloadNetTotalAmount;
	}
	public void setColdTohotDownloadNetTotalAmount(BigDecimal coldTohotDownloadNetTotalAmount) {
		this.coldTohotDownloadNetTotalAmount = coldTohotDownloadNetTotalAmount;
	}
	public BigDecimal getHotDownloadToUserNetPerAmount() {
		return hotDownloadToUserNetPerAmount;
	}
	public void setHotDownloadToUserNetPerAmount(BigDecimal hotDownloadToUserNetPerAmount) {
		this.hotDownloadToUserNetPerAmount = hotDownloadToUserNetPerAmount;
	}
	public BigDecimal getHotDownloadToUserNetOcAmount() {
		return hotDownloadToUserNetOcAmount;
	}
	public void setHotDownloadToUserNetOcAmount(BigDecimal hotDownloadToUserNetOcAmount) {
		this.hotDownloadToUserNetOcAmount = hotDownloadToUserNetOcAmount;
	}
	public BigDecimal getHotDownloadToUserNetCurAmount() {
		return hotDownloadToUserNetCurAmount;
	}
	public void setHotDownloadToUserNetCurAmount(BigDecimal hotDownloadToUserNetCurAmount) {
		this.hotDownloadToUserNetCurAmount = hotDownloadToUserNetCurAmount;
	}
	public BigDecimal getHotDownloadToUserNetTotalAmount() {
		return hotDownloadToUserNetTotalAmount;
	}
	public void setHotDownloadToUserNetTotalAmount(BigDecimal hotDownloadToUserNetTotalAmount) {
		this.hotDownloadToUserNetTotalAmount = hotDownloadToUserNetTotalAmount;
	}
	public int getCheckState() {
		return checkState;
	}
	public void setCheckState(int checkState) {
		this.checkState = checkState;
	}
	public int getBlockHeight() {
		return blockHeight;
	}
	public void setBlockHeight(int blockHeight) {
		this.blockHeight = blockHeight;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getFundTypeName() {
		CoinProps coinProps = DatabasesUtil.coinProps(this.fundsType);
		if(coinProps != null){
			return coinProps.getDatabaseKey();
		}else{
			return this.fundsType+"";
		}
	}
	public void setFundTypeName(String fundTypeName) {
		this.fundTypeName = fundTypeName;
	}
	public BigDecimal getDownloadPerAmount() {
		return downloadPerAmount;
	}
	public void setDownloadPerAmount(BigDecimal downloadPerAmount) {
		this.downloadPerAmount = downloadPerAmount;
	}
	public String getStrCheckState() {
		return strCheckState;
	}
	public void setStrCheckState(String strCheckState) {
		this.strCheckState = strCheckState;
	}
	public BigDecimal getAmount1() {
		return amount1;
	}
	public void setAmount1(BigDecimal amount1) {
		this.amount1 = amount1;
	}
	public BigDecimal getFee1() {
		return fee1;
	}
	public void setFee1(BigDecimal fee1) {
		this.fee1 = fee1;
	}
	public BigDecimal getAmount2() {
		return amount2;
	}
	public void setAmount2(BigDecimal amount2) {
		this.amount2 = amount2;
	}
	public BigDecimal getFee2() {
		return fee2;
	}
	public void setFee2(BigDecimal fee2) {
		this.fee2 = fee2;
	}
	public BigDecimal getAmount3() {
		return amount3;
	}
	public void setAmount3(BigDecimal amount3) {
		this.amount3 = amount3;
	}
	public BigDecimal getFee3() {
		return fee3;
	}
	public void setFee3(BigDecimal fee3) {
		this.fee3 = fee3;
	}
	public BigDecimal getAmount4() {
		return amount4;
	}
	public void setAmount4(BigDecimal amount4) {
		this.amount4 = amount4;
	}
	public BigDecimal getFee4() {
		return fee4;
	}
	public void setFee4(BigDecimal fee4) {
		this.fee4 = fee4;
	}
	public int getMaxHeight() {
		return maxHeight;
	}
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}
	public int getMinHeight() {
		return minHeight;
	}
	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}
	public Timestamp getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(Timestamp maxTime) {
		this.maxTime = maxTime;
	}
	public Timestamp getMinTime() {
		return minTime;
	}
	public void setMinTime(Timestamp minTime) {
		this.minTime = minTime;
	}
	public void setAmount5(BigDecimal amount5) {
		this.amount5 = amount5;
	}

	public void setFee5(BigDecimal fee5) {
		this.fee5 = fee5;
	}

	public void setAmount6(BigDecimal amount6) {
		this.amount6 = amount6;
	}

	public void setFee6(BigDecimal fee6) {
		this.fee6 = fee6;
	}

	public void setAmount7(BigDecimal amount7) {
		this.amount7 = amount7;
	}

	public void setFee7(BigDecimal fee7) {
		this.fee7 = fee7;
	}

	public void setAmount8(BigDecimal amount8) {
		this.amount8 = amount8;
	}

	public void setFee8(BigDecimal fee8) {
		this.fee8 = fee8;
	}

	public BigDecimal getAmount5() {
		return amount5;
	}

	public BigDecimal getFee5() {
		return fee5;
	}

	public BigDecimal getAmount6() {
		return amount6;
	}

	public BigDecimal getFee6() {
		return fee6;
	}

	public BigDecimal getAmount7() {
		return amount7;
	}

	public BigDecimal getFee7() {
		return fee7;
	}

	public BigDecimal getAmount8() {
		return amount8;
	}

	public BigDecimal getFee8() {
		return fee8;
	}

	public BigDecimal getSumSameFee37() {
		return sumSameFee37;
	}

	public void setSumSameFee37(BigDecimal sumSameFee37) {
		this.sumSameFee37 = sumSameFee37;
	}

	public BigDecimal getSumSameFee3() {
		return sumSameFee3;
	}

	public void setSumSameFee3(BigDecimal sumSameFee3) {
		this.sumSameFee3 = sumSameFee3;
	}

	public BigDecimal getSumSameFee7() {
		return sumSameFee7;
	}

	public void setSumSameFee7(BigDecimal sumSameFee7) {
		this.sumSameFee7 = sumSameFee7;
	}
}
