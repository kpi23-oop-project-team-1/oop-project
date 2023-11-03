import "../styles/SearchBar.scss"
import CloseIcon from "../../public/images/close.svg"
import { ChangeEventHandler } from "react"

export type SearchBarProps = {
    text?: string,
    placeholder: string,
    searchButtonText: string
    onInput?: (text: string) => void
}

export default function SearchBar(props: SearchBarProps) {
    function optionalOnInput(text: string) {
        props.onInput && props.onInput(text)
    }

    return (
        <div className="search-bar-container">
            <input 
              type="text"
              placeholder={props.placeholder}
              onChange={e => optionalOnInput(e.target.value)}
              value={props.text}/>
            <button 
              type="button" 
              className="search-bar-clear-query-button"
              title="Clear"
              onClick={() => optionalOnInput("")}>
                <CloseIcon width={16} height={16}/>
            </button>
            <button
              type="button"
              className="search-bar-search-button primary">
                {props.searchButtonText}
            </button>
        </div>
    )
}