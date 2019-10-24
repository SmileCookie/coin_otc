
import {ERRORCONFIG} from '../conf';
import { isFloat } from '../utils'
const BigNumber = require('big.js')
// Exception


export default (Comp) => {
    
    class Erro extends Comp{
        constructor(props){
            super(props);
            this.state = {
                dataError:false
            }

        }

        componentDidMount(){
            super.componentDidMount && super.componentDidMount();
            
        }

       
        // render
        render(){
            return super.render();
        }
    }

};

