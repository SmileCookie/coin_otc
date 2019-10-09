package com.world.model.entity.vote;

import java.util.List;

public class ActivityTicketVo {
     private int state;
     private List<String> userIdList;


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }
}
