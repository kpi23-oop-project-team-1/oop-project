import '../styles/SignUpPage.scss'
import UaFlag from '../../public/images/ua_flag.svg'
import { useMemo, useState } from "react";
import { englishStringResources } from "../StringResources";
import AdvancedInput from "../components/AdvancedInput";
import SimpleHeader from "../components/SimpleHeader";
import { validateEmail, validateTelNumber } from "../utils/dataValidation";
import Footer from '../components/Footer';

export default function SignUpPage() {
    const strRes = englishStringResources

    let [firstName, setFirstName]= useState("")
    let [lastName, setLastName] = useState("")
    let [email, setEmail] = useState("")
    let [telNumber, setTelNumber] = useState("")

    const isValidFirstName = firstName.length > 0
    const isValidLastName = lastName.length > 0
    const isValidEmail = useMemo(() => validateEmail(email), [email])
    const isValidTelNumber = validateTelNumber(telNumber)

    return (
        <>
            <SimpleHeader/>
            <div id="main-content">
                <div id="signup-form">
                    <AdvancedInput 
                        placeholder={strRes.personFirstName} 
                        inputType="text"
                        errorText={isValidFirstName ? undefined : strRes.textEmptyError}
                        onInput={e => setFirstName(e.target.value)}/>

                    <AdvancedInput 
                        placeholder={strRes.personLastName} 
                        inputType="text"
                        errorText={isValidLastName ? undefined : strRes.textEmptyError}
                        onInput={e => setLastName(e.target.value)}/>

                    <AdvancedInput 
                        placeholder={strRes.email} 
                        inputType="email"
                        errorText={isValidEmail ? undefined : strRes.invalidEmail}
                        onInput={e => setEmail(e.target.value)}/>

                    <div id="sign-up-form-telnum-block">
                        <UaFlag id="sign-up-form-ua-flag" width={16} height={16}/>
                        <p id="sign-up-form-country-code">+380</p>
                        <AdvancedInput 
                            placeholder={strRes.telephoneNumber} 
                            inputType="tel"
                            errorText={isValidTelNumber ? undefined : strRes.invalidTelNumber}
                            onInput={e => setTelNumber(e.target.value)}/>
                    </div>

                    <AdvancedInput
                        placeholder={strRes.password}
                        inputType="password"/>

                    <button id="signup-button" className="primary">{strRes.signUp}</button>

                   
                </div>
            </div>
            <Footer/>
        </>
    )
}