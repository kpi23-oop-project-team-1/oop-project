import { useContext, useState } from "react"
import "../styles/MainPage.scss"
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader"
import HorizontalScrollContainer from "../components/HorizontalScrollContainer"
import { ConciseProductInfo } from "../dataModels"
import ProductImageWithStripe from "../components/ProductImageWithStripe"
import { formatPriceToString } from "../utils/stringFormatting"
import { StringResourcesContext } from "../StringResourcesContext"
import Footer from "../components/Footer"
import DeferredDataContainer from "../components/DeferredDataContainer"
import { CartContext, useCart } from "../cart"
import { useValueFromDataSource } from "../dataSource.react"
import { Link } from "react-router-dom"
import { UserTypeContext, useCurrentUserType } from "../user.react"

export default function MainPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const cartAndManager = useCart()
    const userType = useCurrentUserType()

    return (
        <UserTypeContext.Provider value={userType.value}>
            <CartContext.Provider value={cartAndManager}>
                <PageWithSearchHeader
                  dialogSwitch={undefined} 
                  dialogType={dialogType} 
                  onChangeDialogType={setDialogType}>
                    <SpecialProductsBlock/>
                    <AboutBlock/>
                    <Footer/>
                </PageWithSearchHeader>
            </CartContext.Provider>
        </UserTypeContext.Provider>
    )
}

function SpecialProductsBlock() {
    const strRes = useContext(StringResourcesContext)

    let [productsState] = useValueFromDataSource(ds => ds.getSpecialProductsAsync())

    return (
      <div id="special-products-block">
            <h2 className="primary">{strRes.specialProducts}</h2>

            <DeferredDataContainer state={productsState}>
                <HorizontalScrollContainer 
                  data={productsState.value ?? []} 
                  listItemGenerator={product => <SpecialProduct product={product}/>}/>
            </DeferredDataContainer>
      </div>) 
}

type SpecialProductProps = {
    product: ConciseProductInfo
};

function SpecialProduct(props: SpecialProductProps) {
    const product = props.product

    const strRes = useContext(StringResourcesContext)
    const [_, cartManager] = useContext(CartContext)
    const userType = useContext(UserTypeContext)

    function addProductToCart() {
        cartManager.addProduct({ ...product, quantity: 1 })
    }

    return (
        <div className="special-product">
            <Link className="special-product-link-block" to={`/product/${product.id}/`}>
                <ProductImageWithStripe imageSource={product.imageSource} stripeText={product.stripeText}/>
                <p className="special-product-title">{product.title}</p>
                <p className="special-product-price">{formatPriceToString(product.price)}</p>
            </Link>
            
            {
                !cartManager.isProductInCart(product.id) && userType == 'customer-trader' ?
                    <button 
                      className="special-product-add-to-cart primary"
                      onClick={addProductToCart}>
                        {strRes.addToCart}
                    </button>
                    : undefined
            }
            
        </div>
    )
}

function AboutBlock() {
    const strRes = useContext(StringResourcesContext)

    return (<div id="about-us-block">
        <h1 className="uppercase">{strRes.aboutUs}</h1>
        <p>{strRes.aboutShopifyDetail}</p>
    </div>)
}