package com.world.model.dao;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.Fundsintroduction;

import java.util.List;

public class FundsintroductionDao extends DataDaoSupport {


    public List<Fundsintroduction> getFunds(List<Integer> fundsType, int type) {
        String sql = "";
        if(fundsType.size() > 1){
            sql  = "SELECT * FROM fundsintroduction WHERE fundsType in (" + fundsType.get(0) +","+fundsType.get(1) + ") and type = ? ";
        }else{
            sql  = "SELECT * FROM fundsintroduction WHERE fundsType in (" + fundsType.get(0) +") and type = ? ";
        }
        return Data.QueryT(sql, new Object[]{type}, Fundsintroduction.class);
    }
}
