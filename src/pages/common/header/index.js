
import React from 'react'
import { Link } from 'react-router-dom'
import { Button,Tabs } from 'antd'
const TabPane = Tabs.TabPane;

export default class Header extends React.Component{
    constructor(props){
        super(props)
        this.newTabIndex = 0;
        const panes = [
          { title: '我的主页', content: 'Content of Tab Pane 1', key: '1',closable: false }
        ];
        this.state = {
            topNav:[
                {
                    name:'我的主页',
                    url:'/',
                    class:'fa-home'
                }
            ],
            activeKey: panes[0].key,
            panes,
        }
        this.onChange = this.onChange.bind(this)
        this.onEdit = this.onEdit.bind(this)
        this.add = this.add.bind(this)
        this.remove = this.remove.bind(this)
    }

    onChange(activeKey){
        this.setState({ activeKey });
    }
    onEdit(targetKey, action){
        this[action](targetKey);
    }
    add(){
        const panes = this.state.panes;
        const activeKey = `newTab${this.newTabIndex++}`;
        panes.push({ title: 'New Tab', content: 'New Tab Pane', key: activeKey });
        this.setState({ panes, activeKey });
    }
    remove(targetKey){
        let activeKey = this.state.activeKey;
        let lastIndex;
        this.state.panes.forEach((pane, i) => {
            if (pane.key === targetKey) {
            lastIndex = i - 1;
            }
        });
        const panes = this.state.panes.filter(pane => pane.key !== targetKey);
        if (lastIndex >= 0 && activeKey === targetKey) {
            activeKey = panes[lastIndex].key;
        }
        this.setState({ panes, activeKey });
    }


    setActive(index){
        this.setState({
            activeIndex:index
        })
    }

    componentWillReceiveProps(nextProps){
        this.setState({
            topNav:nextProps.topNav
        })
    }

    render(){
        let arrLeng = this.state.topNav.length;
        return(
            <div className="top_nav">
                <div className="nav_menu">
                    <nav>
                        <ul className="nav navbar-nav navbar-left">
                            <li>
                                <a>一级菜单</a>
                            </li>
                        </ul>
                    </nav>
                    <div style={{ marginBottom: 16 }}>
                        <Button onClick={this.add}>ADD</Button>
                    </div>
                    <Tabs
                        hideAdd
                        onChange={this.onChange}
                        activeKey={this.state.activeKey}
                        type="editable-card"
                        onEdit={this.onEdit}
                        >
                        {this.state.panes.map(pane => <TabPane tab={pane.title} key={pane.key} closable={pane.closable}>{pane.content}</TabPane>)}
                    </Tabs>
                </div>
            </div>
        )
    }

}



































