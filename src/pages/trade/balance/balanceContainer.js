import { connect } from 'react-redux';
import './balance.css';
import Balance from './balance';

const mapStateToProps = (state, ownProps) => {
    return {    
        assets: state.assets,
        user:state.session.user,
        money:state.money
    };
}
const mapDispatchToProps = (dispatch) => {
    return {
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(Balance);