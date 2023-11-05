import "../styles/SearchBar.scss"
import CloseIcon from "../../public/images/close.svg"

export type SearchBarProps = {
    text?: string,
    placeholder: string,
    searchButtonText: string,
    onInputFocusChanged: (isFocused: boolean) => void,
    onInputTextChanged: (text: string) => void
}

export default function SearchBar(props: SearchBarProps) {
    return (
        <div className="search-bar-container">
            <input 
              type="text"
              placeholder={props.placeholder}
              onChange={e => props.onInputTextChanged(e.target.value)}
              onFocus={() => props.onInputFocusChanged(true)}
              onBlur={() => props.onInputFocusChanged(false)}
              value={props.text}/>
            <button 
              type="button" 
              className="search-bar-clear-query-button"
              title="Clear"
              onClick={() => props.onInputTextChanged("")}>
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