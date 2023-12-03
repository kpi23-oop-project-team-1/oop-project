import { useContext, useEffect, useMemo, useState } from "react"
import { CartContext, useCart } from "../cart"
import "../styles/AdminPage.scss"
import { UserTypeContext, useCurrentUserType } from "../user.react"
import { DiContainerContext } from "../diContainer"
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader"
import { useValueFromDataSource } from "../dataSource.react"
import DeferredDataContainer from "../components/DeferredDataContainer"
import { ConciseProductInfo } from "../dataModels"
import { useNavigate } from "react-router"
import { Link } from "react-router-dom"

export default function AdminPage() {
    const diContainer = useContext(DiContainerContext)
    
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType | undefined>()
    const [cart, cartManager] = useCart()
    const userType = useCurrentUserType()
    const navigate = useNavigate()

    useEffect(() => {
        if ((userType.type == 'success' && userType.value != 'admin') || userType.type == 'error') {
            navigate("/")
        }
    }, [userType])

    const userCreds = useMemo(() => diContainer.userCredsStore.getCurrentUserCredentials(), [])
    const [productsState] = useValueFromDataSource(async ds => userCreds ? await ds.getProductsWaitingApproval(userCreds) : [], [userCreds])

    return (
        <UserTypeContext.Provider value={userType.value}>
        <CartContext.Provider value={[cart, cartManager]}>
            <PageWithSearchHeader
              dialogType={dialogType}
              onChangeDialogType={setDialogType}>
                
                <DeferredDataContainer state={productsState}>
                    <ProductList products={productsState.value ?? []}/>
                </DeferredDataContainer>

            </PageWithSearchHeader>
        </CartContext.Provider>
        </UserTypeContext.Provider>
    )
}

type ProductListProps = {
    products: ConciseProductInfo[]
}

function ProductList(props: ProductListProps) {
    return (
        <div id="admin-page-product-list">
            {props.products.map(p => <ProductView key={p.id} product={p}/>)}
        </div>
    )
}

type ProductViewProps = {
    product: ConciseProductInfo
}

function ProductView(props: ProductViewProps) {
    return (
        <Link to={`/product/${props.product.id}`}>
            <div className="admin-page-product-view">
                <img src={props.product.imageSource}/>
                <p>{props.product.title}</p>
            </div>
        </Link>
    )
}