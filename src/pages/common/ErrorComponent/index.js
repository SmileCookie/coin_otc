import React from 'react'
import cookie from 'js-cookie';

class ErrorComponent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
             error: null,
             errorInfo: null ,
             styles:props.styles || {},
             classNames:props.classNames || '',
             divStyles:props.divStyles || {},
             isDark:cookie.get("skin") || 'dark'
            };
    }
    componentDidMount(){
       
    }
    
    componentDidCatch(error, errorInfo) {
        // Catch errors in any components below and re-render with error message
        this.setState({
            error: error,
            errorInfo: errorInfo
        })
        // console.log(error)
        // You can also log error messages to an error reporting service here
    }
    
    render() {
      let  {styles,classNames,divStyles,isDark} = this.state
    //   if(isDark == 'dark'){
    //     divStyles = Object.assign(divStyles,{'background':'#17191F'})
    //   }else{
    //     divStyles = Object.assign(divStyles,{'background':'#fff'})
    //   }
      if (this.state.errorInfo) {
        // Error path
        return (
            <div className={classNames || ''} style={divStyles}>
                    <div className="iconfont icon-jiazai new-loading" style={styles || ''} ></div>
                </div>
                )
      }
      // Normally, just render children
      return this.props.children;
    }  
}

export default ErrorComponent