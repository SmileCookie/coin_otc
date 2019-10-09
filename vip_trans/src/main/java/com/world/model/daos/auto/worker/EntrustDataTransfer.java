package com.world.model.daos.auto.worker;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.task.Worker;

/**
 * 转移委托表中三个月之前的数据 到新表里，并移除数据
 * @author apple
 *
 */
public class EntrustDataTransfer extends Worker{

	private static final long serialVersionUID = 1L;

	public EntrustDataTransfer(String name, String des) {
		super(name, des);
	}
	
	private static boolean isSync = false;

	@Override
	public void run() {
		super.run();
		log.info("转移委托记录数据");
		
		if(!isSync){
			isSync = true;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Timestamp time = new Timestamp(cal.getTimeInMillis());
			doTransfer(100,1, time.getTime());//Long.parseLong(sdf.format(time)));
			
			cal.add(Calendar.DAY_OF_MONTH, -6);
			time = new Timestamp(cal.getTimeInMillis());
			doTransfer(100,2, time.getTime());//Long.parseLong(sdf.format(time)));
		}
	}
	
	public void doTransfer(int pageSize, int status, long time){
		String sql="select * from entrust where status=? AND submitTime < ? limit 0, ?";
		List lists = null;
		List<OneSql> sqls = null;
		boolean hasEnd = false;
		//不存在,创建表
		//Data.create("default", "create table if not exists entrust_all like entrust;");
		while (!hasEnd) {
			long t1 = System.currentTimeMillis();
			lists = Data.Query(sql, new Object[]{status, time, pageSize});
			long t2 = System.currentTimeMillis();
			log.info("--------------查询------------------"+(t2-t1)+"ms");
			if(lists.size() == 0){
				hasEnd = true;
				break;
			}
			sqls = new ArrayList<OneSql>();
			String ids = "";
			for(int i = 0; i < lists.size(); i ++){
				List li = (List)lists.get(i);
				
				if(i < lists.size() - 1){
					ids += li.get(0)+",";
				}else{
					ids += li.get(0);
				}
			}
			sqls.add(new OneSql("INSERT INTO entrust_all "+sql, -2, new Object[]{status, time, pageSize}));
			sqls.add(new OneSql("DELETE FROM entrust WHERE entrustId IN ("+ids+")", -2, null));
			if(Data.doTrans(sqls)){
				long t3 = System.currentTimeMillis();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					log.error(e.toString(), e);
				}
				log.info("--------------执行------------------"+(t3-t1)+"ms");
				log.info("成功转移" +lists.size() + "条数据到新表中。");
			}else{
				log.info("处理数据出错。");
			}
		}
		
		isSync = false;
		
	}
	
	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Timestamp time = new Timestamp(cal.getTimeInMillis());
		log.info(time);
		
		cal.add(Calendar.DAY_OF_MONTH, -6);
		time = new Timestamp(cal.getTimeInMillis());
		log.info(time);
	}
	
}
