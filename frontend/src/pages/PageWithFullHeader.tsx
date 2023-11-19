import { useContext } from "react"
import CartDialog from "../components/CartDialog"
import { DialogHolder, DialogInfo, DialogSwitch } from "../components/Dialogs"
import FullHeader from "../components/FullHeader"
import { UserTypeContext, useUserType } from "../user.react"

export type PageWithFullHeaderDialogType = 'cart'

export type PageWithFullHeaderProps<T> = React.PropsWithChildren<{
    dialogType: PageWithFullHeaderDialogType | T | undefined,
    dialogSwitch?: DialogSwitch<T>
    onChangeDialogType: ((type: PageWithFullHeaderDialogType | T | undefined) => void),
}>

export default function PageWithSearchHeader<T>(props: PageWithFullHeaderProps<T>) {
    function dialogSwitch(type: PageWithFullHeaderDialogType | T): DialogInfo {
        // Temporarily like this. More dialogs will be added.
        switch (type) {
            case 'cart':
                return {
                    mode: 'fullscreen',
                    factory: () => <CartDialog onClose={() => props.onChangeDialogType(undefined)}/>
                }
            default:
                throw "Unexpected type in props"
        }
    }

    const userType = useUserType()

    return (
        <UserTypeContext.Provider value={userType.value}>
            <DialogHolder<PageWithFullHeaderDialogType | T> 
              dialogType={props.dialogType}
              dialogSwitch={dialogSwitch}
              onHideDialog={() => props.onChangeDialogType(undefined)}>
                <FullHeader onShowCart={() => props.onChangeDialogType('cart')}/>

                {props.children}
            </DialogHolder>
        </UserTypeContext.Provider>
        
    )
}