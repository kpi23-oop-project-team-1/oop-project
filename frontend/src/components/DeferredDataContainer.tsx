import React from "react"
import LoadingIndicator from "./LoadingIndicator"
import { DeferredDataState } from "../deferredData"
import "../styles/DeferredDataContainer.scss"

export type DeferredDataContainerProps<T> = React.PropsWithChildren<{
    state: DeferredDataState<T>
    errorView?: React.ReactNode
}>

export default function DeferredDataContainer<T>(props: DeferredDataContainerProps<T>) {
    return <div className="deferred-data-container">
        {props.state.type == 'loading' ? <LoadingIndicator/> : undefined}
        {props.state.type == 'error' ? props.errorView : undefined }
        {props.state.type == 'success' ? props.children : undefined }
    </div>
}