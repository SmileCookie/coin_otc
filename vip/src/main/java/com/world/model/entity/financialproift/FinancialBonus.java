package com.world.model.entity.financialproift;

import java.math.BigDecimal;
import java.util.Date;

import com.world.data.mysql.Bean;

public class FinancialBonus extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**/
	private long id;
	
	private long user_id;
	
	private int bonus_type;

	private String bonus_type_name;
	
	private BigDecimal bonus_price;
	
	private int flag;
	
	private BigDecimal vds_price;
	
	private BigDecimal true_price;

	private Date bonus_time;
	
	private int dealflag;

	private String dealflagName;

	private long from_user_id;

	private String remark;

	private int bonus_floor;

	private BigDecimal bonus_ratio;

	private BigDecimal bonus_profit_amount;

	private int bonus_user_level;

	private String up_level;

	private String from_user_name;

	private BigDecimal sum_bonus_usdt_amount;

	private BigDecimal sum_bonus_vds_amount	;

	private BigDecimal bonus_amount_level9;

	private int superNodeProfitCount;

	private String superNodeProfitCountStr;

	private BigDecimal superNodeProfitVipWeight;

	private int newVipWeekUser;

	private BigDecimal newVipWeekAmount;

	public String getSuperNodeProfitCountStr() {
		return superNodeProfitCountStr;
	}

	public void setSuperNodeProfitCountStr(String superNodeProfitCountStr) {
		this.superNodeProfitCountStr = superNodeProfitCountStr;
	}

	public BigDecimal getSuperNodeProfitVipWeight() {
		return superNodeProfitVipWeight;
	}

	public void setSuperNodeProfitVipWeight(BigDecimal superNodeProfitVipWeight) {
		this.superNodeProfitVipWeight = superNodeProfitVipWeight;
	}

	public int getSuperNodeProfitCount() {
		return superNodeProfitCount;
	}

	public void setSuperNodeProfitCount(int superNodeProfitCount) {
		this.superNodeProfitCount = superNodeProfitCount;
	}

	private Date distStartTime;

	private Date distEndTime;

	public Date getDistStartTime() {
		return distStartTime;
	}

	public void setDistStartTime(Date distStartTime) {
		this.distStartTime = distStartTime;
	}

	public Date getDistEndTime() {
		return distEndTime;
	}

	public void setDistEndTime(Date distEndTime) {
		this.distEndTime = distEndTime;
	}

	public int getNewVipWeekUser() {
		return newVipWeekUser;
	}

	public void setNewVipWeekUser(int newVipWeekUser) {
		this.newVipWeekUser = newVipWeekUser;
	}

	public BigDecimal getNewVipWeekAmount() {
		return newVipWeekAmount;
	}

	public void setNewVipWeekAmount(BigDecimal newVipWeekAmount) {
		this.newVipWeekAmount = newVipWeekAmount;
	}

	public BigDecimal getBonus_amount_level9() {
		return bonus_amount_level9;
	}

	public void setBonus_amount_level9(BigDecimal bonus_amount_level9) {
		this.bonus_amount_level9 = bonus_amount_level9;
	}

	public String getFrom_user_name() {
		return from_user_name;
	}

	public BigDecimal getSum_bonus_vds_amount() {
		return sum_bonus_vds_amount;
	}

	public void setSum_bonus_vds_amount(BigDecimal sum_bonus_vds_amount) {
		this.sum_bonus_vds_amount = sum_bonus_vds_amount;
	}

	public BigDecimal getSum_bonus_usdt_amount() {
		return sum_bonus_usdt_amount;
	}

	public void setSum_bonus_usdt_amount(BigDecimal sum_bonus_usdt_amount) {
		this.sum_bonus_usdt_amount = sum_bonus_usdt_amount;
	}

	public void setFrom_user_name(String from_user_name) {
		this.from_user_name = from_user_name;
	}

	public String getUp_level() {
		return up_level;
	}

	public void setUp_level(String up_level) {
		this.up_level = up_level;
	}

	public int getBonus_floor() {
		return bonus_floor;
	}

	public void setBonus_floor(int bonus_floor) {
		this.bonus_floor = bonus_floor;
	}

	public BigDecimal getBonus_ratio() {
		return bonus_ratio;
	}

	public void setBonus_ratio(BigDecimal bonus_ratio) {
		this.bonus_ratio = bonus_ratio;
	}

	public BigDecimal getBonus_profit_amount() {
		return bonus_profit_amount;
	}

	public void setBonus_profit_amount(BigDecimal bonus_profit_amount) {
		this.bonus_profit_amount = bonus_profit_amount;
	}

	public int getBonus_user_level() {
		return bonus_user_level;
	}

	public void setBonus_user_level(int bonus_user_level) {
		this.bonus_user_level = bonus_user_level;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBonus_type_name() {
		return bonus_type_name;
	}

	public void setBonus_type_name(String bonus_type_name) {
		this.bonus_type_name = bonus_type_name;
	}

	public Date getBonus_time() {
		return bonus_time;
	}

	public void setBonus_time(Date bonus_time) {
		this.bonus_time = bonus_time;
	}

	private int matrix_level;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public int getBonus_type() {
		return bonus_type;
	}

	public void setBonus_type(int bonus_type) {
		this.bonus_type = bonus_type;
	}

	public BigDecimal getBonus_price() {
		return bonus_price;
	}

	public void setBonus_price(BigDecimal bonus_price) {
		this.bonus_price = bonus_price;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public BigDecimal getVds_price() {
		return vds_price;
	}

	public void setVds_price(BigDecimal vds_price) {
		this.vds_price = vds_price;
	}

	public String getDealflagName() {
		return dealflagName;
	}

	public void setDealflagName(String dealflagName) {
		this.dealflagName = dealflagName;
	}

	public BigDecimal getTrue_price() {
		return true_price;
	}

	public void setTrue_price(BigDecimal true_price) {
		this.true_price = true_price;
	}

	public int getDealflag() {
		return dealflag;
	}

	public void setDealflag(int dealflag) {
		this.dealflag = dealflag;
	}

	public long getFrom_user_id() {
		return from_user_id;
	}

	public void setFrom_user_id(long from_user_id) {
		this.from_user_id = from_user_id;
	}

	public int getMatrix_level() {
		return matrix_level;
	}

	public void setMatrix_level(int matrix_level) {
		this.matrix_level = matrix_level;
	}
}
