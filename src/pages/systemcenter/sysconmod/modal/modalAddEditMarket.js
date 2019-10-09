import Decorator from 'DTPath'
import {AsyncSelect, SeOp} from "../../../../components/select/asyncSelect";
import {SELECTWIDTH} from 'Conf'
import {Radio, Select, Checkbox, Button, Tabs,message,} from 'antd'
import  '../../../../assets/css/modalAddEditMarker.less'
import Modal from "antd/es/modal";
import axios from 'axios'
import React from "react";


const { TabPane } = Tabs;

const RadioGroup = Radio.Group;
const Option = Select.Option
const CheckboxGroup = Checkbox.Group

@Decorator()
export default class ModalAddEditMarket extends React.Component {
    constructor(props) {
        super(props)
        this.defaultState = {
            type: '',
            typeName:'',
            // 用户配置
            enable: 0,
            userAdNumMax: '',
            userOrderNumMax: '',
            depositNum: '',
            userCancleTimes: '',
            complainTimes: '',
            displayCycle: "",
            intervalTime: '',

            // adBuyFee: '', adSellFee: '', buyMaxNum: "", buyMinNum: "", sellMaxNum: "", sellMinNum: "",
            // adValidTime: "", orderOverTime: "", coinBixDian: "", legalBixDian: "", coinId: "", legalId: "", sort: "",
            checkboxType: '1',
            isInsert: false,
            market: '',
            selectList: [],
            Fmarket: {
                value: '2',
                children: 'BTC'
            },
            Lmarket: {
                value: '3',
                children: 'CNY'
            },
            marketList:[], // 修改时全部市场的配置列表
            newMarkets:[],  // 新增时所用的市场列表
            tempObj:{  //  上传时构造对象  市场配置
                adBuyFee: "0",
                adSellFee: "0",
                adValidTime: '0',
                buyMaxNum: "0",
                buyMinNum: "0",
                // coinBixDian: '',
                // coinId: '',
                createTime: '0',
                createUserId: '0',
                enable: '0',
                id: '0',
                // legalBixDian: '',
                // legalId: '',
                market: "0",
                orderOverTime: '0',
                sellMaxNum: "0",
                sellMinNum: "0",
                // sort: '',
                type: '0',
            }
        }
        // this.modal = {
        //     // visible: false,
        //     // confirmLoading: false,
        //     // ModalText:'model',
        //     // coinType:'',
        //     // coinList:[],
        //     // coinSort:'',
        //     // coinId:'',
        //     // legalId:'',
        //     // sort:'',
        //     // coinBixDian:'',
        //     // legalBixDian:''
        // }
        this.state = {
            ...this.defaultState,
            // ...this.modal
        }
        this.goofn = () => new Map([
            ['freezeOrNo', (v) => this.saveAllDate(v)],    //是否冻结账户
            ['freeze', (v) => this.saveCoinType(v)],    //是否冻结账户
        ])
    }

    componentDidMount() {
        this.setProps(this.props)
    }

    componentWillReceiveProps(nextPorps) {
        this.setProps(nextPorps)
        console.log('----------',nextPorps)
    }

