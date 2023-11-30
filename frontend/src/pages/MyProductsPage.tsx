import { useNavigate } from "react-router";
import { CartContext, useCart } from "../cart";
import { UserTypeContext, navigateToMainPageIfNotBuyerSeller, useUserType } from "../user.react";
import { useContext, useEffect, useState } from "react";
import { StringResourcesContext } from "../StringResourcesContext";
import { DiContainerContext } from "../diContainer";
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader";
import UserSearchFilterPanel from "../components/UserSearchFilterPanel";
import { ConciseProductInfo, ProductStatus, UserProductSearchDesc, UserProductSearchFilter, allProductStatuses, userProductSearchFilterToSearchParams } from "../dataModels";
import { formatPriceToString } from "../utils/stringFormatting";
import { pushElementAndCopy, removeElementAndCopy } from "../utils/arrayUtils";
import Checkbox from "../components/Checkbox";
import { useValueFromDataSource } from "../dataSource.react";
import { DeferredDataState, mutateStateIfSuccess } from "../deferredData";
import DeferredDataContainer from "../components/DeferredDataContainer";
import "../styles/MyProductsPage.scss"
import { Link } from "react-router-dom";
import { isValidNumber } from "../utils/dataValidation";
import PageNavRow from "../components/PageNavRow";
import Footer from "../components/Footer";
import { useMappedSearchParams } from "../utils/urlUtils.react";

const emptySearchDesc: UserProductSearchDesc = {
    totalPages: 0,
}

export default function MyProductsPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)

    const dataSource = diContainer.dataSource
    const userCreds = diContainer.userCredsStore.getCurrentUserCredentials()

    const cartAndManager = useCart()
    const userType = useUserType()
    const navigate = useNavigate()

    navigateToMainPageIfNotBuyerSeller(userType, navigate)

    const [filter, setFilter] = useSearchFilterFromSearchParams()
    const [searchDesc] = useValueFromDataSource(
        async ds => userCreds ? await ds.getUserProductsSearchDesc(filter.status, userCreds) : emptySearchDesc, 
        [filter]
    )
    const [productsState, setProductsState] = useValueFromDataSource(
        async ds => userCreds ? await ds.getUserProducts(filter, userCreds) : [], 
        [filter]
    )

    const [selectedIds, setSelectedIds] = useState<number[]>([])

    function onAddProduct() {
        navigate('/addproduct')
    }

    function onDeleteProducts(ids: number[]) {
        if (userCreds) {
            const oldProducts = productsState

            dataSource.deleteProducts(ids, userCreds).then(() => {
                setProductsState(state => 
                    mutateStateIfSuccess(state, ps => ps.filter(p => !ids.includes(p.id)))
                )
            }).catch(() => {
                setProductsState(oldProducts)
            })
        }
    }

    function onEdit(id: number) {
        navigate(`/editproduct/${id}/`)
    }

    return (
        <CartContext.Provider value={cartAndManager}>
        <UserTypeContext.Provider value={userType.value}>

        <PageWithSearchHeader
          dialogType={dialogType}
          onChangeDialogType={setDialogType}>

            <h1 id="my-products-title">{strRes.myProductsTitle}</h1>

            <div id="my-products-columns-container">
                <UserSearchFilterPanel
                  filter={filter}
                  onChanged={setFilter}/>

                <div id="my-products-right-column">
                    <button 
                      id="my-products-add-product-button"
                      onClick={onAddProduct}>
                        {strRes.addProduct}
                    </button>

                    <ProductGridWithHeader
                      selectedIds={selectedIds}
                      onSelectionChanged={setSelectedIds}
                      products={productsState}
                      onDelete={onDeleteProducts}
                      onEdit={onEdit}/>
                </div>
            </div>

            {
                searchDesc.value && searchDesc.value.totalPages > 1 ?
                <PageNavRow 
                  createLink={page => createSearchFilterUrl({ ...filter, page })}
                  onNavigateLink={page => {
                      const newFilter = { ...filter, page }

                      navigate(createSearchFilterUrl(newFilter))
                      setFilter(newFilter)

                      // Clear the selection as the selected products is no longer on screen
                      setSelectedIds([]) 
                  }}
                  pageCount={searchDesc.value.totalPages}/>
                : undefined 
            }

            <Footer/>
        </PageWithSearchHeader>
        
        </UserTypeContext.Provider>
        </CartContext.Provider>
    )
}

