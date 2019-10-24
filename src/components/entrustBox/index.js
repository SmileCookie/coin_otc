/**
 *  参考文档
 *  https://reactcommunity.org/react-modal/
 */

import React from 'react';
import Modal from 'react-modal';

import './index.less'
const customStyles = {
  overlay : {
    position          : 'fixed',
    top               : 0,
    left              : 0,
    right             : 0,
    bottom            : 0,
    backgroundColor   : 'rgba(0, 0, 0, 0.35)',
    zIndex            : 100
  },
  content : {
    top                   : '50%',
    left                  : '50%',
    right                 : 'auto',
    bottom                : 'auto',
    marginRight           : '-50%',
    padding               : '0',
    borderRadius          :'2px',
    border                : '0',
    background            :' #17191F',
    transform             : 'translate(-50%, -50%)'
  }
};

class EntrustModal extends React.Component {
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
  }

  render() {
    return (
        <div>
          <Modal
              isOpen={this.state.modalIsOpen}
              onAfterOpen={this.afterOpenModal}
              onRequestClose={this.closeModal}
              shouldCloseOnOverlayClick={false}
              style={customStyles}
              contentLabel="Example Modal"
              parentSelector={() => document.body}
          >

            {this.props.children}

          </Modal>
        </div>
    );
  }
}

export default EntrustModal;
