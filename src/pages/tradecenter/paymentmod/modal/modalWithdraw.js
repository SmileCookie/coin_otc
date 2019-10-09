// import React from 'react'
// import { toThousands } from '../../../../utils'

// const ModalWithdraw = ({btnType,tableList,handleInputChange}) => {
//     console.log(btnType)
//     return (
//         <div className="col-md-12 col-sm-12 col-xs-12">
//             <div className="table-responsive">
//                 <table border='1' className="table table-striped jambo_table bulk_action table-linehei">
//                     <thead>
//                         <tr className="headings">
//                             <th className="column-title">序号</th>
//                             <th className="column-title">资金类型</th>
//                             <th className="column-title">选中笔数</th>
//                             <th className="column-title">提现金额</th>
//                             <th className="column-title">实际提现金额</th>
//                             <th className="column-title">提现网络费</th>
//                             {btnType=='playCoin'&&<th className="column-title">热提钱包余额</th>}
//                             {btnType=='playCoin'&&<th className="column-title">资金是否充足</th>}
//                             {btnType=='playCoin'&&<th className="column-title">用户资金状态</th>}                
//                         </tr>
//                     </thead>
//                     <tbody>
//                         {
//                             tableList.map((item,index) => {
//                                 return (
//                                     <tr key={index}>
//                                         <td>{index+1}</td>
//                                         <td>{item.fundstypename}</td>
//                                         <td>{item.count}</td>
//                                         <td>{toThousands(item.amount)}</td>
//                                         <td>{toThousands(item.afterAmount)}</td>
//                                         <td>{item.fees}</td>
//                                         {btnType=='playCoin'&&<td>{item.walletBalance}</td>}
//                                         {btnType=='playCoin'&&<td className={item.enough?'':'red'}>{item.enough?"是":"否"}</td>}
//                                         {btnType=='playCoin'&&<td className={item.customerOperation!='03'?'red':''}>{item.customerOperation=='03'?'正常':'异常'}</td>}  
//                                     </tr>
//                                 )
//                             })
//                         }
//                     </tbody>
//                 </table>
//             </div>
//         </div>
//     )
// }


// export default ModalWithdraw;





















