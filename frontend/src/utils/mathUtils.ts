import { NumberRange } from "../dataModels";

export function clamp(value: number, min: number, max: number): number {
    return Math.min(max, Math.max(min, value))
}

export function rangesIntersects(a: NumberRange, b: NumberRange): boolean {
    return a.start <= b.end && b.start <= a.end
}

export function rangeCompletelyContains(range: NumberRange, other: NumberRange) {
    function contains(x: number): boolean {
        return x >= range.start && x <= range.end
    }
    
    return contains(other.start) && contains(other.end)
}

export function clampRange(innerRange: NumberRange, outerRange: NumberRange): NumberRange {
    if (!rangesIntersects(innerRange, outerRange)) {
        return outerRange
    }

    const start = Math.max(innerRange.start, outerRange.start)
    const end = Math.min(innerRange.end, outerRange.end)

    return { start, end };
}

export function lerp(range: NumberRange, fraction: number): number {
    return range.start + fraction * (range.end - range.start)
}