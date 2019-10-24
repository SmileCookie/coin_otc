import React from 'react';
import './index.less'
class exportModal extends React.Component {
    constructor() {
      super();
      this.state = {
        boxname:[],
      };
    }
    componentDidMount(){
        const {boxname} = this.props
        this.setState({boxname})
    }
    componentWillReceiveProps(NextProps){
        const {boxname} = NextProps
        this.setState({boxname})
    }
  
    render(){
        return(
            <div className="export-csv right" onClick={()=>this.props.fomartexportData()}>
                {this.state.boxname}
            </div>
        )
    }
}
export default exportModal;