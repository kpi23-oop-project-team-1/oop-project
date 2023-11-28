// Common

import { isValidNumber } from "./utils/dataValidation"
import { UrlSearchParamsBuilder } from "./utils/urlUtils"
import { ConciseUserInfo } from './user';

export type NumberRange = { start: number, end: number }

export function numberRangeToString(range: NumberRange): string {
    return `${range.start}-${range.end}`
}

export function parseNumberRange(text: string): NumberRange | undefined {
    const dashIndex = text.indexOf("-")
    if (dashIndex < 0) {
        return undefined
    }

    const startStr = text.substring(0, dashIndex)
    const endStr = text.substring(dashIndex + 1)
    if (!isValidNumber(startStr) || !isValidNumber(endStr)) {
        return undefined
    }

    return { start: parseInt(startStr), end: parseInt(endStr) }
}

export const allProductStatuses = [
    'active',
    'on-moderation',
    'declined',
    'sold',
] as const

export type ProductStatus = (typeof allProductStatuses)[number]

export const allColorIds = [
    'black', 
    'white', 
    'red',
    'cyan',
    'yellow',
    'multicolor'
] as const

export type ColorId = (typeof allColorIds)[number] 

export const allProductStates = [
    'new', 
    'ideal',
    'very-good',
    'good',
    'acceptable'
] as const

export type ProductState = (typeof allProductStates)[number]

export const allCategoryIds = [
    'dress',
    'electronics',
    'home',
    'sport'
] as const

export type CategoryId = (typeof allCategoryIds)[number]

export const allSearchOrders = [
    'recomended',
    'cheapest',
    'most-expensive'
] as const

export type SearchOrder = (typeof allSearchOrders)[number]

export type SearchFilter = {
    page?: number,
    query?: string,
    category?: CategoryId,
    order?: SearchOrder,
    priceRange?: NumberRange,
    colorIds?: ColorId[],
    states?: ProductState[],
}

export function searchFilterToSearchParams(filter: SearchFilter | undefined): string {
    if (!filter) {
        return ""
    }

    var builder = new UrlSearchParamsBuilder()

    if (filter.page && filter.page > 1) {
        builder.appendPrimitive('page', filter.page)
    }

    if (filter.query) {
        builder.appendString('query', filter.query)
    }

    if (filter.order) {
        builder.appendString('order', filter.order)
    }

    if (filter.category) {
        builder.appendString('category', filter.category)
    }

    if (filter.priceRange) {
        builder.appendRange('price', filter.priceRange)
    }

    if (filter.colorIds) {
        builder.appendStringArray('colors', filter.colorIds)
    }

    if (filter.states) {
        builder.appendStringArray('states', filter.states)
    }

    return builder.result()
} 

export type SearchFilterDesc = {
    limitingPriceRange: NumberRange,
    availColorIds: ColorId[],
    availStates: ProductState[]
}

// Sign up

export type SignUpInfo = {
    firstName: string,
    lastName: string,
    telNumber: string
    email: string,
    password: string,
}
 
// Product

export type ConciseProductInfo = {
    id: number,
    title: string,
    imageSource: string,
    stripeText?: string,
    price: number,
    totalAmount: number
}

export type SearchConciseProductsResult = {
    totalProductCount: number,
    pageCount: number,
    products: ConciseProductInfo[]
}

export type ProductComment = {
    user: ConciseUserInfo,
    rating: number,
    text: string,
    dateString: string
}

export type ProductInfo = {
    id: number,
    title: string,
    imageSources: string[],
    stripeText?: string,
    price: number,
    totalAmount: number,
    description: string,
    comments: ProductComment[],
    category: CategoryId,
    state: ProductState,
    color: ColorId,
}

export type NewProductInfo = {
    title: string,
    images: File[]
    price: number,
    totalAmount: number,
    description: string,
    category: CategoryId,
    state: ProductState,
    color: ColorId,
}

export type UserProductSearchFilter = {
    status: ProductStatus,
    page: number
}

export type UserProductSearchDesc = {
    totalPages: number
}

export function userProductSearchFilterToSearchParams(filter: UserProductSearchFilter | undefined): string {
    if (!filter) {
        return ""
    }

    const builder = new UrlSearchParamsBuilder()
    builder.appendString("status", filter.status)

    if (filter.page != 1) {
        builder.appendPrimitive("page", filter.page)
    }
    
    return builder.result()
}