package com.world.model.dao.user;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.user.UserAuth;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by xie on 2017/6/8.
 */
public class UserAuthDao extends DataDaoSupport<UserAuth> {

    public UserAuthDao(){
    }

    //根据IDNumber查询存在条数
    public long getCurrentIsExsitByIdNo(String idNumber){
        List<Long> list = (List<Long>)Data.GetOne("select count(*) from userauth where idNumber = ?", new Object[] { idNumber });
        if(list == null){
            return 0;
        }else{
            return list.get(0);
        }

    }


    //根据用户ID查询存在条数
    public long getCurrentIsExsitById(String id){
        List<Long> list = (List<Long>)Data.GetOne("select count(*) from userauth where id = ?", new Object[] { id });
        if(list == null){
            return 0;
        }else{
            return list.get(0);
        }
    }

    //上传照片
    public void savePhoto(UserAuth userAuth){
        Data.Update("insert into userauth (userId,userName,nation,idNumber,name,degree,status) values(?,?,?,?,?,?,?)",
                new Object[]{userAuth.getUserId(),userAuth.getUserName(),userAuth.getNation(),userAuth.getIdNumber(),userAuth.getName(),userAuth.getDegree(),userAuth.getStatus()});

    }


    public void updateUserAuth(UserAuth userAuth){
        String sqlColumns  = "";
        String valueColumns = "";
        //身份证正面
        if(StringUtils.isNotBlank(userAuth.getIdPhoto1())){
            sqlColumns = "idphoto1 = " + userAuth.getIdPhoto1() + ",";
//            valueColumns =
        }
        //身份证反面
        if(StringUtils.isNotBlank(userAuth.getIdPhoto2())){
            sqlColumns = "idphoto2 = " + userAuth.getIdPhoto2();
        }
        //手持证件照
        if(StringUtils.isNotBlank(userAuth.getIdPhoto3())){
            sqlColumns = "idphoto3 = " + userAuth.getIdPhoto3();
        }
        //住址相关照片
        if(StringUtils.isNotBlank(userAuth.getAddrPhoto())){
            sqlColumns = "addrPhoto = " + userAuth.getAddrPhoto();
        }

        if(userAuth.getDegree() != 0){
            sqlColumns = "degree = " + userAuth.getDegree();
        }

        if(userAuth.getStatus() != 0){
            sqlColumns = "status = " + userAuth.getStatus();
        }
        Data.Update("update userauth set (degree,status) values (?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{userAuth.getUserId(),userAuth.getUserName(),userAuth.getIdNumber(),userAuth.getName(),userAuth.getNation(),
            userAuth.getDegree(),userAuth.getStatus()});
    }


}
