package com.yc.mongo.common;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.yc.mongo.entity.MongoConnectInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/******
 * ���ӳز���
 * @author Administrator
 *
 */
public class ConnnectPool {
    private static Logger log = Logger.getLogger(ConnnectPool.class.getName());
    private static MongoClient mongo = null;

    public synchronized static void init() {
        if (mongo == null) {
            try {
                MongoClientOptions.Builder builder = MongoClientOptions.builder();
                builder.socketKeepAlive(true);
                builder.connectionsPerHost(1000);
                builder.maxWaitTime(5000);
                builder.socketTimeout(0);
                builder.connectTimeout(15000);
                builder.threadsAllowedToBlockForConnectionMultiplier(5000);

                ServerAddress address = null;
                String[] ips3 = MongoConnectInfo.url.split(":");
                if (ips3.length == 2) {
                    address = new ServerAddress(ips3[0], Integer.parseInt(ips3[1]));
                } else {
                    address = new ServerAddress(ips3[0]);
                }

                if (StringUtils.isEmpty(MongoConnectInfo.userName) && StringUtils.isEmpty(MongoConnectInfo.password)) {
                    mongo = new MongoClient(address, builder.build());
                } else {
                    //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
                    MongoCredential credential = MongoCredential.createScramSha1Credential(MongoConnectInfo.userName, MongoConnectInfo.dbName, MongoConnectInfo.password.toCharArray());
                    List<MongoCredential> credentials = new ArrayList<MongoCredential>();
                    credentials.add(credential);

                    mongo = new MongoClient(address, credentials, builder.build());
                }
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
    }

    /*****
     * ��ȡ��ǰ��Ŀ��mongo����
     * @return
     */
    public static Mongo getMongo() {
        if (mongo == null) {
            init();
        }
        return mongo;
    }
}
