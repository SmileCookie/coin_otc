import React from 'react';
import PropTypes from 'prop-types';

import './selectEntrust.less';

class SelectEntrust extends React.Component{
    static propTypes = {
        list: PropTypes.arrayOf(
            PropTypes.shape({
                label: PropTypes.isRequired,
                value: PropTypes.isRequired,
            })
        ).isRequired,
        // currentCode: PropTypes.isRequired,
        getCode: PropTypes.func
    }
    static defaultProps = {
        getCode: (item = {}) => {

        } 
    }
    constructor(props){
        super(props);

        this.state = {
            tith: '',
            ck: 1
        }

        this.getCode = this.getCode.bind(this);
        this.ins = this.ins.bind(this);
        this.out = this.out.bind(this);
    }

    componentWillMount(){
        const { getCode, list } = this.props;
        getCode(list[0]);
    }

    ins(){
        this.setState({
            ck: 0,
        })
    }
    out(){
        this.setState({
            ck: 1
        });
    }
    getCode(e){
        this.setState({
            ck: 1,
            tith: e.label,
        });
        this.props.getCode(e);
    }
    componentDidMount(){
        const props = this.props;
        const tith = props.list.filter(v => {
            return v.value === props.currentCode;
        })[0].label;

        this.setState({
            tith
        });
    }
    render(){
        const { tith, ck } = this.state;
        const { list } = this.props;
        const { ins, out } = this;
        return (
            <div className={`selwp ${ck ?'':'blue'}`}>
                <div onMouseOver={ins} onMouseOut={out} className="stithwp">
                    <strong className="stith">{tith}</strong><em className={`iconfont ${ck ?'icon-xialajiantou-yiru':'icon-xialajiantou-yiru-copy'}`}></em>
                    <div className="country">
                        <div className={`${ck ? 'hide' : ''} goog-menu`}>
                            <div className="goog-menu-con">
                            {
                                list.map(v => {
                                    return (
                                    <div onClick={() => {this.getCode(v)}} key={v.value}>
                                        <div className="goog-menuitem">
                                            <div className="goog-menuitem-content">
                                                <div>
                                                    {/* <div className="goog-inline-block">
                                                        <div className="talk-flag"></div>
                                                    </div> */}
                                                    <span className="talk-select-country-name">{v.label}</span> 
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    )
                                })
                            }
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default SelectEntrust;