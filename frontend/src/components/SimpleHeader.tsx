import logo from '../../public/logo.png';
import "../styles/SimpleHeader.scss";

export default function SimpleHeader() {
    return (
        <div id="header">
            <img id="header-logo" src={logo} alt="Logo"></img>
            <p id="header-logo-name">Website name</p>
        </div>
    );
}