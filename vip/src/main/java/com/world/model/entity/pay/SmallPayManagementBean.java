package com.world.model.entity.pay;

import com.world.data.mysql.Bean;
import org.beetl.sql.core.annotatoin.Table;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Zero on 2019/3/29.
 */

@Table(name = "small_pay_management")
public class SmallPayManagementBean extends Bean {

    public SmallPayManagementBean(Integer id, Integer download, Integer fundstype, String fundstypename) {
        this.id = id;
        this.download = download;
        this.fundstype = fundstype;
        this.fundstypename = fundstypename;
    }

    //主键
    private Integer id ;
    //打币  0：关  1：开
    private Integer download ;
    //币种id
    private Integer fundstype ;
    //币种name
    private String fundstypename ;

    public SmallPayManagementBean() {
    }

    /**主键
     *@return
     */
    public Integer getId(){
        return  id;
    }
    /**主键
     *@param  id
     */
    public void setId(Integer id ){
        this.id = id;
    }

    /**打币  0：关  1：开
     *@return
     */
    public Integer getDownload(){
        return  download;
    }
    /**打币  0：关  1：开
     *@param  download
     */
    public void setDownload(Integer download ){
        this.download = download;
    }

    /**币种id
     *@return
     */
    public Integer getFundstype(){
        return  fundstype;
    }
    /**币种id
     *@param  fundstype
     */
    public void setFundstype(Integer fundstype ){
        this.fundstype = fundstype;
    }

    /**币种name
     *@return
     */
    public String getFundstypename(){
        return  fundstypename;
    }
    /**币种name
     *@param  fundstypename
     */
    public void setFundstypename(String fundstypename ){
        this.fundstypename = fundstypename;
    }


}
