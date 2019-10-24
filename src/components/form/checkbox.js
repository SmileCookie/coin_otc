import React from 'react';
import PropTypes from 'prop-types';
import './form.less';
import { isIE } from '../../utils'; 

const YIXUAN = '#icon-xuanze-yixuan';
const WEIXUAN = '#icon-xuanze-weixuan';
const YIRU = '#icon-xuanze-yiru';

class CheckBox extends React.Component{

    static propTypes = {
        isCk: PropTypes.bool.isRequired,
        setCk: PropTypes.func,
    }

    static defaultProps = {
        setCk(ck){

        },
        isCk: false,
    }

    constructor(props){
        super(props);

        this.isCk = props.isCk;

        this.state = {
            className: this.isCk ? YIXUAN : WEIXUAN
        };

        this.setHvi = this.setHvi.bind(this);
        this.setHvo = this.setHvo.bind(this);
        this.setCk = this.setCk.bind(this);
        this.isIE = isIE();
    }
    setCk(){
        this.isCk = !this.isCk;

        let cn = YIXUAN;

        if(!this.isCk){
            cn = WEIXUAN;
        }
     
        this.setState({
            className: cn
        });

        this.props.setCk(this.isCk);
    }
    setHvi(){
        !this.isCk &&
        this.setState({
            className: YIRU
        });
    }
    setHvo(){
        !this.isCk &&
        this.setState({
            className: WEIXUAN
        });
    }
    
    render(){
        const { setHvi, setHvo, setCk, isIE, isCk } = this;
        const { className } = this.state;

        return (
            <span className="plv bordernone_d checkboxwp">
            {
                !isIE
                ?
                <svg onClick={setCk} onMouseEnter={setHvi} onMouseLeave={setHvo} className="icon icon_d14" aria-hidden="true"><use xlinkHref={className}></use></svg>
                :
                <input type="checkbox" onChange={setCk} style={{verticalAlign:'middle',marginTop:'-5px',marginRight:'-10px'}} defaultChecked={isCk} />
            }
            </span>
        );
    }
}

export default CheckBox;