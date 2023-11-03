import { useState } from 'react'
import logo from '../../public/logo.png'
import { englishStringResources } from '../StringResources'
import "../styles/FullHeader.scss"
import SearchBar from './SearchBar'

export default function FullHeader() {
    const strRes = englishStringResources

    let [searchQuery, setSearchQuery] = useState("")

    return (
        <div id="header">
            <img id="header-logo" src={logo} alt="Logo"></img>
            <p id="header-logo-name">Website name</p>
            <SearchBar 
              placeholder={strRes.headerSearchBoxPlaceholder}
              text={searchQuery}
              searchButtonText={strRes.search}
              onInput={text => setSearchQuery(text)}/>
        </div>
    )
}