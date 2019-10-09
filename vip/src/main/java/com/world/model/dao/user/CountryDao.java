package com.world.model.dao.user;

import java.util.List;

import com.google.code.morphia.query.Query;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.user.Country;
import com.world.web.action.Action;

public class CountryDao extends MongoDao<Country, String> implements Action{

    public List<Country> findAll(){
        Query<Country> query = getQuery().order("code");
        return query.asList();
    }
}
