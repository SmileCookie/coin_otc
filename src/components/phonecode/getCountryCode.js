import React from 'react';
import ReactDOM from 'react-dom';
import './getCountryCode.less';
import PropTypes from 'prop-types';
import ScrollArea from 'react-scrollbar'
export default class GetCode extends React.Component{
    static propTypes = {
        list : PropTypes.arrayOf(PropTypes.shape({
            _id : PropTypes.string.isRequired,
            code : PropTypes.string.isRequired,
            des : PropTypes.string.isRequired,
            name : PropTypes.string.isRequired,
            position : PropTypes.string.isRequired
        })).isRequired,
        getCurrentSelectedCode : PropTypes.func.isRequired,
        selectedCode : PropTypes.string,
        showCode : PropTypes.string,
        startMove: PropTypes.bool,
        style: PropTypes.object,
    }
    static defaultProps = {
        selectedCode : '+86',
        showCode : '0',
        style: {left:'auto',right:0, width:'300px', height:'400px'},
    }

    constructor(props){
        super(props);
        this.state = {
            currentCountry : this.props.list[0].des,
            currentCode : this.props.list[0].code,
            dropDownIsShow : false
        }
        this.noRenderState = {
            scrollTop : 0
        }
        this.setCurrentSelected = this.setCurrentSelected.bind(this);
        this.setDropDownState = this.setDropDownState.bind(this);
        this.min = this.min.bind(this);
        this.mout = this.mout.bind(this);
    }

    min(){
        console.log('min')
        if(this.props.startMove){
            this.setState({
                dropDownIsShow: true
            })
        }
    }
    mout(){
        console.log('mout')
        if(this.props.startMove){
            this.setState({
                dropDownIsShow: false
            })
        }
    }

    componentDidMount(){
        if(this.props.list.length == 0){
            throw new Error('list length is zero');
        }

        document.body.addEventListener('click', (e)=>{
           if(e.target && !e.target.getAttribute('id') && !e.target.getAttribute('data-code')){
                this.state.dropDownIsShow && this.setDropDownState(e); 
           }
        });

        this.setSelectProps(this.props);
    }

    setCurrentSelected(e){
        this.setState({
            currentCountry : e.target.innerHTML,
            currentCode: e.target.getAttribute('data-code')
        });
        
        this.props.getCurrentSelectedCode(e.target.getAttribute('data-code'), e.target.getAttribute('data-name'));

        this.setDropDownState(e);
    }

    setDropDownState(e){
        this.refs.dp && (this.noRenderState.scrollTop = this.refs.dp.scrollTop);
        console.log(this.state.dropDownIsShow)

        this.setState({
            dropDownIsShow : !this.state.dropDownIsShow
        });
        // if(this.props.isRotate){
        //     this.props.isRotate(this.state.dropDownIsShow)
        // }
        setTimeout(() => {;
            this.refs.dp && (ReactDOM.findDOMNode(this.refs.dp).scrollTop = this.noRenderState.scrollTop);
        }, 0);
    }

    componentWillReceiveProps(nprops, nstate){
        if(this.scode !== nprops.selectedCode){
            this.setSelectProps(nprops);
            this.scode = nprops.selectedCode;
        }
    }

    setSelectProps(nprops){
        const currentCountry = this.props.list.find((res) => {
            return res.code == nprops.selectedCode;
        });

        if(currentCountry){
            this.setState({
                currentCountry : currentCountry.des,
                currentCode: currentCountry.code
            })
        }
    }

    render(){
        const { min, mout } = this;
        return (
            <div className="country_select">
                <div className="input-group-btn dropdown">
                {/* <em  className={`iconfont  ${this.state.dropDownIsShow && 'is_rotate'}`} style={{cursor:'pointer'}}>&#xe681;</em> */}
                    <div className={`btn-group  bbyh-bg  ${this.state.dropDownIsShow?'backBg' : ''}`} >
                        <span onClick={this.setDropDownState} id="countryText" >{this.props.showCode == 0 ? this.state.currentCountry : this.state.currentCode}</span>
                    </div>
                    {
                        this.state.dropDownIsShow ?  ((
                            <ScrollArea ref="dp" className="dropdown-menu scrollarea trade-scrollarea scrollarea-content" style={this.props.style}>
                        <ul>
                        {
                            this.props.list.map((res)=>{
                                return (
                                    <li key={res._id}>
                                        <a className={this.state.currentCountry == res.des ? 'active' : ''} onClick={this.setCurrentSelected} data-code={res.code} data-name={res.name}>
                                            {
                                                this.props.showCode == 0 ? res.des : `${res.code} [${res.des}]`
                                            }
                                        </a>
                                    </li>
                                )
                            })
                        }
                    </ul>

                     </ScrollArea>
                     )) : null
                      
                    }
                </div>
            </div>
        );
    }
}