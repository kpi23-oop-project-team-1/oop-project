import { PointerEvent, useRef, useState } from "react"
import "../styles/RangeSlider.scss"
import { clamp, clampRange, lerp } from "../utils/mathUtils"
import { NumberRange } from "../dataModels"

export type RangeSliderPointerStatus = 'up' | 'down'
type ThumbType = 'start' | 'end'

export type RangeSliderProps = {
    limitingRange: NumberRange,
    valueRange: NumberRange,

    onPointerStatusChanged?: (status: RangeSliderPointerStatus) => void 
    onRangeSelected?: (range: NumberRange) => void,
}

function getValuePercentage(value: number, range: NumberRange): number {
    return (value - range.start) / (range.end - range.start)
}

function getPercentageString(value: number): string {
    return (value * 100) + "%";
}

export default function RangeSlider(props: RangeSliderProps) {
    const clampedRange = clampRange(props.valueRange, props.limitingRange)

    const [draggingThumbType, setDraggingThumbType] = useState<ThumbType>()
    const sliderRef = useRef<HTMLDivElement>(null)
    
    const startPerc = getValuePercentage(clampedRange.start, props.limitingRange)
    const endPerc = getValuePercentage(clampedRange.end, props.limitingRange)

    function onMouseDownInThumb(thumbType: ThumbType, e: PointerEvent<HTMLSpanElement>) {
        setDraggingThumbType(thumbType)
        sliderRef.current?.setPointerCapture(e.pointerId)

        if (props.onPointerStatusChanged) {
            props.onPointerStatusChanged('down')
        }
    }

    function onPointerUp(e: PointerEvent<HTMLSpanElement>) {
        setDraggingThumbType(undefined)
        sliderRef.current?.releasePointerCapture(e.pointerId)

        if (props.onPointerStatusChanged) {
            props.onPointerStatusChanged('up')
        }
    }

    function onPointerMove(event: PointerEvent<HTMLDivElement>) {
        if (draggingThumbType == undefined) {
            return
        }

        const width = sliderRef.current?.clientWidth ?? 0
        const clampedX = clamp(event.clientX, 0, width)
        
        const selectedValuePerc = clampedX / width
        let selectedValue = Math.round(lerp(props.limitingRange, selectedValuePerc))
        
        if (draggingThumbType == 'start') {
            selectedValue = Math.min(selectedValue, clampedRange.end)    
        } else {
            selectedValue = Math.max(selectedValue, clampedRange.start)
        }

        const newRange = { ...clampedRange, [draggingThumbType]: selectedValue }
        if (props.onRangeSelected) {
            props.onRangeSelected(newRange)
        }
    }

    return (
        <div 
          className="range-slider" 
          ref={sliderRef}
          onPointerMove={draggingThumbType != undefined ? onPointerMove : undefined}
          onPointerUp={onPointerUp}>
            <div className="range-slider-rail"/>
            <div 
              className="range-slider-track"
              style={ { left: getPercentageString(startPerc), width: getPercentageString(endPerc - startPerc) } }/>

            <div 
              className="range-slider-thumb" 
              style={ { left: getPercentageString(startPerc) } }
              onPointerDown={e => onMouseDownInThumb('start', e)} 
              onPointerUp={onPointerUp}/>

            <div 
              className="range-slider-thumb" 
              style={ { left: getPercentageString(endPerc) } }
              onPointerDown={e => onMouseDownInThumb('end', e)} 
              onPointerUp={onPointerUp}/>
        </div>
    )
}