
import React from 'react'
import { Tabs,Tree,Button,message } from 'antd'
const TabPane = Tabs.TabPane;
const TreeNode = Tree.TreeNode;

message.config({
    top: 100,
    duration:3,
});

const treeData = [{
    title: '0-0',
    key: '0-0',
    children: [{
      title: '0-0-0',
      key: '0-0-0',
      children: [
        { title: '0-0-0-0', key: '0-0-0-0' },
        { title: '0-0-0-1', key: '0-0-0-1' },
        { title: '0-0-0-2', key: '0-0-0-2' },
      ],
    }, {
      title: '0-0-1',
      key: '0-0-1',
      children: [
        { title: '0-0-1-0', key: '0-0-1-0' },
        { title: '0-0-1-1', key: '0-0-1-1' },
        { title: '0-0-1-2', key: '0-0-1-2' },
      ],
    }, {
      title: '0-0-2',
      key: '0-0-2',
    }],
  }, {
    title: '0-1',
    key: '0-1',
    children: [
      { title: '0-1-0-0', key: '0-1-0-0' },
      { title: '0-1-0-1', key: '0-1-0-1' },
      { title: '0-1-0-2', key: '0-1-0-2' },
    ],
  }, {
    title: '0-2',
    key: '0-2',
  }];

export default class Finance extends React.Component{

    constructor(props){
        super(props)
        this.state = {
            expandedKeys: ['0-0-0', '0-0-1'],
            autoExpandParent: true,
            checkedKeys: ['0-0-0'],
            selectedKeys: [],
        }
        this.callback = this.callback.bind(this)
        this.onExpand = this.onExpand.bind(this)
        this.onCheck = this.onCheck.bind(this)
        this.onSelect = this.onSelect.bind(this)
        this.renderTreeNodes = this.renderTreeNodes.bind(this)
        this.success = this.success.bind(this)
        this.error = this.error.bind(this)
        this.warning = this.warning.bind(this)
        this.messageInfo = this.messageInfo.bind(this)
    }

    //提示框
    messageInfo(){
        message.info('This is a message of Info');
    };
    success(){
        message.success('This is a message of success');
    };
      
    error(){
        message.error('This is a message of error');
    };
      
    warning(){
        message.warning('This is message of warning');
    };

    //tree function
    onExpand(expandedKeys){
        // if not set autoExpandParent to false, if children expanded, parent can not collapse.
        // or, you can remove all expanded children keys.
        this.setState({
          expandedKeys,
          autoExpandParent: false,
        });
    }
    onCheck(checkedKeys){
        this.setState({ checkedKeys });
    }
    onSelect(selectedKeys, info){
        this.setState({ selectedKeys });
    }
    renderTreeNodes(data){
        return data.map((item) => {
          if (item.children) {
            return (
              <TreeNode title={item.title} key={item.key} dataRef={item}>
                {this.renderTreeNodes(item.children)}
              </TreeNode>
            );
          }
          return <TreeNode {...item} />;
        });
    }

    callback(key) {
        console.log(key);
    }

    render(){
        return(
            <div className="right-con">
                <div className="page-title">
                    当前位置：财务中心>账务管理>账户管理
                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                      <div className="x_panel">  
                        <Tabs onChange={this.callback} type="card">
                            <TabPane tab="Tab 1" key="1">
                                <div className="x_panel">
                                    <div className="x_title">
                                        <h3>查询</h3>
                                    </div>
                                    <div className="x_content">
                                        <div className="table-responsive">
                                            <table className="table table-striped jambo_table bulk_action">
                                                <thead>
                                                    <tr className="headings">
                                                        <th className="column-title">事项</th>
                                                        <th className="column-title">事项节点</th>
                                                        <th className="column-title">单号（%）</th>
                                                        <th className="column-title">用户名（%）</th>
                                                        <th className="column-title">发起人（%）</th>
                                                        <th className="column-title">任务发起时间</th>
                                                        <th className="column-title">节点开始时间</th>
                                                        <th className="column-title">是否超时</th>
                                                    </tr>
                                                </thead>
                                            </table>
                                        </div>
                                    </div>                            
                                
                                    <div className="x_title">
                                        <h3>我得待办列表</h3>
                                    </div>
                                    <div className="x_content">
                                        <div className="table-responsive">
                                            <table className="table table-striped jambo_table bulk_action">
                                                <thead>
                                                    <tr className="headings">
                                                        <th className="column-title">序号</th>
                                                        <th className="column-title">事项</th>
                                                        <th className="column-title">事项节点</th>
                                                        <th className="column-title">单号</th>
                                                        <th className="column-title">用户编号</th>
                                                        <th className="column-title">发起人</th>
                                                        <th className="column-title">事项发起时间</th>
                                                        <th className="column-title">节点开始时间</th>
                                                        <th className="column-title">计划完成时间</th>
                                                        <th className="column-title">操作</th>                                         
                                                    </tr>
                                                </thead>                    
                                                <tbody>
                                                <tr>
                                                    <td>1</td>
                                                    <td>Google 审核</td>
                                                    <td>更改 Google</td>
                                                    <td>GO20171220000001</td>
                                                    <td>1000001</td>
                                                    <td>admin</td>
                                                    <td>2017-12-20</td>
                                                    <td>2017-12-20</td>
                                                    <td>2017-12-20</td>
                                                    <td onClick={this.openModal}>领办</td>
                                                </tr>
                                                <tr>
                                                    <td>2</td>
                                                    <td>手机审核</td>
                                                    <td>更改手机</td>
                                                    <td>SJ20171220000001</td>
                                                    <td>1000005</td>
                                                    <td>admin</td>
                                                    <td>2017-12-20</td>
                                                    <td>2017-12-20</td>
                                                    <td>2017-12-20</td>
                                                    <td>办理</td>
                                                </tr>
                                                <tr>
                                                    <td>3</td>
                                                    <td>身份认证</td>
                                                    <td>认证审核</td>
                                                    <td>SF20171220000001</td>
                                                    <td>1000002</td>
                                                    <td>admin</td>
                                                    <td>2017-12-20</td>
                                                    <td>2017-12-20</td>
                                                    <td>2017-12-20</td>
                                                    <td>办理</td>
                                                </tr>               
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>   
                            </TabPane>
                            <TabPane tab="Tab 2" key="2">
                                <Tree
                                    checkable
                                    showIcon
                                    onExpand={this.onExpand}
                                    expandedKeys={this.state.expandedKeys}
                                    autoExpandParent={this.state.autoExpandParent}
                                    onCheck={this.onCheck}
                                    checkedKeys={this.state.checkedKeys}
                                    onSelect={this.onSelect}
                                    selectedKeys={this.state.selectedKeys}
                                >
                                    {this.renderTreeNodes(treeData)}
                                </Tree>
                            </TabPane>
                            <TabPane tab="Tab 3" key="3">
                                <Button onClick={this.messageInfo}>Info</Button>
                                <Button onClick={this.success}>Success</Button>
                                <Button onClick={this.error}>Error</Button>
                                <Button onClick={this.warning}>Warning</Button>
                            </TabPane>
                        </Tabs>
                      </div>
                    </div>
                </div>
            </div>
        )
    }

}






































