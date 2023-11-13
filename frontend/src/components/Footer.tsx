import { useContext } from 'react'
import '../styles/Footer.scss'
import { StringResourcesContext } from '../StringResourcesContext'

export default function Footer() {
    const strRes = useContext(StringResourcesContext)

    return (
        <div id="footer">
            <a href="/">{strRes.userAgreement}</a>
            <a href="/">{strRes.faq}</a>
            <p id="footer-copyright">{strRes.copyright}</p>
        </div>
    )
}