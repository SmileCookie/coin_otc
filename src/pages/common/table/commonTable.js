import React from 'react';
import { Table } from 'antd'
import { PAGESIZE, PAGRSIZE_OPTIONS20, PAGEINDEX } from '../../../conf'


export default class CommonTable extends React.Component {
    constructor(props) {
        super(props)
    }
    render() {
        return (
            <Table
                dataSource={this.props.dataSource ? this.props.dataSource : []}
                bordered
                rowKey={r => r.id || r.key}
                scroll={this.props.scroll ? this.props.scroll : {}}
                rowSelection={this.props.rowSelection ? this.props.rowSelection : null}
                columns={this.props.columns}
                pagination={this.props.pagination ? {
                    size: "small",
                    onChange: (pageIndex, pageSize) => this.props.requestTable && this.props.requestTable(pageIndex, pageSize),
                    showTotal: total => `总共 ${total} 条`,
                    onShowSizeChange: (pageIndex, pageSize) => this.props.requestTable && this.props.requestTable(pageIndex, pageSize),
                    pageSizeOptions: PAGRSIZE_OPTIONS20,
                    defaultPageSize: PAGESIZE,
                    showSizeChanger: true,
                    showQuickJumper: true,
                    ...this.props.pagination
                } : false}
                locale={{ emptyText: '暂无数据' }}
            />
        )
    }

}
