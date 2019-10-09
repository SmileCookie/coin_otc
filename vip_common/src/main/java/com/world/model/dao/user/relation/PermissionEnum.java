package com.world.model.dao.user.relation;

import org.apache.log4j.Logger;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public enum PermissionEnum{

	//人员管理
//	person_manager(1, "人员管理", "/u/device/person", 0),
	person_manager_role(2, "编辑角色", "/u/device/person/role", 1),
	person_manager_edit_role(3, "保存角色", "/u/device/person/saveRole", 1),
	person_manager_delete_role(4, "删除角色", "/u/device/person/deleteRole", 1),
	person_manager_user(5, "编辑用户", "/u/device/person/user", 1),
	person_manager_edit_user(6, "保存用户", "/u/device/person/saveUser", 1),
	person_manager_delete_user(7, "删除用户", "/u/device/person/deleteUser", 1),
	//矿场管理
	//factory_manager(10, "矿场管理", "/u/device/factory", 0),
	//factory_manager_add(11, "添加矿场", "/u/device/edit", 10),
	//factory_manager_save(12, "查看矿场", "/u/device/edit", 10),
	
	//设备管理
//	device_manager(20, "设备管理", "/u/device/miner", 0),
	//扫描
	device_manager_scan(21, "检测矿机", "/u/device/battch/scan/battchScan", 20),
	device_manager_scan_add(22, "扫描添加", "/u/device/battch/scan/battchScanAdd", 20),
	//设置矿池
	device_manager_config(23, "切换矿池", "/u/device/battch/config/battchConfig", 20),
	//设置网络
	device_manager_network(24, "更改网络", "/u/device/battch/network/battchNetwork", 20),
	//重启
	device_manager_reboot(25, "重启矿机", "/u/device/battch/reboot/battchReboot", 20),
	//故障处理
	device_manager_hitch(26, "故障处理", "/u/device/battch/hitch/battchHitch", 20),
	//备注
	device_manager_remark(27, "矿机备注", "/u/device/battch/remark/battchRemark", 20),
	//删除
	device_manager_delete(28, "删除矿机", "/u/device/battch/delete/battchDelete", 20),
	//导出
	device_manager_export(29, "导出矿机", "/u/device/battch/exports", 20),
	//初始化矿池地址
	device_manager_initpooladdr(30, "初始化矿池", "/u/device/battch/pooladdr/battchSetup", 20),
	
	//矿池地址
//	device_manager_pooladdr(41, "矿池地址", "", 0),
	device_manager_pooladdr_edit(42, "编辑矿池", "/u/device/pooladdr/save", 30);
	
	private int key;
	private String name;
	private String url;
	private int parent;

	private final static Logger log = Logger.getLogger(PermissionEnum.class);
	
	private PermissionEnum(int key, String name, String url, int parent){
		this.key = key;
		this.name = name;
		this.url = url;
		this.parent = parent;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}
	
	
	private static Map<String , EnumSet<PermissionEnum>> dateEnums = new HashMap<String, EnumSet<PermissionEnum>>();
	
	/*******
	 * 获取所有状态
	 * @return
	 */
	public static EnumSet<PermissionEnum> getAll(Class c){
		if(dateEnums.get(c.getName()) == null){
			dateEnums.put(c.getName(), EnumSet.allOf(c));
		}
		return dateEnums.get(c.getName());
	}
	/*******
	 * 根据键值返回对应状态
	 * @param key
	 * @return
	 */
	public static PermissionEnum getEnumByKey(int key , Class c){
		EnumSet curEs = getAll(c);
		if(curEs != null){
			Iterator it = curEs.iterator();
			while(it.hasNext()){
				PermissionEnum stat=(PermissionEnum) it.next();
				if(stat.getKey() == key){
					return stat;
				}
			}
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		PermissionEnum m = PermissionEnum.getEnumByKey(22, PermissionEnum.class);
		log.info(m.getName());
	}
	
}
