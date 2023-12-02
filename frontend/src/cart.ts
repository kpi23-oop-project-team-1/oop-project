import { createContext, useContext, useMemo } from "react"
import { DeferredDataState, mutateStateIfSuccess } from "./deferredData"
import { mutateElementAndCopy, pushElementAndCopy, removeElementAndCopy } from "./utils/arrayUtils"
import { useValueFromDataSource } from "./dataSource.react"
import { DiContainerContext } from "./diContainer"

export type CartProductInfo = {
    id: number,
    title: string,
    imageSource: string,
    price: number,
    quantity: number,
    totalAmount: number
}

export type Cart = CartProductInfo[]

export type CartManager = {
    isProductInCart(productId: number): boolean
    addProduct(product: CartProductInfo): void
    removeProduct(productId: number): void
    updateProductQuantity(productId: number, value: number): void
    removeAllProductsLocal(): void 
}

export function useCart(): [DeferredDataState<Cart>, CartManager] {
    const diContainer = useContext(DiContainerContext)
    const dataSource = diContainer.dataSource
    
    const userCreds = useMemo(() => diContainer.userCredsStore.getCurrentUserCredentials(), [])
    const [cartState, setCartState] = useValueFromDataSource(
        async ds => userCreds ? await ds.getCartProductsAsync(userCreds) : [], 
    )

    function processPromiseErrors<T>(promise: Promise<T>) {
        promise.catch(() => {
            setCartState({ type: 'error' })
        })
    }

    function mutateCartIfSuccess(fn: (value: Cart) => Cart) {
        setCartState(state => mutateStateIfSuccess(state, fn))
    }

    const manager: CartManager = {
        isProductInCart(productId) {
            return (cartState.value ?? []).findIndex(p => p.id == productId) >= 0
        },
        addProduct(product) {
            if (this.isProductInCart(product.id)) {
                return
            }

            if (cartState.type != 'error' && userCreds) {
                mutateCartIfSuccess(products => pushElementAndCopy(products, product))

                processPromiseErrors(dataSource.addProductToCartAsync(product.id, userCreds))
            }
        },
        removeProduct(productId) {
            if (this.isProductInCart(productId) && userCreds) {
                mutateCartIfSuccess(products => removeElementAndCopy(products, p => p.id == productId))

                processPromiseErrors(dataSource.removeProductFromCartAsync(productId, userCreds))
            }
        },
        updateProductQuantity(productId, value) {
            if (this.isProductInCart(productId) && userCreds) {
                mutateCartIfSuccess(products => 
                    mutateElementAndCopy(
                        products, 
                        p => p.id == productId,
                        p => { return { ...p, quantity: value } }
                    )
                )

                processPromiseErrors(dataSource.updateCartProductQuantityAsync(productId, value, userCreds))
            }
        },
        removeAllProductsLocal() {
            mutateCartIfSuccess(_ => [])
        }
    }

    return [cartState, manager]
}

const stubCartManager: CartManager = {
    addProduct: () => {},
    isProductInCart: () => false,
    removeProduct: () => {},
    updateProductQuantity: () => {},
    removeAllProductsLocal: () => {}
}

export const CartContext = createContext<[DeferredDataState<Cart>, CartManager]>([{ type: 'loading' }, stubCartManager])