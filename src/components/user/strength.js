import React from 'react';
import PropTypes from 'prop-types';
import { injectIntl } from 'react-intl';
/**
 * Strength
 * author luchao.ding
 * date 05/25/2018
 */
class Strength extends React.Component{

    static propTypes = {
        val: PropTypes.string,
        funct: PropTypes.func
    }

    static defaultProps = {
        val: '',
        funct: (level) => {

        }
    }

    constructor(props){
        super(props);

        this.state = {
            msg: '',
            msgClassName: '',
            itemClassName: '',
            level: 0
        }
    }

    componentWillMount(){
        this.checkPwdStrength(this.props);
    }

    componentWillReceiveProps(props){
        this.checkPwdStrength(props);
    }

    checkPwdStrength(props){
        const { val:pwd, intl, funct:setLevel } = props;
        let level = 0,
            msg = '',
            msgClassName = '',
            itemClassName = '';

        if (pwd.length >= 8 && pwd.length <= 20){
            /*
            if (/\d/.test(pwd)) level++; 
            if (/[a-z]/.test(pwd)) level++; 
            if (/[A-Z]/.test(pwd)) level++; 
            if (/\W/.test(pwd)) level++; 
            if (level > 1 && pwd.length > 12) level++;
            */
           if (pwd.length >= 8 && pwd.length <= 12 && (/\d/.test(pwd)) && (/[a-zA-Z]/.test(pwd)) && (/\W/.test(pwd))){
               level += 2;
           } else if(pwd.length > 12) {
               if (/\d/.test(pwd)) level++; 
               if (/[a-zA-Z]/.test(pwd)) level++; 
               if (/\W/.test(pwd)) level++; 
           }
        }

        switch(level){
            case 0:;
            case 1:msg = intl.formatMessage({id: "user.text35"});msgClassName='';itemClassName='';break;
            case 2:msg = intl.formatMessage({id: "user.text36"});msgClassName='med';itemClassName='open_active_2';break;
            case 3:;
            default:msg = intl.formatMessage({id: "user.text37"});msgClassName='strong';itemClassName='open_active_3';break;
        }

        this.setState({
            msg,
            msgClassName,
            level,
            itemClassName
        })

        setLevel(level * 20);
    }

    render(){
        const { msg,msgClassName,level,itemClassName } = this.state;

        return (
            <div className="bk-pwdcheck">
                <ul className="bk-table pwdstr-box">
                {
                    [0,0,0].map((v, k) => {
                        return (
                            <li key={k} className={`bk-cell strength ${k < level && itemClassName}`}></li>
                        )
                    })
                }
					<li className={`bk-cell strength ${msgClassName}`}>{msg}</li>										
				</ul>
            </div>
        )
    }
}
export default injectIntl(Strength);