import { CartProductInfo } from "./cart";
import { 
    CategoryId, 
    ConciseProductInfo,
    SearchConciseProductsResult,
    SearchFilter, 
    SearchFilterDesc, 
    SignUpInfo, 
    searchFilterToSearchParams, 
    ProductInfo,
    NewProductInfo,
    AccountInfo,
    NewAccountInfo,
    UserProductSearchFilter,
    userProductSearchFilterToSearchParams,
    ProductStatus,
    UserProductSearchDesc,
    NewCommentInfo,
    UpdateProductInfo,
    DetailedUserInfo,
} from "./dataModels";
import { UserCredentials, UserType } from "./user";
import { HttpFetchInfo, httpFetchAsync, httpFetchAsyncOr, httpFetchRawAsync } from "./utils/ajaxHttp";

export interface DataSource {
    authenticateAsync(creds: UserCredentials): Promise<undefined>
    signUpAsync(info: SignUpInfo): Promise<undefined>

    getCartProductsAsync(creds: UserCredentials): Promise<CartProductInfo[]>
    addProductToCartAsync(id: number, creds: UserCredentials): Promise<undefined>
    updateCartProductQuantityAsync(productId: number, newAmount: number, creds: UserCredentials): Promise<undefined>
    removeProductFromCartAsync(productId: number, creds: UserCredentials): Promise<undefined>

    getSearchFilterDescAsync(categoryId: CategoryId | undefined): Promise<SearchFilterDesc | undefined>
    getConciseProductsBySearch(filter: SearchFilter): Promise<SearchConciseProductsResult>
    getProductInfo(id: number): Promise<ProductInfo>

    addProduct(info: NewProductInfo, creds: UserCredentials): Promise<undefined>
    updateProduct(info: UpdateProductInfo, creds: UserCredentials): Promise<undefined>

    getUserProductsSearchDesc(status: ProductStatus, creds: UserCredentials): Promise<UserProductSearchDesc>
    getUserProducts(filter: UserProductSearchFilter, creds: UserCredentials): Promise<ConciseProductInfo[]>
    deleteProducts(ids: number[], creds: UserCredentials): Promise<undefined>

    getUserType(creds: UserCredentials): Promise<UserType>
    getUserId(email: String): Promise<number>
    getAccountInfo(id: number): Promise<AccountInfo>
    updateAccountInfo(info: NewAccountInfo, creds: UserCredentials): Promise<undefined>

    getDetailedUserInfo(id: number): Promise<DetailedUserInfo>

    postProductComment(info: NewCommentInfo, creds: UserCredentials): Promise<undefined>
    postUserComment(info: NewCommentInfo, creds: UserCredentials): Promise<undefined>
}

export class ServerDataSource implements DataSource {
    private createUrl(query: string): string {
        return `/api/${query}`
    }

    async authenticateAsync(creds: UserCredentials): Promise<undefined> {
        return this.noResponseFetch({
            method: "GET",
            credentials: creds,
            url: this.createUrl(`auth`)
        })
    }

    async signUpAsync(info: SignUpInfo): Promise<undefined> {
        const formData = new FormData()
        formData.set("firstName", info.firstName)
        formData.set("lastName", info.lastName)
        formData.set("telNumber", info.telNumber)
        formData.set("email", info.email)
        formData.set("password", info.password)

        return this.noResponseFetch({
            method: "POST",
            url: this.createUrl('signup'),
            body: formData
        })
    }

    getCartProductsAsync(creds: UserCredentials): Promise<CartProductInfo[]> {
        return httpFetchAsync<CartProductInfo[]>({
            method: "GET",
            credentials: creds,
            url: this.createUrl("cartproducts")
        })
    }

    addProductToCartAsync(id: number, creds: UserCredentials): Promise<undefined> {
        return this.noResponseFetch({
            method: "POST",
            credentials: creds,
            url: this.createUrl(`addcartproduct?id=${id}`)
        })
    }

    updateCartProductQuantityAsync(productId: number, newAmount: number, creds: UserCredentials): Promise<undefined> {
        return this.noResponseFetch({
            method: "POST",
            credentials: creds,
            url: this.createUrl(`cartproductamount?id=${productId}&amount=${newAmount}`)
        })
    }

    removeProductFromCartAsync(productId: number, creds: UserCredentials): Promise<undefined> {
        return this.noResponseFetch({
            method: "DELETE",
            credentials: creds,
            url: this.createUrl(`cartproduct?id=${productId}`)
        })
    }

    getSearchFilterDescAsync(categoryId: CategoryId | undefined): Promise<SearchFilterDesc | undefined> {
        return httpFetchAsyncOr<SearchFilterDesc | undefined>({
            method: "GET",
            url: this.createUrl(`searchfilterdesc` + (categoryId ? `?category=${categoryId}` : ""))
        }, undefined)
    }

    getConciseProductsBySearch(filter: SearchFilter): Promise<SearchConciseProductsResult> {
        return httpFetchAsync<SearchConciseProductsResult>({
            method: "GET",
            url: this.createUrl("products") + searchFilterToSearchParams(filter),  
        })
    }

