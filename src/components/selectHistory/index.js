import React from 'react';
import PropTypes from 'prop-types';
import './selectHistory.less';
import ScrollArea from 'react-scrollbar'

export default class SelectHistory extends React.Component{
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
            status:0,
        }
        this.changeValue = this.changeValue.bind(this)
        this.changeStatus = this.changeStatus.bind(this)
        this.hideSelect = this.hideSelect.bind(this)
        this.getScrollTop = this.getScrollTop.bind(this)
        this.scrolloFun = this.scrolloFun.bind(this)
    }
    componentDidMount(){
       
        if(this.props.options.length > 0){
            this.setState({
                opval:this.props.options[0].val||'',
                opname:this.props.options[0].key||'',
            })
        }
        if(this.props.defaultValue){
            const defaultItem =this.props.options.find((item,index,arr) => {
                return item.val == this.props.defaultValue
            })
            if(defaultItem){
                this.setState({
                    opval:defaultItem.val,
                    opname:defaultItem.key
                })
            }
        }
        // window.addEventListener('DOMMouseScroll', this.scrolloFun, { passive: false });  
        // window.addEventListener('mousewheel',this.scrolloFun, { passive: false });
        window.addEventListener('mousedown',this.hideSelect)
    }
 
    componentWillReceiveProps(nextProps){
        if(this.state.opname==''&&nextProps.options.length>0){
            this.setState({
                opval:nextProps.options[0].val,
                opname:nextProps.options[0].key,
            })
        }
        if(this.props.defaultValue != undefined){
            const defaultItem =this.props.options.find((item,index,arr) => {
                return item.val == this.props.defaultValue
            })
            if(defaultItem){
                this.setState({
                    opval:defaultItem.val,
                    opname:defaultItem.key
                })
            }
        }
       
    }

    componentWillUnmount(){
      
        window.removeEventListener('mousedown',this.hideSelect)
    }
    changeValue(item,index){
        this.setState({
            opval:item.val,
            opname:item.key,
            status:0
        })
        this.props.Cb(item)
    }
 
    changeStatus(e){
        this.setState({
            status:1,
            clientHeight:document.getElementsByTagName('html')[0].scrollTop
        })
        e.nativeEvent.stopImmediatePropagation();
    }
    hideSelect(e){
        if(e.srcElement.className!="scrollbar"&&e.srcElement.className!="scrollarea-content "&&e.srcElement.className!=""&&e.srcElement.className!="scrollareaLi"){
            this.setState({
                status:0
            })
        }
    }

    scrolloFun(evt){
        evt = evt || window.event;  
        if(evt.preventDefault) {  
            // Firefox  
            evt.preventDefault();  
            evt.stopPropagation();  
            } else{  
            // IE  
            evt.cancelBubble=true;  
            evt.returnValue = false;  
        }  
         return false;  
    }

     getScrollTop() {
        var scroll_top = 0;
        if (document.documentElement && document.documentElement.scrollTop) {
            scroll_top = document.documentElement.scrollTop;
        }
        else if (document.body) {
            scroll_top = document.body.scrollTop;
        }
        return scroll_top;
    }

    render(){
        const { opval,opname,status } = this.state
        const typeClass = this.props.class?this.props.class:''
        return (
            <div className={`select-history ${typeClass} ${status==1?'active':''}`}>
                <div onClick={this.changeStatus} className="select-val">{opname}<i className={status==0?'per-icon-jiao':'per-icon-dao'}></i></div>
                    <ul style={{display:status==0?'none':'block'}}>
                        <ScrollArea stopScrollPropagation={true} className="scrollarea trade-scrollarea scrollarea-content">
                            {
                                this.props.options.length>0?
                                    this.props.options.map((item,index) => {
                                        return (
                                            <li key={item.val} className={item.val==opval?'active scrollareaLi':'scrollareaLi'} onClick={() => this.changeValue(item,index)}>{item.key}</li>
                                        )
                                    }):''
                            }
                    </ScrollArea>
                    </ul>
            </div>
        )
    }

}
































