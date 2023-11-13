import shopifyLogo from '../../public/images/shopify-logo.png';
import "../styles/SimpleHeader.scss";

export default function SimpleHeader() {
    return (
        <div id="header">
            <img id="header-logo" src={shopifyLogo} alt="Logo"></img>
            <p id="header-logo-name">Website name</p>
        </div>
    );
}