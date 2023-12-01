export function encodeBase64(rawText: string): string {
    const bytes = new TextEncoder().encode(rawText)
    const binString = String.fromCodePoint(...bytes)

    return btoa(binString)
}