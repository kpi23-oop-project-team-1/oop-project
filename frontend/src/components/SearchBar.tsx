import { KeyboardEvent, useContext } from "react"
import "../styles/SearchBar.scss"
import { StringResourcesContext } from "../StringResourcesContext"

export type SearchBarProps = {
    text?: string,
    placeholder: string,
    onSubmit: () => void
    onInputTextChanged: (text: string) => void
}

export default function SearchBar(props: SearchBarProps) {
    const strRes = useContext(StringResourcesContext)

    function onKeyPress(e: KeyboardEvent) {
        if (e.key == "Enter") {
            props.onSubmit()
        }
    }

    return (
        <div className="search-bar-container">
            <input 
              type="text"
              placeholder={props.placeholder}
              onChange={e => props.onInputTextChanged(e.target.value)}
              onKeyDown={onKeyPress}
              value={props.text}/>
            
            <button
              type="button"
              className="search-bar-search-button primary"
              onClick={props.onSubmit}>
                {strRes.search}
            </button>
        </div>
    )
}