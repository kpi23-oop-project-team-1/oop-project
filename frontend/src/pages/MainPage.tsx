import { useContext, useState } from "react"
import "../styles/MainPage.scss"
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader"
import { StringResourcesContext } from "../StringResourcesContext"
import Footer from "../components/Footer"
import { CartContext, useCart } from "../cart"
import { UserTypeContext, useCurrentUserType } from "../user.react"

export default function MainPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const cartAndManager = useCart()
    const userType = useCurrentUserType()

    return (
        <UserTypeContext.Provider value={userType.value}>
            <CartContext.Provider value={cartAndManager}>
                <PageWithSearchHeader
                  dialogSwitch={undefined} 
                  dialogType={dialogType} 
                  onChangeDialogType={setDialogType}>
                    <AboutBlock/>
                    <Footer/>
                </PageWithSearchHeader>
            </CartContext.Provider>
        </UserTypeContext.Provider>
    )
}

function AboutBlock() {
    const strRes = useContext(StringResourcesContext)

    return (<div id="about-us-block">
        <h1 className="uppercase">{strRes.aboutUs}</h1>
        <p>{strRes.aboutShopifyDetail}</p>
    </div>)
}