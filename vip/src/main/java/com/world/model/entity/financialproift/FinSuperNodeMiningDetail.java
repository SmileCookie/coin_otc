package com.world.model.entity.financialproift;

import java.math.BigDecimal;

import com.world.data.mysql.Bean;

public class FinSuperNodeMiningDetail extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*主键*/
	private int id;
	/*超级节点表主键*/
	private int sNodeId;
	/*超级节点编号*/
	private String sNodeName;
	/*超级节点地址*/
	private String sNodeAddr;
	/*控矿收益*/
	private BigDecimal miningAmount;
	private int type;
	private BigDecimal profit;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getsNodeId() {
		return sNodeId;
	}
	public void setsNodeId(int sNodeId) {
		this.sNodeId = sNodeId;
	}
	public String getsNodeName() {
		return sNodeName;
	}
	public void setsNodeName(String sNodeName) {
		this.sNodeName = sNodeName;
	}
	public String getsNodeAddr() {
		return sNodeAddr;
	}
	public void setsNodeAddr(String sNodeAddr) {
		this.sNodeAddr = sNodeAddr;
	}
	public BigDecimal getMiningAmount() {
		return miningAmount;
	}
	public void setMiningAmount(BigDecimal miningAmount) {
		this.miningAmount = miningAmount;
	}
	
//			`id`  int(11) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
//			`sNodeId`  int(11) NOT NULL COMMENT '超级节点主键' ,
//			`sNodeName`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '超级节点编号' ,
//			`sNodeAddr`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '超级节点地址' ,
//			`miningAmount`  decimal(25,9) NULL DEFAULT 0.000000000 COMMENT '控矿收益' ,
//			`createTime`  datetime NULL DEFAULT NULL COMMENT '区块时间' ,
//			`height`  bigint(11) NULL DEFAULT 0 COMMENT '区块高度' ,
//			`tid`  bigint(11) NULL DEFAULT 0 COMMENT 'txid' ,
//			`tx_hash`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'tx_hash' ,
//			`sNodeState`  int(2) NULL DEFAULT 0 COMMENT '状态：1正常，0停用' ,
//			`sNodeType`  int(2) NULL DEFAULT 1 COMMENT '1自建，2新增' ,
//			`sNodeBelType`  int(2) NULL DEFAULT 0 COMMENT '归属类型' ,
//			`sNodeShowFlag`  int(2) NULL DEFAULT 0 COMMENT '页面显示标志,统计收益时使用' ,
//			`dealFlag`  int(2) NULL DEFAULT 0 COMMENT '是否已分配 0未分配1已分配' ,
//			`profitBatchNo`  bigint(13) NULL DEFAULT 0 COMMENT '分配批次' ,
	

}
