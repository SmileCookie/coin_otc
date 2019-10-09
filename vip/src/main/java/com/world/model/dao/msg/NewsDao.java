package com.world.model.dao.msg;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.code.morphia.query.Query;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.msg.News;

@SuppressWarnings("serial")
public class NewsDao extends MongoDao<News, String>{
	Logger logger = Logger.getLogger(NewsDao.class);
	
	/**
	 * 根据id查询一条新闻
	 * @param id 标识号
	 * @return 查询的唯一一条结果
	 */
	public News getById(String id){
		Query<News> q = getQuery(News.class).filter("_id =", id);
		return super.findOne(q);
	}

	/**
	 * 根据id查询一条新闻部分列
	 * @param id 标识号
	 * @return 查询的唯一一条结果
	 */
	public News getById(String id,String ...columns){
		Query<News> q = getQuery(News.class).retrievedFields(true,columns).filter("_id =", id);
		return super.findOne(q);
	}
	
	/**
	 * 添加一条新闻
	 */
	public String addNews(News n){
		n.setPubTime(now());
		String nId=super.save(n).getId().toString();
		return nId;
	}
	
	
	/**
	 * 查询一个新闻列表
	 */
	public List<News> search(Query<News> q,int pageIndex,int pageSize){
		
		
		q.offset((pageIndex-1)*pageSize).limit(pageSize);
		return super.find(q).asList(); 
	}
}
