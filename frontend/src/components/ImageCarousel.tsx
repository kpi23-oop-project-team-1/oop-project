import React, { useEffect, useLayoutEffect, useRef, useState } from "react"
import { clamp } from "../utils/mathUtils"
import "../styles/ImageCarousel.scss"
import LeftArrowIcon from "../../public/images/left_arrow.svg"
import RightArrowIcon from "../../public/images/right_arrow.svg"

type ImageCarouselProps = {
    imageSources: string[]
}

export default function ImageCarousel(props: ImageCarouselProps) {
    const containerRef = useRef<HTMLDivElement>(null)
    const carouselRef = useRef<HTMLUListElement>(null)

    const [selectedIndex, setSelectedIndex] = useState(0)
    const [elementWidth, setElementWidth] = useState(0)

    useLayoutEffect(() => {
        const container = containerRef.current
        const observer = new ResizeObserver(() => {
            const bounds = containerRef.current?.getBoundingClientRect()

            setElementWidth(bounds?.width ?? 0)
        })
        
        if (container) {
            observer.observe(container)
        }

        return () => observer.disconnect()
    })

    useEffect(() => {
        carouselRef.current?.scroll({ left: selectedIndex * elementWidth, behavior: 'smooth' })
    }, [selectedIndex]) 

    function onControlButtonClicked(delta: number) {
        setSelectedIndex(i => clamp(i + delta, 0, props.imageSources.length - 1))
    }
    
    return (
        <div 
          className="image-carousel-container" 
          ref={containerRef}>
            <div className="image-carousel-content-with-controls">
                <ul className="image-carousel-content" ref={carouselRef}>
                    {props.imageSources.map(src => 
                        <li style={ { width: `${elementWidth}px` } }>
                            <img src={src}/>
                        </li>   
                    )}
                </ul>

                <ControlButton 
                  type='left'
                  isVisible={selectedIndex > 0}
                  onClick={() => onControlButtonClicked(-1)}>
                    <LeftArrowIcon/>
                </ControlButton>

                <ControlButton 
                  type='right'
                  isVisible={selectedIndex < props.imageSources.length - 1}
                  onClick={() => onControlButtonClicked(1)}>
                    <RightArrowIcon/>
                </ControlButton>
            </div>

            <div className="image-carousel-dots">
                {(() => {
                    const result: React.ReactElement[] = []
                    for (let i = 0; i < props.imageSources.length; i++) {
                        result.push(
                            <div 
                              className={"image-carousel-dot" + (i == selectedIndex ? " selected-dot" : "")}
                              onClick={() => setSelectedIndex(i)}/>
                        )
                    }

                    return result
                })()}
            </div>
        </div>
    )
}

function ControlButton(props: React.PropsWithChildren<{ type: 'left' | 'right', isVisible: boolean, onClick: () => void }>) {
    return (
        <button
          className={`icon-button image-carousel-control-button image-carousel-${props.type}-button`}
          style={ { visibility: props.isVisible ? "visible" : "hidden" } }
          onClick={props.onClick}>
            {props.children}
        </button>
    )
}