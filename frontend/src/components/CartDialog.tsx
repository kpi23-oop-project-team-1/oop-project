import { useContext, useState } from "react"
import { StringResourcesContext } from "../StringResourcesContext"
import DeferredDataContainer from "./DeferredDataContainer"
import { usePopper } from 'react-popper';
import Menu, { MenuItem } from "./Menu"

import "../styles/CartDialog.scss"
import OptionsIcon from "../icons/options.svg"
import CloseIcon from "../icons/close.svg"
import TrashIcon from "../icons/trash.svg"
import NumberSpinner from "./NumberSpinner"
import { formatPriceToString } from "../utils/stringFormatting"
import { Dialog } from "./Dialogs"
import { CartContext, CartProductInfo } from "../cart"
import { Link } from "react-router-dom";

export type CartDialogProps = {
    onClose: () => void
}

export default function CartDialog(props: CartDialogProps) {
    const strRes = useContext(StringResourcesContext)

    let [cartState, cartManager] = useContext(CartContext)

    function computeTotalPrice(): number {
        return (cartState.value ?? []).reduce((s, p) => s + p.price * p.quantity, 0)
    }

    return (
        <Dialog id="cart-dialog">
            <div id="cart-dialog-header">
                <p id="cart-dialog-title">{strRes.cart}</p>
                <button id="cart-dialog-close" className="icon-button" onClick={props.onClose}>
                    <CloseIcon/>
                </button>
            </div>

            <DeferredDataContainer state={cartState}>
                 <div id="cart-dialog-product-list">
                    {(cartState.value ?? []).map(product => 
                        <CartItem 
                          key={product.id}
                          product={product}
                          updateProductAmount={amount => cartManager.updateProductQuantity(product.id, amount)}
                          onRemoveSelf={() => cartManager.removeProduct(product.id)}/>
                    )}
                </div>
            </DeferredDataContainer>

            <div id="cart-dialog-buy-container">
                <p id="cart-dialog-total-price">
                    {formatPriceToString(computeTotalPrice())}
                </p>
                
                <Link to="/" id="cart-dialog-buy-button" className="link-button primary">{strRes.checkout}</Link>
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
            <Link className="cart-item-title" to={`/product/${props.product.id}/`}>{props.product.title}</Link>

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