import '../styles/SignUpPage.scss'
import UaFlag from '../../public/images/ua_flag.svg'
import { useContext, useMemo, useState } from "react";
import AdvancedInput from "../components/AdvancedInput";
import SimpleHeader from "../components/SimpleHeader";
import { validateEmail, validateTelNumber } from "../utils/dataValidation";
import Footer from '../components/Footer';
import { StringResourcesContext } from '../StringResourcesContext';
import { DiContainerContext } from '../diContainer';
import { useNavigate } from 'react-router';

export default function SignUpPage() {
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)
    const dataSource = diContainer.dataSource
    const userCredsStore = diContainer.userCredsStore
    const navigate = useNavigate()

    const [firstName, setFirstName]= useState("")
    const [lastName, setLastName] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [telNumber, setTelNumber] = useState("")
    const [requestError, setRequestError] = useState("")

    const isValidFirstName = firstName.length > 0
    const isValidLastName = lastName.length > 0
    const isValidEmail = useMemo(() => validateEmail(email), [email])
    const isValidPassword = password.length >= 8
    const isValidTelNumber = validateTelNumber(telNumber)

    const isSignUpEnabled = isValidFirstName && isValidLastName && isValidEmail && isValidPassword && isValidTelNumber

    function signUp() {
        dataSource.signUpAsync({ firstName, lastName, email, password, telNumber }).then(() => {
            userCredsStore.saveUser({ email, password })
            navigate("/")
        }).catch(() => {
            setRequestError(strRes.signUpError)
        })
    }

    return (
        <>
            <SimpleHeader/>
            <div id="main-content">
                <div id="signup-form">
                    <AdvancedInput 
                        placeholder={strRes.personFirstName} 
                        inputType="text"
                        errorText={isValidFirstName ? undefined : strRes.textEmptyError}
                        onTextChanged={setFirstName}/>

                    <AdvancedInput 
                        placeholder={strRes.personLastName} 
                        inputType="text"
                        errorText={isValidLastName ? undefined : strRes.textEmptyError}
                        onTextChanged={setLastName}/>

                    <AdvancedInput 
                        placeholder={strRes.email} 
                        inputType="email"
                        errorText={isValidEmail ? undefined : strRes.invalidEmail}
                        onTextChanged={setEmail}/>

                    <div id="sign-up-form-telnum-block">
                        <UaFlag id="sign-up-form-ua-flag" width={16} height={16}/>
                        <p id="sign-up-form-country-code">+38</p>
                        <AdvancedInput 
                            placeholder={strRes.telephoneNumber} 
                            inputType="tel"
                            errorText={isValidTelNumber ? undefined : strRes.invalidTelNumber}
                            onTextChanged={setTelNumber}/>
                    </div>

                    <AdvancedInput
                        placeholder={strRes.password}
                        inputType="password"
                        onTextChanged={setPassword}
                        errorText={isValidPassword ? undefined : strRes.shortPasswordError} />

                    <p className='error'>{requestError}</p>

                    <button id="signup-button" className="primary" onClick={signUp} disabled={!isSignUpEnabled}>{strRes.signUp}</button>
                </div>
            </div>
            <Footer/>
        </>
    )
}