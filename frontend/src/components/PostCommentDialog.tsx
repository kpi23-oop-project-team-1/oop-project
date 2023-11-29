import { useContext, useState } from "react"
import { totalCommentStarCount } from "../dataModels"
import { Dialog } from "./Dialogs"
import { StarRating } from "./StarRating"
import { StringResourcesContext } from "../StringResourcesContext"
import CloseIcon from '../../public/images/close.svg'
import "../styles/PostCommentDialog.scss"

type PostCommentDialogProps = {
    headerText: string,
    onPost: (info: { rating: number, text: string }) => void
    onClose: () => void
}

export default function PostCommentDialog(props: PostCommentDialogProps) {
    const strRes = useContext(StringResourcesContext)

    const [rating, setRating] = useState(5)
    const [text, setText] = useState("")

    function postComment() {
        props.onPost({ rating, text })
    }

    return (
        <Dialog id="post-comment-dialog">
            <div id="post-comment-dialog-header">
                <h1>{props.headerText}</h1>
                <button className="icon-button" id="post-comment-dialog-close" onClick={props.onClose}>
                    <CloseIcon/>
                </button>
            </div>
           
            <StarRating interactive total={totalCommentStarCount} value={rating} onValueChanged={setRating}/>
            <textarea value={text} onChange={e => setText(e.target.value)}/>

            <button 
              id="post-comment-dialog-post-button" 
              className="primary" 
              disabled={text.length == 0}
              onClick={postComment}>
                {strRes.postComment}
            </button>
            
        </Dialog>
    )
}