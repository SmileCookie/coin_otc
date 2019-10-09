package com.world.model.dao.jifen;

import com.world.data.mongo.MongoDao;
import com.world.model.entity.level.FuncJump;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2018/12/34:00 PM
 */
public class FuncJumpDao extends MongoDao<FuncJump,String> {
    Logger logger = Logger.getLogger(FuncJumpDao.class);

    /**
     * 添加
     * @param funcJump
     * @return
     */
    public String addFuncJump(FuncJump funcJump){
        String nid = super.save(funcJump).getId().toString();
        logger.info("成功添加一条新数据，主键："+nid);
        return nid;
    }

    /**
     * 获取
     * @param userId
     * @return
     */
    public List<FuncJump> getFuncJumpList(String userId) {
        List<FuncJump> funcJumps = super.getListByField("userId",userId);
        return funcJumps;
    }
}
