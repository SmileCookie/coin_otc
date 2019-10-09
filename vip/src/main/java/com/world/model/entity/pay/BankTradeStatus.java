package com.world.model.entity.pay;

public enum BankTradeStatus {
    SUCCESS(0, "成功"),
    FAIL(1, "失败"),
    DEALING(2, "处理中"),
    WithdrawDealing(3, "提现中，等待处理"),
    WithdrawSuc(4, "提现成功"),
    WithdrawFail(5, "提现失败"),
    WaitConfirm(6, "等待确认"),
    HasCancel(8, "已取消"),
    WaitRecharge(9, "等待汇款/确认"),
    NEEDAUTH(10, "等待实名认证"),
    WaitRefund(11, "待退款"),
    HasRefund(12, "已退款"),
    Expired(13, "已退款"),
    BEFORE_CONFRIM_WITHDRAW(14, "确认提交到商户平台前锁定"),
    BEFORE_CANCEL_WITHDRAW(15, "取消提交到商户平台前锁定");

    private BankTradeStatus(int id, String value) {
        this.id = id;
        this.value = value;
    }

    private int id;
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
