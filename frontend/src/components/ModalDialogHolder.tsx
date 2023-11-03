import { PropsWithChildren, ReactNode, useEffect } from 'react';
import '../styles/ModalDialogHolder.scss'

export type ModalDialogType = | 'fullscreen' | 'under-header'
export type ModalDialogInfo = {
    type: ModalDialogType
}

export type ModalDialogHolderProps = {
    active: boolean,
    type?: ModalDialogType,
    dialog?: ReactNode
};

export default function ModalDialogHolder(props: PropsWithChildren<ModalDialogHolderProps>) {
    function createModalDialogHolderClass(): string {
        if (props.active) {
            return "active-modal-dialog" + (props.type == "fullscreen" ? " fullscreen" : "")
        }

        return ""
    }
    
    useEffect(() => {
        const classList = document.body.classList

        if (props.active) {
            classList.add("active-full-screen-modal-dialog")
        } else {
            classList.remove("active-full-screen-modal-dialog")
        }
    }, [props.active])

    return (
        <div id="modal-dialog-holder" className={createModalDialogHolderClass()}>
            {props.children}
            {props.active ? (
                <div id="modal-dialog-overlay">
                    {props.dialog}
                </div>
            ) : undefined}
        </div>
    )
}