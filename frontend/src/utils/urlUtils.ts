import { isValidElement } from "react"
import { NumberRange, numberRangeToString } from "../dataModels"
import { splitToTypedStringArray } from "./stringFormatting"

export class UrlSearchParamsBuilder {
    private params: string = ""
  
    private appendKeyValue(key: string, value: string) {
        this.params += this.params.length == 0 ? '?' : '&' 
        this.params += key
        this.params += '='
        this.params += value
    }

    appendPrimitive(key: string, value: number | boolean) {
        this.appendKeyValue(key, value.toString())
    }

    appendString(key: string, value: string) {
        if (value.length > 0) {
            this.appendKeyValue(key, value)
        }
    }

    appendRange(key: string, value: NumberRange) {
        this.appendKeyValue(key, numberRangeToString(value))
    }

    appendStringArray(key: string, value: string[]) {
        if (value.length > 0) {
            this.appendKeyValue(key, value.join(';'))
        }
    }

    result(): string {
        return this.params
    }
}

export class TypedURLSearchParams {
    private params: URLSearchParams

    constructor(params: URLSearchParams) {
        this.params = params
    }
    
    get(name: string): string | undefined {
        return this.params.get(name) ?? undefined
    }

    getInt(name: string, min?: number): number | undefined {
        const raw = this.get(name)
        if (raw && isValidElement(raw)) {
            const num = parseInt(raw)
            if (min && num >= min) {
                return num
            }
        }

        return undefined
    }

    getStringUnion<T extends string>(name: string, allValues: readonly T[]): T | undefined {
        const raw = this.get(name)
        const order = raw as T

        return allValues.includes(order) ? order : undefined
    }

    getStringUnionList<T extends string>(name: string, allValues: readonly T[]): T[] | undefined {
        const raw = this.get(name)

        return raw ? splitToTypedStringArray(raw, ',', allValues) : undefined
    }

    getAndMap<T>(name: string, map: (value: string) => T | undefined): T | undefined {
        const raw = this.get(name)
        
        return raw ? map(raw) : undefined
    }
}

export function getCurrentSearchParams(): URLSearchParams {
    return new URLSearchParams(document.location.search)
}

export function getCurrentTypedSearchParams(): TypedURLSearchParams {
    return new TypedURLSearchParams(getCurrentSearchParams())
}