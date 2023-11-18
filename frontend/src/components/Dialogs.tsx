import { PropsWithChildren, useEffect } from 'react';
import '../styles/Dialogs.scss'

export type DialogMode = 'fullscreen' | 'under-header'

export type DialogInfo = {
    mode: DialogMode,
    factory: () => React.ReactNode
}

export type DialogSwitch<T> = (dialogType: T) => DialogInfo

export type DialogHolderProps<T> = PropsWithChildren<{
    dialogType: T | undefined,
    dialogSwitch: DialogSwitch<T>,
    onHideDialog: () => void
}>;

export function DialogHolder<T>(props: DialogHolderProps<T>) {
    useEffect(() => {
        const classList = document.body.classList

        if (props.dialogType != undefined) {
            classList.add("active-full-screen-dialog")
        } else {
            classList.remove("active-full-screen-dialog")
        }
    }, [props.dialogType])

    const dialogInfo = props.dialogType != undefined ? props.dialogSwitch(props.dialogType) : undefined
    const dialogHolderClass = dialogInfo ? "active-dialog " + dialogInfo.mode : ""

    return (
        <div id="dialog-holder" className={dialogHolderClass}>
            {props.children}
            {dialogInfo ? (
                <div id="dialog-overlay" onClick={props.onHideDialog}>
                    {dialogInfo.factory()}
                </div>
            ) : undefined}
        </div>
    )
}

export type DialogProps = React.PropsWithChildren<{
    id?: string
}>

export function Dialog(props: DialogProps) {
    return (<div className="dialog" id={props.id} onClick={e => e.stopPropagation()}>
        {props.children}
    </div>)
}