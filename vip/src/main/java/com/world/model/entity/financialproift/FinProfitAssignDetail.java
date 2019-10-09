package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Ethan
 * @Date 2019-08-01 14:48
 * @Description
 **/

public class FinProfitAssignDetail extends Bean {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//主键
    private Integer id;
    //vds生态分配人id
    private Integer assignuserid;
    //执行标识 1：执行完成 0未执行
    private Integer flag;
    //收益来源id
    private Integer parentid;
    //获益人用户id
    private Integer profituserid;
    //vds生态分配人vid
    private String assignvid;
    //vds生态投资金额
    private BigDecimal investamount;
    //vds数量
    private BigDecimal profitamount;
    //收益类型 1层级 2指导 3晋级 4领导 5超级节点 6生态 7新增用户
    private Integer profittype;
    //vds生态获益人vid
    private String profitvid;
    //vds折算usdt数量
    private BigDecimal usdtamount;
    //当时usdt汇率价格
    private BigDecimal usdtprice;
    //创建时间
    private Date createtime;
    //受益人名字
    private String profitusername;

    public String getProfitusername() {
        return profitusername;
    }

    public void setProfitusername(String profitusername) {
        this.profitusername = profitusername;
    }

    //结算来源
    private String assignIds;

    public FinProfitAssignDetail() {
    }

    public String getAssignIds() {
        return assignIds;
    }

    public void setAssignIds(String assignIds) {
        this.assignIds = assignIds;
    }

    /**
     * 主键
     *
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键
     *
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * vds生态分配人id
     *
     * @return
     */
    public Integer getAssignuserid() {
        return assignuserid;
    }

    /**
     * vds生态分配人id
     *
     * @param assignuserid
     */
    public void setAssignuserid(Integer assignuserid) {
        this.assignuserid = assignuserid;
    }

    /**
     * 执行标识 1：执行完成 0未执行
     *
     * @return
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * 执行标识 1：执行完成 0未执行
     *
     * @param flag
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * 收益来源id
     *
     * @return
     */
    public Integer getParentid() {
        return parentid;
    }

    /**
     * 收益来源id
     *
     * @param parentid
     */
    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    /**
     * 获益人用户id
     *
     * @return
     */
    public Integer getProfituserid() {
        return profituserid;
    }

    /**
     * 获益人用户id
     *
     * @param profituserid
     */
    public void setProfituserid(Integer profituserid) {
        this.profituserid = profituserid;
    }

    /**
     * vds生态分配人vid
     *
     * @return
     */
    public String getAssignvid() {
        return assignvid;
    }

    /**
     * vds生态分配人vid
     *
     * @param assignvid
     */
    public void setAssignvid(String assignvid) {
        this.assignvid = assignvid;
    }

    /**
     * vds生态投资金额
     *
     * @return
     */
    public BigDecimal getInvestamount() {
        return investamount;
    }

    /**
     * vds生态投资金额
     *
     * @param investamount
     */
    public void setInvestamount(BigDecimal investamount) {
        this.investamount = investamount;
    }

    /**
     * vds数量
     *
     * @return
     */
    public BigDecimal getProfitamount() {
        return profitamount;
    }

    /**
     * vds数量
     *
     * @param profitamount
     */
    public void setProfitamount(BigDecimal profitamount) {
        this.profitamount = profitamount;
    }

    /**
     * 收益类型
     *
     * @return
     */
    public Integer getProfittype() {
        return profittype;
    }

    /**
     * 收益类型
     *
     * @param profittype
     */
    public void setProfittype(Integer profittype) {
        this.profittype = profittype;
    }

    /**
     * vds生态获益人vid
     *
     * @return
     */
    public String getProfitvid() {
        return profitvid;
    }

    /**
     * vds生态获益人vid
     *
     * @param profitvid
     */
    public void setProfitvid(String profitvid) {
        this.profitvid = profitvid;
    }

    /**
     * vds折算usdt数量
     *
     * @return
     */
    public BigDecimal getUsdtamount() {
        return usdtamount;
    }

    /**
     * vds折算usdt数量
     *
     * @param usdtamount
     */
    public void setUsdtamount(BigDecimal usdtamount) {
        this.usdtamount = usdtamount;
    }

    /**
     * 当时usdt汇率价格
     *
     * @return
     */
    public BigDecimal getUsdtprice() {
        return usdtprice;
    }

    /**
     * 当时usdt汇率价格
     *
     * @param usdtprice
     */
    public void setUsdtprice(BigDecimal usdtprice) {
        this.usdtprice = usdtprice;
    }

    /**
     * 创建时间
     *
     * @return
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * 创建时间
     *
     * @param createtime
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}
