import React from "react";
import { message } from 'antd'
import { WidthProvider, Responsive } from "react-grid-layout";
import close from '../../../assets/images/close.svg';
import { Spin,Icon} from 'antd'
import cookie from 'js-cookie';
import { getFromLS, saveToLS} from '../../../utils'
import qs from 'qs';
import axios from '../../../utils/fetch';
import { DOMAIN_VIP} from '../../../conf'
import {
  interviewFlowCols,
  USER_ACTIVE_FORM, USER_CHINA_INTERVIEW,
  USER_EXCHANGE_NUM_RANKING, USER_GLOBAL_INTERVIEW,
  USER_INTERVIEW_FLOW_TREND,
  USER_NEWUSER_TOTAL
} from "../static/actionType";
import EchartsModal from "../chartsmod/userFlowTotal";
import {TradeTable} from "../chartsmod/tableComponent/tradeTable";

const ResponsiveReactGridLayout = WidthProvider(Responsive);
// const originalLayouts = getFromLS("layouts") || {};

/**
 * This layout demonstrates how to sync multiple responsive layouts to localstorage.
 */
class ResponsiveLocalStorageLayout extends React.PureComponent {
  constructor(props) {
    super(props);

    this.state = {
      // getFromLS 增加参数name
      layouts:{},
      isDraggable:false,
      isResizable:false,
    };

  }

  static get defaultProps() {
    return {
      className: "layout",
      cols: { lg: 12, md: 10, sm: 6, xs: 4, xxs: 2 },
      rowHeight: 30
    };
  }
  componentDidMount(){
    // console.log(this.props)
    const { layouts,isDraggable,isResizable } = this.props
    this.setState({
      layouts,
      isDraggable:isDraggable || false,
      isResizable:isResizable || false
    })
  }
  componentWillReceiveProps(nextProps){
    // console.log(this.props)
    // console.log(this.state.layouts)
    const { layouts,isDraggable,isResizable } = nextProps
    this.setState({
      layouts,
      isDraggable:isDraggable || false,
      isResizable:isResizable || false
    })
  }
  resetLayout() {
    this.setState({ layouts: {} });
  }

  // 重新加载
  reload() {
    // console.log(this.props.layoutsName)

    this.setState({
      // getFromLS 增加参数name
      layouts: JSON.parse(JSON.stringify(getFromLS("layouts", this.props.layoutsName) || {}))
    })

  }

  onLayoutChange(layout, layouts) {
    // saveToLS 增加参数name
    // saveToLS("layouts", layouts, this.props.layoutsName);
    // const { isDraggable,isResizable } = this.state;
    // if(isDraggable && isResizable) {
    //   let name = this.props.layoutsName;
      // let id = cookie.get('userId');
      // let type = name == '流量统计-userFlowTotal' ? '11' : name == '金额统计-amountTotal' ? '10': name == '交易统计-tradeTotal'? '9': '8';
    if(this.state.isDraggable) {
      this.props.onLayoutChange&&this.props.onLayoutChange(layouts, this.props.layoutsName);
    }
    // }else {
      this.setState({ layouts });
    // }
  }
  onDelete = (key) => {
    this.props.onDelete(key)
  }


  componentDidUpdate(nextProps, nextState) {
    
    const oldValue = this.props.layoutsName + '_' + this.props.layoutsIndex
    const newValue = nextProps.layoutsName + '_' + nextProps.layoutsIndex
    if (oldValue != newValue) {
      this.reload()
    }
  }
  render() {
    return (
      <div>
        {/* <button onClick={() => this.resetLayout()}>Reset </button> */}
        {/* <button onClick={() => this.reload()}>reload </button> */}

        <ResponsiveReactGridLayout
          className="layout"
          cols={{ lg: 12, md: 10, sm: 6, xs: 4, xxs: 2 }}
          rowHeight={30}
          isDraggable={this.state.isDraggable}
          isResizable={this.state.isResizable}
          layouts={this.state.layouts}
          onLayoutChange={(layout, layouts) =>
            this.onLayoutChange(layout, layouts)
          }
        >
          {this.props.fakeData.map((item, index) => {
            // const Comp = item.comp
            return <div className='grid_table' key={item.key} data-grid={item.dataGrid?item.dataGrid:{ w: 4, h: 8, x: index * 4, y: 0, minW: 3, minH: 6,maxW:12,maxH: 18}} style={{ border: '1px solid #EAEEF2',}}>
                {item.col? '':<div style={{position: 'absolute', width: 10,height: 10,right: 7,top: 0, cursor: 'pointer',color:'#333',zIndex:'1000', display: 'none'}} onClick={() => this.onDelete(item.key)} ><img style={{ width: '100%', height: '100%'}} src={close} alt='关闭' /></div>}
              {
                item.col&&<div style={{width:'100%',height:'10%',backgroundColor:'#f3f3f3',paddingTop:'6px'}}>
                  <div style={{marginTop:'5px',padding:'0 10px'}} className={item.dateComp ? 'left' : 'col-mg-8 col-lg-8 col-md-8 col-sm-8 col-xs-8'}>{item.title||'交易转化率统计'}</div>
                  {item.dateComp ? item.dateComp : ''}
                </div>
              }
              {
                item.col == 2 ?
                <div style={{width:'100%',height:'90%',display:'flex'}}>                
                  <div style={{width:'50%',height:'100%',backgroundColor:"#fff",position:'relative',overflow:'auto' }}>{item.left ? item.tableComp : item.comp}</div>
                  <div style={{width:'50%',height:'100%',backgroundColor:"#fff",position:'relative',overflow:'auto'  }}>{item.left ? item.comp : item.tableComp}</div>
                </div>
                :
                <div style={{height:item.col ? '90%' : '100%',backgroundColor:"#fff",width:'100%',overflow:'auto' }}>
                  {item.comp}
                </div>
              }
            </div>
          })}
        </ResponsiveReactGridLayout>
      </div>
    );
  }
}

// function getFromLS(key, name) {
//   // console.log("getFromLS", name)

//   let ls = {};
//   if (global.localStorage) {
//     try {
//       ls = JSON.parse(global.localStorage.getItem("rgl-8" + name)) || {};
//     } catch (e) {
//       /*Ignore*/
//     }
//   }
//   return ls[key];
// }

// function saveToLS(key, value, name) {
//   // console.log(value)
//   if (global.localStorage) {
//     global.localStorage.setItem(
//       "rgl-8" + name,
//       JSON.stringify({
//         [key]: value
//       })
//     );
//   }
// }




// module.exports = ResponsiveLocalStorageLayout;
export default ResponsiveLocalStorageLayout;

