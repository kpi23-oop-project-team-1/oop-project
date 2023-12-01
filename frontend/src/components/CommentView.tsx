import { CommentInfo, totalCommentStarCount } from "../dataModels"
import { StarRating } from "./StarRating"
import "../styles/CommentView.scss"

type CommentViewProps = {
    comment: CommentInfo
}

export function CommentView(props: CommentViewProps) {
    const comment = props.comment

    return (
        <div className="comment-container">
            <div className="comment-header">
                <p className="comment-display-name">{comment.user.displayName}</p>
                <p className="comment-date">{new Date(comment.dateString).toLocaleDateString()}</p>
            </div>

            <StarRating value={comment.rating} total={totalCommentStarCount}/>
            <p className="comment-text">{comment.text}</p>
        </div>
    )
}