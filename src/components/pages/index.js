/********
****  分页组件  joel
****  
     <Pages
        {...this.props}  total:总页数 , pagesize:单页数据量 , pageIndex:当前页码
        currentPageClick = { this.currentPageClick }  点击事件回掉函数  参数为页码
    />
********/


import React from 'react';
import '../../assets/css/table.less'
import { PAGEINDEX,PAGESIZE } from '../../conf';
import { browserHistory } from 'react-router';
import CT from '../../components/context/index'

class Pages extends React.Component{

    constructor(props) {
        super(props)
        this.state = {
            myIndex : PAGEINDEX,
        }
        this.prevClick = this.prevClick.bind(this);
        this.nextClick = this.nextClick.bind(this);
        this.currentPage = this.currentPage.bind(this);
        this.resetPage = this.resetPage.bind(this);
        this.creatPage = this.creatPage.bind(this)
    }

    prevClick (e,func){
        e.preventDefault();
        let { currentPageClick } = this.props;
        let { myIndex } = this.state;
        if( myIndex > 1){
            func()
            myIndex--;
            this.setState({myIndex}, () => {
                currentPageClick(myIndex);
            })
        }
    }
    nextClick (e,func){
        e.preventDefault();
        
        let { total, currentPageClick,pagesize} = this.props;
        let { myIndex } = this.state;
        let pageSize = pagesize?pagesize:PAGESIZE;
        let pageTotal = Math.ceil(total/pageSize);
        if( myIndex < pageTotal){
            func()
            myIndex++;
            this.setState({myIndex}, () => {
                currentPageClick(myIndex);
            })
        }
    }
    //重置点击页数
    resetPage(){
        
        
        this.setState({
            myIndex:PAGEINDEX
        },() =>console.log(this.state.myIndex))
    }
    currentPage (e){
       
        e.preventDefault();
    
        // let firstScroll = document.getElementsByClassName('firstScroll');
        // firstScroll[0].firstChild.style.margin = '0';
        
        //gotoTop&& gotoTop()
        let { currentPageClick,pageIndex } = this.props;
        let { myIndex } = this.state;
        
        myIndex = parseInt( e.target.text );
        this.setState({myIndex}, () => {
            if( myIndex != pageIndex ){
                this.func()
                currentPageClick(myIndex);
            }
         })
    }

    creatPage(func){
        this.func = func
        let listTab = [];
        let { pageIndex,total,pagesize} = this.props;
        let pageSize = pagesize?pagesize:PAGESIZE;
        let pageTotal = Math.ceil(total/pageSize);
        let path = browserHistory.getCurrentLocation().pathname;
        let chooseStyle = {
            background:" #3E85A2",
            borderRadius: "1px",
            color:"#FFFFFF"
        };      
        let active = path.indexOf('otc') == -1 ? chooseStyle : Object.assign({},chooseStyle,{
                background:'#159DA6'
        });
        
        if( pageTotal > 5 ){
            if (pageIndex != 1 && pageIndex >= 4 && pageTotal != 4) {
                listTab.push(
                    <li key={1}>
                        <a onClick={this.currentPage} href="javascript:;"> 1 </a>
                    </li>
                )
            }
            if (pageIndex - 2 > 2 && pageIndex <= pageTotal && pageTotal > 5) {
                    listTab.push(
                        <li key="pre_dian">
                            <a href="javascript:;"> ... </a>
                        </li>
                    )
            }
            let start = pageIndex - 2,
                end = pageIndex + 2;
            if ((start > 1 && pageIndex < 4) || pageIndex == 1) {
                end++;
            }
            if (pageIndex > pageTotal - 4 && pageIndex >= pageTotal) {
                start--;
            }
            for (; start <= end; start++) {
                if (start <= pageTotal && start >= 1) {
                    if (start != pageIndex) {
                       listTab.push(
                            <li key={start}>
                                <a onClick={ this.currentPage } href="javascript:;"> {start} </a>
                            </li>
                        )
                    } 
                    else {
                        listTab.push(
                            <li key={start}>
                                <a style={active} onClick={this.currentPage } href="javascript:;"> {start} </a>
                            </li>
                        )
                    }
                }
            }
            if (pageIndex + 2 < pageTotal - 1 && pageIndex >= 1 && pageTotal > 5) {
                listTab.push(
                    <li key='next_dian' className="qweqwe">
                        <a href="javascript:;"> ... </a>
                    </li>
                )
            }
            if (pageIndex != pageTotal && pageIndex < pageTotal - 2 && pageTotal != 4) {
               listTab.push(
                    <li key={pageTotal}>
                        <a  onClick={this.currentPage } href="javascript:;"> {pageTotal} </a>
                    </li>
                )
            }
        }
        else{
            for (var i = 1; i < pageTotal+1; i++) {
                let chooseActive;
                if(pageIndex == i){
                    chooseActive = active
                }else{
                    chooseActive = null
                }
                listTab.push(
                    <li key={i}>
                        <a style={chooseActive} onClick={ this.currentPage } href="javascript:;"> {i} </a>
                    </li>
                )
            }
        }
        return listTab
    }


    render() {
        
        let { pageIndex,total,pagesize} = this.props;
        let pageSize = pagesize?pagesize:PAGESIZE;
        let pageTotal = Math.ceil(total/pageSize);
        let path = browserHistory.getCurrentLocation().pathname;
        const cm = CT()
        return (
            <cm.Consumer>
            {
                (gotoTop) => {
                    //console.log(gotoTop)
                    return (
                        <ul className="list_ul clickTop">
                            {total>0?
                            <li> 
                                <a className={ pageIndex !== 1 ? "disabled" : ""} onClick = { (e) =>this.prevClick(e,gotoTop) } href="javascript:;"> 
                                    &lt;
                                </a>
                            </li>:<li></li>}
                            {this.creatPage(gotoTop)}
                            {total>0?
                            <li> 
                                <a className={ pageIndex !== pageTotal ? "disabled" : ""} onClick = { (e) => this.nextClick(e,gotoTop) } href="javascript:;"> 
                                    &gt;
                                </a> 
                            </li>
                            :<li></li>}
                        </ul> 
                    )
                //    return <div onClick={a}>123321</div>
                }
            }
        </cm.Consumer> 

            
        )
    }

}
// export const ThemeContext = React.createContext({
//     toggleTheme: () => {},
//   });

// Pages.contextType = ThemeContext;

 export default Pages;
