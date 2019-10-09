package com.match.domain;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/11/17 10:42 AM
 */
public enum MatchResultEnum {
    CANCEL(1),
    NO_TRANS(2),
    CAN_TRANS(3),
    UPDATE(4);

    private int type;

    MatchResultEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
