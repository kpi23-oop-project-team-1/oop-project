import CartDialog from "../components/CartDialog"
import { DialogHolder, DialogInfo, DialogSwitch } from "../components/Dialogs"
import FullHeader from "../components/FullHeader"

export type PageWithFullHeaderDialogType = 'cart'

export type PageWithFullHeaderProps<T> = React.PropsWithChildren<{
    dialogType: PageWithFullHeaderDialogType | T | undefined,
    dialogSwitch?: DialogSwitch<T>
    onChangeDialogType: ((type: PageWithFullHeaderDialogType | T | undefined) => void),
}>

export default function PageWithSearchHeader<T>(props: PageWithFullHeaderProps<T>) {
    function dialogSwitch(type: PageWithFullHeaderDialogType | T): DialogInfo {
        switch (type) {
            case 'cart':
                return {
                    mode: 'fullscreen',
                    factory: () => <CartDialog onClose={() => props.onChangeDialogType(undefined)}/>
                }
            default:
                if (props.dialogSwitch) {
                    return props.dialogSwitch(type)
                }
                throw "Unexpected type in props"
        }
    }

    return (
        <DialogHolder<PageWithFullHeaderDialogType | T> 
          dialogType={props.dialogType}
          dialogSwitch={dialogSwitch}
          onHideDialog={() => props.onChangeDialogType(undefined)}>
            <FullHeader onShowCart={() => props.onChangeDialogType('cart')}/>
            {props.children}
        </DialogHolder>
    )
}