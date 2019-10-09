package com.yc.dao.msg;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.world.util.date.TimeUtil;
import com.yc.entity.msg.Msg;
import com.yc.entity.msg.MsgSendStatus;
import com.yc.entity.msg.MsgType;
import com.yc.mongo.MorphiaMongo;
import org.apache.log4j.Logger;

public class MsgDao extends BasicDAO<Msg, String>{
	private static Logger log = Logger.getLogger(MsgDao.class.getName());

	public MsgDao(MorphiaMongo mm) {
		super(mm.getMongo(), mm.getMorphia(), mm.getDbName());
	}
	
	/****
	 * ��ȡδ���͵���Ϣ
	 * @param size
	 * @return
	 */
	public List<Msg> findByPage(Query<Msg> q,int pageNo, int pageSize){
		List<Msg> list=null;
		try {
			q.offset((pageNo - 1)*pageSize);
			q.limit(pageSize);
			list = this.find(q).asList();
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return list;
	}
	/****
	 * ��ȡδ���͵���Ϣ
	 * @param size
	 * @return
	 */
	public List<Msg> getNoSendMsgs(int size,int type){
		List<Msg> list=null;
		try {
			Datastore ds = this.getDatastore();
			Query<Msg> q = ds.createQuery(Msg.class).filter("sendStat =", MsgSendStatus.no.getKey()).order("addDate").limit(size);
			if(type>0){
				q.filter("type =", type);
			}
			list = this.find(q).asList();
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return list;
	}
	
	/****
	 * ��ȡ�û����췢��ͬ����Ϣ������
	 * @param m
	 * @return
	 */
	public long todayCount(Msg m , MsgSendStatus mss){
		try {
			Datastore ds = this.getDatastore();
			Date today0=TimeUtil.getTodayFirst();//����0ʱ��ʱ��ֵ
			Query<Msg> q = ds.createQuery(Msg.class)//.filter("sendStat =", mss.getKey())
				.filter("userId =", m.getUserId()).filter("sysId =", m.getSysId()).filter("addDate >=", today0).filter("type =", m.getType());
			return this.count(q);
		} catch (Exception e) {
			log.error(e.toString(), e);
			return 0;
		}
	}
	
	public long getCount(MsgSendStatus mss,MsgType mt,boolean isToday,int sysId){
		try {
			Datastore ds = this.getDatastore();
			
			Query<Msg> q = ds.createQuery(Msg.class);
			q.filter("sendStat =", mss.getKey()).filter("type =", mt.getKey());
			if(isToday){
				Timestamp today0=TimeUtil.getTodayFirst();//����0ʱ��ʱ��ֵ
				q.filter("addDate >=", today0);
			}
			if(sysId>0){
				q.filter("sysId =", sysId);
			}
			
			return this.count(q);
		} catch (Exception e) {
			log.error(e.toString(), e);
			return 0;
		}
	}
}
