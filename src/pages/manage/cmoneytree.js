import React from 'react';
import {FormattedMessage, injectIntl, FormattedDate, } from 'react-intl';
import Nav from "../../components/navigator/nav";
import {browserHistory} from 'react-router'
import {Link} from "react-router";
import {optPop, formatDate} from "../../utils";
import {getUserBaseInfo} from "../../redux/modules/session";
import {connect} from "react-redux";
import Pages from "../../components/pages";
import {CopyToClipboard} from "react-copy-to-clipboard";
import {Dialog} from '../money/dialog'
import EntrustModal from "../../components/entrustBox";
import Transfer from "./transfer";
import {userFinCenInfo, centerDetailsList, tableList, centerInfo} from '../money/index.model'
import '../../assets/css/money.less'
import axios from "axios";
import {DOMAIN_VIP,MONEYMANAGEMENTBASE} from "../../conf";
import RSC from 'react-scrollbars-custom';
import qs from 'qs';

// 附属表格
import MTree from './mTree';
/**
 * 理财中心组件
 */

class CmoneyTree extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            centerInfo: '',
            totalNum:'',
            detailsList: '',
            chax:'',
            i:0,
            list: [],
            isLoading: true,
            pagesize: 10,
            pageIndex: 1,
            totalCount: 0,
        }
        this.test = {    //分页测试json
            total: '23',
            pagesize: '5',
            pageIndex: '1'
        }
        this.openDialogIn = this.openDialogIn.bind(this); 
        this.openDialogIns = this.openDialogIns.bind(this);
        this.openDialogInse = this.openDialogInse.bind(this);
        // this.currentPageClick = this.currentPageClick.bind(this);
        this.getInfo = this.getInfo.bind(this);
        this.getTableList = this.getTableList.bind(this);
        this.getVollar = this.getVollar.bind(this);
        this.lc = this.lc.bind(this);
        this.handlePageChanged = this.handlePageChanged.bind(this);
        this.getRes = this.getRes.bind(this);
    }

    getRes(){
        const { pageIndex, } = this.state;
        this.setState({
            isLoading: true,
        }, () => {
            // 创造mock数据
            // setTimeout(()=>{
            // this.setState({
            //     totalCount: 200,
            //     pageIndex,
            //     list: [{
            //         id:1,
            //         hierarchyLevel:'hierarchyLevel',
            //         hierarchyTotalNum:'hierarchyTotalNum',
            //         userActiveNum:'userActiveNum',
            //         userEmptyNum:'userEmptyNum',
            //     }],
            //     isLoading: false,
            // })},3000)

            axios.get(DOMAIN_VIP + '/manage/financial/userHierarchyInfo?' + qs.stringify({pageIndex})).then((res) => {
                const {totalCount, pageIndex, list, } = res.data.datas;
                this.setState({
                    totalCount,
                    pageIndex,
                    list,
                    isLoading: false,
                });
            }).catch(()=>{
                this.setState({
                    isLoading: false,
                })
            });
        })
    }

    handlePageChanged(pageIndex = 1){

        this.setState({
            pageIndex
        }, () => {
            this.getRes();
        })
    }

    lc(){
        axios.get(DOMAIN_VIP+'/manage/financial/userFinVdsBalance').then((data)=>{
            const datas = data.data.datas;
            //console.log(datas, '====>>>')
            try{
                this.setState({
                    centerInfo:{
                        ...this.state.centerInfo,
                        ...datas,
                    }
                })
            } catch(e){

            }
        });
    }
    componentWillMount() {
        // localStorage.setItem('1003614ispay',1)
    }

    componentDidMount() {
        this.getRes();
        // 数据get
        this.getTableList();
        this.getInfo();
        //setInterval(()=>{
            this.getInfo();
        //},3000)
        // setInterval(()=>{
        //     this.getVollar();
        // },2000)
           // },2000)
           axios.get(DOMAIN_VIP+'/manage/financial/userInvitationNum').then((data)=>{
            const datas = data.data.datas;
            
            try{
                this.setState({
                    totalNum:datas.totalNum
                });
                
            } catch(e){

            }
        });
        this.lc();
        setInterval(()=>{
            this.lc();
        },3000)

    }

    getVollar(){
        axios.get(DOMAIN_VIP+'/manage/financial/userFinancialInfo').then((data)=>{
            let vollar = data.data.datas
            this.setState({
                centerInfo:centerInfo
            })
        })
    }
    // 翻页
    // currentPageClick(index) {
    //     console.log(index);
    // }

    // 获取 用户信息渲染数据或跳转
    getInfo() {
        userFinCenInfo().then((res) => {
            this.setState({
                 
                centerInfo: {
                    ...this.state.centerInfo,
                    ...res,
                }
            },()=>{
                // console.log('vollar done')
                // this.forceUpdate();
            })
        })
    }

    // 获取收益详情
    getTableList() {
        centerDetailsList().then((res) => {
            this.setState({
                detailsList: res
            })
        })
    }

    // 资金划转内部弹窗用 - open
    openDialogIn() {
        this.setState({dialog: <Transfer closeModal={this.modal.closeModal} fromtype={5} totype={1} fundsType='VDS'/>});
        this.modal.openModal();
    }
    openDialogIns(){
        if(this.state.chax==""){
            document.getElementById('charting_library').contentWindow.location.reload(true);
        }else{
            this.setState(
                {
                 chax:''
                }
            )
        }
       
    }
   openDialogInse(){
   var  url="&username="+ document.getElementById('youxiang').value+"&i="+this.state.i+1
   this.setState(
       {
        chax:url,
        i:this.state.i+1
       }
   )
   }
    render() {
        const URL = [{name: '理财中心', link: MONEYMANAGEMENTBASE +  'cmoney'},{name: '团队邀请', link: MONEYMANAGEMENTBASE +  'cmoneytree'}];
        const userInfo = this.props.baseUserInfo;
        const {formatMessage} = this.props.intl;
        let {centerInfo, detailsList,chax,pageIndex,pagesize,totalCount,isLoading,list} = this.state;
        //console.log(centerInfo);
        // detailsList = []
        // console.log(this.props.location.pathname);
        
        return (
            <div className="mwp mwp_d cmoney">
                <div className={'ja' === this.props.language.locale ? 'cmy':'cmy2'}>
                <Nav path={this.props.location.pathname} ay={URL}/>
                </div>
                <div className="min_wp min_h527_d">
                    <div className="account clearfix">
                        {/*  用户信息 */}
                        <div className="top clearfix" style={{padding: '45px 60px'}}>
                            <div className="account-left">
                                <svg className="icon" aria-hidden="true">
                                    <use xlinkHref="#icon-zhanghu-yonghutouxiang"></use>
                                </svg>
                            </div>
                            <div className="account-right" style={{marginBottom:'20px'}}>
                                <p><span>UID: {userInfo.userName}</span></p>
                                <p className="clearMarPa"><span>VID: </span><span>{centerInfo.userVID}</span></p>
                                <p>
                                    <span>{formatMessage({id: "理财账户可用数量:"})} </span><span>{centerInfo.userFinVdsAmount} Vollar</span>
                                    <span
                                    className="money-btn mar-lt20"
                                    onClick={this.openDialogIn}>{formatMessage({id: "划转"})}</span></p>
                            </div>
                            <div className="spploading">
                                
                                <MTree isLoading={isLoading} list={list} />

                                <div className="bk-pageNav">
                                    <div className="tablist deposits" style={{display: totalCount > pagesize ? 'block' : 'none'  }}>
                                        <Pages 
                                        pageIndex={pageIndex}
                                        pagesize={pagesize}
                                        total={totalCount}
                                        currentPageClick = { this.handlePageChanged }
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>
                       
                        <div className="account-center"></div>
                        
                        <div className="charting_librarys">
                        <span
                                    className="money-btn mar-lt20 ssea" 
                                    onClick={this.openDialogIns}></span>
                        <h2>{formatMessage({id: "团队邀请"})}:{this.state.totalNum}</h2>
                        <div className="rres">
                            <input type="text" id="youxiang" placeholder={formatMessage({id: "nuser118"})}   />
                            <span
                                    className="money-btn mar-lt20 " 
                                    style={{"margin-left":"10px"}}
                                    onClick={this.openDialogInse}>
                                       {formatMessage({id: "搜索"})} 
                                    </span>
                        </div>
                            <RSC style={{width:'100%',height:'500px'}}>
                            <iframe className="charting_library" id="charting_library" width="100%" height="100%" style={{minWidth:'100%'}} src={`/bw/src/charting_library/index.html?admin=${DOMAIN_VIP}`+chax}></iframe>
                            </RSC>  
                        </div>

                    </div>
                </div>
                {/*分页*/}
                {/*<div className="tablist">*/}
                {/*    <Pages*/}
                {/*        {...this.test}*/}
                {/*        currentPageClick={this.currentPageClick}*/}
                {/*    />*/}
                {/*</div>*/}
                {/* 对话框 */}
                <EntrustModal ref={modal => this.modal = modal}>
                    {this.state.dialog}
                </EntrustModal>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        language: state.language,
        baseUserInfo: state.session.baseUserInfo
    }
};
const mapDispatchToProps = {
    getUserBaseInfo
};


export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(CmoneyTree));