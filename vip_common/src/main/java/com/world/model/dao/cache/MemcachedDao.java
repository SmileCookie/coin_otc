package com.world.model.dao.cache;
import net.rubyeye.xmemcached.KeyIterator;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.utils.AddrUtil;

import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.config.MemoryConfig;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.cache.Memcached;


/**友情链接*/

@SuppressWarnings("deprecation")
public class MemcachedDao extends MongoDao<Memcached, String>{
	
	private static final long serialVersionUID = 1L;

	public String saveOrNo(Memcached mem){
		if(getMem(mem.getKey()) == null){
			String nid = super.save(mem).getId().toString();
			return nid;
		}
		return null;
	}
	
	public Memcached getMem(String key){
		Memcached mem = super.getByField("key", key);
		return mem;
	} 
	
	public void saveData(){
		try{
			String obj = Cache.Get("save_mem_data");
			if(obj != null){
				return;
			}
			
			MemcachedClient memcachedClient = MemoryConfig.getMem();
			KeyIterator it = memcachedClient.getKeyIterator(AddrUtil.getOneAddress(GlobalConfig.memcachedIp + ":" + GlobalConfig.memcachedPort));
			
			Cache.Set("save_mem_data", "1", 20*60);
			while (it.hasNext()) {
				String key = it.next();
				Memcached mem = new Memcached(super.getDatastore());
				mem.setKey(key);
				saveOrNo(mem);
				log.info("mongodb update success...");
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
}
