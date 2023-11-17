import SimpleHeader from "../components/SimpleHeader";
import AdvancedInput from "../components/AdvancedInput";
import "../styles/SignInPage.scss";
import { useContext, useMemo, useState } from 'react';
import { validateEmail } from "../utils/dataValidation";
import { Link } from "react-router-dom";
import { StringResourcesContext } from "../StringResourcesContext";

export default function SignInPage() {
    const strRes = useContext(StringResourcesContext)

    let [email, setEmail] = useState("")
    let [password, setPassword] = useState("")
   
    const isValidEmail = useMemo(() => validateEmail(email), [email]);
    const emailErrorText = isValidEmail ? undefined : strRes.invalidEmail;

    return (
        <>
            <SimpleHeader/>
            <div id="main-content">
                <div id="signin-form">
                    <div id="user-info-block">
                        <AdvancedInput
                            placeholder={strRes.email} 
                            inputType="email" 
                            onTextChanged={setEmail}
                            errorText={emailErrorText} />

                        <AdvancedInput 
                            placeholder={strRes.password} 
                            inputType="password" 
                            reserveSpaceForError={false}
                            onTextChanged={setPassword}/>

                        <div id="signup-block">
                            <p>{strRes.newToWebsite}</p>
                            <Link to="signup">{strRes.signUp}</Link>
                        </div>
                    </div>

                    <button 
                     id="signin-button" 
                     className="primary" 
                     disabled={!isValidEmail || password.length == 0} 
                     type="button">
                        {strRes.signIn}
                    </button>
                </div>
            </div>
        </>
    )
}