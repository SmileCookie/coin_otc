import React, { Component } from "react";
import { Table, Icon, Spin } from 'antd'
import { USER_EXCHANGE_NUM_RANKING } from '../../static/actionType'


export class TradeTable extends Component {
    constructor(props) {
        super(props)
        this.state = {
            columns: [],
            tableData: [],
            loading: false
        }

    }
    componentDidMount() {
        this.setDefaultState(this.props)
        // console.log(this.props)
    }
    componentWillReceiveProps(nextProps) {
        this.setDefaultState(nextProps)
        // console.log(nextProps)
    }
    setDefaultState = props => {
        if (props) {
            const { columns, tableData, loading } = props
            this.setState({ columns, tableData, loading: loading })
        }
    }
    render() {
        const { columns, tableData } = this.state;
        let tableStyle = Object.assign({}, { paddingLeft: '20px', paddingRight: '20px', border: 'none', marginTop: '20px',marginBottom: '20px' }, this.props.style ? this.props.style : {})
        return (
            this.state.loading
                ?
                <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)' }}>
                    <Spin indicator={<Icon type="loading" style={{ fontSize: 60 }} spin />}></Spin>
                </div>
                :
                <div style={{height: this.props.title == USER_EXCHANGE_NUM_RANKING?'100%': ''}}>
                    {!this.props.hideTitle && this.props.title && <h5 style={{ marginLeft: '10px', marginTop: 0, fontWeight: 600, paddingTop: '5px',position:'absolute',zIndex:'10' }}>{this.props.title}</h5>}
                    {!this.props.hideTitle && this.props.title && <h5 style={{height: '5px', }}></h5>}
                    {tableData.length > 0 ? <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 table-responsive table-cock" style={tableStyle}>
                    {this.props.title == USER_EXCHANGE_NUM_RANKING ?
                        <ul className='cock-ul'>
                        {tableData.map((item, index) => {
                            return <li key={item.id || index} >
                                <p>{item.username}</p><p>{item.proportion}%</p>
                                <div className='precent' style={{width: `${item.proportion}%` }}></div>
                            </li>
                        })}
                    </ul>:
                    <Table
                        rowKey="id"
                            columns={columns}
                            dataSource={tableData}
                            bordered
                            locale={{ emptyText: '暂无数据' }}
                            pagination={false}
                        />}
                    </div> : <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)', fontSize: '16px' }}>暂无数据</div>}
                </div>

        )
    }
}

export class ListComp extends Component {
    constructor(props) {
        super(props)
        this.state = {
            loading: false,
            tableData: [1,2,3,4,5,6,7,8,9]
        }

    }
    static get defaultProps() {
        return {
            title: false
        }
    }
    render() {
        const { tableData } = this.state
        let tableStyle = Object.assign({}, { paddingLeft: '20px', paddingRight: '20px', border: 'none', marginTop: '20px' }, this.props.style ? this.props.style : {})
        return (
            this.state.loading
                ?
                <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)' }}>
                    <Spin indicator={<Icon type="loading" style={{ fontSize: 60 }} spin />}></Spin>
                </div>
                :
                <div>
                    {this.props.title && <h5 style={{ marginLeft: '20px', marginTop: 0, fontWeight: 600, paddingTop: '5px' }}>{this.props.title}</h5>}
                    {tableData.length > 0 ? <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12 table-responsive table-cock" style={tableStyle}>
                        <ul className='cock-ul'>
                            {tableData.map((item, index) => {
                                return <li key={item} >
                                    <p>Max</p><p>10%</p>
                                    <div style={{width: '100%' }}></div>
                                </li>
                            })}
                        </ul>
                    </div> : <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)', fontSize: '16px' }}>暂无数据</div>}
                </div>

        )
    }
}
