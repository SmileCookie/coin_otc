package com.world.model.dao.msg;



import java.sql.Timestamp;
import java.util.List;

import com.Lan;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.msg.Message;
import com.world.model.entity.msg.MsgBean;
import com.world.model.entity.msg.TipMsg;
import com.world.model.entity.msg.TipType;



public class MsgDao extends MongoDao<Message, String>{
	
	public static void sendMsg(String userId, String userName, TipType tipType){
		
		TipMsg tip = new TipMsg(userId, userName, tipType);
		tip.setLanguage("cn");
		MsgBean mb = new MsgBean();
		mb.setUserId(userId);
		mb.setReason("");
		mb.setOther("");
		tip.setConBean(mb);
		sendByTip(tip);
	}
	
	public static void sendByTip(TipMsg tip){
		String msgStr=tip.getMsgStr();
		if(msgStr!=null&&msgStr.length()>0&&tip.getUserId().length() > 0){//防止发送空消息
			MsgDao msgDao=new MsgDao();
			Message msg = new Message(msgDao.getDatastore());
			msg.setTitle(Lan.Language(tip.getLanguage(), tip.getTipType().getValue()));
			msg.setTo(tip.getUserId());
			msg.setBody(tip.getMsgStr());
			msg.setLanguage(tip.getLanguage());
			msg.setAllowReply(false);
			if(tip.getTipType()!=null){
				if(tip.getTipType().getMsgType()!=null){
					msg.setMsgType( tip.getTipType().getMsgType().getKey() );
				}
			}
			
			msgDao.addMessage(msg);
			
		}
	}
	
	public Message getMessageById(String messageId){
		Query<Message> q=null;
		q = getQuery(Message.class).filter("_id =", messageId);
		return super.findOne(q);
	}
	//根据某个字段获取消息
    public Message getMessageByColumn(String obj, String column){
	 	Query<Message> q=null;
		q = getQuery(Message.class).filter(column, obj);
		return super.findOne(q);
	}
	
    public List<Message> search(Query<Message> q , int pageIndex , int pageSize){
		q.offset((pageIndex-1)*pageSize).limit(pageSize);
		return super.find(q).asList();
	}
	
    public long searchCount(Query<Message> q){
		return super.find(q).countAll();
	}
	
	//用户接口的发送
	public String addMessage(Message m){
		m.setSendTime(now());
		//m.setReadTime(now());
		m.setReadTime(new Timestamp(0));
		m.setStatus(1);
		m.setIsDelOfFrom(false);
		m.setIsDelOfTo(false);
	    m.setType(com.world.model.dao.msg.msgType.note.getId());
		String nid = super.save(m).getId().toString();
		log.info("成功添加一条新数据，主键："+nid);
		return nid;
	}
	
	//临时测试
	public UpdateResults<Message> updateEmailCode(String uid, String email, String emailCode){
		Datastore ds = super.getDatastore();
		Query<Message> q = ds.find(Message.class, "_id", uid);  
		UpdateOperations<Message> ops = ds.createUpdateOperations(Message.class);
		
		ops.set("userContact.emailCode", emailCode);
		ops.set("userContact.checkEmail", email);
		
		UpdateResults<Message> ur = super.update(q, ops);
		return ur;
	}
	//返回指定用户的所有和未读消息
	public String GetCount(String username){
		Query<Message> q=this.getQuery(Message.class).filter("type", com.world.model.dao.msg.msgType.note.getId()).filter("to", username).filter("isDelOfTo", false);
		int countAll=Integer.parseInt(Long.toString(this.count(q)));
		Query<Message> q2=this.getQuery(Message.class).filter("type", com.world.model.dao.msg.msgType.note.getId()).filter("to", username).filter("status", 1).filter("isDelOfTo", false);
		int unReadCount=Integer.parseInt(Long.toString(this.count(q2)));
		return countAll+":"+unReadCount;
	}
	//将他设置成已读
	public UpdateResults<Message> hasRead(String uid){
			Datastore ds = super.getDatastore();
			Query<Message> q = ds.find(Message.class, "_id", uid);  
			UpdateOperations<Message> ops = ds.createUpdateOperations(Message.class);
			ops.set("status", 2);
			ops.set("readTime", now());
			UpdateResults<Message> ur = super.update(q, ops);
			return ur;
	}

	
}
