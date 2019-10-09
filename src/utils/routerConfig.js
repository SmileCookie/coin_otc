import axios from './fetch'
import { Switch, Route } from 'react-router-dom'
import { DOMAIN_VIP } from '../conf'

class RC extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            list: []
        }
    }
    componentDidMount() {
        this.fetchRoutes()
    }
    fetchRoutes = () => {
        axios.get(DOMAIN_VIP + "/sys/menu/getRoleMenu").then(res => {
            let  result = res.data;
            if(result.code == 0){
                result = result.data
                console.log(result)
            }
        })
    }
    render() {
        const { list } = this.state
        console.log(list)
        return (
            <div>
                <Switch>
                    {
                        list.map((v) => {
                            
                            return <Route 
                                path={}
                            />
                        })
                    }
                </Switch>
            </div>
        )
    }
}

export default RC