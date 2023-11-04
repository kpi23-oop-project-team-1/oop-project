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
    function createDialogHolderClass(dialogInfo: DialogInfo | undefined): string {
        if (dialogInfo != undefined) {
            return "active-dialog" + (dialogInfo.mode == "fullscreen" ? " fullscreen" : "")
        }

        return ""
    }

    function createDialogClass(dialogInfo: DialogInfo): string {
        return `dialog ${dialogInfo.mode}`; 
    }
    
    useEffect(() => {
        const classList = document.body.classList

        if (props.dialogType != undefined) {
            classList.add("active-full-screen-dialog")
        } else {
            classList.remove("active-full-screen-dialog")
        }
    }, [props.dialogType])

    const dialogInfo = props.dialogType != undefined ? props.dialogSwitch(props.dialogType) : undefined

    return (
        <div id="dialog-holder" className={createDialogHolderClass(dialogInfo)}>
            {props.children}
            {dialogInfo ? (
                <div id="dialog-overlay" onClick={props.onHideDialog}>
                    <div className={createDialogClass(dialogInfo)} onClick={e => e.stopPropagation()}>
                        {dialogInfo.factory()}
                    </div>
                </div>
            ) : undefined}
        </div>
    )
}