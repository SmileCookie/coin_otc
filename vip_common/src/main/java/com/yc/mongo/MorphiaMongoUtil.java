package com.yc.mongo;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.yc.entity.msg.Msg;
import com.yc.mongo.common.ConnnectPool;
import com.yc.mongo.entity.MongoConnectInfo;

public class MorphiaMongoUtil {
	private static MorphiaMongo morphiaMongo;
	synchronized static void init(){
		Mongo mongo = ConnnectPool.getMongo();

		Morphia morphia = new Morphia();
		morphia.map(Msg.class);
//		morphia.map(ReplayMsg.class);
		morphiaMongo=new MorphiaMongo(mongo,morphia,MongoConnectInfo.dbName);
	}
	/*******
	 * ��ȡmongo�����Ĳ���
	 * @return
	 */
	public static MorphiaMongo getMorphiaMongo(){
		if(morphiaMongo == null){
			init();
		}
		return morphiaMongo;
	}
}
