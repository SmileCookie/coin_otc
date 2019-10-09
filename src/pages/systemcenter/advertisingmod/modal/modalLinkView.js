import React, { Component } from 'react'
import { Tabs,Table } from 'antd'
const { TabPane } = Tabs
const { Column } = Table
class ModalLinkView extends Component {
    constructor(props){
        super(props)
        this.state = {
            key:'cn'
        }

    }
    componentDidMount(){
    }
    componentWillReceiveProps(nextProps){
        this.setState({
            key:'cn'
        })
    }
    callback = key => {
        this.setState({
            key
        })
    }
    render(){
        const { links,type } = this.props;
        const { key } = this.state
        const tableSource = [];
        links.key = links.cn
        tableSource.push(links)

        return (
            <div className="col-md-12 col-sm-12 col-xs-12 hei400 table-responsive table-rap-first">
                <Tabs defaultActiveKey='cn' onChange={this.callback} activeKey={key}>
                    <TabPane tab='简体' key="cn"></TabPane>
                    <TabPane tab='繁体' key="hk"></TabPane>
                    <TabPane tab='英文' key="en"></TabPane>
                    <TabPane tab='日文' key="jp"></TabPane>
                    <TabPane tab='韩文' key="kr"></TabPane>
                </Tabs>
                <Table dataSource={tableSource} bordered pagination={false}>
                    <Column title='链接' dataIndex={key} key='cn' render={text=><a href={text} target='_blank'>{text}</a>}/>
                    {type ==1&&<Column title='图片' dataIndex={key} key='src' render={text=>
                        <div style={{width:"270px",height:"150px"}}>
                            <img style={{width:'100%',height:'100%'}} src={text} alt='图片'/>
                        </div>}/>}
                </Table>
            </div>)
    }
}
export default ModalLinkView