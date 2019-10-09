package com.yc.biz.msg;

import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.yc.dao.msg.MsgDao;
import com.yc.entity.msg.Msg;
import com.yc.entity.msg.MsgSendStatus;
import com.yc.entity.msg.MsgType;
import org.apache.log4j.Logger;

/****
 * msgҵ����
 * @author Administrator
 *
 */
public class MsgBiz {

	private final static Logger log = Logger.getLogger(MsgBiz.class.getName());

	public MsgBiz() {}
	public MsgBiz(MsgDao msgDao) {
		this.msgDao = msgDao;
		
	}
	////����߳��Ƿ���  δ��������
	private MsgDao msgDao;
	/****
	 * δ������Ϣ
	 * @param size
	 * @return
	 */
	public List<Msg> getNoSendMsgs(int size){
		List<Msg> list=null;
		if(msgDao!=null){
			list=msgDao.getNoSendMsgs(size,0);
		}
		return list;
	}
	/*****
	 * ����Ϣ�б���ɸѡ����Ӧ����Ϣ����
	 * @param msgs
	 * @return
	 */
	public List<Msg> getNoSendMsgsByType(List<Msg> msgs,MsgType type){
		List<Msg> smsMsgs=new ArrayList<Msg>();
		if(msgs!=null&&msgs.size()>0){
			for(Msg m : msgs){
				if(m.getType()==type.getKey()){
					smsMsgs.add(m);
				}
			}
		}
		return smsMsgs;
	}
	
	/****
	 * ��ȡδ���͵��ֻ������Ϣ
	 * @param size
	 * @return
	 */
	public List<Msg> getNoSendSmsMsgs(int size){
		List<Msg> list=null;
		if(msgDao!=null){
			list=msgDao.getNoSendMsgs(size,2);
		}
		return list;
	}
	
	/****
	 * ��ȡδ���͵�email��Ϣ
	 * @param size
	 * @return
	 */
	public List<Msg> getNoSendEmailMsgs(int size){
		List<Msg> list=null;
		if(msgDao!=null){
			list=msgDao.getNoSendMsgs(size,1);
		}
		return list;
	}
	/****
	 * ����һ����Ϣ
	 * @param m
	 */
	public void addMsg(Msg m){
		Key<Msg> key=msgDao.save(m);
		log.info(key.toString());
	}
	/*****
	 * �޸���Ϣ״̬
	 * @param m
	 * @param setStat
	 */
	public UpdateResults<Msg> updateMsgStat(Msg m,MsgSendStatus setStat){
		Datastore ds = msgDao.getDatastore();
		Query<Msg> q = ds.createQuery(Msg.class).filter("_id =", m.get_id());
		UpdateOperations<Msg> ops = ds.createUpdateOperations(Msg.class).set("sendStat",setStat.getKey());
		UpdateResults<Msg> res = msgDao.update(q, ops);
		
		return res;
	}
	
	/****
	 * ��ȡ�û����췢��ͬ����Ϣ������
	 * @param m
	 * @return
	 */
	public long todayCount(Msg m){
		return msgDao.todayCount(m,MsgSendStatus.success);
	}
	
	public long getCount(MsgSendStatus mss,MsgType mt,boolean isToday,int sysId){
		return msgDao.getCount(mss,mt,isToday,sysId);
	}
}
