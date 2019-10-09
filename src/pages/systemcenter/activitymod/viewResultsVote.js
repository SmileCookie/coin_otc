
import React from 'react'
import ReactDOM from 'react-dom';
import axios from '../../../utils/fetch'
import qs from 'qs'
import moment from 'moment'
import VoteIndex from './voteIndex'
import DistributeVote from './distributeVote'
import { Button, message, Progress, Modal } from 'antd'
import { DOMAIN_VIP, DEFAULTVALUE, TIMEFORMAT, NUMBERPOINT, SELECTWIDTH, TIMEFORMAT_ss, TIMEFORMAT_DAYS_ss } from '../../../conf'
const confirm = Modal.confirm;
const Big = require('big.js')
export default class ViewResultsVote extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            head_list:{},
            selectCount:"",
            tableList:"",
            count:"",
            realtotalCount:'',
            brushtotalCount:'',
            totalCount:''
            
        }
        this.show_click = this.show_click.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
        this.brushVoteClick = this.brushVoteClick.bind(this)
        this.resetList = this.resetList.bind(this)
        this.showConfirm = this.showConfirm.bind(this)
    }

    componentDidMount() {
        axios.post(DOMAIN_VIP + "/voteManage/queryById", qs.stringify({
            activityId: this.props.activityId
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                    this.setState({
                        head_list: result.data
                    })
            }
        })
        this.resetList();
    }
    resetList(){
        axios.post(DOMAIN_VIP + "/voteManage/voteResult", qs.stringify({
            activityId: this.props.activityId
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                let datar = result.data;
                this.setState({
                    tableList: result.data,
                    count: result.count
                })
                let realtotalCount=0,brushtotalCount=0,totalCount=0
                for (let i = 0; i < datar.length; i++){
                    realtotalCount += datar[i].realCount
                    brushtotalCount += Number(new Big(datar[i].voteCount).minus(new Big(datar[i].realCount)).toFixed())
                    totalCount += datar[i].voteCount
                    var item = datar.coinNameJson
                    this.setState({
                        item:""
                    })
                }
                this.setState({
                    realtotalCount,
                    brushtotalCount,
                    totalCount
                })
                console.log(realtotalCount,brushtotalCount,totalCount)
            }else{
                message.warning(result.msg)
            }
        })
    }
    //输入时 input 设置到 state
    handleInputChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
    }
    //修改状态
    show_click(index,ids,coin_id) {
        this.props.showHideClick(index, ids, coin_id);
    }
    brushVoteClick(coinId, voteCount){
        let btn = ReactDOM.findDOMNode(this.refs[coinId]);
        let btn_prev = btn.parentNode;
        let btn_prev_child = btn_prev.firstChild.value;
        if (Number(btn_prev_child) < 0){
            let minus_num = voteCount + Number(btn_prev_child);
            if (minus_num < 0) {
                message.error("不能输入负数的绝对值大于票数!!!")
                return;
            }
        }
        
        axios.post(DOMAIN_VIP + "/voteManage/brushVote", qs.stringify({
            brushCount: btn_prev_child,
            activityId: this.props.activityId,
            coinId: coinId
        })).then(res => {
            const result = res.data
            if (result.code == 0) {
                this.setState({
                    tableList:""
                })
                this.resetList();
            }
            else{
                message.error(result.msg)
            }
        })
    }
    showConfirm(coinId, voteCount) {
        const that = this;
        confirm({
            title: '确定执行此操作？',
            content: '',
            onOk() {
                that.brushVoteClick(coinId, voteCount)
            },
            onCancel() {
                console.log('Cancel');
            },
        });
    }
    render() {
        Big.RM = 0;
        const { head_list, selectCount, tableList, count,realtotalCount,brushtotalCount,totalCount} = this.state
        return (
            <div>
                <div className="col-md-12 col-sm-12 col-xs-12 mb10">
                    <h3 className="center">活动名称：{head_list.activityNameSimple}</h3>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="col-md-2 col-sm-2 col-xs-2">
                            创建人: {head_list.createUser}
                        </div>
                        <div className="col-md-2 col-sm-2 col-xs-2">
                            创建时间: {moment(head_list.createTime).format(TIMEFORMAT_ss)}
                        </div>
                        <div className="col-md-2 col-sm-2 col-xs-2">
                            投票开始时间: {moment(head_list.startTime).format(TIMEFORMAT_ss)}
                        </div>
                        <div className="col-md-2 col-sm-2 col-xs-2">
                            投票截止时间: {moment(head_list.endTime).format(TIMEFORMAT_ss)}
                        </div>
                        <div onClick={() => { this.show_click(4, this.props.activityId) }} 
                                className="col-md-1 col-sm-1 col-xs-1 color_blue">{count}人参与</div>
                        <div    className="col-md-1 col-sm-1 col-xs-1">
                             <Button type="primary" onClick={() => { this.show_click(0) }} >返回上一级</Button>
                        </div>
                    </div>
                </div>
                {
                    tableList.length > 0 ? tableList.map((item, index) => {
                        return(
                            <div className="col-md-12 col-sm-12 col-xs-12" key={index}>
                                <div className="col-md-12 col-sm-12 col-xs-12 padding_0">
                                    <div className="col-md-8 col-sm-8 col-xs-8">
                                        <div className="col-md-2 col-sm-2 col-xs-2">序号：{index+1} </div>
                                        <div className="col-md-2 col-sm-2 col-xs-2">{item.coinNameJson} </div>
                                        <div onClick={() => { this.show_click(4, this.props.activityId, item.coinId) }}
                                            className="col-md-2 col-sm-2 col-xs-2 color_blue">{item.realCount}票 
                                        </div>
                                        <div className="col-md-2 col-sm-2 col-xs-2">刷票：{ new Big(item.voteCount).minus(new Big(item.realCount)).toFixed() } </div>
                                        <div className="col-md-2 col-sm-2 col-xs-2">总量：{item.voteCount} </div>
                                    </div>
                                </div>
                                <div className="col-md-12 col-sm-12 col-xs-12">
                                    <div className="col-md-8 col-sm-8 col-xs-8 padding_0">
                                        <div className="col-md-1 col-sm-1 col-xs-1">
                                            占比
                                        </div>
                                        <div className="col-md-11 col-sm-11 col-xs-11">
                                            <Progress percent={parseFloat(item.rate)} />
                                        </div>
                                    </div>
                                    <div className="col-md-3 col-sm-3 col-xs-3">
                                        <div className="form-group">
                                            <label className="col-sm-2">刷票：</label>
                                            <div className="col-sm-6">
                                                <input type="text" className="form-control input_view_vote matright_10"  onChange={this.handleInputChange} />
                                                <Button onClick={() => { this.showConfirm(item.coinId, item.voteCount) }} ref={item.coinId}
                                                        type="primary" className="ant-btn ant-btn-primary ant-btn-sm" >确认</Button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )
                    }) : ""
                }
                {tableList?<div className="col-md-12 col-sm-12 col-xs-12 martop10 mbt20">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="col-md-1 col-sm-1 col-xs-1"></div>
                        <div className="col-md-2 col-sm-2 col-xs-2">
                            实际总投量:{realtotalCount}
                        </div>
                        <div className="col-md-2 col-sm-2 col-xs-2">
                            总刷票量:{brushtotalCount}
                        </div>
                        <div className="col-md-2 col-sm-2 col-xs-2">
                            总量:{totalCount}
                        </div>
                    </div>
                </div>:''

                }
                
            </div>
        )
    }

}





























