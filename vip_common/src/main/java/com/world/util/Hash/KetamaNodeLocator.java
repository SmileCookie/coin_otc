package com.world.util.Hash;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public final class KetamaNodeLocator {
	
	private TreeMap<Long, Node> ketamaNodes;
	private HashAlgorithm hashAlg;
	private int numReps = 160;
	/**
	 * 功能:初始化一个一致性hash算法
	 * @param nodes 默认节点集合
	 * @param alg hash种子
	 * @param nodeCopies 虚拟节点数量
	 */
    public KetamaNodeLocator(List<Node> nodes, HashAlgorithm alg, int nodeCopies) {
		hashAlg = alg;
		ketamaNodes=new TreeMap<Long, Node>();
		
        numReps= nodeCopies;
        
		for (Node node : nodes) {
			for (int i = 0; i < numReps / 4; i++) {
				byte[] digest = hashAlg.computeMd5(node.name + i);
				for(int h = 0; h < 4; h++) {
					long m = hashAlg.hash(digest, h);
					
					ketamaNodes.put(m, node);
				}
			}
		}
	//	log.info("size:"+ketamaNodes.size());
    }

    /**
     * 功能:获取主节点,本机每秒钟性能在30万/秒左右,可以用于单机
     * @param k 键
     * @return 主键节点
     */
	public Node getPrimary(final String k) {
		byte[] digest = hashAlg.computeMd5(k);
		Node rv=getNodeForKey(hashAlg.hash(digest, 0));
		return rv;
	}
	/**
	 * 功能:获取这个key的节点
	 * @param hash
	 * @return
	 */
	Node getNodeForKey(long hash) {
		final Node rv;
		Long key = hash;
		if(!ketamaNodes.containsKey(key)) {
//			SortedMap<Long, Node> tailMap=ketamaNodes.tailMap(key);
//			if(tailMap.isEmpty()) {
//				key=ketamaNodes.firstKey();
//			} else {
//				key=tailMap.firstKey();
//			}
			//For JDK1.6 version
			key = ketamaNodes.ceilingKey(key);
			if (key == null) {
				key = ketamaNodes.firstKey();
			}
		}
		rv=ketamaNodes.get(key);
		return rv;
	}
}
