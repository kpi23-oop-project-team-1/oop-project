import { useContext } from 'react'
import '../styles/Footer.scss'
import { StringResourcesContext } from '../StringResourcesContext'
import { Link } from 'react-router-dom'

export default function Footer() {
    const strRes = useContext(StringResourcesContext)

    return (
        <div id="footer">
            <Link to="/">{strRes.userAgreement}</Link>
            <Link to="/">{strRes.faq}</Link>
            <p id="footer-copyright">{strRes.copyright}</p>
        </div>
    )
}