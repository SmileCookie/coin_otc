package com.world.util.Hash;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class Shard { // S类封装了机器节点的信息 ，如name、password、ip、port等  

    private final static Logger log = Logger.getLogger(Shard.class.getName());

	private TreeMap<Long, Node> nodes; // 虚拟节点到真实节点的映射  
    private TreeMap<Long,Node> treeKey; //key到真实节点的映射  
    private List<Node> shards = null; // 真实机器节点  
    private final int NODE_NUM = 128; // 每个机器节点关联的虚拟节点个数  
    boolean flag = false;  
      
    public Shard(List<Node> nodes) {
    	this.shards = nodes;
        init(nodes);  
    }  

    public static void main(String[] args) {  
//        Node s1 = new Node("s1", "192.168.1.1");
//        Node s2 = new Node("s2", "192.168.1.2");
//        Node s3 = new Node("s3", "192.168.1.3");
//        Node s4 = new Node("s4", "192.168.1.4");
//        Node s5 = new Node("s5", "192.168.1.5");
//        shards.add(s1);  
//        shards.add(s2);  
//        shards.add(s3);  
//        shards.add(s4);  
//        shards.add(s5);
//        Shard sh = new Shard();  
//        log.info("添加客户端，一开始有4个主机，分别为s1,s2,s3,s4,每个主机有100个虚拟主机：");
//        sh.keyToNode("101客户端");  
//        sh.keyToNode("102客户端");	
//        sh.keyToNode("103客户端");  
//        sh.keyToNode("104客户端");  
//        sh.keyToNode("105客户端");  
//        sh.keyToNode("106客户端");  
//        sh.keyToNode("107客户端");  
//        sh.keyToNode("108客户端");  
//        sh.keyToNode("109客户端");  
        String[] users = new String[]{"user1" , "user2" , "user3" , "user4", "user5", "user6", "user7", "user8", "user9", "user10", "user11", "user12", "user13", "user14"};
        
//        for(String s : users) {
//            Node node = sh.getNode(s);
//            
//            log.info(s + "分布在：" + node.ip);
//        }
        
//        
//        log.info("起始的客户端到主机的映射为：");
//        printKeyTree();  
//          
//        sh.deleteS(s2);  
//          
//          
//        sh.addS(s5);  
//          
//        log.info("最后的客户端到主机的映射为：");
//        printKeyTree();  
    }  
    public void printKeyTree(){  
        for(Iterator<Long> it = treeKey.keySet().iterator();it.hasNext();){  
            Long lo = it.next();  
            log.info("hash(" + lo + ")连接到主机->" + treeKey.get(lo));
        }  
          
    }  
      
    private void init(List<Node> nnodes) { // 初始化一致性hash环  
        nodes = new TreeMap<Long, Node>();  
        treeKey = new TreeMap<Long, Node>();  
        DecimalFormat df = new DecimalFormat("000");
        for (int i = 0; i != shards.size(); ++i) { // 每个真实机器节点都需要关联虚拟节点  
            final Node shardInfo = shards.get(i);
            for (int n = 0; n < NODE_NUM; n++)  
                // 一个真实机器节点关联NODE_NUM个虚拟节点  
                nodes.put(hash(shardInfo.ip + "-" + df.format(n)), shardInfo);  
            }  
    }  
    //增加一个主机  
    private void addS(Node s) {  
        log.info("增加主机" + s + "的变化：");
        for (int n = 0; n < NODE_NUM; n++){
        	 DecimalFormat df = new DecimalFormat("000");
        	 addS(hash("SHARD-" + s.name + "-NODE-" + n), s);
        }  
            
    }  
      
    //添加一个虚拟节点进环形结构,lg为虚拟节点的hash值  
    public void addS(Long lg,Node s){  
        SortedMap<Long, Node> tail = nodes.tailMap(lg);  
        SortedMap<Long,Node>  head = nodes.headMap(lg);  
        Long begin = 0L;  
        SortedMap<Long, Node> between;  
        if(head.size()==0){  
            between = treeKey.tailMap(nodes.lastKey());  
            flag = true;  
        }else{  
            begin = head.lastKey();  
            between = treeKey.subMap(begin, lg);  
            flag = false;  
        }  
        nodes.put(lg, s);  
        for(Iterator<Long> it=between.keySet().iterator();it.hasNext();){  
            Long lo = it.next();  
            if(flag){  
                treeKey.put(lo, nodes.get(lg));  
                log.info("hash(" + lo + ")改变到->" + tail.get(tail.firstKey()));
            }else{  
                treeKey.put(lo, nodes.get(lg));  
                log.info("hash(" + lo + ")改变到->" + tail.get(tail.firstKey()));
            }  
        }  
    }  
      
    //删除真实节点是s  
    public void deleteS(Node s){  
        if(s==null){  
            return;  
        }  
        log.info("删除主机" + s + "的变化：");
        for(int i=0;i<NODE_NUM;i++){  
            //定位s节点的第i的虚拟节点的位置  
            SortedMap<Long, Node> tail = nodes.tailMap(hash("SHARD-" + s.name + "-NODE-" + i));  
            SortedMap<Long,Node>  head = nodes.headMap(hash("SHARD-" + s.name + "-NODE-" + i));  
            Long begin = 0L;  
            Long end = 0L;  
              
            SortedMap<Long, Node> between;  
            if(head.size()==0){  
                between = treeKey.tailMap(nodes.lastKey());  
                end = tail.firstKey();  
                tail.remove(tail.firstKey());  
                nodes.remove(tail.firstKey());//从nodes中删除s节点的第i个虚拟节点  
                flag = true;  
            }else{  
                begin = head.lastKey();  
                end = tail.firstKey();  
                tail.remove(tail.firstKey());  
                between = treeKey.subMap(begin, end);//在s节点的第i个虚拟节点的所有key的集合  
                flag = false;  
            }  
            for(Iterator<Long> it = between.keySet().iterator();it.hasNext();){  
                Long lo  = it.next();  
                if(flag){  
                    treeKey.put(lo, tail.get(tail.firstKey()));  
                    log.info("hash(" + lo + ")改变到->" + tail.get(tail.firstKey()));
                }else{  
                    treeKey.put(lo, tail.get(tail.firstKey()));  
                    log.info("hash(" + lo + ")改变到->" + tail.get(tail.firstKey()));
                }  
            }  
        }  
          
    }  

    //映射key到真实节点  
    public void keyToNode(String key){  
        SortedMap<Long, Node> tail = nodes.tailMap(hash(key)); // 沿环的顺时针找到一个虚拟节点  
        if (tail.size() == 0) {  
            return;  
        }  
        treeKey.put(hash(key), tail.get(tail.firstKey()));  
        log.info(key + "（hash：" + hash(key) + "）连接到主机->" + tail.get(tail.firstKey()));
    } 
    
    public Node getNode(String key){
    	SortedMap<Long, Node> tail = nodes.tailMap(hash(key)); // 沿环的顺时针找到一个虚拟节点  
    	if (tail.size() == 0) {  
            return null;  
        } 
        treeKey.put(hash(key), tail.get(tail.firstKey()));  
        return tail.get(tail.firstKey());
    }
    
     private static long md5HashingAlg(String key) {
    	 MessageDigest md5;
 		try {
 			md5 = MessageDigest.getInstance("MD5");
 		} catch (NoSuchAlgorithmException e) {
 			throw new RuntimeException("MD5 not supported", e);
 		}
    	md5.reset();
    	md5.update(key.getBytes());
    	byte[] bKey = md5.digest();
    	long hash = 0;
    	for(int i = 0; i < 4; i++){
            hash += ((long)(bKey[i*4 + 3]&0xFF) << 24)
                | ((long)(bKey[i*4 + 2]&0xFF) << 16)
                | ((long)(bKey[i*4 + 1]&0xFF) <<  8)
                | ((long)(bKey[i*4 + 0]&0xFF));
        }
//    	long res = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16) | ((long) (bKey[1] & 0xFF) << 8)| (long) (bKey[0] & 0xFF);
    	return hash;
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
      
    /** 
     *  MurMurHash算法，是非加密HASH算法，性能很高， 
     *  比传统的CRC32,MD5，SHA-1（这两个算法都是加密HASH算法，复杂度本身就很高，带来的性能上的损害也不可避免） 
     *  等HASH算法要快很多，而且据说这个算法的碰撞率很低. 
     *  http://murmurhash.googlepages.com/ 
     */  
    
    private static Long hash(String key) {
    	return md5HashingAlg(key);
    }
    private static Long hash2(String key) {  
          
        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());  
        int seed = 0x1234ABCD;  
          
        ByteOrder byteOrder = buf.order();  
        buf.order(ByteOrder.LITTLE_ENDIAN);  

        long m = 0xc6a4a7935bd1e995L;  
        int r = 47;  

        long h = seed ^ (buf.remaining() * m);  

        long k;  
        while (buf.remaining() >= 8) {  
            k = buf.getLong();  

            k *= m;  
            k ^= k >>> r;  
            k *= m;  

            h ^= k;  
            h *= m;  
        }  

        if (buf.remaining() > 0) {  
            ByteBuffer finish = ByteBuffer.allocate(8).order(  
                    ByteOrder.LITTLE_ENDIAN);  
            // for big-endian version, do this first:  
            // finish.position(8-buf.remaining());  
            finish.put(buf).rewind();  
            h ^= finish.getLong();  
            h *= m;  
        }  

        h ^= h >>> r;  
        h *= m;  
        h ^= h >>> r;  

        buf.order(byteOrder);  
        return h;  
    }  
}  