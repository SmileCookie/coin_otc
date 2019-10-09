package com.world.model.entity.user;

/**
 * Created by xie on 2017/6/8.
 */

import com.world.data.big.table.TableInfo;
import com.world.data.mysql.Bean;

public class UserAuth extends Bean {


    private String userId;
    private String userName;
    private String idNumber;
    private String name ;
    private int degree;

    private String idPhoto1;
    private String idPhoto2;
    private String idPhoto3;
    private String startDateOfId;
    private String endDateOfId;

    private int status;
    private String addrPhoto;
    private String nation;


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

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public String getIdPhoto1() {
        return idPhoto1;
    }

    public void setIdPhoto1(String idPhoto1) {
        this.idPhoto1 = idPhoto1;
    }

    public String getIdPhoto2() {
        return idPhoto2;
    }

    public void setIdPhoto2(String idPhoto2) {
        this.idPhoto2 = idPhoto2;
    }

    public String getIdPhoto3() {
        return idPhoto3;
    }

    public void setIdPhoto3(String idPhoto3) {
        this.idPhoto3 = idPhoto3;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAddrPhoto() {
        return addrPhoto;
    }

    public void setAddrPhoto(String addrPhoto) {
        this.addrPhoto = addrPhoto;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getStartDateOfId() {
        return startDateOfId;
    }

    public void setStartDateOfId(String startDateOfId) {
        this.startDateOfId = startDateOfId;
    }

    public String getEndDateOfId() {
        return endDateOfId;
    }

    public void setEndDateOfId(String endDateOfId) {
        this.endDateOfId = endDateOfId;
    }
}
