export function formatPriceToString(price: number): string {
    price = Math.floor(price)

    const intPart = price / 100
    const remPart = price % 100

    return `${intPart}.${remPart}₴`
}