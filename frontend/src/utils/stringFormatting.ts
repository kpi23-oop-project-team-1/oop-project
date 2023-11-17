export function formatPriceToString(price: number): string {
    return `${Math.floor(price)}₴`
}

export function splitToTypedStringArray<T extends string>(text: string, delimiter: string, keys: readonly string[]): T[] {
    const raw = text.split(delimiter)
    const result: T[] = []

    for (const element of raw) {
        if (keys.includes(element)) {
            result.push(element as T)
        }
    }

    return result
}