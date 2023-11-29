import "../styles/TextButton.scss"

export type TextButtonProps = {
    id: string
    text: string
    onClick: () => void
    highlighted: string
}

export default function TextButton(props: TextButtonProps) {
    return (
        <button
         id={props.id}
         className="text-button"
         onClick={props.onClick}>
           {props.text}
           <hr/>
        </button>
    )
}