import React, { useState } from 'react'
import shopifyLogo from '../../public/images/shopify-logo.png'
import CategoryIcon from '../../public/images/category.svg'
import FavoriteIcon from '../../public/images/favorite.svg'
import CartIcon from '../../public/images/cart.svg'
import UserIcon from '../../public/images/user.svg'
import { ukrainianStringResources } from '../StringResources'
import "../styles/FullHeader.scss"
import SearchBar from './SearchBar'

export type FullHeaderProps = {
};

export default function FullHeader(props: FullHeaderProps) {
    const strRes = ukrainianStringResources

    let [searchQuery, setSearchQuery] = useState("")

    return (
        <div id="header">
            <div id="header-logo-block">
                <img id="header-logo" src={shopifyLogo} alt="Logo"></img>
            </div> 

            <div id="header-search-block">
                <button id="header-category-button" className="icon-button">
                    <CategoryIcon/>
                </button>

                <SearchBar 
                  placeholder={strRes.headerSearchBoxPlaceholder}
                  text={searchQuery}
                  searchButtonText={strRes.search}
                  onInputTextChanged={text => setSearchQuery(text)}
                  onInputFocusChanged={() => {}}/>
            </div>

            <div id="header-option-block">
                <CartButton productCount={1}/>
                <HeaderLinkRoundButton href='/' icon={<FavoriteIcon/>} type='fill-only'/>
                <HeaderLinkRoundButton href='/' icon={<UserIcon/>} type="stroke-only" id='header-user-button'/>
            </div>
        </div>
    )
}

type HeaderLinkRoundButtonType = 'stroke-only' | 'fill-only'

function HeaderLinkRoundButton(props: { id?: string, href: string, icon: React.ReactElement, type: 'stroke-only' | 'fill-only' }) {
    return (
        <a href={props.href} className={`header-round-button icon-button ${props.type}`} id={props.id}>
            {props.icon}
        </a>
    )
}

function CartButton(props: { productCount: number }) {
    return (
        <div id="header-cart-button-container">
            <button className='icon-button'>
                <CartIcon/>
            </button>
            <p>{props.productCount}</p>
        </div>
    )
}