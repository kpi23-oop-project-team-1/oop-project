import React from "react"
import LoadingIndicator from "./LoadingIndicator"
import "../styles/DeferredDataContainer.scss"

export type DeferredDataState<T> = {
    type: 'success' | 'error' | 'loading' | 'initialized',
    value?: T
} 

export type DeferredDataContainerProps<T> = React.PropsWithChildren<{
    createPromise: () => Promise<T>,
    state: DeferredDataState<T>
    setState: (state: DeferredDataState<T>) => void,
    errorView?: React.ReactNode
}>

export default function DeferredDataContainer<T>(props: DeferredDataContainerProps<T>) {
    var isLoading = false
    
    if (props.state.type == 'initialized') {
        props.setState({ type: 'loading' })

        props.createPromise().then(result => {
            props.setState({ type: 'success', value: result })
        }, () => {
            props.setState({ type: 'error' })
        })
    }

    return <div className="deferred-data-container">
        {props.state.type == 'loading' ? <LoadingIndicator/> : undefined}
        {props.state.type == 'error' ? props.errorView : undefined }
        {props.state.type == 'success' ? props.children : undefined }
    </div>
}