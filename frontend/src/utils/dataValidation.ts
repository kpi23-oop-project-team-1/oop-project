const emailRegex = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;

export function validateEmail(text: string): boolean {
    return emailRegex.test(text)
}

export function validateTelNumber(text: string): boolean {
    if (text.length != 10) {
        return false
    }

    for (let i = 0; i < text.length; i++) {
        const c = text.charAt(i)
        if (c < '0' || c > '9') {
            return false
        }
    }

    return true
}