import React from 'react'
import axios from 'axios'
import qs from 'qs'
import { COIN_KEEP_POINT,LEVEL_RULES,DOMAIN_VIP } from '../../../conf'
import { FormattedMessage } from 'react-intl'

export default class levelRule extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            tableList:[]
        }
        this.requestTable = this.requestTable.bind(this)
    }

    componentDidMount(){
        this.requestTable()
    }

    requestTable(){
        axios.get(DOMAIN_VIP+"/manage/level/level").then(res => {
            const result = res.data
            if(result.isSuc){
                this.setState({
                    tableList:result.datas.userVipList
                })
            }
        })
    }


    render(){
        const { tableList } = this.state
        return (
            <div className="level-box">
                <h4 className="sub-tit mt30"><FormattedMessage id="level.rule1" /></h4>
                <table className="table table-striped table-bordered text-left table-level">
                <thead>
                    <tr>
                        <th><FormattedMessage id="level.rule2" /></th>
                        <th><FormattedMessage id="level.rule3" /></th>
                        <th><FormattedMessage id="level.rule4" /></th>
                        <th><FormattedMessage id="level.rule5" /></th>
                    </tr>
                </thead>
                <tbody>
                    {
                        tableList.length>0&&tableList.map((item,index)=>{
                            return(
                                <tr key={index}>
                                    <td>VIP-{item.vipRate}</td>
                                    <td>{item.jifen}</td>
                                    <td>{item.discount}%</td>
                                    <td>{item.memo?item.memo:"--"}</td>
                                </tr>
                            )
                        })
                    }
                </tbody>
            </table>
        </div>
        )
    }
}