import "../styles/ProductsPage.scss"
import { useContext, useState } from "react"
import { StringResourcesContext } from "../StringResourcesContext"
import { CartContext, useCart } from "../cart"
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader"
import SearchFilterPanel from "../components/SearchFilterPanel"
import { ConciseProductInfo, SearchFilter, SearchOrder, allCategoryIds, allColorIds, allProductStates, allSearchOrders, parseNumberRange, searchFilterToSearchParams } from "../dataModels"
import ProductImageWithStripe from "../components/ProductImageWithStripe"
import { formatPriceToString } from "../utils/stringFormatting"
import { useValueFromDataSource } from "../dataSource.react"
import DeferredDataContainer from "../components/DeferredDataContainer"
import { Dropdown } from "../components/Dropdown"
import PageNavRow from "../components/PageNavRow"
import Footer from "../components/Footer"
import { Link } from "react-router-dom"
import { UserTypeContext, useCurrentUserType } from "../user.react"
import { useMappedSearchParams } from "../utils/urlUtils.react"
import { GlobalSearchQueryContext } from "../globalSearchQueryContext"

export default function ProductsPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const cartAndManager = useCart()
    const userType = useCurrentUserType()
    
    const [commitedFilter, setCommitedFilter] = useSearchFilterFromSearchParams()
    const [filter, setFilter] = useState<SearchFilter>(commitedFilter)

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
        <GlobalSearchQueryContext.Provider value={commitedFilter.query ?? undefined}>

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

        </GlobalSearchQueryContext.Provider>
        </CartContext.Provider>
        </UserTypeContext.Provider>
    )
}

function createSearchFilterUrl(filter: SearchFilter) {
    return `/products${searchFilterToSearchParams(filter)}`
}

function useSearchFilterFromSearchParams(): [SearchFilter, React.Dispatch<React.SetStateAction<SearchFilter>>] {
    return useMappedSearchParams<SearchFilter>(params => {
        const query = params.get("query")
        const page = params.getInt("page", 0)
        const category = params.getStringUnion("category", allCategoryIds)
        const order = params.getStringUnion("order", allSearchOrders)
        const priceRange = params.getAndMap("price", parseNumberRange)
        const colorIds = params.getStringUnionList("colors", allColorIds)
        const states = params.getStringUnionList("states", allProductStates)

        return { query, category, order, priceRange, colorIds, states, page }
    }, searchFilterToSearchParams)
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
              selectedValueId={props.searchOrder}
              allIds={allSearchOrders}
              labelMap={strRes.searchOrderLabels}
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