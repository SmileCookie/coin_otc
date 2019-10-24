import React from "react";

class Extension extends React.Component{
    constructor(props){
        super(props);

        console.log(this.props.children);
    }
    render(){
        return <div>11111{this.props.children}</div>
    }
}
export default Extension;