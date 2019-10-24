import React from 'react'
import { connect } from 'react-redux'
import Footer from './footer'

const mapStateToProps = ( state, ownProps ) => {
    return {
        footStau:state.trade.footStau
    }
}


const mapDispatchToProps = (dispatch) => {
    return {

    }
}


export default connect(mapStateToProps,mapDispatchToProps)(Footer)














