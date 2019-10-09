package com.world.util.Hash;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * 功能:测试一致性hash函数的分布情况
 * @author Administrator
 *
 */
public class HashAlgorithmTest {

	private final static Logger log = Logger.getLogger(HashAlgorithmTest.class);

	static Random ran = new Random();
	
	/** key's count */
	private static final Integer EXE_TIMES = 100000;
	
	private static final Integer NODE_COUNT = 11;
	
	private static final Integer VIRTUAL_NODE_COUNT = 160;
	
	public static void main(String[] args) {
		HashAlgorithmTest test = new HashAlgorithmTest();
		
		/** Records the times of locating node*/
		Map<Node, Integer> nodeRecord = new HashMap<Node, Integer>();
		
		List<Node> allNodes = test.getNodes(NODE_COUNT);
		KetamaNodeLocator locator = new KetamaNodeLocator(allNodes, HashAlgorithm.KETAMA_HASH, VIRTUAL_NODE_COUNT);
		
		List<String> allKeys = test.getAllStrings();
		long start=System.currentTimeMillis();
		//log.info(System.currentTimeMillis());
		for (String key : allKeys) {
			Node node = locator.getPrimary(key);
			Integer times = nodeRecord.get(node); 
			if(times == null) {
				nodeRecord.put(node, 1);
			}else {
				nodeRecord.put(node, times + 1);
			}
		}
		for(int i=0;i<100;i++){
			//测试同一个函数的数量
			Node node = locator.getPrimary("dfgfgfgh"); 
			log.info(node.name);
		}
		long end=System.currentTimeMillis();
		log.info("耗时:" + (end - start) + "::" + nodeRecord.size());
		log.info("Nodes count : " + NODE_COUNT + ", Keys count : " + EXE_TIMES + ", Normal percent : " + (float) 100 / NODE_COUNT + "%");
		log.info("-------------------- boundary  ----------------------");
		for (Map.Entry<Node, Integer> entry : nodeRecord.entrySet()) {
			log.info("Node name :" + entry.getKey() + " - Times : " + entry.getValue() + " - Percent : " + (float) entry.getValue() / EXE_TIMES * 100 + "%");
		}
		
	}
	
	
	/**
	 * Gets the mock node by the material parameter
	 * 
	 * @param nodeCount 
	 * 		the count of node wanted
	 * @return
	 * 		the node list
	 */
	private List<Node> getNodes(int nodeCount) {
		List<Node> nodes = new ArrayList<Node>();
		
		for (int k = 1; k <= nodeCount; k++) {
			Node node = new Node("node" + k);
			nodes.add(node);
		}
		
		return nodes;
	}
	
	/**
	 *	All the keys	
	 */
	private List<String> getAllStrings() {
		List<String> allStrings = new ArrayList<String>(EXE_TIMES);
		
		for (int i = 0; i < EXE_TIMES; i++) {//
			String ls=generateRandomString(ran.nextInt(50));
			allStrings.add(ls);
		}
		
		return allStrings;
	}
	
	/**
	 * To generate the random string by the random algorithm
	 * <br>
	 * The char between 32 and 127 is normal char
	 * 
	 * @param length
	 * @return
	 */
	private String generateRandomString(int length) {
		StringBuffer sb = new StringBuffer(length);
		
		for (int i = 0; i < length; i++) {
			sb.append((char) (ran.nextInt(95) + 32));
		}
		
		return sb.toString();
	}
}
