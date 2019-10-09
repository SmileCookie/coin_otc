
import { connect } from 'react-redux'
import { Button } from "antd";
import { todoAdd } from '../redux/module/test'
import Decorator from 'DTPath'
@connect(
    (state, ownProps) => {
        console.log(ownProps)
        console.log(state.test)
        return {
            test: state.test
        }
    },
    {
        todoAdd
    }
)
@Decorator()
export default class Test extends React.Component {
    constructor(props) {
        super(props)
    }

    render() {
        console.log(this.props)
        console.log(this.state)
        let { count,isLoading } = this.props.test
        return (
            <div className="right-con">
                <div className="page-title">

                </div>
                <div className="clearfix"></div>
                <div className="row">
                    <div className="col-md-12 col-sm-12 col-xs-12">
                        <div className="x_panel">
                            <div className="x_content">

                                目前count值:{isLoading ? <h1>loading...</h1> : count}
                                
                            </div>
                            <Button type='primary' onClick={() => this.props.todoAdd(count + 1)}>点击</Button>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}