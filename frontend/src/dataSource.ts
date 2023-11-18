import { CartProductInfo } from "./cart";
import { AuthCreditials, AuthStatusResult, CategoryId, ConciseProductInfo, SearchConciseProductsResult as SearchProductsResult, SearchFilter, SearchFilterDesc, SignUpInfo, SignUpStatusResult, StatusVoidResult, searchFilterToSearchParams, ProductInfo } from "./dataModels";
import { httpFetchAsync } from "./utils/ajaxHttp";

export interface DataSource {
    authenticateAsync(creds: AuthCreditials): Promise<AuthStatusResult>
    signUpAsync(info: SignUpInfo): Promise<SignUpStatusResult>

    getSpecialProductsAsync(): Promise<ConciseProductInfo[]>

    getCartProductsAsync(): Promise<CartProductInfo[]>
    addProductToCartAsync(id: number): Promise<undefined>
    updateCartProductQuantityAsync(productId: number, newAmount: number): Promise<undefined>
    removeProductFromCartAsync(productId: number): Promise<undefined>

    getSearchFilterDescAsync(categoryId: CategoryId | undefined): Promise<SearchFilterDesc>
    getConciseProductsBySearch(filter: SearchFilter | undefined): Promise<SearchProductsResult>
    getProductInfo(id: number): Promise<ProductInfo>
}

export class ServerDataSource implements DataSource {
    readonly serverUrl: string

    constructor(serverUrl: string) {
        this.serverUrl = serverUrl
    }

    private createUrl(query: string): string {
        return `${this.serverUrl}/${query}`
    }

    async authenticateAsync(creds: AuthCreditials): Promise<AuthStatusResult> {
        const response = await httpFetchAsync<any>({
            method: "POST",
            url: this.createUrl(`api/auth?email=${creds.email}&password=${creds.password}`)
        })

        return response as AuthStatusResult
    }

    async signUpAsync(info: SignUpInfo): Promise<SignUpStatusResult> {
        const response = await httpFetchAsync<any>({
            method: "POST",
            url: this.createUrl(`api/signUp?firstName=${info.firstName}`)
        })

        return response as SignUpStatusResult
    }

    async getSpecialProductsAsync(): Promise<ConciseProductInfo[]> {
        return await httpFetchAsync<ConciseProductInfo[]>({
            method: "GET",
            url: this.createUrl("api/specialproducts")
        })
    }

    async getCartProductsAsync(): Promise<CartProductInfo[]> {
        return await httpFetchAsync<CartProductInfo[]>({
            method: "GET",
            url: this.createUrl("api/cartproducts")
        })
    }

    async addProductToCartAsync(id: number): Promise<undefined> {
        return await httpFetchAsync<undefined>({
            method: "POST",
            url: this.createUrl(`api/addcartproduct?id=${id}`)
        })
    }

    async updateCartProductQuantityAsync(productId: number, newAmount: number): Promise<undefined> {
        return await httpFetchAsync<undefined>({
            method: "POST",
            url: this.createUrl(`api/cartproductamount?id=${productId}&amount=${newAmount}`)
        })
    }

    async removeProductFromCartAsync(productId: number): Promise<undefined> {
        return await httpFetchAsync<undefined>({
            method: "DELETE",
            url: this.createUrl(`api/cartproduct?id=${productId}`)
        })
    }

    getSearchFilterDescAsync(categoryId: CategoryId | undefined): Promise<SearchFilterDesc> {
        return httpFetchAsync<SearchFilterDesc>({
            method: "GET",
            url: this.createUrl(`api/searchfilterdesc` + (categoryId ? `?category=${categoryId}` : ""))
        })    
    }

    getConciseProductsBySearch(filter: SearchFilter | undefined): Promise<SearchProductsResult> {
        return httpFetchAsync<SearchProductsResult>({
            method: "GET",
            url: this.createUrl("api/products") + searchFilterToSearchParams(filter),  
        })
    }

    getProductInfo(id: number): Promise<ProductInfo> {
        return httpFetchAsync<ProductInfo>({
            method: "GET",
            url: this.createUrl(`api/product?id=${id}`)
        })
    }
}

export class TestDataSource implements DataSource {
    private readonly cart: CartProductInfo[] = [
        { id: 1, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 1", quantity: 1, totalAmount: 3 },
        { id: 2, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 2", quantity: 1, totalAmount: 5 }
    ]

    async authenticateAsync(creds: AuthCreditials): Promise<AuthStatusResult> {
        return { type: "success", value: { accessKey: "" } };
    }

    async signUpAsync(info: SignUpInfo): Promise<StatusVoidResult> {
        return { type: "success" };
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

    async addProductToCartAsync(id: number): Promise<undefined> {
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
            availStatuses: ['acceptable', 'good', 'ideal', 'new', 'very-good'],
            limitingPriceRange: { start: 100, end: 1000 }
        }
    }

    async getConciseProductsBySearch(filter: SearchFilter | undefined): Promise<SearchProductsResult> {
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
            status: 'good',
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

}