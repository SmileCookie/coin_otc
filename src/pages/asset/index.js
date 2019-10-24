import React from 'react';

import Marketbar from '../common/marketbar/marketbarContainer';

// import '../../assets/css/asset.css';

class Asset extends React.Component {
    render() {
        return (
            <div className="wrap account-wrap">
                <div className="account-wrap-inner">
                    <Marketbar />
                    <div className="account-content">
                        content
                    </div>
                </div>
            </div>
        );
    }
}

export default Asset;