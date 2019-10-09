
import React from 'react'
import ReactDOM from 'react-dom'


export default class Overlay extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            isShow:false
        }

        this.container = document.createElement('div');
        document.body.appendChild(this.container);

    }
    componentWillReceiveProps(nextProps){
        this.setState({
            isShow:nextProps.isShow
        })
    }
    
    componentWillUnmount(){
        document.body.removeChild(this.container)
    }

    render(){
        return(
            ReactDOM.createPortal(
                <div id="CalenderModalEdit" className="modal fade in" tabIndex="-1" role="dialog" style={{display:'block'}}>
                    <div className="modal-dialog" style={{width:this.props.width+'px'}}>
                        <div className="modal-content">
                            <div className="modal-header">
                            <button type="button" className="close" onClick={this.props.onClose}>×</button>
                            <h4 className="modal-title" id="myModalLabel2">{this.props.title || 'Edit Calendar Entry'}</h4>
                            </div>
                            <div className="modal-body">
                                {this.props.children}
                            </div>
                            <div className="modal-footer">
                            <button type="button" className="btn btn-default antoclose2" onClick={this.props.onClose}>Close</button>
                            <button type="button" className="btn btn-primary antosubmit2" onClick={this.props.onSave || this.props.onClose}>Save changes</button>
                            </div>
                        </div>
                    </div>
                </div>,
                this.container
            )
        )
    }
}
































