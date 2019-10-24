import React from 'react'
import PropTypes from 'prop-types';
import { connect } from 'react-redux';

export default class KlineSelect extends React.Component{
    static propTypes = {  
        options:PropTypes.arrayOf(PropTypes.shape({
            name : PropTypes.oneOfType([ 
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
            selectName:'',
            selectVal:0,
            status:false,
            activeIndex:0,
            iconfont:false
        }
        this.selectDom = React.createRef()
    } 

    componentDidMount(){
        const { selectVal,options } = this.props
        const nowItemIndex = options.findIndex((item) => {return item.val == selectVal})
        const nowItem = options.find((item) => {return item.val == selectVal})
        this.setState({
            selectName:selectVal?nowItem.name:options[0].name,
            selectVal:selectVal?selectVal:options[0].val,
            activeIndex:nowItemIndex,
            iconfont:options[0].ele?true:false
        })
        window.addEventListener('click',this.hideSelect)
    }

    componentWillReceiveProps(nextProps){
        if(nextProps.selectVal != this.state.selectVal){
            const nowItemIndex = nextProps.options.findIndex((item) => {return item.val == nextProps.selectVal})
            const nowItem = nextProps.options.find((item) => {return item.val == nextProps.selectVal})
            this.setState({
                selectName:nowItem.name,
                selectVal:nowItem.val,
                activeIndex:nowItemIndex,
            })
        }
    }
    componentWillUnmount(){
        window.removeEventListener('click',this.hideSelect)
    }
    changeValue = (item,index) => {
        this.setState({
            selectName:item.name,
            selectVal:item.val,
            status:0,
            activeIndex:index
        })
        this.props.Cb(item.val)
    }
    changeStatus = () => {
        this.setState((prevState)=> {
            return{status:!prevState.status}
        })
        // e.nativeEvent.stopImmediatePropagation();
    }
    hideSelect = (e) => {
        e.stopPropagation();
        if(!this.selectDom.current.contains(e.target)){
            this.setState({
                status:false
            })
        }
    }

    render(){
        const { selectName,selectVal,status,activeIndex,iconfont } = this.state
        const { className,options } = this.props
        return (
            <div className={`kline-select-type ${className}`}  ref={this.selectDom}>
                <div className={`kline-select-val ${status?'active':''}`} onClick={this.changeStatus}>
                    {iconfont?options[activeIndex].ele:selectVal < 60 ? selectVal:selectName}
                    <i className="iconfont icon-xiala kline-per-icon-jiao"></i>
                </div>
                <ul style={{display:status?'block':'none'}}>
                    {
                        this.props.options.length>0?
                            this.props.options.map((item,index) => {
                                return (
                                    <li 
                                        key={item.val} 
                                        className={item.val==selectVal?'active':''} 
                                        onClick={() => this.changeValue(item,index)}

                                    >
                                        {item.ele&&item.ele}{item.name}
                                    </li>
                                )
                            }):''
                    }
                </ul>
            </div>
        )
    }
}














