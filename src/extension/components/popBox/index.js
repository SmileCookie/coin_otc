/**
 *  参考文档
 *  https://reactcommunity.org/react-modal/
 */

import React from 'react'
import Modal from 'react-modal'
import cookie from 'js-cookie'
import './index.less'

const customStyles = {
  overlay : {
    position          : 'fixed',
    top               : 0,
    left              : 0,
    right             : 0,
    bottom            : 0,
    backgroundColor   : 'rgba(0, 0, 0, 0.3)',
    zIndex            : 8888
  },
  content : {
    top                   : '50%',
    left                  : '50%',
    right                 : 'auto',
    bottom                : 'auto',
    marginRight           : '-50%',
    transform             : 'translate(-50%, -50%)',
    padding               : '0',
    backgroundColor       : '#17191F',
    borderRadius          : '2px',
    border                : 'none',
    boxShadow             : '0 2px 8px 0 rgba(0,0,0,0.20)'
  }
};

class ReactModal extends React.Component {
  constructor() {
    super();

    this.state = {
      modalIsOpen: false
    };

    this.openModal = this.openModal.bind(this);
    this.afterOpenModal = this.afterOpenModal.bind(this);
    this.closeModal = this.closeModal.bind(this);
  }

  openModal() {
    this.setState({modalIsOpen: true});
  }

  afterOpenModal() {
    
  }

  closeModal() {
    if(!this.props.clickNotClose){
      this.setState({modalIsOpen: false});
    }
    this.props.clearError?this.props.clearError():''
  }

  render() {
    let modalSkin =''
    if(cookie.get('skin') == 'light'){
        customStyles.content['backgroundColor'] = '#fff'
        modalSkin = 'Overlay-Light';
    }else{
        customStyles.content['backgroundColor'] = '#17191F'
        modalSkin = '';
    }
    return (
      <div>
        <Modal
          isOpen={this.state.modalIsOpen}
          onAfterOpen={this.afterOpenModal}
          onRequestClose={this.closeModal}
          style={customStyles}
          contentLabel="Example Modal"
          parentSelector={() => document.body}  
          overlayClassName={modalSkin}    
          >
          
          {this.props.children}
        
        </Modal>
      </div>
    );
  }
}

 export default ReactModal;