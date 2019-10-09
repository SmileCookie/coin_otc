import React from 'react'
import axios from 'axios'
import qs from 'qs'
import cookie from 'js-cookie'
import { DOMAIN_VIP } from '../src/conf'
import {Router,Route,Link,Switch,Redirect} from 'react-router-dom'
import {Provider}  from 'react-redux'
import store from './redux'
import history from './utils/history'
import App from './pages/app'
import Home from './pages/home'
import Login from './pages/login'

const PrivateRoute = ({ component: Component,path }) => (
    <Route exact path={path} render={props => (
        cookie.get("token")?(
        <Component {...props}/>
      ) : (
        <Redirect to={{
          pathname: '/login'
        }}/>
      )
    )}/>
  )

const createRoutes = () => {
        return(
          <Provider store={store()}> 
            <Router history={history}>
                <Switch>
                      <PrivateRoute path="/"  exact component ={App} />
                      <Route path='/login' exact component={Login} />
                </Switch>
            </Router>
            </Provider> 
            
        )
}
export default createRoutes;

// <Router>
// <App>
//     <Route path="/" component={Home}/>
// </App>
// </Router>
// <Router history={browserHistory}>
// {createRoutes()}
// </Router>    




