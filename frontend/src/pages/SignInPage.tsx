import SimpleHeader from "../components/SimpleHeader";
import "../styles/SignInPage.scss";
import { useContext, useMemo, useState } from 'react';
import { validateEmail } from "../utils/dataValidation";
import { Link, useNavigate } from "react-router-dom";
import { StringResourcesContext } from "../StringResourcesContext";
import { DiContainerContext } from "../diContainer";
import { LabeledTextInput } from "../components/LabeledInputs";

export default function SignInPage() {
    const strRes = useContext(StringResourcesContext)

    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [signInError, setSignInError] = useState("")
   
    const isValidEmail = useMemo(() => validateEmail(email), [email]);
    const emailErrorText = isValidEmail ? undefined : strRes.invalidEmail;

    const diContainer = useContext(DiContainerContext)
    const dataSource = diContainer.dataSource
    const userCredsStore = diContainer.userCredsStore
    const navigate = useNavigate()

    function signIn() {
        const creds = {email, password}

        dataSource.authenticateAsync(creds).then(() => {
            userCredsStore.saveUser(creds)
            navigate("/")
        }).catch(() => {
            setSignInError(strRes.invalidEmailOrPassword)
        })
    }

    return (
        <>
            <SimpleHeader/>
            <div id="sign-in-main-content">
                <div id="signin-form">
                    <div id="user-info-block">
                        <LabeledTextInput
                            label={strRes.email}
                            placeholder={strRes.email} 
                            inputType="email" 
                            text={email}
                            onTextChanged={setEmail}
                            errorText={emailErrorText} />

                        <LabeledTextInput 
                            label={strRes.password}
                            placeholder={strRes.password} 
                            inputType="password" 
                            text={password}
                            onTextChanged={setPassword}/>

                        <p className="error">{signInError}</p>

                        <div id="signup-block">
                            <p>{strRes.newToWebsite}</p>
                            <Link to="/signup">{strRes.signUp}</Link>
                        </div>
                    </div>

                    <button 
                     id="signin-button" 
                     className="primary" 
                     disabled={!isValidEmail || password.length == 0} 
                     type="button"
                     onClick={signIn}>
                        {strRes.signIn}
                    </button>
                </div>
            </div>
        </>
    )
}