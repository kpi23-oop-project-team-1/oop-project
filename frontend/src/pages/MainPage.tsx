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
import DeferredDataContainer, { DeferredDataState } from "../components/DeferredDataContainer"
import { DiContainerContext, TestDiContainer } from "../diContainer"

export default function MainPage() {
    let [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()

    return (
        <StringResourcesContext.Provider value={ukrainianStringResources}>
            <DiContainerContext.Provider value={TestDiContainer}>
                <PageWithSearchHeader
                  dialogSwitch={undefined} 
                  dialogType={dialogType} 
                  onChangeDialogType={setDialogType}>
                    <SpecialProductsBlock/>
                    <AboutBlock/>
                    <Footer/>
                </PageWithSearchHeader>
            </DiContainerContext.Provider>
        </StringResourcesContext.Provider>
    )
}

function SpecialProductsBlock() {
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)

    let [productsState, setProductsState] = useState<DeferredDataState<ConciseProductInfo[]>>({ type: 'initialized' })

    return (
      <div id="special-products-block">
            <h2 className="primary">{strRes.specialProducts}</h2>

            <DeferredDataContainer 
              createPromise={() => diContainer.dataSource.getSpecialProductsAsync()}
              state={productsState}
              setState={setProductsState}>
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
    const strRes = useContext(StringResourcesContext);

    return (<div className="special-product">
        <ProductImageWithStripe imageSource={props.product.imageSource} stripeText={props.product.stripeText}/>
        <p className="special-product-title">{props.product.title}</p>
        <p className="special-product-price">{formatPriceToString(props.product.price)}</p>
        <button className="special-product-add-to-cart primary">{strRes.addToCart}</button>
    </div>)
}

function AboutBlock() {
    const strRes = useContext(StringResourcesContext)

    return (<div id="about-us-block">
        <h1 className="uppercase">{strRes.aboutUs}</h1>
        <p>{strRes.aboutShopifyDetail}</p>
    </div>)
}