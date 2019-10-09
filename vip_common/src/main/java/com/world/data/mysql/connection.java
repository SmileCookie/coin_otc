package com.world.data.mysql;

import cn.hutool.db.ds.hikari.HikariDSFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.config.GlobalConfig;
import com.world.config.json.DbConfig;
import com.world.data.mysql.pool.CommonBasicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 获取连接相关
 *
 * @author pc
 */
public class connection {

    public static Logger log = Logger.getLogger(connection.class);
    //public static Context initContext = null;
    //public static Context envContext = null;
    public static String groupName_static = "default";

    private static Map<String, ConnectionProp> dbMaps = null;

    private static Map<String, BasicDataSource> dataSourceMaps = new HashMap<String, BasicDataSource>();

    private static Map<String, HikariDataSource> hikariDataSourceMap = new HashMap<>();


    /***
     * 同类线程必须等待返回结果
     * @return
     */
    public static synchronized Map<String, ConnectionProp> initDbInfo() {
        Map<String, ConnectionProp> dbMaps2 = null;

        if (dbMaps == null) {
            dbMaps2 = new HashMap<String, ConnectionProp>();

//            ConnectionProp mysqlDb_1 = new ConnectionProp(GlobalConfig.mysqlDbIp, GlobalConfig.mysqlDb_1, GlobalConfig.mysqlDbUserName, GlobalConfig.mysqlDbPwd, false);
//
//            ConnectionProp mysqlDb_3 = new ConnectionProp(GlobalConfig.mysqlDbIp_btc, GlobalConfig.mysqlDb_1_btc, GlobalConfig.mysqlDbUserName_btc, GlobalConfig.mysqlDbPwd_btc, false);
//
//
//            ConnectionProp mysqlDb_bak = new ConnectionProp(GlobalConfig.getValue("mysqlDbIp_backup_1"),
//                    GlobalConfig.getValue("mysqlDb_1_backup_1"),
//                    GlobalConfig.getValue("mysqlDbUserName_backup_1"),
//                    GlobalConfig.getValue("mysqlDbPwd_backup_1"), false);

//            dbMaps2.put(GlobalConfig.mysqlDb_1, mysqlDb_1);
//            dbMaps2.put(GlobalConfig.mysqlDb_1_btc, mysqlDb_3);
//            String mysqlDb_1_backup_1 = GlobalConfig.getValue("mysqlDb_1_backup_1");
//            if (mysqlDb_1_backup_1 != null) {
//                dbMaps2.put(mysqlDb_1_backup_1, mysqlDb_bak);
//            }
            ////初始化json配置文件里的mysql的连接
            try {
                JSONArray jas = (JSONArray) DbConfig.getValue("mysqls");
                if (jas != null && jas.size() > 0) {
                    for (Object o : jas) {
                        JSONObject jo = (JSONObject) o;
                        ConnectionProp db = new ConnectionProp(jo.getString("ip"), jo.getString("db"), jo.getString("userName"), jo.getString("pwd"), false);
                        dbMaps2.put(jo.getString("db"), db);
                    }
                }
            } catch (Exception e) {
                log.error("从配置文件获取mysql信息异常", e);
            }

            dbMaps = dbMaps2;
            //dbMaps2.put(GlobalConfig.mysqlDb_1_bao_usd, mysqlDb_6);
            //dbMaps2.put(GlobalConfig.mysqlDb_1_bao_eur, mysqlDb_7);
            //dbMaps = dbMaps2;
        }
        return dbMaps;
    }

    /**
     * 重新初始化数据库配置
     */
    public static synchronized boolean reloadMysql() {
        log.info("重新加载数据库配置：开始");
        try {
            JSONArray jas = (JSONArray) DbConfig.getValue("mysqls");
            if (jas != null && jas.size() > 0) {
                dbMaps = new HashMap<>();
                for (Object o : jas) {
                    JSONObject jo = (JSONObject) o;
                    ConnectionProp db = new ConnectionProp(jo.getString("ip"), jo.getString("db"), jo.getString("userName"), jo.getString("pwd"), false);
                    dbMaps.put(jo.getString("db"), db);
                    log.info("重新加载 " + jo.getString("db") + " 配置");
                }
            }
        } catch (Exception e) {
            log.error("从配置文件获取mysql信息异常", e);
            return false;
        }

        log.info("重新加载数据库配置：结束");
        return true;
    }


    public synchronized static void close() {
        log.info("注销mysql连接");
//        closeHikari();
        closeDbcp();
    }

