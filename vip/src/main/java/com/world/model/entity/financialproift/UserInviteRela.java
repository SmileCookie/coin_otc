package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

public class UserInviteRela extends Bean {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;

    private long user_id;

    private long hierarchyLevel;

    private long hierarchyTotalNum;

    private long userActiveNum;

    private long userEmptyNum;

    private long userNoActiveNum;


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

    public long getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(long hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public long getHierarchyTotalNum() {
        return hierarchyTotalNum;
    }

    public void setHierarchyTotalNum(long hierarchyTotalNum) {
        this.hierarchyTotalNum = hierarchyTotalNum;
    }

    public long getUserActiveNum() {
        return userActiveNum;
    }

    public void setUserActiveNum(long userActiveNum) {
        this.userActiveNum = userActiveNum;
    }

    public long getUserEmptyNum() {
        return userEmptyNum;
    }

    public void setUserEmptyNum(long userEmptyNum) {
        this.userEmptyNum = userEmptyNum;
    }

    public long getUserNoActiveNum() {
        return userNoActiveNum;
    }

    public void setUserNoActiveNum(long userNoActiveNum) {
        this.userNoActiveNum = userNoActiveNum;
    }

}
