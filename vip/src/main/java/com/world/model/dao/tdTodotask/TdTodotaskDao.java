package com.world.model.dao.tdTodotask;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.level.TdTodotask;

import java.sql.Timestamp;
import java.util.Date;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2018/1/27上午11:03
 */
public class TdTodotaskDao extends DataDaoSupport<TdTodotask> {

    /**
     * 新增代办任务
     *
     * @param busid
     * @param taskId
     * @param todoName
     * @param todoNodeName
     * @param userId
     * @param userName
     * @param url
     * @return
     */
    public int addTdTodotask(String busid, int taskId, String todoName, String todoNodeName, String userId, String userName, String url){
        String sql = "insert into `vip_main`.`td_todotask` ( `busId`, `taskId`, `toDoName`, `toDoNodeName`, `userId`, `userName`, `InitiatorId`, `InitiatorName`, `toDoStartTime`, `toDoNodeStartTime`, `todoPlanCompTime`, `todoRealCompTime`, `toDoState`, `planOperId`, `planOperName`, `realOperId`, `realOperName`, `url`) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] param  = saveTdTodotask(busid,taskId,todoName,todoNodeName,userId,userName,url);
        int state = Data.Insert(sql,param);
        return state;
    }

    /**
     * 获取最大单号
     *
     * @param busIdLike
     * @return
     */
    public String getMaxBusId(String busIdLike){
        String maxBusId = "";
        String sql = "select IFNULL(max(busid),'') maxBusId from td_todotask  where  busid like concat('%',?,'%')";
        TdTodotask tdTodotask = (TdTodotask) Data.GetOne(sql,new Object[] { busIdLike },TdTodotask.class);
        maxBusId = tdTodotask.getMaxBusId();
        return maxBusId;
    }

    /**
     * 新增代办任务
     *
     * @param busid
     * @param taskId
     * @param todoName
     * @param todoNodeName
     * @param userId
     * @param userName
     * @param url
     * @return
     */
    public Object[] saveTdTodotask(String busid, int taskId, String todoName, String todoNodeName, String userId, String userName, String url){
        Date date = new Date();
        Timestamp timeStamp = new Timestamp(date.getTime());
        TdTodotask tdTodotask = new TdTodotask();
        //单号
        tdTodotask.setBusid(busid);
        //外键
        tdTodotask.setTaskid(taskId);
        //代办事项名称
        tdTodotask.setTodoname(todoName);
        //事项节点
        tdTodotask.setTodonodename(todoNodeName);
        //用户ID
        tdTodotask.setUserid(userId);
        //用户名
        tdTodotask.setUsername(userName);
        //发起人ID
        tdTodotask.setInitiatorid("admin");
        //发起人名称
        tdTodotask.setInitiatorname("admin");
        //事项发起时
        tdTodotask.setTodostarttime(date);
        //节点开始时间
        tdTodotask.setTodonodestarttime(date);
        //事项计划完成时间
        tdTodotask.setTodoplancomptime(date);
        //事项实际完成时间
        tdTodotask.setTodorealcomptime(date);
        //事项状态 01-代办（可领办），02-办理中（办理），03-办理完成'
        tdTodotask.setTodostate(1);
        tdTodotask.setPlanoperid("");
        tdTodotask.setPlanopername("");
        tdTodotask.setRealoperid("");
        tdTodotask.setRealopername("");
        //跳转路径
        tdTodotask.setUrl(url);
        Object[] param = new Object[] {busid,taskId,todoName,todoNodeName,userId,userName,"admin","admin",timeStamp,timeStamp,timeStamp,timeStamp,1,"","","","",url};
        return param;
    }
}
