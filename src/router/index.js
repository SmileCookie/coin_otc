/**
 * recursion router function
 * @description router config will use this function return.
 * @author luchao.ding
 */
import React from 'react';
import {Route,Switch} from 'react-router-dom';

// router config
import Routes from "./conf";

/**
 * 
 * @param {Route Rules} Routes 
 */
const fmRoutes = (Routes) => {
  return (
    <React.Fragment>
      <Switch>
        {
          Routes.map((route) => {
            return (<Route {...route} render={(props) => {
              return (
                <React.Fragment>
                  <route.components {...props} cd={route.routes ? fmRoutes(route.routes) : null} />
                </React.Fragment>
              )
            }} />)
          })
        }
      </Switch>
    </React.Fragment>
  )
};

export default () => {
  return fmRoutes(Routes);
};


