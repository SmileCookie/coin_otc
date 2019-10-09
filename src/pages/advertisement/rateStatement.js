
import React from 'react';
import {post, get} from 'nets';
import { FormattedMessage,injectIntl } from 'react-intl';
import {withRouter} from 'react-router';
import '../../assets/style/advertisement/rateStatement.less';
class RateStatement extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            title: ['fee1_title', 'fee1_content', 'fee2_title', 'fee2_content'],
            list: [],
        };
        this.submit = this.submit.bind(this)
    }
    componentWillMount(){

    }
    async componentDidMount() {
        const { list, title } = this.state
        let result = await post('/web/otcIntroduction/getMsg', {type: title.join(',')});
        result = result.data;
        for (var i = 0, len = title.length/2; i < len ; i++) {
            let titleIndex = i + 1
            !list[i] && (list[i] = {}) 
            result.map((item) => {
                if (item.type.indexOf(titleIndex) > -1) {
                    if (item.type.indexOf('title') > -1) {
                        list[i].title = item.descript
                    }else {
                        list[i].content = item.descript
                    }
                }
            })
        }
        this.setState({
            list
        })
    }
    submit() {
        this.props.history.go(-1);
    }

    render() {
        const {formatMessage} = this.props.intl;
        const { list, title } = this.state;
        return (
            <div className="rate_statement">
                <div className="rate_content">
                    <div className="page_title padding_b20 under_line"><FormattedMessage id="费率说明" /></div>
                    {
                        list.map((item, index) => {
                            return (
                                <div key={index}>
                                    <div className="rate_title">{item.title}</div>
                                    <div style={{textAlign: 'justify'}} dangerouslySetInnerHTML={{__html: item.content}}></div>
                                </div>

                            )
                        })
                    }
                    <div className="btn_box">
                        <input type="button" className="btn submit" value={formatMessage({id:"明白了，继续发布广告"})} onClick={this.submit} />
                    </div>
                </div>
            </div>
        )
    }
}

export default withRouter(injectIntl(RateStatement));