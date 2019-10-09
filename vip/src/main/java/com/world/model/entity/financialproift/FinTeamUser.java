package com.world.model.entity.financialproift;

import java.time.LocalDateTime;

import com.world.data.mysql.Bean;


public class FinTeamUser extends Bean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* id */
	private Long id;
	/* 用户编号 */
	private Long userNo;
	/* 用户名 */
	private String username;
	/* 推荐人名字 */
	private Long recName;
	/* 日期 */
	private LocalDateTime regtime;
	/* 状态 */
	private Integer state;
	/* 节点位置 */
	private String nodePartition;
	/*接点人*/
	private Long nodeName;

	private Integer level;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserNo() {
		return userNo;
	}

	public void setUserNo(Long userNo) {
		this.userNo = userNo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDateTime getRegtime() {
		return regtime;
	}

	public void setRegtime(LocalDateTime regtime) {
		this.regtime = regtime;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getNodePartition() {
		return nodePartition;
	}

	public void setNodePartition(String nodePartition) {
		this.nodePartition = nodePartition;
	}


	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Long getRecName() {
		return recName;
	}

	public void setRecName(Long recName) {
		this.recName = recName;
	}

	public Long getNodeName() {
		return nodeName;
	}

	public void setNodeName(Long nodeName) {
		this.nodeName = nodeName;
	}

//	@Override
//	public int compareTo(FinTeamUser o) {
//		if (userNo > o.userNo){
//			return 1;
//		}else if(userNo < o.userNo){
//			return -1;
//		}
//		return 0;
//	}
}
