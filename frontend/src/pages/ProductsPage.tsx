import "../styles/ProductsPage.scss"
import { useContext, useEffect, useState } from "react"
import { StringResourcesContext } from "../StringResourcesContext"
import { CartContext, useCart } from "../cart"
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader"
import SearchFilterPanel from "../components/SearchFilterPanel"
import { CategoryId, ColorId, ConciseProductInfo, ProductStatus, SearchFilter, SearchOrder, allCategoryIds, allColorIds, allProductStatuses, allSearchOrders, parseNumberRange, searchFilterToSearchParams } from "../dataModels"
import ProductImageWithStripe from "../components/ProductImageWithStripe"
import { formatPriceToString, splitToTypedStringArray } from "../utils/stringFormatting"
import { useValueFromDataSource } from "../dataSource.react"
import DeferredDataContainer from "../components/DeferredDataContainer"
import { Dropdown } from "../components/Dropdown"
import PageNavRow from "../components/PageNavRow"
import Footer from "../components/Footer"
import { isValidNumber } from "../utils/dataValidation"
import { Link } from "react-router-dom"
import { UserTypeContext, useUserType } from "../user.react"

export default function ProductsPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const cartAndManager = useCart()
    const userType = useUserType()

    const [filter, setFilter] = useState<SearchFilter>(extractSearchFilterFromSearchParams())
    const [commitedFilter, setCommitedFilter] = useState(filter)

    useEffect(() => {
        updateUrl(commitedFilter)
    }, [commitedFilter])

    const [filterDescState] = useValueFromDataSource(ds => ds.getSearchFilterDescAsync(filter.category))
    const [searchResultState] = useValueFromDataSource(ds => ds.getConciseProductsBySearch(commitedFilter), [commitedFilter])
    const searchResult = searchResultState.value

    const strRes = useContext(StringResourcesContext)

    function setFilterAndCommit(filter: SearchFilter) {
        setFilter(filter)
        setCommitedFilter(filter)
    }

    return (
        <UserTypeContext.Provider value={userType.value}>
        <CartContext.Provider value={cartAndManager}>
            <PageWithSearchHeader
              dialogType={dialogType}
              onChangeDialogType={setDialogType}>
                <div id="products-page-filter-and-grid">
                    <DeferredDataContainer state={filterDescState}>
                        <SearchFilterPanel 
                          filterDesc={filterDescState?.value} 
                          filter={filter} 
                          onChanged={setFilter}
                          urlFactory={createSearchFilterUrl}
                          commitFilter={setCommitedFilter}/>
                    </DeferredDataContainer>
                    <div id="products-page-right-side">
                        <h2>{filter.category ? strRes.productCategoryLabels[filter.category] : strRes.allProductsCategory}</h2>
                        <ProductsGridHeader
                          totalProductCount={searchResult?.totalProductCount}
                          searchOrder={filter.order ?? 'recomended'}
                          onSearchOrderChanged={order => setFilterAndCommit({ ...filter, order })}/>
                        <DeferredDataContainer state={searchResultState}>
                            <ProductsGrid products={searchResult?.products ?? []}/>
                        </DeferredDataContainer>
                        {searchResult && searchResult.pageCount > 1 &&
                            <PageNavRow 
                              pageCount={searchResult.pageCount} 
                              createLink={i => createSearchFilterUrl({ ...commitedFilter, page: i })}
                              onNavigateLink={i => setFilterAndCommit({ ...filter, page: i })}/>
                        }
                    </div>
                </div>
                <Footer/>
            </PageWithSearchHeader>
        </CartContext.Provider>
        </UserTypeContext.Provider>
    )
}

function updateUrl(filter: SearchFilter) {
    navigateToUrl(createSearchFilterUrl(filter))
}

function createSearchFilterUrl(filter: SearchFilter) {
    return `${window.location.protocol}//${window.location.host}/products${searchFilterToSearchParams(filter)}`
}

function navigateToUrl(url: string) {
    history.pushState({}, "", url)
}

function extractSearchFilterFromSearchParams(): SearchFilter {
    const params = new URLSearchParams(document.location.search)
    const query = params.get("query") ?? undefined

    const pageStr = params.get("page")
    let page: number | undefined = undefined
    if (pageStr != null && isValidNumber(pageStr)) {
        page = parseInt(pageStr)
    }

    let category = (params.get("category") ?? undefined) as CategoryId | undefined
    if (!allCategoryIds.includes(category as CategoryId)) {
        category = undefined
    }

    let order = (params.get("order") ?? undefined) as SearchOrder | undefined
    if (!allSearchOrders.includes(order as SearchOrder)) {
        order = undefined
    }

    const priceRangeStr = params.get("price") 
    const priceRange = priceRangeStr != null ? parseNumberRange(priceRangeStr) : undefined
    const colorIdsStr = params.get("colors")
    const statusesStr = params.get("statuses")

    const colorIds = colorIdsStr != null ? splitToTypedStringArray<ColorId>(colorIdsStr, ';', allColorIds) : undefined
    const statuses = statusesStr != null ? splitToTypedStringArray<ProductStatus>(statusesStr, ';', allProductStatuses) : undefined

    return { query, category, order, priceRange, colorIds, statuses, page }
}

type ProductsGridHeaderProps = {
    totalProductCount: number | undefined,
    searchOrder: SearchOrder,
    onSearchOrderChanged: (value: SearchOrder) => void
}

function ProductsGridHeader(props: ProductsGridHeaderProps) {
    const strRes = useContext(StringResourcesContext)

    return <div id="products-grid-header">
        <p id="products-grid-product-count">{`${strRes.productCountLabel}${props.totalProductCount ?? ""}`}</p>

        <div id="product-grid-order-block">
            <p>{strRes.orderBy}</p>
            <Dropdown 
              entries={allSearchOrders.map(id => ({ id, label: strRes.searchOrderLabels[id] }) )}
              selectedValueId={props.searchOrder}
              onSelected={props.onSearchOrderChanged}/>
        </div>
        
    </div>
}

type ProductsGridProps = {
    products: ConciseProductInfo[]
}

function ProductsGrid(props: ProductsGridProps) {
    return <div id="products-grid">
        {props.products.map(product => 
            <ProductView
              key={product.id}
              product={product}/>
        )}
    </div>
}

type ProductViewProps = {
    product: ConciseProductInfo
}

function ProductView(props: ProductViewProps) {
    const product = props.product

    return (
        <Link className="products-grid-view" to={`/product/${product.id}/`}>
            <ProductImageWithStripe
              imageSource={product.imageSource}
              stripeText={product.stripeText}/>

            <p className="products-grid-view-title">{product.title}</p>
            <p className="products-grid-view-price">{formatPriceToString(product.price)}</p>
        </Link>
    )
}