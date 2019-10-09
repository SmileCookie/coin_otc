
package com.world.data.big;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.world.util.string.MD5;
import org.apache.log4j.Logger;

/**
 * 一致性hash算法
 * User: test
 * Date: 12-5-24
 * Time: 下午5:37
 * To change this template use File | Settings | File Templates.
 */
public class ConsistencyHash {
    private final static Logger log = Logger.getLogger(ConsistencyHash.class.getName());

    private TreeMap<Long,Object> nodes = null;
    //节点信息
    private List<Object> shards = new ArrayList<Object>();
    //设置虚拟节点数目
    private int VIRTUAL_NUM = 4;

    /**
     * 初始化一致环
     */
    public void init() {
         shards.add("table_0");
         shards.add("table_1");
         shards.add("table_2");
         shards.add("table_3");
         shards.add("table_4");
         shards.add("table_5");

        nodes = new TreeMap<Long,Object>();
        for(int i=0; i<shards.size(); i++) {
            Object shardInfo = shards.get(i);
            for(int j=0; j<VIRTUAL_NUM; j++) {
                nodes.put(hash(computeMd5("SHARD-" + i + "-NODE-" + j),j), shardInfo);
            }
        }
    }

    /**
     * 根据key的hash值取得服务器节点信息
     * @param hash
     * @return
     */
    public Object getShardInfo(long hash) {
        Long key = hash;
        SortedMap<Long, Object> tailMap=nodes.tailMap(key);
		if(tailMap.isEmpty()) {
			key = nodes.firstKey();
		} else {
			key = tailMap.firstKey();
		}
        return nodes.get(key);
    }

    /**
     * 打印圆环节点数据
     */
     public void printMap() {
         log.info(nodes);
     }

    /**
     * 根据2^32把节点分布到圆环上面。
     * @param digest
     * @param nTime
     * @return
     */
      public long hash(byte[] digest, int nTime) {
		long rv = ((long) (digest[3+nTime*4] & 0xFF) << 24)
				| ((long) (digest[2+nTime*4] & 0xFF) << 16)
				| ((long) (digest[1+nTime*4] & 0xFF) << 8)
				| (digest[0+nTime*4] & 0xFF);

		return rv & 0xffffffffL; /* Truncate to 32-bits */
	  }

	/**
	 * Get the md5 of the given key.
     * 计算MD5值
	 */
	 public byte[] computeMd5(String k) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 not supported", e);
		}
		md5.reset();
		byte[] keyBytes = null;
		try {
			keyBytes = k.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unknown string :" + k, e);
		}

		md5.update(keyBytes);
		return md5.digest();
	 }

     public static void main(String[] args) {
         //Random ran = new Random();
         ConsistencyHash hash = new ConsistencyHash();
         hash.init();
//         hash.printMap();
//         //循环50次，是为了取50个数来测试效果，当然也可以用其他任何的数据来测试
//         for(int i=0; i<60; i++) {
//        	 byte[] md5 = hash.computeMd5(String.valueOf(i));
//        	 int next = ran.nextInt(hash.VIRTUAL_NUM);
//        	 long hashx = hash.hash(md5 , next);
//             log.info(hash.getShardInfo(hashx));
//         }
         
         
         log.info("---------------------------------");
         
         log.info("分布到：" + hash.getShardInfo(70615));
         
         log.info("分布到：" + md5HashingAlg("60110"));
         
   }
     
     public static String md5HashingAlg(String key) {
    	 MessageDigest md5 = null;
         if(md5==null) {
             try {
                 md5 = MessageDigest.getInstance("MD5");
             } catch (NoSuchAlgorithmException e) {
                 throw new IllegalStateException( "++++ no md5 algorythm found");            
             }
         }

         md5.reset();
         md5.update(key.getBytes());
         byte[] bKey = md5.digest();
         long res = ((long)(bKey[3]&0xFF) << 24) | ((long)(bKey[2]&0xFF) << 16) | ((long)(bKey[1]&0xFF) << 8) | (long)(bKey[0]&0xFF);
         return String.valueOf(res).substring(0, 2);
     }
     
     
     private static int origCompatHashingAlg(String key) {
    	 int hash=0;
    	 char[] cArr = key.toCharArray();
    	 for(int i=0;i<cArr.length;++i){
    		 hash=(hash*33)+cArr[i];
    	 }
         return hash % 11;
     }

}

/****
192.168.0.0-服务器0
192.168.0.2-服务器2
192.168.0.3-服务器3
192.168.0.4-服务器4
192.168.0.1-服务器1
192.168.0.2-服务器2
192.168.0.4-服务器4
192.168.0.2-服务器2
192.168.0.1-服务器1
192.168.0.4-服务器4
192.168.0.0-服务器0
192.168.0.4-服务器4
192.168.0.4-服务器4
192.168.0.4-服务器4
192.168.0.3-服务器3
192.168.0.1-服务器1
192.168.0.3-服务器3
192.168.0.4-服务器4
192.168.0.3-服务器3
192.168.0.4-服务器4
 */


