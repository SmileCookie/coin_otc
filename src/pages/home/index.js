import React from 'react';
import { FormattedMessage} from 'react-intl';
import { Link } from 'react-router'

class Home extends React.Component {

    render() {
        return (
            <div className="wrap">
                <FormattedMessage id="common.hello" />,
                home
            </div>
        )
    }
}

export default Home;