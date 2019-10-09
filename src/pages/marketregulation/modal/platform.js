import React from 'react'
import qs from 'qs'
import { Table } from 'antd'
import moment from 'moment'
import axios from '../../../utils/fetch'
import { DOMAIN_VIP, SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT, PAGRSIZE_OPTIONS20, DAYFORMAT } from '../../../conf'
const columns = [{
    title: '序号',
    dataIndex: 'index',
    key: 'index',
}, {
    title: '用户编号',
    dataIndex: 'userId',
    key: 'userId',
}, {
    title: '登录时间',
    dataIndex: 'date',
    key: 'date',
    render: (record) => record ? moment(record).format(TIMEFORMAT_ss) : '--',
}, {
    title: 'IP',
    dataIndex: 'ip',
    key: 'ip',
    render: (record) => {
        return <a href={`http://www.ip138.com/ips138.asp?ip=${record}&action=2`} target='_blank'>{record}</a>
    }
}, {
    title: '城市',
    dataIndex: 'city',
    key: 'city',
}, {
    title: '登录方式',
    dataIndex: 'describe',
    key: 'describe',
}];
export default class PlatformModal extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            pageIndex: PAGEINDEX,
            pageSize: PAGESIZE,
            pageTotal: DEFAULTVALUE,
            tableSource:[],
            userId:''
        }

    }
    componentDidMount() {
        this.setState({
            userId:this.props.userId
        },()=>{
            this.requestTable()
        })
    }
    componentWillReceiveProps(nextProps) {
        this.setState({
            userId:nextProps.userId
        },()=>{
            this.requestTable()
        })
    }
    requestTable = (currentIndex, currentSize) => {
        const { pageIndex, pageSize,userId} = this.state
        axios.post(DOMAIN_VIP + '/loginInfo/queryList', qs.stringify({
            userId,
            pageIndex: currentIndex || pageIndex,
            pageSize:  currentSize || pageSize,

        })).then(res => {
            const result = res.data;
            // console.log(result)
            if (result.code == 0) {
                let tableSource = result.data.list || [];
                for (let i = 0; i < tableSource.length; i++) {
                    tableSource[i].index = (result.data.currPage - 1) * result.data.pageSize + i + 1;
                    tableSource[i].key = tableSource[i].id
                }
                this.setState({
                    tableSource,
                    pageTotal:result.data.totalCount
                })
            } else {
                message.warning(result.msg);
            }
        })
    }
    onChangePageNum = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        }, () => {

            this.requestTable(pageIndex, pageSize)
        })
    }
    onShowSizeChange = (pageIndex, pageSize) => {
        this.setState({
            pageIndex,
            pageSize
        }, () => {

            this.requestTable(pageIndex, pageSize)
        })
    }
    render() {
        const { tableSource,pageIndex,pageSize,pageTotal } = this.state
        return (
            <div className='table-responsive'>
                <Table
                    dataSource={tableSource}
                    columns={columns}
                    scroll={{x:1400}}
                    bordered
                    locale={{ emptyText: '暂无数据' }}
                    className='title_cont'
                    pagination={{
                        size: "small",
                        pageSize: pageSize,
                        current: pageIndex,
                        total: pageTotal,
                        onChange: this.onChangePageNum,
                        showTotal: total => `总共 ${total} 条`,
                        onShowSizeChange: this.onShowSizeChange,
                        pageSizeOptions: PAGRSIZE_OPTIONS20,
                        defaultPageSize: PAGESIZE,
                        showSizeChanger: true,
                        showQuickJumper: true

                    }} />
            </div>
        )
    }
}