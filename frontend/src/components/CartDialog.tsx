import { useContext, useState } from "react"
import { StringResourcesContext } from "../StringResourcesContext"
import { CartProductInfo } from "../dataModels"
import DeferredDataContainer, { DeferredDataState } from "./DeferredDataContainer"
import { DiContainerContext } from "../diContainer"
import { usePopper } from 'react-popper';
import Menu, { MenuItem } from "./Menu"

import "../styles/CartDialog.scss"
import OptionsIcon from "../../public/images/options.svg"
import CloseIcon from "../../public/images/close.svg"
import TrashIcon from "../../public/images/trash.svg"
import NumberSpinner from "./NumberSpinner"
import { formatPriceToString } from "../utils/stringFormatting"
import { removeElementAndCopy, mutateElementAndCopy } from "../utils/arrayUtils"
import { Dialog } from "./Dialogs"

export type CartDialogProps = {
    onClose: () => void
}

export default function CartDialog(props: CartDialogProps) {
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)
    const dataSource = diContainer.dataSource

    let [cartDataState, setCartDataState] = useState<DeferredDataState<CartProductInfo[]>>({ type: 'initialized' })

    function getCartProducts(): CartProductInfo[] {
        return cartDataState.value ?? []
    }

    function opportunisticOperation<T>(promise: Promise<T>) {
        promise.catch(() => {
            setCartDataState({ type: 'initialized' })
        })
    }

    function updateProductQuantity(productId: number, newQuantity: number) {
        setCartDataState(state => {
            return { 
                type: 'success', 
                value: mutateElementAndCopy(
                    state.value ?? [],
                    p => p.id == productId,
                    p => { return { ...p, quantity: newQuantity } }
                ) 
            }
        })
        
        opportunisticOperation(dataSource.updateCartProductQuantity(productId, newQuantity))
    }

    function removeProduct(productId: number) {
        setCartDataState(state => {
            return { 
                type: 'success',
                 value: removeElementAndCopy(state.value ?? [], p => p.id == productId) 
            }
        })

        opportunisticOperation(dataSource.removeProductFromCart(productId))
    }

    function computeTotalPrice(): number {
        return getCartProducts().reduce((s, p) => s + p.price * p.quantity, 0)
    }

    return (
        <Dialog id="cart-dialog">
            <div id="cart-dialog-header">
                <p id="cart-dialog-title">{strRes.cart}</p>
                <button id="cart-dialog-close" className="icon-button" onClick={props.onClose}>
                    <CloseIcon/>
                </button>
            </div>

            <DeferredDataContainer
              createPromise={() => dataSource.getCartProducts()}
              state={cartDataState}
              setState={setCartDataState}>
                 <div id="cart-dialog-product-list">
                    {getCartProducts().map(product => 
                        <CartItem 
                          product={product}
                          key={product.id}
                          updateProductAmount={amount => updateProductQuantity(product.id, amount)}
                          onRemoveSelf={() => removeProduct(product.id)}/>
                    )}
                </div>
            </DeferredDataContainer>

            <div id="cart-dialog-buy-container">
                <p id="cart-dialog-total-price">
                    {formatPriceToString(computeTotalPrice())}
                </p>
                
                <a href="/" id="cart-dialog-buy-button" className="link-button primary">{strRes.checkout}</a>
            </div>
        </Dialog>
    )
}

type CartItemProps = {
    product: CartProductInfo,
    updateProductAmount: (value: number) => void,
    onRemoveSelf: () => void
}

function CartItem(props: CartItemProps) {
    return (
        <div className="cart-item-container">
            <CartItemMainInfo product={props.product} onRemoveSelf={props.onRemoveSelf}/>
            <div className="cart-item-price-amount">
                <NumberSpinner 
                  value={props.product.quantity}
                  minValue={1}
                  maxValue={props.product.totalAmount}
                  onValueChanged={props.updateProductAmount}/>

                <p className="cart-item-price">{formatPriceToString(props.product.price)}</p>
            </div>
        </div>
    )
}

type CartItemMainInfoProps = {
    product: CartProductInfo,
    onRemoveSelf: () => void
}

function CartItemMainInfo(props: CartItemMainInfoProps) {
    let [optionsMenuElement, setOptionsMenuElement] = useState<HTMLDivElement | null>(null);
    let [optionsButtonElement, setOptionsButtonElement] = useState<HTMLButtonElement | null>(null);
    let [isOptionsOpen, setOptionsOpen] = useState(false);

    const { styles, attributes } = usePopper(optionsButtonElement, optionsMenuElement, { placement: 'bottom-end' });
    const strRes = useContext(StringResourcesContext)

    return (
        <div className="cart-item-main-info">
            <img src={props.product.imageSource}/>
            <a className="cart-item-title" href="/">{props.product.title}</a>
            <button 
              className="cart-item-options-button icon-button"
              ref={setOptionsButtonElement}
              onClick={() => setOptionsOpen(s => !s)}>
                <OptionsIcon/>
            </button>

            {isOptionsOpen &&
                <div 
                  className="cart-item-options-menu"
                  ref={setOptionsMenuElement}
                  style={styles.popper}
                  {...attributes.popper}>
                    <Menu>
                        <MenuItem 
                          text={strRes.remove}
                          icon={<TrashIcon/>}
                          onClick={props.onRemoveSelf}/>
                    </Menu>
                </div>
            }
        </div>
    )
}