    private static void closeDbcp() {
        Collection<BasicDataSource> c = dataSourceMaps.values();
        Iterator<BasicDataSource> it = c.iterator();
        int i = 0;
        for (; it.hasNext(); ) {
            try {
                BasicDataSource dbs = it.next();
                if (dbs != null) {
                    log.info("关闭mysql连接：" + dbs.getConnection().toString());
                    if (i == 0) {
                        if (!dbs.isClosed()) {
                            dbs.close();
                        }
                    }
                }
            } catch (SQLException e) {
                log.error(e.toString(), e);
            }
            i++;
        }
    }

    private static void closeHikari() {
        log.info("注销mysql连接");
        Collection<HikariDataSource> c = hikariDataSourceMap.values();
        Iterator<HikariDataSource> it = c.iterator();
        int i = 0;
        for (; it.hasNext(); ) {
            try {
                HikariDataSource dbs = it.next();
                if (dbs != null) {
                    log.info("关闭mysql连接：" + dbs.getConnection().toString());
                    if (i == 0) {
                        if (!dbs.isClosed()) {
                            dbs.close();
                        }
                    }
                }
            } catch (SQLException e) {
                log.error(e.toString(), e);
            }
            i++;
        }
    }

    //获取一个连接
    public static Connection GetConnection() {
        return GetConnection(groupName_static);
    }

    public static synchronized HikariDataSource setupHikariDataSource(String ip, String db, String userName, String password) {
        HikariDataSource dataSource = hikariDataSourceMap.get(db);
        if (dataSource != null) {
            return dataSource;
        }

        log.info("创建连接池：" + ip + ":" + db);
        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        String connectURI = "jdbc:mysql://" + ip + "/" + db + "?useUnicode=true&characterEncoding=UTF-8";
        dataSource.setJdbcUrl(connectURI);
        if (GlobalConfig.mysqlDb_TimeBetweenEvictionRunsMillis > 0 && GlobalConfig.mysqlDb_minEvictableIdleTimeMillis > 0) {
            dataSource.setIdleTimeout(GlobalConfig.mysqlDb_minEvictableIdleTimeMillis);
        }
        dataSource.setMaximumPoolSize(GlobalConfig.mysqlDb_maxActive);
        dataSource.setMinimumIdle(GlobalConfig.mysqlDb_minIdle);
        dataSource.setConnectionTimeout(GlobalConfig.mysqlDb_maxWait);
        hikariDataSourceMap.put(db, dataSource);
        return dataSource;
    }

    public static synchronized BasicDataSource setupDataSource(String ip, String db, String userName, String password) {
        BasicDataSource isource = dataSourceMaps.get(db);
        if (isource != null) {
            return isource;
        }
        log.info("创建连接池：" + ip + ":" + db);
        BasicDataSource source = new CommonBasicDataSource();
        source.setDriverClassName("com.mysql.jdbc.Driver");
        // TODO: 2017/5/9 suxinjie 如果使用p6spy 使用p6spy代理的驱动和连接
//			source.setDriverClassName("com.p6spy.engine.spy.P6SpyDriver");
        source.setUsername(userName);
        source.setPassword(password);
        String connectURI = "jdbc:mysql://" + ip + "/" + db + "?useUnicode=true&characterEncoding=UTF-8";
//			String connectURI="jdbc:p6spy:mysql://" + ip + "/" + db + "?useUnicode=true&characterEncoding=UTF-8";
        source.setUrl(connectURI);
        if (GlobalConfig.mysqlDb_TimeBetweenEvictionRunsMillis > 0 && GlobalConfig.mysqlDb_minEvictableIdleTimeMillis > 0) {
            //启动connection校验定时器,定时器运行时间间隔就是timeBetweenEvictionRunsMillis的值.默认为-1,表示不启动定时器,这里设定为1小时,只要小于mysql的wait_timeout就可以了
            source.setTimeBetweenEvictionRunsMillis(GlobalConfig.mysqlDb_TimeBetweenEvictionRunsMillis);
            //connection的空闲时间大于这个值,就直接被关闭,并从连接池中删除
            source.setMinEvictableIdleTimeMillis(GlobalConfig.mysqlDb_minEvictableIdleTimeMillis);
        }

        //20170401 add by suxinjie
        //最大连接数量
        source.setMaxActive(GlobalConfig.mysqlDb_maxActive);
        //初始化连接
        source.setInitialSize(GlobalConfig.mysqlDb_initialSize);
        //最大空闲连接
        source.setMaxIdle(GlobalConfig.mysqlDb_maxIdle);
        //最小空闲连接
        source.setMinIdle(GlobalConfig.mysqlDb_minIdle);
        //超时等待时间以毫秒为单位 1000等于60秒
        source.setMaxWait(GlobalConfig.mysqlDb_maxWait);
        //防止拿到关闭的链接,避免java.sql.SQLException:connection is closed
        source.setValidationQuery(GlobalConfig.mysqlDb_validationQuery);

        dataSourceMaps.put(db, source);
        return source;
    }

