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
import { HttpFetchInfo, httpFetchAsync, httpFetchRawAsync } from "./utils/ajaxHttp";

export interface DataSource {
    authenticateAsync(creds: UserCredentials): Promise<undefined>
    signUpAsync(info: SignUpInfo): Promise<undefined>

    getSpecialProductsAsync(): Promise<ConciseProductInfo[]>

    getCartProductsAsync(creds: UserCredentials): Promise<CartProductInfo[]>
    addProductToCartAsync(id: number, creds: UserCredentials): Promise<undefined>
    updateCartProductQuantityAsync(productId: number, newAmount: number, creds: UserCredentials): Promise<undefined>
    removeProductFromCartAsync(productId: number, creds: UserCredentials): Promise<undefined>

    getSearchFilterDescAsync(categoryId: CategoryId | undefined): Promise<SearchFilterDesc>
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

    getSpecialProductsAsync(): Promise<ConciseProductInfo[]> {
        return httpFetchAsync<ConciseProductInfo[]>({
            method: "GET",
            url: this.createUrl("specialproducts")
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

    getSearchFilterDescAsync(categoryId: CategoryId | undefined): Promise<SearchFilterDesc> {
        return httpFetchAsync<SearchFilterDesc>({
            method: "GET",
            url: this.createUrl(`searchfilterdesc` + (categoryId ? `?category=${categoryId}` : ""))
        })    
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
            url: this.createUrl("product"),
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
        formData.set('email', info.email)
        formData.set('password', info.password)
        formData.set('username', info.username)
        formData.set('aboutMe', info.aboutMe)
        formData.set('firstName', info.firstName)
        formData.set('lastName', info.lastName)
        formData.set('telNumber', info.telNumber)
        formData.set('address', info.address)
        formData.set('pfpFile', info.pfpFile)

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

export class TestDataSource implements DataSource {
    private readonly cart: CartProductInfo[] = [
        { id: 1, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 1", quantity: 1, totalAmount: 3 },
        { id: 2, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 2", quantity: 1, totalAmount: 5 }
    ]

    async authenticateAsync(): Promise<undefined> {
        return undefined
    }

    async signUpAsync(): Promise<undefined> {
        return undefined
    }
    
    async getSpecialProductsAsync(): Promise<ConciseProductInfo[]> {
        await new Promise(r => setTimeout(r, 500));

        return [
            { id: 0, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 1", stripeText: "Stripe", totalAmount: 5 },
            { id: 1, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 2", stripeText: "Stripe", totalAmount: 5 },
            { id: 2, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 3", stripeText: "Stripe", totalAmount: 5 },
            { id: 3, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 4", stripeText: "Stripe", totalAmount: 5 },
            { id: 4, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 5", stripeText: "Stripe", totalAmount: 5 },
            { id: 5, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 6", stripeText: "Stripe", totalAmount: 5 },
            { id: 6, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 7", stripeText: "Stripe", totalAmount: 5 },
            { id: 7, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 8", stripeText: "Stripe", totalAmount: 5 },
            { id: 8, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 9", stripeText: "Stripe", totalAmount: 5 },
            { id: 9, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 10", stripeText: "Stripe", totalAmount: 5 }
        ]
    }

    async getCartProductsAsync(): Promise<CartProductInfo[]> {
        return this.cart
    }

    async addProductToCartAsync(): Promise<undefined> {
        return undefined
    }

    async updateCartProductQuantityAsync(productId: number, newAmount: number): Promise<undefined> {
        const index = this.cart.findIndex(p => p.id == productId)
        if (index > 0) {
            this.cart[index].quantity = newAmount
        }
        return undefined
    }

    async removeProductFromCartAsync(productId: number): Promise<undefined> {
        const index = this.cart.findIndex(p => p.id == productId)
        if (index > 0) {
            this.cart.splice(index, 1)
        }
        return undefined
    }

    async getSearchFilterDescAsync(categoryId: CategoryId | undefined): Promise<SearchFilterDesc> {
        await new Promise(r => setTimeout(r, 500));

        return {
            availColorIds: ['black', 'white', 'cyan'],
            availStates: ['acceptable', 'good', 'ideal', 'new', 'very-good'],
            limitingPriceRange: { start: 100, end: 1000 }
        }
    }

    async getConciseProductsBySearch(filter: SearchFilter | undefined): Promise<SearchConciseProductsResult> {
        await new Promise(r => setTimeout(r, 1000));

        return {
            totalProductCount: 100,
            pageCount: 10,
            products: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map(i => i + ((filter?.page ?? 1) - 1) * 10).map(id => ({
                id: id,
                imageSource: "./images/test_product_image.png",
                price: id * 100,
                title: "Product " + id,
                totalAmount: id * 2
            })),
        }
    }

    async getProductInfo(id: number): Promise<ProductInfo> {
        await new Promise(r => setTimeout(r, 1000));

        return {
            id,
            title: "Product " + id,
            category: "dress",
            color: 'black',
            imageSources: ["/images/placeholder-1.png", "/images/placeholder-2.png", "/images/placeholder-3.png"],
            price: 100,
            state: 'good',
            totalAmount: 5,
            description: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            comments: [
                {
                    id: 0,
                    user: {
                        id: 1,
                        displayName: "Display name"
                    },
                    rating: 4,
                    text: "Comment text",
                    dateString: "2023-11-18"
                },
                {
                    id: 1,
                    user: {
                        id: 2,
                        displayName: "Display name"
                    },
                    rating: 5,
                    text: "Comment text 2",
                    dateString: "2023-11-19"
                }
            ]
        }
    }

    async addProduct(): Promise<undefined> {
        return undefined
    }

    async updateProduct(): Promise<undefined> {
        return undefined
    }

    async getUserProductsSearchDesc(): Promise<UserProductSearchDesc> {
        return { totalPages: 10 }
    }

    async getUserProducts(filter: UserProductSearchFilter): Promise<ConciseProductInfo[]> {
        await new Promise(r => setTimeout(r, 1000));

        return [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map(i => i + ((filter?.page ?? 1) - 1) * 10).map(id => ({
            id,
            imageSource: "./images/test_product_image.png",
            price: id * 100,
            title: "Product " + id,
            totalAmount: id * 2
        }))
    }

    async deleteProducts(ids: number[], creds: UserCredentials): Promise<undefined> {
        return undefined
    }

    async getUserType(creds: UserCredentials): Promise<UserType> {
        return 'customer-trader'
    }

    async getUserId(email: String): Promise<number> {
        return 128
    }

    async getAccountInfo(id: number): Promise<AccountInfo> {
        await new Promise(r => setTimeout(r, 1000));

        return {
            id: id,
            email: "test@email.com",
            password: "",

            username: "Username",
            pfpSource: "/images/test_product_image.png",

            aboutMe: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",

            firstName: "Name",
            lastName: "Surname",
            telNumber: "1234567890",
            address: "Kyiv"
        }
    }

    async updateAccountInfo(): Promise<undefined> {
        return undefined
    }

    async postProductComment(info: NewCommentInfo, creds: UserCredentials): Promise<undefined> {
        return undefined
    }

    async postUserComment(info: NewCommentInfo, creds: UserCredentials): Promise<undefined> {
        return undefined
    }

    async getDetailedUserInfo(id: number): Promise<DetailedUserInfo> {
        return {
            pfpSource: "/images/test_product_image.png",
            description: "123",
            displayName: "Display name",
            comments: [
                {
                    id: 1,
                    user: {
                        id: 2,
                        displayName: "Display name"
                    },
                    rating: 5,
                    text: "Comment text 2",
                    dateString: "2023-11-19"
                }
            ]
        }
    }
}

export class TemporaryDataSource implements DataSource {
    server = new ServerDataSource()
    test = new TestDataSource()

    authenticateAsync(creds: UserCredentials): Promise<undefined> {
        return this.server.authenticateAsync(creds)
    }
    signUpAsync(info: SignUpInfo): Promise<undefined> {
        return this.server.signUpAsync(info)
    }

    getSpecialProductsAsync(): Promise<ConciseProductInfo[]> {
        return this.test.getSpecialProductsAsync()
    }
    getCartProductsAsync(creds: UserCredentials): Promise<CartProductInfo[]> {
        return this.test.getCartProductsAsync()
    }
    addProductToCartAsync(id: number, creds: UserCredentials): Promise<undefined> {
        return this.test.addProductToCartAsync()
    }
    updateCartProductQuantityAsync(productId: number, newAmount: number, creds: UserCredentials): Promise<undefined> {
        return this.test.updateCartProductQuantityAsync(productId, newAmount)
    }
    removeProductFromCartAsync(productId: number, creds: UserCredentials): Promise<undefined> {
        return this.test.removeProductFromCartAsync(productId)
    }
    getSearchFilterDescAsync(categoryId: CategoryId | undefined): Promise<SearchFilterDesc> {
        return this.test.getSearchFilterDescAsync(categoryId)
    }
    getConciseProductsBySearch(filter: SearchFilter | undefined): Promise<SearchConciseProductsResult> {
        return this.test.getConciseProductsBySearch(filter)
    }
    getProductInfo(id: number): Promise<ProductInfo> {
        return this.test.getProductInfo(id)
    }
    
    addProduct(info: NewProductInfo, creds: UserCredentials): Promise<undefined> {
        return this.test.addProduct()
    }
    updateProduct(info: UpdateProductInfo, creds: UserCredentials): Promise<undefined> {
        return this.test.updateProduct()
    }
    getUserProductsSearchDesc(status: ProductStatus, creds: UserCredentials): Promise<UserProductSearchDesc> {
        return this.test.getUserProductsSearchDesc()
    }
    getUserProducts(filter: UserProductSearchFilter, creds: UserCredentials): Promise<ConciseProductInfo[]> {
        return this.test.getUserProducts(filter)
    }
    deleteProducts(ids: number[], creds: UserCredentials): Promise<undefined> {
        return this.test.deleteProducts(ids, creds)
    }

    getUserType(creds: UserCredentials): Promise<UserType> {
        return this.server.getUserType(creds)
    }
    getUserId(email: String): Promise<number> {
        return this.test.getUserId(email)
    }
    getAccountInfo(id: number): Promise<AccountInfo> {
        return this.test.getAccountInfo(id)
    }
    updateAccountInfo(info: NewAccountInfo, creds: UserCredentials): Promise<undefined> {
        return this.test.updateAccountInfo()
    }

    postProductComment(info: NewCommentInfo, creds: UserCredentials): Promise<undefined> {
        return this.server.postProductComment(info, creds)
    }
    getDetailedUserInfo(id: number): Promise<DetailedUserInfo> {
        return this.server.getDetailedUserInfo(id)
    }
    postUserComment(info: NewCommentInfo, creds: UserCredentials): Promise<undefined> {
        return this.server.postProductComment(info, creds)
    }
}