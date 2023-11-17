import React, { useContext, useState } from 'react'
import shopifyLogo from '../../public/images/shopify-logo.png'
import CategoryIcon from '../../public/images/category.svg'
import FavoriteIcon from '../../public/images/favorite.svg'
import CartIcon from '../../public/images/cart.svg'
import UserIcon from '../../public/images/user.svg'
import "../styles/FullHeader.scss"
import SearchBar from './SearchBar'
import { CartContext } from '../cart'
import { Link, To } from 'react-router-dom'
import { StringResourcesContext } from '../StringResourcesContext'

export type FullHeaderProps = {
    onShowCart: () => void
};

export default function FullHeader(props: FullHeaderProps) {
    const strRes = useContext(StringResourcesContext)

    let [searchQuery, setSearchQuery] = useState("")
    const [cart] = useContext(CartContext)

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
                <CartButton productCount={(cart.value ?? []).length} onClick={props.onShowCart}/>
                <HeaderLinkRoundButton to='/' icon={<FavoriteIcon/>} type='fill-only'/>
                <HeaderLinkRoundButton to='/' icon={<UserIcon/>} type="stroke-only" id='header-user-button'/>
            </div>
        </div>
    )
}

function HeaderLinkRoundButton(props: { id?: string, to: To, icon: React.ReactElement, type: 'stroke-only' | 'fill-only' }) {
    return (
        <Link to={props.to} className={`header-round-button icon-button ${props.type}`} id={props.id}>
            {props.icon}
        </Link>
    )
}

function CartButton(props: { productCount: number, onClick: () => void }) {
    return (
        <div id="header-cart-button-container" onClick={props.onClick}>
            <button className='icon-button'>
                <CartIcon/>
            </button>
            <p>{props.productCount}</p>
        </div>
    )
}