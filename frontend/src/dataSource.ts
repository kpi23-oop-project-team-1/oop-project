import { AuthCreditials, AuthStatusResult, CartProductInfo, ConciseProductInfo, SignUpInfo, SignUpStatusResult, StatusVoidResult } from "./dataModels";
import { httpFetchAsync } from "./utils/ajaxHttp";

export interface DataSource {
    authenticateAsync(creds: AuthCreditials): Promise<AuthStatusResult>
    signUpAsync(info: SignUpInfo): Promise<SignUpStatusResult>

    getSpecialProductsAsync(): Promise<ConciseProductInfo[]>

    getCartProducts(): Promise<CartProductInfo[]>
    addProductToCart(id: number): Promise<undefined>
    updateCartProductQuantity(productId: number, newAmount: number): Promise<undefined>
    removeProductFromCart(productId: number): Promise<undefined>
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

    async getCartProducts(): Promise<CartProductInfo[]> {
        return await httpFetchAsync<CartProductInfo[]>({
            method: "GET",
            url: this.createUrl("cartproducts")
        })
    }

    async addProductToCart(id: number): Promise<undefined> {
        return await httpFetchAsync<undefined>({
            method: "POST",
            url: this.createUrl(`addcartproduct?id=${id}`)
        })
    }

    async updateCartProductQuantity(productId: number, newAmount: number): Promise<undefined> {
        return await httpFetchAsync<undefined>({
            method: "POST",
            url: this.createUrl(`cartproductamount?id=${productId}&amount=${newAmount}`)
        })
    }

    async removeProductFromCart(productId: number): Promise<undefined> {
        return await httpFetchAsync<undefined>({
            method: "DELETE",
            url: this.createUrl(`cartproduct?id=${productId}`)
        })
    }
}

export class TestDataSource implements DataSource {
    private readonly cart: CartProductInfo[] = [
        { id: 0, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", quantity: 1, totalAmount: 3 },
        { id: 1, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", quantity: 1, totalAmount: 5 }
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
            { id: 0, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", stripeText: "Stripe" },
            { id: 1, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", stripeText: "Stripe" },
            { id: 2, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", stripeText: "Stripe" },
            { id: 3, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", stripeText: "Stripe" },
            { id: 4, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", stripeText: "Stripe" },
            { id: 5, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", stripeText: "Stripe" },
            { id: 6, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", stripeText: "Stripe" },
            { id: 7, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", stripeText: "Stripe" },
            { id: 8, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", stripeText: "Stripe" },
            { id: 9, imageSource: "/images/test_product_image.png", price: 1000, title: "Product", stripeText: "Stripe" }
        ]
    }

    async getCartProducts(): Promise<CartProductInfo[]> {
        return this.cart
    }

    async addProductToCart(id: number): Promise<undefined> {
        return undefined
    }

    async updateCartProductQuantity(productId: number, newAmount: number): Promise<undefined> {
        const index = this.cart.findIndex(p => p.id == productId)
        if (index > 0) {
            this.cart[index].quantity = newAmount
        }
        return undefined
    }

    async removeProductFromCart(productId: number): Promise<undefined> {
        const index = this.cart.findIndex(p => p.id == productId)
        if (index > 0) {
            this.cart.splice(index, 1)
        }
        return undefined
    }

}