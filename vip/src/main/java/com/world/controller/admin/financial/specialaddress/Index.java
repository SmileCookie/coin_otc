package com.world.controller.admin.financial.specialaddress;

import com.world.cache.Cache;
import com.world.data.mysql.Query;
import com.world.model.entity.usercap.dao.CommAttrDao;
import com.world.model.entity.usercap.entity.CommAttrBean;
import com.world.model.service.SpecialAddressBuild;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by xie on 2017/9/5.
 */
@FunctionAction(jspPath = "/admins/financial/specialaddress/", des = "字典表维护")
public class Index extends AdminAction {

    private CommAttrDao commAttrDao = new CommAttrDao();

    @Page(Viewer = DEFAULT_INDEX)
    public void index() {

        int pageSize = 20;
        int currentPage = intParam("page");
        String paraValue = param("paraValue");
        String attrTypeDesc = param("attrTypeDesc");

        Query<CommAttrBean> query = commAttrDao.getQuery();
        query.setSql("select * from comm_attr ");
        query.setCls(CommAttrBean.class);

        if(StringUtils.isNotBlank(paraValue)) {
            query.append(" paraValue='" + paraValue + "'");
        }

        if(StringUtils.isNotBlank(attrTypeDesc)){
            query.append(" attrTypeDesc='" + attrTypeDesc + "'");
        }

        int total = query.count();
        if (total > 0) {
            query.append(" ORDER BY attrType,paraCode");
            List<CommAttrBean> datalist = query.getPageList(currentPage, pageSize);
            setAttr("dataList", datalist);
        }
        setPaging((int) total, currentPage, pageSize);

    }
//    public void setType(){
//        List<Map<String,String>> attrTypelist =  new ArrayList<>();
//        for(AddressType type : AddressType.values()){
//            Map<String, String> tmpMap = new HashMap<>();
//            tmpMap.put("code", type.getKey()+"");
//            tmpMap.put("value", type.getValue());
//            attrTypelist.add(tmpMap);
//        }
//
//        setAttr("attrTypelist", attrTypelist);
//    }

    @Page(Viewer = JSON)
    public void sendAddress(){
        boolean flag = false;
        try{
            flag = SpecialAddressBuild.INSTANCE.pushSpecialAddress();
        }catch (Exception e) {
            flag = false;
        }
        if(flag){
            json("推送成功。", true, "");
        }else{
            json("推送失败。", false, "");
        }
    }

    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }

    @Page(Viewer = DEFAULT_AORU,des="新增/编辑")
    public void aoru() {
        int attrId = intParam("id");
        if(attrId>0){
            CommAttrBean commAttrBean = commAttrDao.queryCommAttrById(attrId);
            setAttr("commAttrBean", commAttrBean);
//            int fundsTypeCode = 2;
//            try{
//                fundsTypeCode = new Integer(commAttrBean.getParaCode());
//            }catch(Exception e){
//
//            }
//            setAttr("paraCode", fundsTypeCode);

        }
//        setType();
//        setAttr("ft", DatabasesUtil.getCoinPropMaps());
    }

    @Page(Viewer = ".xml",des="保存新增/编辑")
    public void doAoru() {
        try {
            int attrId = intParam("id");
            int attrType = intParam("attrType");//类型
            String attrTypeDesc = param("attrTypeDesc");//类型备注
            String paraCode = param("paraCode");
            String paraName = param("paraName");
            String paraValue = param("paraValue");
            int attrState = intParam("attrState");
            //判断paraCode如果是个数为是否以0开头
            try{
                if(StringUtils.isNotBlank(paraCode)){
                    int code = Integer.valueOf(paraCode);
                    paraCode = code < 10 ? "0"+ code : "" + code;//参数代码
                }else{
                    paraCode = "";
                }

            }catch (Exception e){
                WriteRight("操作失败");
                return;
            }
            String paraDesc = param("paraDesc");//记录备注

            CommAttrBean commAttrBean = new CommAttrBean();
            commAttrBean.setAttrId(attrId);
            commAttrBean.setAttrType(attrType);
            commAttrBean.setAttrTypeDesc(attrTypeDesc);
            commAttrBean.setParaCode(paraCode);
            commAttrBean.setParaValue(paraValue);
            commAttrBean.setParaName(paraName);
            commAttrBean.setParaDesc(paraDesc);
            commAttrBean.setAttrState(attrState);
            //旧值
            String oldParaName = "";
            if(attrId > 0){//更新
                if(attrType == 10020001){
                    //修改
                    oldParaName = getOldParamName(attrId);
                }
                commAttrDao.update(commAttrBean);
            }else{//新增
                commAttrDao.insert(commAttrBean);
            }
            /*start by xwz 20171108 上限*/
            if(attrType == 10020001){
                //修改
                if(!oldParaName.equals(paraName)){
                    Cache.Delete(oldParaName + "_market_depth_deviation_rate");
                }

                if(attrState == 1){
                    Cache.Set(paraName + "_market_depth_deviation_rate", paraValue);
                }else{
                    //如果设为启用，市场深度默认值设置为5
                    Cache.Set(paraName + "_market_depth_deviation_rate", "5");
                }
            }
            WriteRight("操作成功");
            return;
        } catch (Exception ex) {
            log.error("内部异常", ex);
            WriteError("操作失败");
            return;
        }
    }

    private String getOldParamName(int attrId){
        String oldParaName = "";
        try{
            CommAttrBean oldCommAttrBean = commAttrDao.queryCommAttrById(attrId);
            oldParaName = oldCommAttrBean.getParaName();
        }catch (Exception e){
            log.error("获取字典表oldParaName失败,attrId:"+attrId,e);
        }
        return oldParaName;
    }

    @Page(Viewer = XML,des="删除")
    public void doDel() {
        int attrId = intParam("id");
        if (attrId > 0) {
            String oldParaName = getOldParamName(attrId);
            commAttrDao.delete(attrId);
            if(StringUtils.isNotBlank(oldParaName)){
                Cache.Delete(oldParaName + "_market_depth_deviation_rate");
            }
            WriteRight("操作成功！");

            return;
        }
        WriteError("未知错误导致删除失败！");
        return;
    }

}