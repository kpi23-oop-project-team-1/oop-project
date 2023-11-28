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
    UserProductSearchFilter,
    userProductSearchFilterToSearchParams,
    ProductStatus,
    UserProductSearchDesc, 
} from "./dataModels";
import { UserCreditials, UserType } from "./user";
import { httpFetchAsync, httpFetchRawAsync } from "./utils/ajaxHttp";

export interface DataSource {
    authenticateAsync(creds: UserCreditials): Promise<undefined>
    signUpAsync(info: SignUpInfo): Promise<undefined>

    getSpecialProductsAsync(): Promise<ConciseProductInfo[]>

    getCartProductsAsync(creds: UserCreditials): Promise<CartProductInfo[]>
    addProductToCartAsync(id: number, creds: UserCreditials): Promise<undefined>
    updateCartProductQuantityAsync(productId: number, newAmount: number, creds: UserCreditials): Promise<undefined>
    removeProductFromCartAsync(productId: number, creds: UserCreditials): Promise<undefined>

    getSearchFilterDescAsync(categoryId: CategoryId | undefined): Promise<SearchFilterDesc>
    getConciseProductsBySearch(filter: SearchFilter | undefined): Promise<SearchConciseProductsResult>
    getProductInfo(id: number): Promise<ProductInfo>

    getUserType(creds: UserCreditials): Promise<UserType>

    addProduct(info: NewProductInfo, creds: UserCreditials): Promise<undefined>

    getUserProductsSearchDesc(status: ProductStatus, creds: UserCreditials): Promise<UserProductSearchDesc>
    getUserProducts(filter: UserProductSearchFilter, creds: UserCreditials): Promise<ConciseProductInfo[]>
    deleteProducts(ids: number[], creds: UserCreditials): Promise<undefined>
}

export class ServerDataSource implements DataSource {
    private createUrl(query: string): string {
        return `/api/${query}`
    }

    async authenticateAsync(creds: UserCreditials): Promise<undefined> {
        await httpFetchRawAsync({
            method: "GET",
            creditials: creds,
            url: this.createUrl(`auth`)
        })
        return undefined
    }

    async signUpAsync(info: SignUpInfo): Promise<undefined> {
        const formData = new FormData()
        formData.set("firstName", info.firstName)
        formData.set("lastName", info.lastName)
        formData.set("telNumber", info.telNumber)
        formData.set("email", info.email)
        formData.set("password", info.password)

        await httpFetchRawAsync({
            method: "POST",
            url: this.createUrl('signup'),
            body: formData
        })
        return undefined
    }

    getSpecialProductsAsync(): Promise<ConciseProductInfo[]> {
        return httpFetchAsync<ConciseProductInfo[]>({
            method: "GET",
            url: this.createUrl("specialproducts")
        })
    }

    getCartProductsAsync(creds: UserCreditials): Promise<CartProductInfo[]> {
        return httpFetchAsync<CartProductInfo[]>({
            method: "GET",
            creditials: creds,
            url: this.createUrl("cartproducts")
        })
    }

    async addProductToCartAsync(id: number, creds: UserCreditials): Promise<undefined> {
        await httpFetchRawAsync({
            method: "POST",
            creditials: creds,
            url: this.createUrl(`addcartproduct?id=${id}`)
        })
        return undefined
    }

    async updateCartProductQuantityAsync(productId: number, newAmount: number, creds: UserCreditials): Promise<undefined> {
        await httpFetchRawAsync({
            method: "POST",
            creditials: creds,
            url: this.createUrl(`cartproductamount?id=${productId}&amount=${newAmount}`)
        })
        return undefined
    }

    async removeProductFromCartAsync(productId: number, creds: UserCreditials): Promise<undefined> {
        await httpFetchRawAsync({
            method: "DELETE",
            creditials: creds,
            url: this.createUrl(`cartproduct?id=${productId}`)
        })
        return undefined
    }

    getSearchFilterDescAsync(categoryId: CategoryId | undefined): Promise<SearchFilterDesc> {
        return httpFetchAsync<SearchFilterDesc>({
            method: "GET",
            url: this.createUrl(`searchfilterdesc` + (categoryId ? `?category=${categoryId}` : ""))
        })    
    }

