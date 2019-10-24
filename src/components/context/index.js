import React from 'react';
let Rx = null;
export default (func) => {
    if(!Rx){
        Rx = React.createContext(
            func
        )
    }
    return Rx;
}