import { useContext, useState } from "react"
import "../styles/MainPage.scss"
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader"
import HorizontalScrollContainer from "../components/HorizontalScrollContainer"
import { ConciseProductInfo } from "../dataModels"
import ProductImageWithStripe from "../components/ProductImageWithStripe"
import { formatPriceToString } from "../utils/stringFormatting"
import { StringResourcesContext } from "../StringResourcesContext"
import { ukrainianStringResources } from "../StringResources"
import Footer from "../components/Footer"
import DeferredDataContainer from "../components/DeferredDataContainer"
import { DiContainerContext, TestDiContainer } from "../diContainer"
import { CartContext, useCart } from "../cart"
import { useValueFromDataSource } from "../dataSource.react"

export default function MainPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const cartAndManager = useCart(TestDiContainer.dataSource)

    return (
        <StringResourcesContext.Provider value={ukrainianStringResources}>
            <DiContainerContext.Provider value={TestDiContainer}>
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
            </DiContainerContext.Provider>
        </StringResourcesContext.Provider>
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

    function addProductToCart() {
        cartManager.addProduct({ ...product, quantity: 1 })
    }

    return (<div className="special-product">
        <ProductImageWithStripe imageSource={product.imageSource} stripeText={product.stripeText}/>
        <p className="special-product-title">{product.title}</p>
        <p className="special-product-price">{formatPriceToString(product.price)}</p>
        
        {
            !cartManager.isProductInCart(product.id) ?
                <button 
                  className="special-product-add-to-cart primary"
                  onClick={addProductToCart}>
                    {strRes.addToCart}
                </button>
                : undefined
        }
    </div>)
}

function AboutBlock() {
    const strRes = useContext(StringResourcesContext)

    return (<div id="about-us-block">
        <h1 className="uppercase">{strRes.aboutUs}</h1>
        <p>{strRes.aboutShopifyDetail}</p>
    </div>)
}