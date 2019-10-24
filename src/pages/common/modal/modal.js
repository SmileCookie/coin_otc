// components  
  
import React from 'react';  
import { Link } from 'react-router';  
  
class ConfirmWins extends React.Component {  
  constructor() {  
    super();  
  }  
  
  render() {  
    const props = this.props;  
    return (  
      <div className="confirm-wins-container">  
        <div className="wins">  
          <div className="title text-center">{props.title}</div>  
          <div className="desc gray-text96">{props.desc}</div>  
          <div className="fn-btn text-center">  
            {props.leftBtn?<span className="btn left-btn" onClick={props.onLeftClick}>{props.leftBtn.text}</span>:''}  
            <span className="btn right-btn" onClick={props.onRightClick}>{props.rightBtn.text}</span>  
          </div>  
        </div>  
      </div>  
    );  
  }  
}  

ConfirmWins.propTypes = {  
  title: React.PropTypes.string.isRequired,  
  desc: React.PropTypes.string.isRequired,  
  leftBtn: React.PropTypes.object,  
  rightBtn: React.PropTypes.object.isRequired,  
  onLeftClick: React.PropTypes.func,  
  onRightClick: React.PropTypes.func.isRequired,  
};  
  
export default ConfirmWins;  