    getConciseProductsBySearch(filter: SearchFilter | undefined): Promise<SearchConciseProductsResult> {
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

    getUserType(creds: UserCreditials): Promise<UserType> {
        return httpFetchAsync<UserType>({
            method: "GET",
            creditials: creds,
            url: this.createUrl("usertype")
        })    
    }

    async addProduct(info: NewProductInfo, creds: UserCreditials): Promise<undefined> {
        const formData = new FormData()
        formData.set('title', info.title)
        formData.set('description', info.description)
        formData.set('price', info.price.toString())
        formData.set('amount', info.totalAmount.toString())
        formData.set('category', info.category)
        formData.set('state', info.state)
        formData.set('color', info.color)
        
        for (const file of info.images) {
            formData.append('image', file)
        }

        await httpFetchRawAsync({
            url: this.createUrl("addproduct"),
            method: "POST",
            creditials: creds,
            body: formData
        })

        return undefined
    }

    getUserProductsSearchDesc(status: ProductStatus, creds: UserCreditials): Promise<UserProductSearchDesc> {
        return httpFetchAsync<UserProductSearchDesc>({
            method: "GET",
            creditials: creds,
            url: this.createUrl(`userproductssearchdesc?status=${status}`)
        })
    }

    getUserProducts(filter: UserProductSearchFilter, creds: UserCreditials): Promise<ConciseProductInfo[]> {
        return httpFetchAsync<ConciseProductInfo[]>({
            method: "GET",
            url: this.createUrl("userproducts") + userProductSearchFilterToSearchParams(filter),
            creditials: creds
        })
    }

    async deleteProducts(ids: number[], creds: UserCreditials): Promise<undefined> {
        await httpFetchRawAsync({
            method: "DELETE",
            url: this.createUrl("product"),
            creditials: creds,
            body: JSON.stringify(ids)
        })    
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
                    user: {
                        id: 1,
                        displayName: "Display name"
                    },
                    rating: 4,
                    text: "Comment text",
                    dateString: "2023-11-18"
                },
                {
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

    async getUserType(creds: UserCreditials): Promise<UserType> {
        return 'customer-trader' 
    }

    async addProduct(): Promise<undefined> {
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

    async deleteProducts(ids: number[], creds: UserCreditials): Promise<undefined> {
        return undefined
    }
}

export class TemporaryDataSource implements DataSource {
    server = new ServerDataSource()
    test = new TestDataSource()

    authenticateAsync(creds: UserCreditials): Promise<undefined> {
        return this.server.authenticateAsync(creds)
    }
    signUpAsync(info: SignUpInfo): Promise<undefined> {
        return this.server.signUpAsync(info)
    }
    getUserType(creds: UserCreditials): Promise<UserType> {
        return this.server.getUserType(creds)
    }

    getSpecialProductsAsync(): Promise<ConciseProductInfo[]> {
        return this.test.getSpecialProductsAsync()
    }
    getCartProductsAsync(creds: UserCreditials): Promise<CartProductInfo[]> {
        return this.test.getCartProductsAsync()
    }
    addProductToCartAsync(id: number, creds: UserCreditials): Promise<undefined> {
        return this.test.addProductToCartAsync()
    }
    updateCartProductQuantityAsync(productId: number, newAmount: number, creds: UserCreditials): Promise<undefined> {
        return this.test.updateCartProductQuantityAsync(productId, newAmount)
    }
    removeProductFromCartAsync(productId: number, creds: UserCreditials): Promise<undefined> {
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
    
    addProduct(info: NewProductInfo, creds: UserCreditials): Promise<undefined> {
        return this.test.addProduct()
    }
    getUserProductsSearchDesc(status: ProductStatus, creds: UserCreditials): Promise<UserProductSearchDesc> {
        return this.test.getUserProductsSearchDesc()
    }
    getUserProducts(filter: UserProductSearchFilter, creds: UserCreditials): Promise<ConciseProductInfo[]> {
        return this.test.getUserProducts(filter)
    }
    deleteProducts(ids: number[], creds: UserCreditials): Promise<undefined> {
        return this.test.deleteProducts(ids, creds)
    }
    
}