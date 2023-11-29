import "../styles/UserPanel.scss"
import { Link } from "react-router-dom"

export type UserPanelProps = {
    id: number
    username: string
    pfpSource: string
}

export default function UserPanel(props: UserPanelProps) {
    return (
        <Link to={`/user/${props.id}/`}>
            <div className="user-panel">
                <img src={props.pfpSource}></img>
                <p>{props.username}</p>
            </div>
        </Link>
    )
}