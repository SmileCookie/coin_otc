package com.world.data.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectPool {

    private final static Logger log = Logger.getLogger(ConnectPool.class.getName());

    public static Map<String, MongoClient> mongos = new HashMap<String, MongoClient>();


    public synchronized static MongoClient init(String dbName, String ip, String user, String pwd) {
        String[] ips = ip.split(",");

        MongoClient mongo = mongos.get(ip);
        if (mongo == null) {
            try {
                List<ServerAddress> replicaSetSeeds = new ArrayList<ServerAddress>();

                for (String url : ips) {
                    ServerAddress address = null;
                    String[] ips3 = url.split(":");
                    if (ips3.length == 2) {
                        address = new ServerAddress(ips3[0], Integer.parseInt(ips3[1]));
                    } else {
                        address = new ServerAddress(ips3[0]);
                    }
                    replicaSetSeeds.add(address);
                }

                if (StringUtils.isEmpty(user) && StringUtils.isEmpty(pwd)) {
                    mongo = new MongoClient(replicaSetSeeds);
                } else {
                    //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
                    MongoCredential credential = MongoCredential.createScramSha1Credential(user, dbName, pwd.toCharArray());
                    List<MongoCredential> credentials = new ArrayList<MongoCredential>();
                    credentials.add(credential);

                    mongo = new MongoClient(replicaSetSeeds, credentials);
                }

                MongoOptions opt = mongo.getMongoOptions();
                opt.connectionsPerHost = 3000;
//				opt.autoConnectRetry = true;
                mongos.put(ip, mongo);
            } catch (Exception e) {
                log.error("初始化mongo客户端异常，dbName=" + dbName + ",ip=" + ip + ",user=" + user + ",pwd=" + pwd, e);
            }
        }
        return mongo;
    }

    /***
     * 获取mongodb实例  集群多实例 ip以逗号隔开 如：127.0.0.1:1000,127.0.0.1:2000,127.0.0.1:3000
     * @param ip
     * @return
     */
    public static MongoClient getMongo(String dbName, String ip, String user, String pwd) {
        MongoClient mongo = mongos.get(ip);
        if (mongo == null) {
            mongo = init(dbName, ip, user, pwd);
        }
        return mongo;
    }
}