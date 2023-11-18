import { useRef } from "react"
import LeftArrowIcon from '../../public/images/left_arrow.svg'
import RightArrowIcon from '../../public/images/right_arrow.svg'
import "../styles/HorizontalScrollContainer.scss"

export type HorizontalScrollContainerProps<T> = {
    data: (T & { id: number })[],
    listItemGenerator: (item: T) => React.ReactNode
}

export default function HorizontalScrollContainer<T>(props: HorizontalScrollContainerProps<T>) {
    const contentListRef = useRef<HTMLUListElement>(null)

    function scrollLeft() {
        scrollDistance(-1)
    }

    function scrollRight() {
        scrollDistance(1);
    }

    function scrollDistance(dist: number) {
        contentListRef.current?.scrollBy({ left: dist, behavior: "smooth" })
    }

    return (<div className="horizontal-scroll-container" >
        <button className="horizontal-scroll-control-button horizontal-scroll-control-button-left icon-button" onClick={scrollLeft}>
            <LeftArrowIcon/>
        </button>

        <ul className="horizontal-scroll-container-content-list" ref={contentListRef}>
            {props.data.map(dataItem => {
                return <li key={dataItem.id}>{props.listItemGenerator(dataItem)}</li>
            })}
        </ul>
        
        <button className="horizontal-scroll-control-button horizontal-scroll-control-button-right icon-button" onClick={scrollRight}>
            <RightArrowIcon/>
        </button>
    </div>
    )
}