    getProductInfo(id: number): Promise<ProductInfo> {
        return httpFetchAsync<ProductInfo>({
            method: "GET",
            url: this.createUrl(`product?id=${id}`)
        })
    }

    addProduct(info: NewProductInfo, creds: UserCredentials): Promise<undefined> {
        const formData = this.createFormDataFromNewProductInfo(info)
        for (const file of info.images) {
            formData.append('image', file)
        }

        return this.noResponseFetch({
            url: this.createUrl("addproduct"),
            method: "POST",
            credentials: creds,
            body: formData
        })
    }

    updateProduct(info: UpdateProductInfo, creds: UserCredentials): Promise<undefined> {
        const formData = this.createFormDataFromNewProductInfo(info)
        formData.set("id", info.id.toString())

        var blob = new Blob()
        for (const file of info.images) {
            if (file) {
                formData.append('image', file)
            } else {
                formData.append('image', blob)
            }
        }

        return this.noResponseFetch({
            url: this.createUrl("updateproduct"),
            method: "POST",
            credentials: creds,
            body: formData
        })
    }

    private createFormDataFromNewProductInfo(info: Omit<NewProductInfo, 'images'>): FormData {
        const formData = new FormData()
        formData.set('title', info.title)
        formData.set('description', info.description)
        formData.set('price', info.price.toString())
        formData.set('amount', info.totalAmount.toString())
        formData.set('category', info.category)
        formData.set('state', info.state)
        formData.set('color', info.color)

        return formData
    }

    getUserProductsSearchDesc(status: ProductStatus, creds: UserCredentials): Promise<UserProductSearchDesc> {
        return httpFetchAsync<UserProductSearchDesc>({
            method: "GET",
            credentials: creds,
            url: this.createUrl(`userproductssearchdesc?status=${status}`)
        })
    }

    getUserProducts(filter: UserProductSearchFilter, creds: UserCredentials): Promise<ConciseProductInfo[]> {
        return httpFetchAsync<ConciseProductInfo[]>({
            method: "GET",
            url: this.createUrl("userproducts") + userProductSearchFilterToSearchParams(filter),
            credentials: creds
        })
    }

    deleteProducts(ids: number[], creds: UserCredentials): Promise<undefined> {
        return this.noResponseFetch({
            method: "DELETE",
            url: this.createUrl("deleteproducts"),
            credentials: creds,
            body: JSON.stringify(ids)
        })
    }

    getUserType(creds: UserCredentials): Promise<UserType> {
        return httpFetchAsync<UserType>({
            method: "GET",
            credentials: creds,
            url: this.createUrl("usertype")
        })
    }

    getUserId(email: String): Promise<number> {
        return httpFetchAsync<number>({
            method: "GET",
            url: this.createUrl(`accountid?email=${email}`)
        })
    }

    getAccountInfo(id: number): Promise<AccountInfo> {
        return httpFetchAsync<AccountInfo>({
            method: "GET",
            url: this.createUrl(`accountinfo?id=${id}`)
        })
    }

    updateAccountInfo(info: NewAccountInfo, creds: UserCredentials): Promise<undefined> {
        const formData = new FormData()
     
        formData.set('username', info.username)
        formData.set('aboutMe', info.aboutMe)
        formData.set('firstName', info.firstName)
        formData.set('lastName', info.lastName)
        formData.set('telNumber', info.telNumber)

        if (info.password.length > 0) {
            formData.set('password', info.password)
        }

        if (info.pfpFile) {
            formData.set('pfpFile', info.pfpFile)
        }

        return this.noResponseFetch({
            url: this.createUrl("updateaccountinfo"),
            method: "POST",
            credentials: creds,
            body: formData
        })
    }

    postProductComment(info: NewCommentInfo, creds: UserCredentials): Promise<undefined> {
        return this.noResponseFetch({
            method: "POST",
            url: this.createUrl("postproductcomment"),
            body: this.commentInfoToFormData(info),
            credentials: creds
        })
    }

    postUserComment(info: NewCommentInfo, creds: UserCredentials): Promise<undefined> {
        return this.noResponseFetch({
            method: "POST",
            url: this.createUrl("postusercomment"),
            body: this.commentInfoToFormData(info),
            credentials: creds
        })
    }

    private commentInfoToFormData(info: NewCommentInfo) {
        const formData = new FormData()
        formData.set("targetId", info.targetId.toString())
        formData.set("rating", info.rating.toString())
        formData.set("text", info.text)

        return formData
    }

    getDetailedUserInfo(id: number): Promise<DetailedUserInfo> {
        return httpFetchAsync({
            method: "GET",
            url: this.createUrl(`detaileduserinfo?id=${id}`)
        })
    }

    private async noResponseFetch(info: HttpFetchInfo): Promise<undefined> {
        await httpFetchRawAsync(info)

        return undefined
    }
}