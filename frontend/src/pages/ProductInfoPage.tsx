import { useContext, useEffect, useMemo, useState } from "react";
import { CartContext, CartProductInfo, useCart } from "../cart";
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader";
import { useNavigate, useParams } from "react-router";
import { isValidNumber } from "../utils/dataValidation";
import ImageCarousel from "../components/ImageCarousel";
import { useValueFromDataSource } from "../dataSource.react";
import { formatPriceToString } from "../utils/stringFormatting";
import { StringResourcesContext } from "../StringResourcesContext";
import { CommentInfo, ProductInfo, totalCommentStarCount } from "../dataModels";
import { StarRating } from "../components/StarRating";
import "../styles/ProductInfoPage.scss"
import NumberInput from "../components/NumberInput";
import Footer from "../components/Footer";
import { UserTypeContext, useCurrentUserType } from "../user.react";
import PostCommentDialog from "../components/PostCommentDialog";
import { DialogInfo } from "../components/Dialogs";
import { DiContainerContext } from "../diContainer";
import { CommentView } from "../components/CommentView";

type OwnPageDialogType = 'post-comment'
type DialogType = PageWithFullHeaderDialogType | OwnPageDialogType

export default function ProductInfoPage() {
    const productId = useProductId()
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)
    const dataSource = diContainer.dataSource

    const [dialogType, setDialogType] = useState<DialogType | undefined>()
    const [cart, cartManager] = useCart()
    const [quantity, setQuantity] = useState(1)
    const navigate = useNavigate()

    useEffect(() => {
        if (productId == undefined) {
            navigate("/")
        }
    }, [productId])

    const [productState, setProductState] = useValueFromDataSource(ds => ds.getProductInfo(productId ?? -1), [productId])
    const product = productState.value

    const isProductInCart = useMemo(() => cartManager.isProductInCart(productId ?? 0), [cart])
    const userCreds = useMemo(() => diContainer.userCredsStore.getCurrentUserCredentials(), [])
    const userType = useCurrentUserType()

    function addToCart() {
        if (product) {
            cartManager.addProduct(productToCartProduct(product, quantity))
        }
    }

    function showCommentDialog() {
        setDialogType('post-comment')
    }

    function doPostComment(info: { rating: number, text: string }) {
        if (userCreds) {
            dataSource.postProductComment({ targetId: productId ?? -1, ...info }, userCreds).then(() => {
                setDialogType(undefined)
                setProductState({ type: 'loading' })
            }).catch(() => {

            })
        }
    }

    function dialogSwitch(): DialogInfo {
        // Currently there's only 'post-comment' dialog type
        return { 
            mode: 'fullscreen',
            factory: () => <PostCommentDialog onClose={() => setDialogType(undefined)} onPost={doPostComment} headerText={strRes.rateProductHeader}/> 
        }
    }

    return (
        <UserTypeContext.Provider value={userType.value}>
        <CartContext.Provider value={[cart, cartManager]}>
            <PageWithSearchHeader 
              dialogType={dialogType} 
              dialogSwitch={dialogSwitch}
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
                                {product?.comments.map(comment => <CommentView key={comment.id} comment={comment}/>)}
                            </Section>
                            
                        </div>

                        <div id="product-info-content-right-side">
                            <p id="product-info-price">{product ? formatPriceToString(product.price) : ""}</p>

                            {
                                userType.value != 'customer-trader' ?
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
                                      onClick={showCommentDialog}>
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

    return productIdStr != undefined && isValidNumber(productIdStr) ? parseInt(productIdStr) : undefined
}