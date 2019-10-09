// import React from 'react'
// import axios from '../../../../utils/fetch'
// import qs from 'qs'
// import { DOMAIN_VIP } from '../../../../conf'


// export default class ModalSure extends React.Component{

//     constructor(props){
//         super(props)
//         // this.state = {
//         //     hasAmount:''
//         // }
//         // this.requestTable = this.requestTable.bind(this)
//     }

//     // componentDidMount(){
//     //     this.requestTable(this.props.item)
//     // }

//     // componentWillReceiveProps(nextProps){
//     //     this.requestTable(nextProps.item)
//     // }

//     // requestTable(item){
//     //     axios.post(DOMAIN_VIP+"/accountManage/query",qs.stringify({
//     //         id:0,
//     //         fundType: item.fundstype,
//     //         type: 3,
//     //         pageIndex: 1,
//     //         pageSize: 10
//     //     })).then(res => {
//     //         const result = res.data
//     //         if(result.code == 0){
//     //             this.setState({
//     //                 hasAmount:result.data.finanaccountList[0].amount
//     //             })
//     //             // const sureBtn = this.props.item.afterAmount > result.data.finanaccountList[0].amount;
//     //             // this.props.setSureBtn(sureBtn)
//     //         }
//     //     })
//     // }

//     render(){
//         // const { hasAmount } = this.state
//         return(
//             <div className="col-md-12 col-sm-12 col-xs-12">
//                 <div className="col-md-12 col-sm-12 col-xs-12">
//                     <div className="form-group">
//                         <label className="col-sm-3 control-label">转入地址：</label>
//                         <div className="col-sm-8">
//                             <span className="line34 blue">{this.props.item.toaddress}</span>
//                             <span className="line34 red">请核实地址后提现给用户</span>
//                         </div>
//                     </div>
//                 </div>
//                 <div className="col-md-12 col-sm-12 col-xs-12">
//                     <div className="form-group">
//                         <label className="col-sm-3 control-label">转出数量：</label>
//                         <div className="col-sm-8">
//                             <span className="line34">{this.props.item.afterAmount}</span>
//                         </div>
//                     </div>
//                 </div>
//                 <div className="col-md-12 col-sm-12 col-xs-12">
//                     <div className="form-group">
//                         <label className="col-sm-3 control-label">账户类型：</label>
//                         <div className="col-sm-8">
//                             <span className="line34">{this.props.item.fundstypename}提现账户 | 余额 {this.props.hasAmount}</span>
//                         </div>
//                     </div>
//                 </div>
//             </div>
//         )
//     }


// }















