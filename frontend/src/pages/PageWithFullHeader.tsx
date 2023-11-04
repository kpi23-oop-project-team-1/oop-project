import { DialogHolder, DialogInfo, DialogSwitch } from "../components/Dialogs"
import FullHeader from "../components/FullHeader"

export type PageWithFullHeaderDialogType = undefined

export type PageWithFullHeaderProps<T> = React.PropsWithChildren<{
    // Actually this declaration has no sense as the CombinedPageWithSearchHeaderDialogType is intended to strip undefined when possible.
    // And we are always adding undefined. But this makes the transpiler happy.
    dialogType: PageWithFullHeaderDialogType | T | undefined,
    dialogSwitch?: DialogSwitch<T>
    onChangeDialogType: ((type: PageWithFullHeaderDialogType | T | undefined) => void),
}>

export default function PageWithSearchHeader<T>(props: PageWithFullHeaderProps<T>) {
    function dialogSwitch(type: PageWithFullHeaderDialogType | T): DialogInfo {
        // Temporarily like this. More dialogs will be added.
        switch (type) {
            default:
                if (type != null && props.dialogSwitch != undefined) {
                    return props.dialogSwitch(type)
                }
                throw "Expected dialogSwitch in props"
        }
    }

    return (
        <DialogHolder<PageWithFullHeaderDialogType | T> 
          dialogType={props.dialogType}
          dialogSwitch={dialogSwitch}
          onHideDialog={() => props.onChangeDialogType(undefined)}>
            <FullHeader />
            {props.children}
        </DialogHolder>
    )
}