import React from 'react';
import axios from 'axios';
import list from './country.js'

require("./index.less");

export default class PhoneCode extends React.Component{
        
        constructor(props){
            super(props)
            this.state = {
                countryModal:false,
                countryCode:"",             
                curtCountry:this.props.curtcountry || 
                            {
                                "_id" : "236",
                                "code" : "+86",
                                "des" : "中国",
                                "name" : "中国",
                                "position" : "-825px"
                            },
                countryList:[]
            }
           
            this.handleInputChange = this.handleInputChange.bind(this)
            this.mouseModalEnter = this.mouseModalEnter.bind(this)
            this.mouseModalLeave = this.mouseModalLeave.bind(this)
        }
        //输入时 input 设置到 satte
        handleInputChange(event) {
            const target = event.target;
            const inputValue = target.type === 'checkbox' ? target.checked : target.value;
            const name = target.name;
            this.setState({
                [name]: inputValue
            });
            const countryCode = this.state.countryList.findIndex((value, index, arr) => {
                     return value.code == inputValue;
                  })
            if(countryCode > 0){
                this.setState({
                    curtCountry:this.state.countryList[countryCode]
                })
                this.props.obrainCountryCode(this.state.countryList[countryCode].code)
            
            }
        }

        mouseModalEnter(){
            this.setState({
                countryModal:true                
            })
        }

        mouseModalLeave(){
            this.setState({
                countryModal:false                
            })
        }

        componentDidMount(){
            this.setState({
                countryList:list.country
            })
        }

        chooseCountryCode(index){
            const countryCode = this.state.countryList[index]
            this.setState({
                countryModal:false,
                countryCode:countryCode.code,
                curtCountry:countryCode
            })
            this.props.obrainCountryCode(countryCode.code)
        }

        render(){
            return (
                <div className="country" onMouseOver={this.mouseModalEnter} onMouseOut={this.mouseModalLeave}>
                    <div className="talk-flag" style={{backgroundPosition:"0"+ this.state.curtCountry.position}}></div>
                    <input type="text" name="countryCode"  className="inp-country" value={this.state.countryCode || this.state.curtCountry.code} onChange={this.handleInputChange} />
                    <div className="country-icon">
                        <span className="arrow"></span>
                    </div>
                    <div className={this.state.countryModal? "goog-menu" : "goog-menu hide"}>
                      <div className="goog-menu-con">
                        {
                            this.state.countryList? 
                              this.state.countryList.map((item,index) => {
                                return (
                                        <div key={index}>
                                            <div className="goog-menuitem" onClick={ () => this.chooseCountryCode(index) }>
                                                <div className="goog-menuitem-content">
                                                    <div>
                                                        <div className="goog-inline-block">
                                                            <div className="talk-flag" style={{backgroundPosition:"0"+ item.position}}></div>
                                                        </div>
                                                        <span className="talk-select-country-name">（{item.des}）</span> 
                                                        <span className="talk-select-country-code">{item.code}</span>
                                                    </div>
                                                </div>
                                            </div>
                                            {index==0?<div className="goog-menuseparator"></div>:""}
                                        </div>
                                    )
                                }) : ""
                        }
                      </div>
                    </div>
                </div>
            )   
        }
}




























