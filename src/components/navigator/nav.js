import React from 'react';
import PropTypes from 'prop-types';
import { injectIntl } from 'react-intl';
import { Link } from 'react-router';
import cookie from 'js-cookie'

import './n.less';

class Nav extends React.Component{
    static propTypes = {
        path: PropTypes.string.isRequired,
        ay: PropTypes.arrayOf(
            PropTypes.shape({
                name: PropTypes.string.isRequired,
                link: PropTypes.string.isRequired,
            })
        ).isRequired,
    }

    render(){
        const {path, ay} = this.props;
        let currentPath = path.split('/');
        currentPath = currentPath[currentPath.length - 1];
        const isShow = ay.some(r => {
            return r.link.includes(currentPath);
        });
       let _lan = cookie.get('zlan')
        return (
            isShow
            &&
            <ul className="clearfix pub_nav pub_nav_d">
                {
                    ay.map(r => {
                        return (<li style={{width: _lan == 'jp' || _lan == 'kr'?'145px':_lan == 'en'?'190px':'125px'}} className={r.link.includes(currentPath) ? 'ac' : ''} key={r.link}>
                            <Link to={r.link}>{this.props.intl.formatMessage({id: r.name})}</Link>
                        </li>)
                    })
                }
            </ul>
        )
    }
}

export default injectIntl(Nav);