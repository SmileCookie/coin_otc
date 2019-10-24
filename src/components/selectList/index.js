import React from 'react';
import PropTypes from 'prop-types';
import ScrollArea from 'react-scrollbar'
import { browserHistory } from 'react-router';

import './selectList.less';
import { connect } from 'react-redux';
import cookie from 'js-cookie'

@connect(
    state => ({
        lng: state.language.locale
    })
)
class SelectList extends React.Component{
    static propTypes = {  
        options:PropTypes.arrayOf(PropTypes.shape({
            key : PropTypes.oneOfType([ 
                PropTypes.string,
                PropTypes.object,
            ]),
            val : PropTypes.oneOfType([ 
                PropTypes.string,
                PropTypes.number,
            ])
        })).isRequired
    }

    static defaultProps = {
        defaultValue:'',
        Cb(params){},
        className:''
    }
    constructor(props){
        super(props)
        this.state = {
            opval:'',
            opname:'',
            status:0
        }
        this.changeValue = this.changeValue.bind(this)
        this.changeStatus = this.changeStatus.bind(this)
        this.hideSelect = this.hideSelect.bind(this)
        this.defaultSet = 0;
        
    }
    componentDidMount(){
        
        if(this.props.options.length > 0){
             //console.log(this.props.options)
            this.setState({
                opval:this.props.options[0].val||'',
                opname:this.props.options[0].key||'',
            })
        }
        if(this.props.defaultValue){
            const defaultItem = this.props.options.find((item,index,arr) => {
                return item.val == this.props.defaultValue
            })
            //console.log(defaultItem)
            if(defaultItem){
                this.setState({
                    opval:defaultItem.val,
                    opname:defaultItem.key
                })
            }
        }
        
        window.addEventListener('click',this.hideSelect)
    }

    componentWillReceiveProps(nextProps){
        
        if(!this.defaultSet && this.props.defaultValue==''&&nextProps.options.length>0){
            this.setState({
                opval:nextProps.options[0].val,
                opname:nextProps.options[0].key,
            })
            this.defaultSet = 1;
        }
        if(nextProps.setVal!=this.state.opval){
            const thisItem  = nextProps.options.find((item) => {return item.val == nextProps.setVal})
            //console.log(nextProps.setVal)
            if(thisItem){
                this.setState({
                    opval:thisItem.val,
                    opname:thisItem.key,
                })
            }
        }
    }

    componentWillUnmount(){
        window.removeEventListener('click',this.hideSelect)
    }
    changeValue(item,index){
        this.setState({
            opval:item.val,
            opname:item.key,
            status:0
        })
        this.props.Cb(item.val,index)
    }
    changeStatus(e){
        const { status } = this.state
        this.setState({
            status:!status
        })
        e.nativeEvent.stopImmediatePropagation();
    }
    hideSelect(){
        this.setState({
            status:0
        })
    }

    render(){
       
        const { opval,opname,status } = this.state
        const typeClass = this.props.class?this.props.class:''
        let _lan = cookie.get('zlan')
        let _AmountRouter =  browserHistory.getCurrentLocation().pathname;
        let _isRouter = _AmountRouter.indexOf('announcements') > -1
        //console.log(opname)
        return (
            <div  style={{width:_lan == 'jp' && _isRouter?'183px':'100px'}} className={`select-type ${'en' === this.props.lng && window.location.href.indexOf("announcements") > -1 ? 'spx' : ''}  ${typeClass} ${status==1?'active':''}`}>
                <div onClick={this.changeStatus} className="select-val">{opname}<i className="per-icon-jiao"></i></div>
                <ul style={{display:status==0?'none':'block',width:_lan == 'jp' && _isRouter?'183px':_lan == 'en' && _isRouter?'160px':'100px'}}>
                    <ScrollArea className="future_scrollarea select_list_scrollarea scrollarea trade-scrollarea scrollarea-content">
                    {
                        this.props.options.length>0?
                            this.props.options.map((item,index) => {
                                return (
                                    <li key={item.val} className={item.val===opval?'active':''} onClick={() => this.changeValue(item,index)}>{item.key}</li>
                                )
                            }):''
                    }
                    </ScrollArea>
                </ul>
            </div>
        )
    }

}

export default SelectList;