function createSearchFilterUrl(filter: UserProductSearchFilter) {
    return `/myproducts${userProductSearchFilterToSearchParams(filter)}`
}

function useSearchFilterFromSearchParams(): [UserProductSearchFilter, React.Dispatch<React.SetStateAction<UserProductSearchFilter>>] {
    return useMappedSearchParams(params => {
        const status = params.getStringUnion("status", allProductStatuses) ?? 'active'
        const page = params.getInt("page", 1) ?? 1

        return { status, page }
    }, userProductSearchFilterToSearchParams)
}

type ProductGridWithHeaderProps = {
    products: DeferredDataState<ConciseProductInfo[]>,
    selectedIds: number[],
    onSelectionChanged: (ids: number[]) => void,
    onDelete: (ids: number[]) => void,
    onEdit: (productId: number) => void
}

function ProductGridWithHeader(props: ProductGridWithHeaderProps) {
    const strRes = useContext(StringResourcesContext)

    const [isSelectAllChecked, setSelectedAllChecked] = useState(false)
    
    function onSelectAllCheckedChanged(state: boolean) {
        const products = props.products

        if (products.type == 'success') {
            setSelectedAllChecked(state)
            props.onSelectionChanged(state ? products.value.map(p => p.id) : [])
        }
    }
    
    return (
        <>
            <div className="my-products-grid-header">
                <Checkbox
                  id="my-products-select-all"
                  label={strRes.selectAll}
                  checked={isSelectAllChecked}
                  onCheckedChanged={onSelectAllCheckedChanged}/>

                <button onClick={() =>props.onDelete(props.selectedIds)} id="my-products-delete-button">
                    {strRes.delete}
                </button>
            </div>

            <DeferredDataContainer state={props.products}>
                <ProductGrid
                  products={props.products.value ?? []}
                  selectedIds={props.selectedIds}
                  onSelectionChanged={props.onSelectionChanged}
                  onEdit={props.onEdit}/>
            </DeferredDataContainer>
        </>
    )
}

type ProductGridProps = {
    products: ConciseProductInfo[],
    selectedIds: number[],
    onSelectionChanged: (ids: number[]) => void,
    onEdit: (productId: number) => void
}

function ProductGrid(props: ProductGridProps) {
    function onSelectionChanged(id: number, isSelected: boolean) {
        props.onSelectionChanged(
            isSelected ? 
            pushElementAndCopy(props.selectedIds, id) : 
            removeElementAndCopy(props.selectedIds, i => i == id)
        )
    }

    return (
        <div className="my-products-grid">
            {props.products.map(product => 
                <ProductGridItem
                  product={product}
                  isSelected={props.selectedIds.includes(product.id)}
                  onSelectionChanged={isSelected => onSelectionChanged(product.id, isSelected)}
                  onEdit={() => props.onEdit(product.id)}/>
            )}
        </div>
    )
}

type ProductGridItemProps = {
    product: ConciseProductInfo,
    isSelected: boolean,
    onSelectionChanged: (isSelected: boolean) => void
    onEdit: () => void,
}

function ProductGridItem(props: ProductGridItemProps) {
    const product = props.product
    const strRes = useContext(StringResourcesContext)
    
    return (
        <div className="my-products-grid-item">
            <Link to={`/product/${product.id}`}>
                <img src={product.imageSource}/>
            </Link>

            <p className="my-products-grid-item-title">{product.title}</p>
            <p className="my-products-grid-item-price">{formatPriceToString(product.price)}</p>
            <button className="my-products-grid-item-edit">{strRes.edit}</button>
            <input type="checkbox" checked={props.isSelected} onChange={e => props.onSelectionChanged(e.target.checked)}/>
        </div>
    )
}