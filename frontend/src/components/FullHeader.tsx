import React, { useContext, useEffect, useState } from 'react'
import shopifyLogo from '../../public/images/shopify-logo.png'
import CategoryIcon from '../icons/category.svg'
import AdminIcon from '../icons/admin.svg'
import CartIcon from '../icons/cart.svg'
import UserIcon from '../icons/user.svg'
import "../styles/FullHeader.scss"
import SearchBar from './SearchBar'
import { CartContext } from '../cart'
import { Link, To } from 'react-router-dom'
import { StringResourcesContext } from '../StringResourcesContext'
import { UserTypeContext } from '../user.react'
import { GlobalSearchQueryContext } from '../globalSearchQueryContext'

export type FullHeaderProps = {
    onSearchSubmit: (query: string) => void,
    onShowCart: () => void
};

export default function FullHeader(props: FullHeaderProps) {
    const strRes = useContext(StringResourcesContext)
    const userType = useContext(UserTypeContext)

    const globalSearchQuery = useContext(GlobalSearchQueryContext)
    const [searchQuery, setSearchQuery] = useState(globalSearchQuery ?? "")

    useEffect(() => {
        if (globalSearchQuery) {
            setSearchQuery(globalSearchQuery)
        }
    }, [globalSearchQuery])

    const [cart] = useContext(CartContext)

    return (
        <div id="header">
            <div id="header-logo-block">
                <a href="/"><img id="header-logo" src={shopifyLogo} alt="Logo"></img></a>
            </div>

            <div id="header-search-block">
                <button id="header-category-button" className="icon-button">
                    <CategoryIcon/>
                </button>

                <SearchBar
                  placeholder={strRes.headerSearchBoxPlaceholder}
                  text={searchQuery}
                  onSubmit={() => props.onSearchSubmit(searchQuery)}
                  onInputTextChanged={setSearchQuery}/>
            </div>

            <div id="header-option-block">
                {
                    userType == 'customer-trader' ?
                    <>
                        <CartButton productCount={(cart.value ?? []).length} onClick={props.onShowCart}/>
                    </>
                    : undefined
                }

                {
                    userType == 'admin' ?
                    <HeaderLinkRoundButton to={"/admin"} icon={<AdminIcon/>} type='fill-only'/>
                    : undefined
                }


                <HeaderLinkRoundButton to='/myaccount' icon={<UserIcon/>} type="stroke-only" id='header-user-button'/>
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