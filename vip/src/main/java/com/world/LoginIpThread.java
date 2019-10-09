package com.world;


import com.world.model.dao.user.UserLoginIpDao;
import com.world.model.entity.user.UserLoginIp;
import com.world.util.date.TimeUtil;
import com.world.util.ip.IpUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Elysion
 * @Description:
 * @date 2018/7/19下午7:53
 * @deprecated 统一使用MQ队列实现 2019-03-09 Jack
 */
public class LoginIpThread extends Thread{

    private static Logger log = Logger.getLogger(LoginIpThread.class.getName());

    private String loginName;
    private String userId;
    private String userName;
    private String newIp;
    private int terminal;
    private String version;
    private Timestamp now;
    UserLoginIpDao userLoginIpDao = new UserLoginIpDao();
    public LoginIpThread(String loginName, String userId, String userName, String newIp, int terminal, String version, Timestamp now){
        this.loginName = loginName;
        this.userId = userId;
        this.userName = userName;
        this.newIp = newIp;
        this.terminal = terminal;
        this.version = version;
        this.now = now;

    }
    @Override
    public void run() {
        List<UserLoginIp> lists = getIps(userId, 1, 50);
        String removeId = "";
        if(lists.size() >= 50){
            removeId = lists.get(lists.size() - 1).getId();
            userLoginIpDao.delById(removeId);
        }
        String loginTerminal = StringUtils.isBlank(version)?"网页":version;
        UserLoginIp uli = new UserLoginIp(userLoginIpDao.getDatastore());
        uli.setUserId(userId);
        uli.setDate(now);
        uli.setIp(newIp);
        uli.setTerminal(terminal);
        uli.setDescribe(loginName+"：登录账号【"+userName+"】，登录方式【"+loginTerminal+"】");
        try {
            String city = IpUtil.getCity(newIp);
            if(newIp.equals("127.0.0.1")){
                city = "本机";
            }

            if(city != null){
                uli.setCity(city);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        userLoginIpDao.save(uli);
    }







    public List<UserLoginIp> getIps(String userId, int pageIndex, int pageSize){
        List<UserLoginIp> lists = userLoginIpDao.findPage(userLoginIpDao.getQuery().filter("userId =", userId).order("-date"), pageIndex, pageSize);
        return lists;
    }





}
