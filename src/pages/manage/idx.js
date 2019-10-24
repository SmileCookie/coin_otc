import React from 'react';
import Crumbs from '../../components/navigator/crumbs';
import Nav from '../../components/navigator/nav';
import '../../assets/css/userauth.less';
import '../../assets/css/uc.less';
import '../../assets/css/dgq.less';
import {USERCENTERTAB} from '../../conf';

class MG extends React.Component{
    constructor(props){
        super(props);
    }
    render(){
        return (
            <div className="mwp mwp_d">
                <Crumbs path={this.props.location.pathname} ay={this.props.routes} />
                <Nav path={this.props.location.pathname} ay={USERCENTERTAB} />
                <div className="min_wp min_h527_d">
                    {
                        this.props.children
                    }
                </div>
            </div>
        );
    }
}

export default MG;