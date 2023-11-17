import { NumberRange } from "../dataModels";

export function clamp(value: number, min: number, max: number): number {
    return Math.min(max, Math.max(min, value))
}

export function clampRange(innerRange: NumberRange, outerRange: NumberRange): NumberRange {
    const start = Math.max(innerRange.start, outerRange.start)
    const end = Math.min(innerRange.end, outerRange.end)

    return { start, end };
}

export function lerp(range: NumberRange, fraction: number): number {
    return range.start + fraction * (range.end - range.start)
}