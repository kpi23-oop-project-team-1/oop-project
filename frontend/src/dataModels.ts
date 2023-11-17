// Common

import { isValidNumber } from "./utils/dataValidation"
import { UrlSearchParamsBuilder } from "./utils/urlUtils"

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

export const allColorIds = [
    'black', 
    'white', 
    'red',
    'cyan',
    'yellow',
    'multicolor'
] as const

export type ColorId = (typeof allColorIds)[number] 

export const allProductStatuses = [
    'new', 
    'ideal',
    'very-good',
    'good',
    'acceptable'
] as const

export type ProductStatus = (typeof allProductStatuses)[number]

export const allCategoryIds = [
    'dress'
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
    statuses?: ProductStatus[],
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

    if (filter.statuses) {
        builder.appendStringArray('statuses', filter.statuses)
    }

    return builder.result()
} 

export type SearchFilterDesc = {
    limitingPriceRange: NumberRange,
    availColorIds: ColorId[],
    availStatuses: ProductStatus[]
}

export type AuthSuccessResult = {
    type: 'success',
    accessKey: string
}

export type FailStatusResult = { type: 'fail', reason?: string }
export type SuccessVoidStatusResult = { type: 'success' }
export type SuccessStatusValueResult<T> = SuccessVoidStatusResult & { value: T }

export type StatusValueResult<T> = SuccessStatusValueResult<T> | FailStatusResult
export type StatusVoidResult = SuccessVoidStatusResult | FailStatusResult

// Auth

export type AuthResult = { 
    accessKey: string
}

export type AuthStatusResult = StatusValueResult<AuthResult>

export type AuthCreditials = { 
    email: string,
    password: string
}

// Sign up

export type SignUpInfo = {
    firstName: string,
    lastName: string,
    telNumber: string
    email: string,
    password: string,
}

export type SignUpStatusResult = StatusVoidResult
 
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
