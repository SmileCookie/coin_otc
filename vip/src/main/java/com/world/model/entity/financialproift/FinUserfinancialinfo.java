package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Ethan
 * @Date 2019-07-26 14:54
 * @Description
 **/

public class FinUserfinancialinfo extends Bean {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id ;
    //0默认值，1已认证，2已支付
    private Integer authpayflag ;
    //邀请总人数
    private Integer invitationtotalnum ;
    //投资金额对应的矩阵
    private Integer matrixlevel ;
    //用户ID
    private Integer userid ;
    //我的邀请码
    private String invitationcode ;
    //邀请人用户名
    private String invitationusername ;
    //推进人邀请码
    private String pinvitationcode ;
    //用户名
    private String username ;
    //用户类型
    private String usertype ;
    //用户VID
    private String uservid ;
    //VIP增值权重
    private BigDecimal vipweight ;
    //邀请人数更新时间
    private Date modifytime ;
    //投资时间
    private Date profittime ;

    public FinUserfinancialinfo() {
    }

    public Integer getId(){
        return  id;
    }
    public void setId(Integer id ){
        this.id = id;
    }

    /**0默认值，1已认证，2已支付
     *@return
     */
    public Integer getAuthpayflag(){
        return  authpayflag;
    }
    /**0默认值，1已认证，2已支付
     *@param  authpayflag
     */
    public void setAuthpayflag(Integer authpayflag ){
        this.authpayflag = authpayflag;
    }

    /**邀请总人数
     *@return
     */
    public Integer getInvitationtotalnum(){
        return  invitationtotalnum;
    }
    /**邀请总人数
     *@param  invitationtotalnum
     */
    public void setInvitationtotalnum(Integer invitationtotalnum ){
        this.invitationtotalnum = invitationtotalnum;
    }

    /**投资金额对应的矩阵
     *@return
     */
    public Integer getMatrixlevel(){
        return  matrixlevel;
    }
    /**投资金额对应的矩阵
     *@param  matrixlevel
     */
    public void setMatrixlevel(Integer matrixlevel ){
        this.matrixlevel = matrixlevel;
    }

    /**用户ID
     *@return
     */
    public Integer getUserid(){
        return  userid;
    }
    /**用户ID
     *@param  userid
     */
    public void setUserid(Integer userid ){
        this.userid = userid;
    }

    /**我的邀请码
     *@return
     */
    public String getInvitationcode(){
        return  invitationcode;
    }
    /**我的邀请码
     *@param  invitationcode
     */
    public void setInvitationcode(String invitationcode ){
        this.invitationcode = invitationcode;
    }

    /**邀请人用户名
     *@return
     */
    public String getInvitationusername(){
        return  invitationusername;
    }
    /**邀请人用户名
     *@param  invitationusername
     */
    public void setInvitationusername(String invitationusername ){
        this.invitationusername = invitationusername;
    }

    /**推进人邀请码
     *@return
     */
    public String getPinvitationcode(){
        return  pinvitationcode;
    }
    /**推进人邀请码
     *@param  pinvitationcode
     */
    public void setPinvitationcode(String pinvitationcode ){
        this.pinvitationcode = pinvitationcode;
    }

    /**用户名
     *@return
     */
    public String getUsername(){
        return  username;
    }
    /**用户名
     *@param  username
     */
    public void setUsername(String username ){
        this.username = username;
    }

    /**用户类型
     *@return
     */
    public String getUsertype(){
        return  usertype;
    }
    /**用户类型
     *@param  usertype
     */
    public void setUsertype(String usertype ){
        this.usertype = usertype;
    }

    /**用户VID
     *@return
     */
    public String getUservid(){
        return  uservid;
    }
    /**用户VID
     *@param  uservid
     */
    public void setUservid(String uservid ){
        this.uservid = uservid;
    }

    /**VIP增值权重
     *@return
     */
    public BigDecimal getVipweight(){
        return  vipweight;
    }
    /**VIP增值权重
     *@param  vipweight
     */
    public void setVipweight(BigDecimal vipweight ){
        this.vipweight = vipweight;
    }

    /**邀请人数更新时间
     *@return
     */
    public Date getModifytime(){
        return  modifytime;
    }
    /**邀请人数更新时间
     *@param  modifytime
     */
    public void setModifytime(Date modifytime ){
        this.modifytime = modifytime;
    }

    /**投资时间
     *@return
     */
    public Date getProfittime(){
        return  profittime;
    }
    /**投资时间
     *@param  profittime
     */
    public void setProfittime(Date profittime ){
        this.profittime = profittime;
    }
}
