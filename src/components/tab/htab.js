import React from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router';

import './tab.less';

class HTab extends React.Component{
    static propTypes = {
        list: PropTypes.arrayOf(
            PropTypes.shape({
                tith: PropTypes.string.isRequired,
                link: PropTypes.string.isRequired,
            })
        ).isRequired,
        currentFlg: PropTypes.string.isRequired,
        setSelected: PropTypes.func,
        sp: PropTypes.string,
    }
    static defaultProps = {
        setSelected: (flg) => {

        },
        sp: '',
    }
    constructor(props){
        super(props);

        this.state = {
            flg: +props.currentFlg
        }

        this.setCk = this.setCk.bind(this);
    }
    setCk(e){
        const ct = e.target.getAttribute('alt');
        this.setState({
            flg: +ct
        });
        this.props.setSelected(ct);
    }
    render(){
        const { list, currentFlg, sp, } = this.props;
         const len = list.length - 1,
              { setCk } = this,
              { flg } = this.state;
             
        return (
            <ul className="tabwp clearfix" style={{width:'1000px'}}>
                {
                    list.map((item, i) => {
                        return (
                            <li className={`${len === i ? 'lst' : ''} ${item.link.indexOf(sp+currentFlg) > -1 || (!item.link && i === flg) ? 'ac' : ''}`} key={item.tith}>
                                <Link onClick={setCk} alt={i} to={item.link ? item.link : null}>{item.tith}</Link>
                            </li>
                        )
                    })
                }
            </ul>
        )
    }
}

export default HTab;