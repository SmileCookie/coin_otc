import React from 'react';
import PropTypes from 'prop-types';

class Radio extends React.Component{

    static propTypes = {
        isCk: PropTypes.bool,
        cb: PropTypes.func,
    }

    static defaultProps = {
        isCk: false,
        cb: () => {

        }
    }

    constructor(props){
        super(props);

        this.state = {
            ck: this.props.isCk
        };

        this.ck = this.ck.bind(this);
    }

    ck(){
        const { ck } = this.state;
        this.setState({
            ck: !ck
        });
        this.props.cb(!ck);
    }

    render(){
        const { ck } = this;
        const { ck:sck } = this.state;

        return (
            <div onClick={ck} className={`rd_wp ${sck ? 'ac' : ''}`}>
                <em></em>
            </div>
        );
    }
}

export default Radio;