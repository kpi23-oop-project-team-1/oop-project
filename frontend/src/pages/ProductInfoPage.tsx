import { useContext, useMemo, useState } from "react";
import { CartContext, CartProductInfo, useCart } from "../cart";
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader";
import { useParams } from "react-router";
import { isValidNumber } from "../utils/dataValidation";
import ImageCarousel from "../components/ImageCarousel";
import { useValueFromDataSource } from "../dataSource.react";
import { formatPriceToString } from "../utils/stringFormatting";
import { StringResourcesContext } from "../StringResourcesContext";
import { ProductComment, ProductInfo } from "../dataModels";
import { StarRating } from "../components/StarRating";
import "../styles/ProductInfoPage.scss"
import NumberInput from "../components/NumberInput";
import Footer from "../components/Footer";
import { UserTypeContext, useUserType } from "../user.react";

export default function ProductInfoPage() {
    const productId = useProductId() ?? 0
    const strRes = useContext(StringResourcesContext)

    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const [cart, cartManager] = useCart()
    const [quantity, setQuantity] = useState(1)

    const [productState] = useValueFromDataSource(ds => ds.getProductInfo(productId), [productId])
    const product = productState.value

    const isProductInCart = useMemo(() => cartManager.isProductInCart(productId), [cart])
    const userType = useUserType()

    function addToCart() {
        if (product) {
            cartManager.addProduct(productToCartProduct(product, quantity))
        }
    }

    return (
        <UserTypeContext.Provider value={userType.value}>
        <CartContext.Provider value={[cart, cartManager]}>
            <PageWithSearchHeader 
              dialogType={dialogType} 
              onChangeDialogType={setDialogType}>
                <div id="product-info-content">
                    <ImageCarousel imageSources={product?.imageSources ?? []}/>

                    <div id="product-info-content-grid">
                        <div id="product-info-content-left-side">
                            <p id="product-info-title">{product?.title ?? ""}</p>

                            <Section header={strRes.description}>
                                <p id="product-info-description">{product?.description ?? ""}</p>
                            </Section>

                            <Section header={strRes.characteristics}>
                                <Characteristic name={strRes.category} value={product?.category} valueToTextMap={strRes.productCategoryLabels}/>
                                <Characteristic name={strRes.productState} value={product?.state} valueToTextMap={strRes.productStateLabels}/>
                                <Characteristic name={strRes.color} value={product?.color} valueToTextMap={strRes.colorLabels}/>
                            </Section>

                            <Section header={strRes.productComments}>
                                {product?.comments.map(comment => <CommentView comment={comment}/>)}
                            </Section>
                            
                        </div>

                        <div id="product-info-content-right-side">
                            <p id="product-info-price">{product ? formatPriceToString(product.price) : ""}</p>

                            {
                                userType.value != 'buyer-seller' ?
                                undefined 
                                :
                                <>
                                    {isProductInCart ? 
                                        <p id="product-info-in-cart-label">{strRes.productAlreadyInCart}</p> 
                                        :  
                                        <>
                                            <p id="product-info-quantity-label">{strRes.quantity}</p>
                                            <NumberInput 
                                              id="product-info-quantity-input"
                                              value={quantity}
                                              onChanged={setQuantity}
                                              validateNumber={value => value >= 1 && value <= (product?.totalAmount ?? 1)}/>
                                            <button 
                                              id="product-info-add-to-cart"
                                              className="primary"
                                              onClick={addToCart}>
                                                {strRes.addToCart}
                                            </button>
                                    </>}

                                    <button 
                                      id="product-info-write-comment"
                                      onClick={() => {}}>
                                        {strRes.writeComment}
                                    </button>
                                </>
                            }
                        </div>
                    </div>
                </div> 
                <Footer/>
            </PageWithSearchHeader>
        </CartContext.Provider>
        </UserTypeContext.Provider>
    )
}

function productToCartProduct(product: ProductInfo, quantity: number): CartProductInfo {
    return { ...product, quantity, imageSource: product.imageSources[0] }
}

function Section(props: React.PropsWithChildren<{ header: string }>) {
    return (
        <>
            <h2>{props.header}</h2>
            <div className="product-info-section-content">
                {props.children}
            </div>
        </>
    )
}

type CommentViewProps = {
    comment: ProductComment
}

function CommentView(props: CommentViewProps) {
    const comment = props.comment

    return (
        <div className="product-info-comment-container">
            <div className="product-info-comment-header">
                <p className="product-info-comment-display-name">{comment.user.displayName}</p>
                <p className="product-info-comment-date">{new Date(comment.dateString).toLocaleDateString()}</p>
            </div>

            <StarRating value={comment.rating} total={5}/>
            <p className="product-info-comment-text">{comment.text}</p>
        </div>
    )
}

type CharacteristicProps<T extends number | string> = {
    name: string,
    value: T | undefined,
    valueToTextMap: Record<T, string> | ((value: T) => string)
}

function Characteristic<T extends number | string>(props: CharacteristicProps<T>) {
    const map = props.valueToTextMap
    const valueStr = props.value ? 
        (typeof map == 'function' ? map(props.value) : map[props.value])
        : undefined

    return (
        <div className="product-info-characteristic">
            <span className="product-info-characteristic-name">{props.name}:</span>
            <span className="product-info-characteristic-value">{valueStr}</span>
        </div>
    )
}

function useProductId(): number | undefined {
    const params = useParams()
    const productIdStr = params.productId

    return productIdStr && isValidNumber(productIdStr) ? parseInt(productIdStr) : undefined
}