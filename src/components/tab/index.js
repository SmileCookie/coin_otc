
import React from 'react';
import '../../assets/style/base/tab.less';
import { FormattedMessage } from 'react-intl';

class Tab extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            list: props.list || [],
            tabIndex: props.index || '',
            className: props.className || ''
        };
        
    }
    componentDidMount(){
    }
    componentWillReceiveProps(props) {
        let list = props.list != undefined ? props.list : [];
        let tabIndex = props.index != undefined ? props.index : [];
        let className =  props.className != undefined ? props.className : '';
        this.setState({
            list,
            tabIndex,
            className
        })
    }
    onChange = (e,value)=> { 
        this.setState({
            tabIndex: value
        })
        this.props.onChange(value);
    };
    render(){
        const {tabIndex,list, className} = this.state
        return (
            <div className="tab_box">
                {
                    list.map((item, i)=>{
                        return (
                            <div key={i} className={`tab_item ${className} ${tabIndex==i?'active':''}`} onClick={e=>this.onChange(e, i)}><FormattedMessage id={item} /></div>
                        )
                    })
                }
            </div>
        )
    }
}

export default Tab;