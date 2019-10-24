import React from 'react'
require('./jquery.mousewheel')
import cookie from 'js-cookie';
import { DOMAIN_TRANS } from 'Conf'

export default class OriginalKline extends React.PureComponent{
    constructor(props){
        super(props)
        this.state = {
            width:0,
            height:0,
            urlname:1,
            index:0,
            nameid:0,
            skins:11,
            DOMAIN_TRANS:DOMAIN_TRANS,
        }
        this.kline = React.createRef()
        this.hidden = false
    }
    
    componentWillMount(){
        let {lang, currentMarket, skin, index,state} = this.props;
        const language = lang == "en" ? "en-us" : lang == "zh" ? "zh-cn" : lang == "ko" ? "kr" :lang == "ja" ? "jp" : lang == undefined ? "en-us":lang  ;
        const symbolName = currentMarket.split('_').join('/')
        var url = window.location.href;
        var indes = url.lastIndexOf("\/");
        var str = url.substring(indes + 1, url.length);
        var nameid = "original_kline" + this.props.index
        var states=0
        if (index == undefined) {
            var nameid = "original_kline" + 10
            var indexs=10
            this.setState({
                nameid: nameid,
                index: indexs
            })
        }else{
            this.setState({
                nameid: nameid,
                index: this.props.index
            })
        }
         //在用户新添加模块的时候去判断当前当前的模块是否被删除过通过判断条件是在删除的时候存的COOK
         if(cookie.get("zuid")==undefined){
            if(localStorage.getItem('zindex')!=undefined){
                var koo=localStorage.getItem('zindex')
                if(koo!=undefined){
                    if(koo.indexOf(nameid)==-1){
         
                    }else{
                        states=1
                        var koo=koo.replace(nameid,"")
                        localStorage.setItem("zindex",koo)
                    }
                }
             } 
         }else{
            if(cookie.get("zindex")!=undefined){
                var koo=cookie.get("zindex")
                if(koo.indexOf(nameid)==-1){
         
                }else{
                    states=1
                    var koo=koo.replace(nameid,"")
                    cookie.set("zindex",koo)
                }
             }
         }
       
      
        let urlname = "theme=" + skin + "&urlname=" + str + "&admin=" + this.state.DOMAIN_TRANS + "&lang=" + language + "&symbol=" + currentMarket + "&symbolName=" + symbolName+"&nameid="+nameid+"&state="+states
        this.setState({
            urlname: urlname,
            nameid: nameid

        })
    }
    
    componentDidMount() {
      
       
    }

   
    //监听换肤
    componentWillReceiveProps(nextProps){
        
        if(nextProps.skin != this.props.skin){
           
    
        }
        if(nextProps.currentMarket != this.props.currentMarket){
            const setSymbolName = nextProps.currentMarket.toUpperCase().split('_').join('/');
            var url = window.location.href;
            var indes = url.lastIndexOf("\/");
            var str = url.substring(indes + 1, url.length);
            const language = nextProps.lang == "en" ? "en-us" : nextProps.lang == "zh" ? "zh-cn" : nextProps.lang == "ko" ? "kr" :nextProps.lang == "ja" ? "jp" : nextProps.lang == undefined ? "en-us":nextProps.lang ;
            let state=0
            if(str=="multitrade"){
                state=1
            }
           let urlname = "theme=" +nextProps.skin + "&urlname=" + str + "&admin=" + DOMAIN_TRANS + "&lang=" + language + "&symbol=" +nextProps.currentMarket + "&symbolName=" +setSymbolName+"&nameid="+this.state.nameid +"&state="+state
            this.setState({
                urlname: urlname,
            })
        }
    }
    //防止休眠时的错误
    hiddenChange = () => {
    }
   
//全屏
setFullscren = (e) => {
    
    const name ="original_kline"+e.currentTarget.id
    let el=document.getElementById(name);
    let isFullscreen=document.fullScreen||document.mozFullScreen||document.webkitIsFullScreen;
    if(!isFullscreen){//进入全屏,多重短路表达式
        (el.requestFullscreen&&el.requestFullscreen())||
        (el.mozRequestFullScreen&&el.mozRequestFullScreen())||
        (el.webkitRequestFullscreen&&el.webkitRequestFullscreen())||(el.msRequestFullscreen&&el.msRequestFullscreen());
      }else{//退出全屏,三目运算符
        document.exitFullscreen?document.exitFullscreen():
        document.mozCancelFullScreen?document.mozCancelFullScreen():
        document.webkitExitFullscreen?document.webkitExitFullscreen():'';
      }
} 
    render(){
        return(

              <div className="original_kline"  ref={this.kline}>

        <div className="original_kline" id={ this.state.nameid }>
        <div  onClick={this.setFullscren} className="ctrl_btn fullscreen" id= { this.state.index} ></div>
        <iframe className="ifre" width="100%" height="100%" src={`/bw/src/charting_library/init.html?${this.state.urlname}`}></iframe>
        </div>
            </div>
        )
    }
}