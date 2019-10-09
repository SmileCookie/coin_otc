package com.world.model.financial.entity;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

public class BillFinancial extends Bean {
    private long id ;
    //是否已核算,账户管理每日结算使用,默认值0，核算中1，已核算2
    private Integer finanAccount ;
    //币种类型
    private Integer fundsType ;
    //0等待确认 1失败 2成功 3取消
    private Integer status ;
    //业务类型
    private Integer type ;
    //发生额
    private BigDecimal amount ;
    //操作后余额
    private BigDecimal balance ;
    //业务id
    private Long businessId ;
    //创建时间
    private Long createTime ;
    //手续费
    private BigDecimal fees ;
    //合约
    private String market ;
    //用户资金监控表监控编号
    private String ucmId ;
    //用户id
    private Long userId ;

    private Integer uId;
    //用户名称
    private String userName ;
    //
    private String typeName ;

    private String remark ;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BillFinancial() {
    }

    public BillFinancial(Long userId, String userName,  String typeName, String remark,Integer type, BigDecimal amount, Long date, BigDecimal balance, Integer fundsType, BigDecimal fee) {
        this.userId = userId;
        this.userName = userName;
        this.type = type;
        this.amount = amount;
        this.createTime = date;
        this.balance = balance;
        this.fundsType = fundsType;
        this.fees = fee;
        this.typeName = typeName;
        this.remark = remark;
    }



    public long getId(){
        return  id;
    }
    public void setId(long id ){
        this.id = id;
    }

    /**是否已核算,账户管理每日结算使用,

     默认值0，核算中1，已核算2
     *@return
     */
    public Integer getFinanAccount(){
        return  finanAccount;
    }
    /**是否已核算,账户管理每日结算使用,

     默认值0，核算中1，已核算2
     *@param  finanAccount
     */
    public void setFinanAccount(Integer finanAccount ){
        this.finanAccount = finanAccount;
    }

    public Integer getuId() {
        return uId;
    }

    public void setuId(Integer uId) {
        this.uId = uId;
    }

    /**币种类型
     *@return
     */
    public Integer getFundsType(){
        return  fundsType;
    }
    /**币种类型
     *@param  fundsType
     */
    public void setFundsType(Integer fundsType ){
        this.fundsType = fundsType;
    }

    /**0等待确认 1失败 2成功 3取消
     *@return
     */
    public Integer getStatus(){
        return  status;
    }
    /**0等待确认 1失败 2成功 3取消
     *@param  status
     */
    public void setStatus(Integer status ){
        this.status = status;
    }

    /**业务类型
     *@return
     */
    public Integer getType(){
        return  type;
    }
    /**业务类型
     *@param  type
     */
    public void setType(Integer type ){
        this.type = type;
    }

    /**发生额
     *@return
     */
    public BigDecimal getAmount(){
        return  amount;
    }
    /**发生额
     *@param  amount
     */
    public void setAmount(BigDecimal amount ){
        this.amount = amount;
    }

    /**操作后余额
     *@return
     */
    public BigDecimal getBalance(){
        return  balance;
    }
    /**操作后余额
     *@param  balance
     */
    public void setBalance(BigDecimal balance ){
        this.balance = balance;
    }

    /**业务id
     *@return
     */
    public Long getBusinessId(){
        return  businessId;
    }
    /**业务id
     *@param  businessId
     */
    public void setBusinessId(Long businessId ){
        this.businessId = businessId;
    }

    /**创建时间
     *@return
     */
    public Long getCreateTime(){
        return  createTime;
    }
    /**创建时间
     *@param  createTime
     */
    public void setCreateTime(Long createTime ){
        this.createTime = createTime;
    }

    /**手续费
     *@return
     */
    public BigDecimal getFees(){
        return  fees;
    }
    /**手续费
     *@param  fees
     */
    public void setFees(BigDecimal fees ){
        this.fees = fees;
    }

    /**合约
     *@return
     */
    public String getMarket(){
        return  market;
    }
    /**合约
     *@param  market
     */
    public void setMarket(String market ){
        this.market = market;
    }

    /**用户资金监控表监控编号
     *@return
     */
    public String getUcmId(){
        return  ucmId;
    }
    /**用户资金监控表监控编号
     *@param  ucmId
     */
    public void setUcmId(String ucmId ){
        this.ucmId = ucmId;
    }

    /**用户id
     *@return
     */
    public Long getUserId(){
        return  userId;
    }
    /**用户id
     *@param  userId
     */
    public void setUserId(Long userId ){
        this.userId = userId;
    }

    /**用户名称
     *@return
     */
    public String getUserName(){
        return  userName;
    }
    /**用户名称
     *@param  userName
     */
    public void setUserName(String userName ){
        this.userName = userName;
    }
}