    setProps = async (props) => {
        await this.setState({
            ...this.defaultState,
            _type: props._type,
            _tk: props._tk
        })
        await this.requestSelec()
        await this.getAllMarkets()
        if (props.item) {
            await this.getMarketDetails()
            this.setState({isInsert: false})
        } else {
            await this.setMarketDeatails();
            this.setState({isInsert: true})
        }
        // if (props._type == 1) {
            if (props.item) {
                const {
                    type, enable, userAdNumMax, userOrderNumMax, depositNum, complainTimes, userCancleTimes, displayCycle,
                    intervalTime, id,typeName
                } = props.item
                this.setState({

                    type:type.toString(),
                    enable,
                    userAdNumMax,
                    userOrderNumMax,
                    depositNum,
                    complainTimes,
                    userCancleTimes,
                    displayCycle,
                    intervalTime,
                    id,
                    typeName
                })
            }
        // } else if (props._type == 2) {
        //     if (props.item) {
        //         const {
        //             market, type, enable, adBuyFee, adSellFee, buyMaxNum, buyMinNum, sellMaxNum, sellMinNum,
        //             adValidTime, orderOverTime, coinBixDian, legalBixDian, coinId, legalId, sort, id
        //         } = props.item
        //         this.setState({
        //             market, enable, adBuyFee, adSellFee, buyMaxNum, buyMinNum, sellMaxNum, sellMinNum,
        //             adValidTime, orderOverTime, coinBixDian, legalBixDian, coinId, legalId, sort, id,
        //             checkboxType: type
        //         })
        //     }
        // }
    }
    // checkboxChange
    inputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            item: Object.assign({}, this.state.item, {[name]: value})
        })
    }

    // 上一版本， 废弃
    onSave = async () => {
        const {
            _tk, _type, market, type, enable, adBuyFee, adSellFee, buyMaxNum, buyMinNum, sellMaxNum, sellMinNum,
            adValidTime, orderOverTime, coinBixDian, legalBixDian, coinId, legalId, sort,
            userAdNumMax, userOrderNumMax, depositNum, complainTimes, userCancleTimes, displayCycle,
            intervalTime, checkboxType, id, Fmarket, Lmarket
        } = this.state
        let urls = this.state.isInsert ? {
            1: '/userConfig/insert',
            2: '/marketConfig/insert'
        } : {
            1: '/userConfig/update',
            2: '/marketConfig/update'
        }
        let params = _type == 1 ? {
            type, enable, userAdNumMax, userOrderNumMax, depositNum, complainTimes, userCancleTimes, displayCycle,
            intervalTime, id, checkboxType
        } : {
            market: Fmarket.children + '/' + Lmarket.children,
            type: checkboxType,
            enable,
            adBuyFee,
            adSellFee,
            buyMaxNum,
            buyMinNum,
            sellMaxNum,
            sellMinNum,
            adValidTime,
            orderOverTime,
            coinBixDian,
            legalBixDian,
            coinId: Fmarket.value,
            legalId: Lmarket.value,
            sort,
            id
        }
        await this.request({url: urls[_tk], type: 'post', isP: true}, params)
        this.props.handleCancel()
        this.props.requestTable()
    }
    checkboxTypeSe = v => {
        this.setState({checkboxType: v})
    }
    // 所有币种
    requestSelec = async () => {
         let result = await this.request({url: '/common/coinAll'})
        // let result = await this.request({url: '/otcCointype/queryAttr'})
        let arr = [<Option key='0' value=''>请选择</Option>];
        for (let i in result) {
            arr.push(<Option value={result[i].fundsType} key={result[i].fundsType}>{result[i].propTag}</Option>)

        }
        this.setState({
            selectList:[...arr],
            coinList:[...result] // 全币种列表
        })
    }


    selectMarket = (v, _k) => {
        this.setState({
            [_k]: Object.assign({}, this.state[_k], v)
        })
    }
    saveCoinType = (v) =>{
        let {coinList,coinId,sort,coinBixDian,legalBixDian,isInsert} = this.state,
            name;
        for(let l of coinList){
            if (l.fundsType == coinId){
                name = l.propTag + '/CNY'
                break;
            }
        }
        let params = {
            market:name,
            enable:0,
            coinId:coinId,
            coinBixDian:coinBixDian,
            legalBixDian:legalBixDian,
            legalId:3,
            sort:sort,
        }
        // this.request({url: '/marketConfig/addMarket',type:'post',isP:true},params)
        axios.post('/marketConfig/addMarket',qs.stringify(params)).then((data) =>{
            console.log(data);
            // message.warning(data.msg);
            if (data.data.code == 0){
                message.success(data.data.msg);
                this.setState({
                    // visible: false,
                    confirmLoading: false,
                });
                this.closeModal();
                isInsert ? this.setMarketDeatails()  : this.getMarketDetails()
            }else {
                this.setState({
                    confirmLoading: false,
                });
                message.error(data.data.msg);
            }
        })
    }
    addMarketByCoin =  () =>{
        let id = ''
        // if (this.ckMarketType()){
            Modal.confirm({
                title:`你确定要保存吗？`,
                okText:'确定',
                okType:'more',
                cancelText:'取消',
                onOk:async () => {
                    this.modalGoogleCode(id, 'freeze', 'check')
                },
                onCancel(){
                    console.log('Cancel')
                }
            })
        // }
    }
    //
    getAllMarkets = async () =>{
        let result  = await this.request({url: '/common/getOtcMarketList',type:'post'})
        console.log(result);
    }
    checkoutUserType = () =>{
        let {type} = this.state,
            flg = true;
        if(!type) {
            message.warning('有必填内容填写不正确，请正确填写')
            flg = false
        }
        return flg
    }

    dataSave = () => {
        let id = '';
        // if (this.ckRequired() && this.checkoutUserType()) {
            Modal.confirm({
                title:`你确定要保存吗？`,
                okText:'确定',
                okType:'more',
                cancelText:'取消',
                onOk:async () => {
                    this.modalGoogleCode(id, 'freezeOrNo', 'check')
                },
                onCancel(){
                    console.log('Cancel')
                }
            })
        // }
    }
    /**
     * @Description 保存参数配置
     * @author Eric Ye
     * @date 2019-07-27
     */
    // 保存参数配置
    saveAllDate = async (v) =>{
        // if(this.ckRequired() && this.checkoutUserType() ){
            let params,templist = [],data;
            let {newMarkets,marketList, type, enable, userAdNumMax, userOrderNumMax,
                depositNum, complainTimes, userCancleTimes, displayCycle, intervalTime, id, checkboxType,isInsert
            } = this.state;
            let url = isInsert ?  '/userConfig/insert' : '/userConfig/update'
            let mkList = isInsert ? newMarkets : marketList
            for (let market of mkList){
                let _key = market.market.toString(),
                    _pre = _key.substr(0,_key.length - 4),
                    obj = Object.assign({},this.state.tempObj);
                Object.keys(obj).map(v=>{  !!this.state[`${_pre}` + v] && ({[`${_pre}` + v]:obj[v]}=this.state) }) // 解构 obj
                obj.market = _key;
                obj.type = type;
                templist.push(obj);
                obj = null
            }
            // 参数转jsonString 解决传list后台收不到的bug
            params ={
                userConfig:JSON.stringify({
                    type, enable, userAdNumMax, userOrderNumMax,
                    depositNum, complainTimes, userCancleTimes, displayCycle, intervalTime, id, checkboxType
                }),
                marketConfig:JSON.stringify([...templist])
            }

            await this.request({url: url,type:'post',isP:true},params)

            this.props.handleCancel()
            this.props.requestTable()
        // }

    }

    // 设置新增时 分类下的市场列表 form
    setMarketDeatails = async () =>{
        let obj = Object.assign({},this.state.tempObj);
        let result  = await this.request({url: '/common/getOtcMarketList',type:'post'})
        let list = result;
        this.setState({
            newMarkets: [...result],
            activeKey:list[0] ? list[0].market : ''
        })
        for (let market of list){
            let _key = market.market.toString(),
                _pre = _key.substr(0,_key.length - 4);
            for (let key in obj){
                this.setState({
                    [_pre + key]: key == 'enable' ? 0 : ''
                },() =>{
                    // console.log(`new form   [${_pre + key}]   =====    `+      this.state[_pre + key])
                })
            }
        }
    }

    // 获取用户资质下的币种详情
    getMarketDetails  = async () => {
        let {_type} = this.state,
            params = {
                type : _type
            }
        let result  = await this.request({url: '/userConfig/find',type:'post'},params)
        console.log(result);

        this.setState({
            marketList:[...result.marketConfigs],
            activeKey:result.marketConfigs[0] ? result.marketConfigs[0].market : ''
        },() =>{
            // console.log("old form" +  this.state)
        })

        let list = result.marketConfigs;
        for (let market of list){
            let _key = market.market.toString(),
                _pre = _key.substr(0,_key.length - 4);
            for (let key in market){
                this.setState({
                    [_pre + key]: market[key]
                },() =>{
                    // console.log(this.state[_pre + key])
                })
            }
        }
    }
    onChangeTab = activeKey => {
        this.setState({ activeKey });
    };

    handleOk = () => {
        // this.setState({
        //     confirmLoading: true,
        // });
        this.addMarketByCoin();

    }
    showModal = () =>{
        this.setState({
            visible: true,
        });
    }
    closeModal = () =>{
        this.setState({
            visible: false,
            coinType:'',
            // coinList:[],
            coinSort:'',
            coinId:'',
            legalId:'',
            sort:'',
            coinBixDian:'',
            legalBixDian:''
        });
    }
    ckEmpyt = (e) =>{
        console.log(e);
        if (!e.target.value && e.currentTarget.attributes.inputname.value){
            let inputname = e.currentTarget.attributes.inputname.value
            message.warning(`【${inputname}】内容不能为空!`,1.5)
            return false;
        }
    }
    ckRequired = (e) =>{
        let list = document.querySelectorAll('[required]');
        let flg = true;
        for (let l of list){
            if (!Number(l.value)) {
                message.warning('有必填内容填写不正确，请正确填写')
                flg = false;
                break;
            }
        }
        return flg
    }

    ckMarketType = () =>{
        let list = document.querySelectorAll('input.market-type');
        let flg = true;
        for (let l of list){
            if (!l.value) {
                message.warning('有必填内容填写不正确，请正确填写')
                flg = false;
                this.setState({
                    confirmLoading: false,
                });
                break;
            }
        }
        return flg
    }

    render() {
        const {
            market, type, typeName, enable, adBuyFee, adSellFee, buyMaxNum, buyMinNum, sellMaxNum, sellMinNum,
            adValidTime, orderOverTime, coinBixDian, legalBixDian, coinId, legalId, sort,
            userAdNumMax, userOrderNumMax, depositNum, complainTimes, userCancleTimes, displayCycle,
            intervalTime, checkboxType, selectList, Fmarket, Lmarket, visible, confirmLoading,ModalText,coinType,marketList,isInsert,newMarkets,coinSort
        } = this.state
        let templist = [];
        !isInsert ? templist = [...marketList] : templist = [...newMarkets]
        return (
            <div>
                {/* 用户资质配置 */}
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-5 control-label-large text-right">用户资质：<i>*</i></label>
                            <div className="col-sm-7">
                                <Select defaultValue={type} value={type}  style={{ width: SELECTWIDTH }} onChange={v => this.onSelectChoose(v, 'type')} name="type" disabled={!isInsert}>
                                    {this.props._userType}
                                </Select>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-5 control-label-large text-right">24小时累计取消次数：<i>*</i></label>
                            <div className="col-sm-7">
                                <input autoComplete="off"  type="text" className="form-control" name="userCancleTimes" inputname="24小时累计取消次数" required
                                       value={userCancleTimes} onChange={this.handleInputChange}/>
                                <span className="line34 marl10">次</span>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-5 control-label-large text-right">启用状态：<i>*</i></label>
                            <div className="col-sm-7">
                                <RadioGroup name='enable' onChange={this.handleInputChange} value={enable}>
                                    <Radio value={1}>启用</Radio>
                                    <Radio value={0}>禁用</Radio>
                                </RadioGroup>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-5 control-label-large text-right">累计申诉次数：<i>*</i></label>
                            <div className="col-sm-7">
                                <input autoComplete="off" type="text" className="form-control" name="complainTimes" value={complainTimes} inputname="累计申诉次数" required
                                       onChange={this.handleInputChange}/>
                                <span className="line34 marl10">次</span>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-5 control-label-large text-right">同时上架广告数：<i>*</i></label>
                            <div className="col-sm-7">
                                <input autoComplete="off" type="text" className="form-control" name="userAdNumMax" value={userAdNumMax} inputname="同时上架广告数" required
                                       onChange={this.handleInputChange}/>
                                <span className="line34 marl10">个</span>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-5 control-label-large text-right">指标显示周期：<i>*</i></label>
                            <div className="col-sm-7">
                                <input autoComplete="off" type="text" className="form-control" name="displayCycle" value={displayCycle}  inputname="指标显示周期" required
                                       onChange={this.handleInputChange}/>
                                {/* <b className="icon-fuzzy">个 </b> */}
                                <span className="line34 marl10">天</span>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-5 control-label-large text-right">同时进行订单数：<i>*</i></label>
                            <div className="col-sm-7">
                                <input autoComplete="off" type="text" className="form-control" name="userOrderNumMax" inputname="同时进行订单数" required
                                       value={userOrderNumMax} onChange={this.handleInputChange}/>
                                <span className="line34 marl10">个</span>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-5 control-label-large text-right">间隔时间：<i>*</i></label>
                            <div className="col-sm-7">
                                <input autoComplete="off" type="text" className="form-control" name="intervalTime" value={intervalTime} inputname="间隔时间" required
                                       onChange={this.handleInputChange}/>
                                <span className="line34 marl10">分</span>
                            </div>
                        </div>
                    </div>
                    <div className="col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-5 control-label-large text-right">保证金数量：<i>*</i></label>
                            <div className="col-sm-7">
                                <input autoComplete="off" type="text" className="form-control" name="depositNum" value={depositNum} inputname="保证金数量" required
                                       onChange={this.handleInputChange}/>
                                <span className="line34 marl10">usdt</span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* 市场配置  */}
                <div className="col-md-12 col-sm-12 col-xs-12">
                    {/* 市场列表   */}
                    <Tabs type="card" activeKey={this.state.activeKey} onChange={this.onChangeTab}>
                        {

                            templist.length > 0 ?
                            templist.map((v,i) => {
                                let _key = v.market.toString(),
                                    _pre = _key.substr(0,_key.length - 4);
                                return (
                                    <TabPane tab={v.market} key={v.market}>
                                        <div className="col-md-12 col-sm-12 col-xs-12">
                                            <div className="col-md-12 col-sm-12 col-xs-12">
                                                <div className="form-group">
                                                    <label className="col-sm-3 control-label-large text-right">启用状态：<i>*</i></label>
                                                    <div className="col-sm-9">
                                                        <RadioGroup name={`${_pre}enable`} onChange={this.handleInputChange} value={this.state[`${_pre}enable`]}>
                                                            <Radio value={1}>启用</Radio>
                                                            <Radio value={0}>禁用</Radio>
                                                        </RadioGroup>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="col-md-12 col-sm-12 col-xs-12">
                                                <div className="form-group">
                                                    <label className="col-sm-3 control-label-large text-right">手续费-购买广告：<i>*</i></label>
                                                    <div className="col-sm-9">
                                                        <input autoComplete="off" type="text" className="form-control" name={`${_pre}adBuyFee`} value={this.state[`${_pre}adBuyFee`]} required
                                                               onChange={this.handleInputChange}/>
                                                        <span className="line34 marl10"></span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="col-md-12 col-sm-12 col-xs-12">
                                                <div className="form-group">
                                                    <label className="col-sm-3 control-label-large text-right">手续费-出售广告：<i>*</i></label>
                                                    <div className="col-sm-9">
                                                        <input autoComplete="off" type="text" className="form-control" name={`${_pre}adSellFee`} value={this.state[`${_pre}adSellFee`]} required
                                                               onChange={this.handleInputChange}/>
                                                        <span className="line34 marl10"></span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="col-md-12 col-sm-12 col-xs-12">
                                                <div className="form-group">
                                                    <label className="col-sm-3 control-label-large text-right">广告有效期：<i>*</i></label>
                                                    <div className="col-sm-9">
                                                        <input autoComplete="off" type="text" className="form-control" name={`${_pre}adValidTime`} value={this.state[`${_pre}adValidTime`]} required
                                                               onChange={this.handleInputChange}/>
                                                        <span className="line34 marl10">天</span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="col-md-12 col-sm-12 col-xs-12">
                                                <div className="form-group">
                                                    <label className="col-sm-3 control-label-large text-right">订单时效：<i>*</i></label>
                                                    <div className="col-sm-9">
                                                        <input autoComplete="off" type="text" className="form-control"  name={`${_pre}orderOverTime`} value={this.state[`${_pre}orderOverTime`]} required
                                                               onChange={this.handleInputChange}/>
                                                        <span className="line34 marl10">分</span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="col-md-12 col-sm-12 col-xs-12">
                                                <div className="form-group">
                                                    <label className="col-sm-3 control-label-large text-right">出售限额：<i>*</i></label>
                                                    <div className="col-sm-8 ">
                                                        <div className="left col-sm-5 sm-box"><input autoComplete="off" type="text" placeholder='最小值'
                                                                                                     className="form-control" name={`${_pre}sellMinNum`} required
                                                                                                     value={this.state[`${_pre}sellMinNum`]}
                                                                                                     onChange={this.handleInputChange}/></div>
                                                        <div className="left line34">-</div>
                                                        <div className="left col-sm-5 sm-box"><input autoComplete="off" type="text" placeholder='最大值'
                                                                                                     className="form-control" name={`${_pre}sellMaxNum`} required
                                                                                                     value={this.state[`${_pre}sellMaxNum`]}
                                                                                                     onChange={this.handleInputChange}/></div>
                                                        <span className="line34 marl10">CNY   提示：包含边界值</span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="col-md-12 col-sm-12 col-xs-12">
                                                <div className="form-group">
                                                    <label className="col-sm-3 control-label-large text-right">买入限额：<i>*</i></label>
                                                    <div className="col-sm-8 ">
                                                        <div className="left col-sm-5 sm-box"><input autoComplete="off" type="text" placeholder='最小值'
                                                                                                     className="form-control" name={`${_pre}buyMinNum`} required
                                                                                                     value={this.state[`${_pre}buyMinNum`]}
                                                                                                     onChange={this.handleInputChange}/></div>
                                                        <div className="left line34">-</div>
                                                        <div className="left col-sm-5 sm-box"><input autoComplete="off" type="text" placeholder='最大值'
                                                                                                     className="form-control" name={`${_pre}buyMaxNum`} required
                                                                                                     value={this.state[`${_pre}buyMaxNum`]}
                                                                                                     onChange={this.handleInputChange}/></div>
                                                        <span className="line34 marl10">CNY   提示：包含边界值</span>
                                                    </div>
                                                </div>
                                            </div>
                                            {/*<div className="col-md-12 col-sm-12 col-xs-12">*/}
                                            {/*    <div className="form-group">*/}
                                            {/*        <label className="col-sm-3 control-label-large text-right">虚拟币小数点位数：<i>*</i></label>*/}
                                            {/*        <div className="col-sm-9">*/}
                                            {/*            <input autoComplete="off" type="text" className="form-control" name={`${_pre}coinBixDian`} value={this.state[`${_pre}coinBixDian`]}*/}
                                            {/*                   onChange={this.handleInputChange}/>*/}
                                            {/*            <span className="line34 marl10">位</span>*/}
                                            {/*        </div>*/}
                                            {/*    </div>*/}
                                            {/*</div>*/}
                                            {/*<div className="col-md-12 col-sm-12 col-xs-12">*/}
                                            {/*    <div className="form-group">*/}
                                            {/*        <label className="col-sm-3 control-label-large text-right">法币小数点位数：<i>*</i></label>*/}
                                            {/*        <div className="col-sm-9">*/}
                                            {/*            <input autoComplete="off" type="text" className="form-control" name={`${_pre}legalBixDian`} value={this.state[`${_pre}legalBixDian`]}*/}
                                            {/*                   onChange={this.handleInputChange}/>*/}
                                            {/*            <span className="line34 marl10">位</span>*/}
                                            {/*        </div>*/}
                                            {/*    </div>*/}
                                            {/*</div>*/}
                                            {/*<div className="col-md-12 col-sm-12 col-xs-12">*/}
                                            {/*    <div className="form-group">*/}
                                            {/*        <label className="col-sm-3 control-label-large text-right">顺序：<i>*</i></label>*/}
                                            {/*        <div className="col-sm-9">*/}
                                            {/*            <input autoComplete="off" type="text" className="form-control" name={`${_pre}sort`} value={this.state[`${_pre}sort`]}*/}
                                            {/*                   onChange={this.handleInputChange}/>*/}
                                            {/*        </div>*/}
                                            {/*    </div>*/}
                                            {/*</div>*/}
                                        </div>
                                    </TabPane>
                                )
                            })
                            :
                            null
                            }
                    </Tabs>
                    <div className="add-marketing" onClick={this.showModal}>新增市场</div>
                </div>

                <div className='col-md-12 col-sm-12 col-xs-12 line marbot10'></div>
                {/*  保存footer  */}
                <div className="col-md-4 col-sm-4 col-xs-4 right">
                    <div className="right">
                        <Button key="back" onClick={this.props.handleCancel}>取消</Button>
                        <Button key="submit" type="more" onClick={this.dataSave}>保存</Button>
                    </div>
                </div>
                {/* 对话框 */}
                <Modal
                    title="新增市场"
                    visible={visible}
                    onOk={this.handleOk}
                    confirmLoading={confirmLoading}
                    onCancel={this.closeModal}
                    okText="确认"
                    cancelText="取消"
                    width={700}
                >
                    <div className="col-md-12 col-sm-12 col-xs-12" id="marketType">
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label-large text-right">市场全称：<i>*</i></label>
                                <div className="col-sm-3">
                                    <Select value={coinId} style={{ width: 100 }} onChange={v => this.onSelectChoose(v, 'coinId')} name="numCoin" inputname="市场全称" >
                                        {selectList}
                                    </Select>
                                </div>
                                <div className="col-sm-1" style={{ fontSize: '22px'}}>/</div>
                                <div className="col-sm-3">
                                    <Select value={3} style={{ width: 100 }}  name="otcCoin" >
                                        <Option value={3} >{'CNY'}</Option>
                                    </Select>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label-large text-right">虚拟币小数点位数：<i>*</i></label>
                                <div className="col-sm-9">
                                    <input autoComplete="off" type="text" className="form-control market-type" inputname="虚拟币小数点位数"  name="coinBixDian"  value={coinBixDian}
                                           onChange={this.handleInputChange}  />
                                    <span className="line34 marl10">位</span>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label-large text-right">法币小数点位数：<i>*</i></label>
                                <div className="col-sm-9">
                                    <input autoComplete="off" type="text" className="form-control market-type" inputname="法币小数点位数"  name="legalBixDian" value={legalBixDian}
                                           onChange={this.handleInputChange}  />
                                    <span className="line34 marl10">位</span>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label-large text-right">顺序：<i>*</i></label>
                                <div className="col-sm-9">
                                    <input autoComplete="off" type="text" className="form-control market-type" inputname="顺序"   name="sort" value={sort}
                                           onChange={this.handleInputChange}  />
                                    <span className="line34 marl10"></span>
                                </div>
                            </div>
                        </div>
                        <div className="col-md-12 col-sm-12 col-xs-12">
                            <div className="form-group">
                                <label className="col-sm-3 control-label-large text-right"><i>注：</i></label>
                                <div className="col-sm-9">
                                    <span className="line34 marl10">新增市场后，需要对所有资质的用户进行该市场的配置</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    {/*</div>*/}
                </Modal>
            </div>
        )
    }
}