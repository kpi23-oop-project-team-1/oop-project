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
    'waiting-for-moderation',
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

export function searchFilterToSearchParams(filter: SearchFilter): string {
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
    limitingPriceRange: NumberRange | undefined,
    colorIds: ColorId[],
    states: ProductState[]
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
    imageSource: string
    price: number,
    totalAmount: number
}

export type SearchConciseProductsResult = {
    totalProductCount: number,
    pageCount: number,
    products: ConciseProductInfo[]
}

export const totalCommentStarCount = 5;

export type CommentInfo = {
    id: number,
    user: ConciseUserInfo,
    rating: number,
    text: string,
    dateString: string
}

export type NewCommentInfo = {
    targetId: number,
    rating: number,
    text: string
}

export type ProductInfo = {
    id: number,
    title: string,
    imageSources: string[],
    price: number,
    totalAmount: number,
    description: string,
    comments: CommentInfo[],
    category: CategoryId,
    state: ProductState,
    color: ColorId,
    status: ProductStatus
    trader: ConciseUserInfo,
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

export type UpdateProductInfo = { id: number } & Omit<NewProductInfo, 'images'> & { images: (File | undefined)[] }

// Account

export type AccountInfo = {
    id: number,
    email: string,

    username: string,
    pfpSource: string,

    aboutMe: string,

    firstName: string,
    lastName: string,
    telNumber: string
}

export type NewAccountInfo = {
    password: string,

    username: string,
    pfpFile: File | undefined,

    aboutMe: string,

    firstName: string,
    lastName: string,
    telNumber: string
}

// Search

export type UserProductSearchFilter = {
    status: ProductStatus,
    page: number
}

export type UserProductSearchDesc = {
    totalPages: number
}

export function userProductSearchFilterToSearchParams(filter: UserProductSearchFilter): string {
    const builder = new UrlSearchParamsBuilder()
    builder.appendString("status", filter.status)

    if (filter.page != 1) {
        builder.appendPrimitive("page", filter.page)
    }
    
    return builder.result()
}

export type DetailedUserInfo = {
    pfpSource: string,
    displayName: string,
    comments: CommentInfo[],
    description: string
}