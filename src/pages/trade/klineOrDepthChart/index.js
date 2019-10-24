import React from 'react'
import OriginalKline from 'Components/originalKline'
import Kline from '../../../components/kline/index'
import Depthhighchart from '../depthhighchart/depthhighchartContainer'
import { FormattedMessage } from 'react-intl';
import SelectList from 'Components/selectList'
import './index.less'

export default class KlineAndDepthChart extends React.Component{
    constructor(props){
        super(props)

        this.sl = React.createRef();

        this.state = {
            tab:0
        }
        this.options = [
                {key:'Original',val:"0"},
                {key:<FormattedMessage id="深度图" />,val:"2"}
            ]
        this.tabBox = React.createRef()
        this.tabChange = this.tabChange.bind(this)
        this.upZindex = this.upZindex.bind(this)
        this.downZindex = this.downZindex.bind(this)
        this.autoZoom = this.autoZoom.bind(this)
        this.addZoom = this.addZoom.bind(this)
    }

    addZoom(){
        this.zoom = document.getElementById("sizeIcon");
        this.zoom.addEventListener('click', this.autoZoom);
    }

    autoZoom(){
        if(!this.autoflg){
            this.csl.setAttribute("style", "z-index:9");
            this.sl.current.className="dn"
        } else {
            this.csl.removeAttribute("style");
            this.sl.current.className=""
        }

        this.autoflg = !this.autoflg;
    }

    upZindex(){
        !this.autoflg && this.csl.setAttribute("style", "z-index:99999999");
    }

    downZindex(){
        this.csl.removeAttribute("style");
        this.csl.click();

    }

    componentDidMount(){
        try{
            this.csl = document.getElementsByClassName("up-k-sel")[0];
            this.sl.current.addEventListener('mouseenter', this.upZindex);
            this.sl.current.addEventListener('click', this.upZindex);
            this.csl.addEventListener("mouseleave", this.downZindex);
            
            this.addZoom();
            // get pk select width
        } catch(e){

        }
    }

    componentWillUnmount(){
        try{
            this.sl.current.removeEventListener('mouseenter', this.upZindex);
            this.sl.current.removeEventListener('click', this.upZindex);
            this.csl.removeEventListener('mouseleave', this.downZindex);
            this.zoom.removeEventListener('click', this.autoZoom);
        }catch(e){

        }
    }

    tabChange(val){
        this.setState({ 
            tab: val
        }, () => {
            try{
                this.addZoom();
            }catch(e){

            }
        })
    }

    render(){
        //throw new Error(2222)
        const { tab } = this.state 
        return(
            <div className="tab-box">
                <div style={{width:'175px',height:'24px',top:'5px',right:'40px',position:'absolute',zIndex:'9999999'}} ref={this.sl}></div>
                <ul className="clearfix up-k-sel">
                    {
                        this.options.map((v)=>(
                            <li className={v.val == tab ? 'ac' : ''} onClick={() => {
                                this.tabChange(v.val); 
                            }} key={v.val}>{v.key}</li>
                        ))
                    }
                </ul>
                <div className="tab-box-body" ref={this.tabBox}>
                    {tab==0&&<div className='tab-box-body-item' key="0"><OriginalKline {...this.props} /></div>}
                    {tab==1&&<div className='tab-box-body-item' key="1"><Kline {...this.props} /></div>}                    
                    {tab==2&&<div className='tab-box-body-item wssk' key="2"><Depthhighchart/></div>}
                </div>
            </div>
        )
    }
}
































