import "../styles/StarRating.scss"
import StarIcon from "../../public/images/star.svg"

type StarRatingProps = {
    total: number,
    value: number
}

export function StarRating(props: StarRatingProps) {
    const starElements: React.ReactElement[] = []

    for (let i = 1; i <= props.total; i++) {
        starElements.push(
            <Star isFilled={i <= props.value}/>
        )
    }
    
    return (
        <div className="star-rating-container">
            {starElements}
        </div>
    )
}

function Star(props: { isFilled: boolean }) {
    return (
        <StarIcon className={"star" + (props.isFilled ? " filled-star" : "")}/>
    )
}