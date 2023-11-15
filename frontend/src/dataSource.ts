import { CartProductInfo } from "./cart";
import { AuthCreditials, AuthStatusResult, ConciseProductInfo, SignUpInfo, SignUpStatusResult, StatusVoidResult } from "./dataModels";
import { httpFetchAsync } from "./utils/ajaxHttp";

export interface DataSource {
    authenticateAsync(creds: AuthCreditials): Promise<AuthStatusResult>
    signUpAsync(info: SignUpInfo): Promise<SignUpStatusResult>

    getSpecialProductsAsync(): Promise<ConciseProductInfo[]>

    getCartProductsAsync(): Promise<CartProductInfo[]>
    addProductToCartAsync(id: number): Promise<undefined>
    updateCartProductQuantityAsync(productId: number, newAmount: number): Promise<undefined>
    removeProductFromCartAsync(productId: number): Promise<undefined>
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
            url: this.createUrl(`auth?email=${creds.email}&password=${creds.password}`)
        })

        return response as AuthStatusResult
    }

    async signUpAsync(info: SignUpInfo): Promise<SignUpStatusResult> {
        const response = await httpFetchAsync<any>({
            method: "POST",
            url: this.createUrl(`signUp?firstName=${info.firstName}`)
        })

        return response as SignUpStatusResult
    }

    async getSpecialProductsAsync(): Promise<ConciseProductInfo[]> {
        return await httpFetchAsync<ConciseProductInfo[]>({
            method: "GET",
            url: this.createUrl("specialproducts")
        })
    }

    async getCartProductsAsync(): Promise<CartProductInfo[]> {
        return await httpFetchAsync<CartProductInfo[]>({
            method: "GET",
            url: this.createUrl("cartproducts")
        })
    }

    async addProductToCartAsync(id: number): Promise<undefined> {
        return await httpFetchAsync<undefined>({
            method: "POST",
            url: this.createUrl(`addcartproduct?id=${id}`)
        })
    }

    async updateCartProductQuantityAsync(productId: number, newAmount: number): Promise<undefined> {
        return await httpFetchAsync<undefined>({
            method: "POST",
            url: this.createUrl(`cartproductamount?id=${productId}&amount=${newAmount}`)
        })
    }

    async removeProductFromCartAsync(productId: number): Promise<undefined> {
        return await httpFetchAsync<undefined>({
            method: "DELETE",
            url: this.createUrl(`cartproduct?id=${productId}`)
        })
    }
}

export class TestDataSource implements DataSource {
    private readonly cart: CartProductInfo[] = [
        { id: 0, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 1", quantity: 1, totalAmount: 3 },
        { id: 1, imageSource: "/images/test_product_image.png", price: 1000, title: "Product 2", quantity: 1, totalAmount: 5 }
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

}