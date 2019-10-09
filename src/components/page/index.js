import React from 'react';
import { PAGEINDEX,PAGESIZE } from 'conf'
import './table.less'
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
    }
    componentWillReceiveProps(props) {
        // 解决在模块子组件下再次渲染 
        let preIndex = props.pageIndex 
        if (preIndex) { 
            this.setState({
                myIndex: preIndex++
            })
        }
    }

    prevClick (e){
        e.preventDefault();
        let { currentPageClick } = this.props;
        let { myIndex } = this.state;
        if( myIndex > 1){
            myIndex--;
            this.setState({myIndex}, () => {
                currentPageClick(myIndex);
            })
        }
    }
    nextClick (e){
        e.preventDefault();
        let { total, currentPageClick,pagesize} = this.props;
        let { myIndex } = this.state;
        let pageSize = pagesize?pagesize:PAGESIZE;
        let pageTotal = Math.ceil(total/pageSize);
        if( myIndex < pageTotal){
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
        console.log(e);
        e.preventDefault();
        let { currentPageClick,pageIndex } = this.props;
        let { myIndex } = this.state;
        
        myIndex = parseInt( e.target.text );
        this.setState({myIndex}, () => {
            if( myIndex != pageIndex ){
                currentPageClick(myIndex);
            }
         })
    }

    render() {
        //console.log(3782738937973);
        
        let listTab = [];
        let { pageIndex,total,pagesize} = this.props;
        let pageSize = pagesize?pagesize:PAGESIZE;
        let pageTotal = Math.ceil(total/pageSize);
        let chooseStyle = {
            background:" #3E85A2",
            borderRadius: "1px",
            color:"#FFFFFF"
        };      
        // let active = path.indexOf('otc') == -1 ? chooseStyle : Object.assign({},chooseStyle,{
        //         background:'#159DA6'
        // });
        
        if( pageTotal > 5 ){
            if (pageIndex != 1 && pageIndex >= 4 && pageTotal != 4) {
                listTab.push(
                    <li key={1}>
                        <a onClick={ this.currentPage } href="javascript:;"> 1 </a>
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
                                <a  onClick={ this.currentPage } href="javascript:;"> {start} </a>
                            </li>
                        )
                    } 
                    else {
                        listTab.push(
                            <li key={start}>
                                <a style={chooseStyle} onClick={ this.currentPage } href="javascript:;"> {start} </a>
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
                        <a  onClick={ this.currentPage } href="javascript:;"> {pageTotal} </a>
                    </li>
                )
            }
        }
        else{
            for (var i = 1; i < pageTotal+1; i++) {
                let chooseActive;
                if(pageIndex == i){
                    chooseActive = chooseStyle
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

       
        return (
            <ul className="list_ul">
                {total>0?
                <li> 
                    <a className={ pageIndex !== 1 ? "disabled" : ""} onClick = { this.prevClick } href="javascript:;"> 
                        &lt;
                    </a>
                </li>:<li></li>}
                {listTab}
                {total>0?
                <li> 
                    <a className={ pageIndex !== pageTotal ? "disabled" : ""} onClick = { this.nextClick } href="javascript:;"> 
                        &gt;
                    </a> 
                </li>
                :<li></li>}
            </ul> 
        )
    }

}
 export default Pages;