    public static Connection GetConnection(String groupName) {
        return GetConnection(groupName, false);
    }


    public static ConnectionProp getProp(String groupName) {
        if (dbMaps == null) {
            dbMaps = initDbInfo();
        }
        String key = "";
        if (groupName.equals("ltc")) {
            key = "ltcworld";
        } else if (groupName.equals("btc")) {
            key = "btcworld";
        } else if (groupName.equals("eth")) {
            key = "ethworld";
        } else if (groupName.equals("dao")) {
            key = "daoworld";
        } else if (groupName.equals("etc")) {
            key = "etcworld";
        } else if (groupName.equals(groupName_static)) {
            key = "vip_main";
        } else {
            key = groupName;
        }

        ConnectionProp cp = dbMaps.get(key);

        if (cp == null) {
            dbMaps = initDbInfo();
            cp = dbMaps.get(key);
        }

        if (dbMaps == null) {
            return null;
        }
        if (cp != null) {
            cp.dbName = key;
        }
        return cp;
    }

    public static Connection GetConnection(String groupName, boolean isMain) {
        return getDbcpConnection(groupName);
    }

    private static Connection getHikariConnection(String groupName) {
        ConnectionProp cp = getProp(groupName);

        try {
            HikariDataSource dataSource = hikariDataSourceMap.get(cp.dbName);
            if (dataSource == null) {
                dataSource = setupHikariDataSource(cp.ip, cp.dbName, cp.dbUserName, cp.dbPwd);
                cp.isOk = true;
            }
            Connection conn = dataSource.getConnection();
            return conn;
        } catch (Exception ex) {
            log.error(groupName + ",连接出现异常！" + groupName + ",ip:" + cp.ip + ",dbName:" + cp.dbName, ex);
            throw new RuntimeException("无法与数据库" + cp.dbName + "建立连接");
        }
    }

    private static Connection getDbcpConnection(String groupName) {
        ConnectionProp cp = getProp(groupName);

        try {
            BasicDataSource dataSource = dataSourceMaps.get(cp.dbName);
            if (dataSource == null) {
                dataSource = setupDataSource(cp.ip, cp.dbName, cp.dbUserName, cp.dbPwd);
                cp.isOk = true;
            }
            Connection conn = dataSource.getConnection();
            return conn;
            //return ((BasicDataSource) pool.borrowObject(cp.dbName)).getConnection();
        } catch (Exception ex) {
            log.error(groupName + ",连接出现异常！" + groupName + ",ip:" + cp.ip + ",dbName:" + cp.dbName, ex);
            //log.error(ex.toString(), ex);
            throw new RuntimeException("无法与数据库" + cp.dbName + "建立连接");
        }
    }


//		public static Connection getBaoEthConnection(){
//			 return GetConnection(GlobalConfig.mysqlDb_1_bao_eth);
//		}
//		
//		public static Connection getBaoDaoConnection(){
//			 return GetConnection(GlobalConfig.mysqlDb_1_bao_dao);
//		}
//		
//		public static Connection getBaoEtcConnection(){
//			 return GetConnection(GlobalConfig.mysqlDb_1_bao_etc);
//		}
//		
//		
//		public static Connection getBaoLtcConnection(){
//			 return GetConnection(GlobalConfig.mysqlDb_1_bao_ltc);
//		}
//		
//		public static Connection GetLtcConnection(){
//			 return GetConnection(GlobalConfig.mysqlDb_1_ltc);
//		}

    /***
     * btc
     * @return
     */
    public static synchronized Connection GetBtcConnection() {
        return GetConnection(GlobalConfig.mysqlDb_1_btc);
    }

    public static boolean containsKey(String dbName) {
        return dbMaps.keySet().contains(dbName);
    }

//		/***
//		 * eth
//		 * @return
//		 */
//		public static synchronized Connection GetEthConnection(){
//			 return GetConnection(GlobalConfig.mysqlDb_1_eth);
//		}
//		
//		/***
//		 * dao
//		 * @return
//		 */
//		public static synchronized Connection GetDaoConnection(){
//			 return GetConnection(GlobalConfig.mysqlDb_1_dao);
//		}
//		
//		/***
//		 * etc
//		 * @return
//		 */
//		public static synchronized Connection GetEtcConnection(){
//			 return GetConnection(GlobalConfig.mysqlDb_1_etc);
//		}

//		public static void conn(){
//			KeyedPoolableObjectFactory factory = new CommonKeyedPoolableObjectFactory();
//
//			GenericKeyedObjectPool pool= new GenericKeyedObjectPool(factory, 100, GenericObjectPool.WHEN_EXHAUSTED_BLOCK, 100, true, true);
//			
//			pool.
//		}

}
