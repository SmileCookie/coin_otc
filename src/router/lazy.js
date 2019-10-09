/**
 * lazy component
 * @description use high component
 * @author luchao.ding
 */
import React, { Suspense } from 'react';

// when router select, use Loading transition.
const Loading = (
    <h3></h3>
);

/**
 * @typedef {Function<Function, Function>}
 */
export default (Cmp = () => {}, loading = Loading) => (props) => {
    return (
        <React.Fragment>
            <Suspense fallback={loading}>
                <Cmp {...props} />
            </Suspense>
        </React.Fragment>
    )
};