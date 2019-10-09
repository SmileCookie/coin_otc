package com.world.model.entity;

import com.world.data.mysql.Bean;

public class Fundsintroduction extends Bean {
    private int fundsType;
    private int type;
    private String descriptEN;
    private String descriptCN;
    private String descriptHK;
    private String descriptKR;
    private String descriptJP;
    private String coinName;

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getDescriptHK() {
        return descriptHK;
    }

    public void setDescriptHK(String descriptHK) {
        this.descriptHK = descriptHK;
    }

    public String getDescriptKR() {
        return descriptKR;
    }

    public void setDescriptKR(String descriptKR) {
        this.descriptKR = descriptKR;
    }

    public String getDescriptJP() {
        return descriptJP;
    }

    public void setDescriptJP(String descriptJP) {
        this.descriptJP = descriptJP;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescriptEN() {
        return descriptEN;
    }

    public void setDescriptEN(String descriptEN) {
        this.descriptEN = descriptEN;
    }

    public String getDescriptCN() {
        return descriptCN;
    }

    public void setDescriptCN(String descriptCN) {
        this.descriptCN = descriptCN;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    private String descript;
}
