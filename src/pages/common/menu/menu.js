import React from 'react';
import { Link,IndexLink} from 'react-router'
import { FormattedMessage } from 'react-intl';


export default class Menu extends React.Component{
    render(){
        const link = window.location.href;
        return (
            <div className="menu">
               <ul>{
                   this.props.menu.map((item,index)=>{
                        if(item.onlyActiveOnIndex && item.onlyActiveOnIndex == true ){
                            return(
                                <li key={index}>
                                    <Link to={item.linkTo} activeClassName={item.activeClassName} onlyActiveOnIndex={true}>{item.text}</Link>               
                                </li>
                            )
                        }else{
                            return(
                                <li key={index}>
                                    <Link to={item.linkTo} className={link.indexOf(item.linkTo) > -1 && !link.split('auth/')[1] ? item.activeClassName : (link.split('auth/')[1] && index == 0 ? item.activeClassName : '')} >{item.text}</Link>               
                                </li>
                            )
                        }
                   })
                   }
               </ul>
            </div>
        )
    }
}

































