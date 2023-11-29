import "../styles/StarRating.scss"
import StarIcon from "../icons/star.svg"

type StarRatingProps = {
    total: number,
    value: number,
    interactive?: boolean,
    onValueChanged?: (value: number) => void
}

export function StarRating(props: StarRatingProps) {
    const isInteractive = props.interactive ?? false

    const starElements: React.ReactElement[] = []

    for (let i = 1; i <= props.total; i++) {
        starElements.push(
            <Star 
              key={i} 
              isFilled={i <= props.value} 
              onClick={isInteractive ? (() => props.onValueChanged?.(i)) : undefined}/>
        )
    }
    
    return (
        <div className="star-rating-container">
            {starElements}
        </div>
    )
}

function Star(props: { isFilled: boolean, onClick?: () => void }) {
    return (
        <StarIcon className={"star" + (props.isFilled ? " filled-star" : "")} onClick={props.onClick}/>
    )
}