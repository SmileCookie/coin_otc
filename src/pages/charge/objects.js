import React from 'react';
import Form from '../../decorator/form';
import { DOMAIN_VIP,PAGEINDEX,PAGESIZE } from '../../conf'
import '../../assets/css/table.less';
import axios from 'axios';
import '../../assets/css/chargeList.less'


@Form
class WithdrawalCharge extends React.Component{
    constructor(props){
        super(props)

        this.state = {
            tableList:[
              
            ],
        }
        this.requestTable = this.requestTable.bind(this)
    }
    componentDidMount(){
      
        this.requestTable();
    }

    requestTable(){
        axios.get(DOMAIN_VIP+"/getMinFees").then(res => {
            const result = res.data
               
            if(result.isSuc){
                let _arr =[];
                console.log(result.datas);
                for(let i in result.datas){
                    let _i = i.toUpperCase();
                    let _data = result.datas[i] + ' '+ _i
                    _arr.push({name:_i,data:_data})
                }
                //console.log(_arr)         
                this.setState({
                    tableList:_arr
                })
            }
        })
        
    }
   
    render(){
        const {tableList} = this.state;
        const { formatMessage } = this.intl;
        const integral = this.props.integral;
       
        return(
             <div className="tableContent">  
                     <div className="grade-effect">
                        <div className="grade-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>{formatMessage({id: "提现币种"})}</th>
                                        <th>{formatMessage({id: "网络手续费"})}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                {
                                    tableList.length>0&&tableList.map((item,index)=>{
                                        return(
                                            <tr key={index}>
                                                <td>{item.name}</td>
                                                <td>{item.data}</td>
                                            </tr>
                                        )
                                    })
                                }
                                </tbody>
                            </table>
                        </div>
                    </div>
             </div>
        )
    }
}
export default WithdrawalCharge