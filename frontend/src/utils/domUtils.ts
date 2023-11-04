export function setBodyClass(value: string) {
    document.body.className = value
}

export function getDomElementBounds(selector: string): DOMRect {
    return document.querySelector(selector)?.getBoundingClientRect() ?? new DOMRect()
}