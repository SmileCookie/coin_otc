package com.world.model.entity.level;

import com.world.data.mysql.Bean;

import java.util.Date;

/**
 * <p>@Description:代办任务表 </p>
 *
 * @author guankaili
 * @date 2018/1/24上午10:32
 */
public class TdTodotask extends Bean{

    /**
     * 主键
     */
    private Integer id ;
    /**
     * 外键，审核事项表的业务主键例如身份认证审核表
     */
    private Integer taskid ;
    /**
     * 事项状态 01-代办（可领办），02-办理中（办理），03-办理完成
     */
    private Integer todostate ;
    /**
     * 发起人Id 操作员或系统自动生成,暂时都为admin
     */
    private String initiatorid ;
    /**
     * 发起人名称 暂时都为admin
     */
    private String initiatorname ;
    /**
     * 业务主键 代办事项
     */
    private String busid ;
    /**
     * 领办人Id 运营操作员
     */
    private String planoperid ;
    /**
     * 领办人名称
     */
    private String planopername ;
    /**
     * 实际办理人Id
     */
    private String realoperid ;
    /**
     * 实际办理人名称
     */
    private String realopername ;
    /**
     * 代办事项名称
     */
    private String todoname ;
    /**
     * 事项节点 1个事项可能有多个节点步骤
     */
    private String todonodename ;
    /**
     * 用户Id
     */
    private String userid ;
    /**
     * 用户名
     */
    private String username ;
    /**
     * 节点开始时间
     */
    private Date todonodestarttime ;
    /**
     * 事项发起时间
     */
    private Date todostarttime ;
    /**
     * 事项计划完成时间
     */
    private Date todoplancomptime ;
    /**
     * 事项实际完成时间
     */
    private Date todorealcomptime ;
    /**
     * 跳转路径
     */
    private String url ;
    /**
     * 最大单号
     */
    private String maxBusId;

    public TdTodotask() {
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

    /**外键，审核事项表的业务主键例如身份认证审核表
     *@return
     */
    public Integer getTaskid(){
        return  taskid;
    }
    /**外键，审核事项表的业务主键例如身份认证审核表
     *@param  taskid
     */
    public void setTaskid(Integer taskid ){
        this.taskid = taskid;
    }

    /**事项状态 01-代办（可领办），02-办理中（办理），03-办理完成
     *@return
     */
    public Integer getTodostate(){
        return  todostate;
    }
    /**事项状态 01-代办（可领办），02-办理中（办理），03-办理完成
     *@param  todostate
     */
    public void setTodostate(Integer todostate ){
        this.todostate = todostate;
    }

    /**发起人Id 操作员或系统自动生成,暂时都为admin
     *@return
     */
    public String getInitiatorid(){
        return  initiatorid;
    }
    /**发起人Id 操作员或系统自动生成,暂时都为admin
     *@param  initiatorid
     */
    public void setInitiatorid(String initiatorid ){
        this.initiatorid = initiatorid;
    }

    /**发起人名称 暂时都为admin
     *@return
     */
    public String getInitiatorname(){
        return  initiatorname;
    }
    /**发起人名称 暂时都为admin
     *@param  initiatorname
     */
    public void setInitiatorname(String initiatorname ){
        this.initiatorname = initiatorname;
    }

    /**业务主键 代办事项
     *@return
     */
    public String getBusid(){
        return  busid;
    }
    /**业务主键 代办事项
     *@param  busid
     */
    public void setBusid(String busid ){
        this.busid = busid;
    }

    /**领办人Id 运营操作员
     *@return
     */
    public String getPlanoperid(){
        return  planoperid;
    }
    /**领办人Id 运营操作员
     *@param  planoperid
     */
    public void setPlanoperid(String planoperid ){
        this.planoperid = planoperid;
    }

    /**领办人名称
     *@return
     */
    public String getPlanopername(){
        return  planopername;
    }
    /**领办人名称
     *@param  planopername
     */
    public void setPlanopername(String planopername ){
        this.planopername = planopername;
    }

    /**实际办理人Id
     *@return
     */
    public String getRealoperid(){
        return  realoperid;
    }
    /**实际办理人Id
     *@param  realoperid
     */
    public void setRealoperid(String realoperid ){
        this.realoperid = realoperid;
    }

    /**实际办理人名称
     *@return
     */
    public String getRealopername(){
        return  realopername;
    }
    /**实际办理人名称
     *@param  realopername
     */
    public void setRealopername(String realopername ){
        this.realopername = realopername;
    }

    /**代办事项名称
     *@return
     */
    public String getTodoname(){
        return  todoname;
    }
    /**代办事项名称
     *@param  todoname
     */
    public void setTodoname(String todoname ){
        this.todoname = todoname;
    }

    /**事项节点 1个事项可能有多个节点步骤
     *@return
     */
    public String getTodonodename(){
        return  todonodename;
    }
    /**事项节点 1个事项可能有多个节点步骤
     *@param  todonodename
     */
    public void setTodonodename(String todonodename ){
        this.todonodename = todonodename;
    }

    /**用户Id
     *@return
     */
    public String getUserid(){
        return  userid;
    }
    /**用户Id
     *@param  userid
     */
    public void setUserid(String userid ){
        this.userid = userid;
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

    /**节点开始时间
     *@return
     */
    public Date getTodonodestarttime(){
        return  todonodestarttime;
    }
    /**节点开始时间
     *@param  todonodestarttime
     */
    public void setTodonodestarttime(Date todonodestarttime ){
        this.todonodestarttime = todonodestarttime;
    }

    /**事项发起时间
     *@return
     */
    public Date getTodostarttime(){
        return  todostarttime;
    }
    /**事项发起时间
     *@param  todostarttime
     */
    public void setTodostarttime(Date todostarttime ){
        this.todostarttime = todostarttime;
    }

    /**事项计划完成时间
     *@return
     */
    public Date getTodoplancomptime(){
        return  todoplancomptime;
    }
    /**事项计划完成时间
     *@param  todoplancomptime
     */
    public void setTodoplancomptime(Date todoplancomptime ){
        this.todoplancomptime = todoplancomptime;
    }

    /**事项实际完成时间
     *@return
     */
    public Date getTodorealcomptime(){
        return  todorealcomptime;
    }
    /**事项实际完成时间
     *@param  todorealcomptime
     */
    public void setTodorealcomptime(Date todorealcomptime ){
        this.todorealcomptime = todorealcomptime;
    }
    /**跳转路径
     *@return
     */
    public String getUrl(){
        return  url;
    }
    /**跳转路径
     *@param  url
     */
    public void setUrl(String url ){
        this.url = url;
    }

    public String getMaxBusId() {
        return maxBusId;
    }

    public void setMaxBusId(String maxBusId) {
        this.maxBusId = maxBusId;
    }
}
