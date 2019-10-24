import React from 'react';
import PropTypes from 'prop-types';
import { injectIntl } from 'react-intl';

import { CT } from '../../conf';
import { formatURL } from '../../utils';
import { Link } from 'react-router';
import './n.less';

class Crumbs extends React.Component{
    static propTypes = {
        path: PropTypes.string.isRequired,
        ay: PropTypes.array.isRequired,
    }

    render(){
        const { path, ay } = this.props;
        let uri = path.split('/');
        let ftFlg = uri.indexOf(CT);
        uri = uri.filter((v, k) => k >= ftFlg).map(v => {
            const name = ay.filter(o => {
                return o.path === v;
            })[0].id;
            return {
                url: v === CT ? '/' + v : v,
                name
            }
        });
        const lstItem = uri.pop();
        
        return (
            lstItem.name
            ?
            <ul className="cr_wp cr_wp_d clearfix">
                {
                    uri.length > 0 ? (
                        uri.map(v => {
                            return (
                                <li key={v.name}>
                                    <Link to={formatURL(v.url)}>
                                        {
                                            this.props.intl.formatMessage({id: v.name})
                                        }
                                    </Link><em>&gt;</em>
                                </li>
                            )
                        })
                    ) : null
                }
                <li>
                    {
                        this.props.intl.formatMessage({id: lstItem.name})
                    }
                </li>
            </ul>
            :
            null
        );
    }
}

export default injectIntl(Crumbs);
