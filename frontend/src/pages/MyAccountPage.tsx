import { useContext, useMemo, useState } from "react";
import "../styles/MyAccountPage.scss"
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader"
import Footer from "../components/Footer"
import TextButton from "../components/TextButton"
import UserPanel from "../components/UserPanel"
import { StringResourcesContext } from "../StringResourcesContext"
import DeferredDataContainer from "../components/DeferredDataContainer"
import { CartContext, useCart } from "../cart"
import { useValueFromDataSource } from "../dataSource.react"
import { DiContainerContext } from "../diContainer";
import { AccountInfo, NewAccountInfo } from "../dataModels";
import { Link } from "react-router-dom"
import { UserTypeContext, navigateToSignInPageIfNotSignedIn, useUserType } from "../user.react"
import { LabeledTextInputProps, LabeledTextInput, LabeledTextAreaInput, LabeledDropdownInputProps, LabeledDropdownInput, LabeledNumberInputProps, LabeledNumberInput, LabeledFileImageLoaderView } from "../components/LabeledInputs"
import { validateEmail, validateTelNumber } from "../utils/dataValidation";
import { useNavigate } from "react-router";

export default function MyAccountPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)

    const dataSource = diContainer.dataSource
    const userCreds = diContainer.userCredsStore.getCurrentUserCredentials()

    const cartAndManager = useCart()
    const userType = useUserType()

    const navigate = useNavigate()

    navigateToSignInPageIfNotSignedIn(userCreds, navigate)

    // Get account info
    const emptyAccountInfo: AccountInfo = {
        id: 0,
        email: "",
        password: "",
        username: "",
        pfpSource: "",
        aboutMe: "",
        firstName: "",
        lastName: "",
        telNumber: "",
        address: ""
    }
    const [userIdState] = useValueFromDataSource(async ds => userCreds ? await ds.getUserId(userCreds.email) : undefined)
    const userId = userIdState && userIdState.value ? userIdState.value : 0

    const [accountState] = useValueFromDataSource(async ds => userCreds ? await ds.getAccountInfo(userId) : undefined)
    const accountInfo = accountState && accountState.value ? accountState.value : emptyAccountInfo

    // Account parameters
    const [email, setEmail] = useState(accountInfo.email)
    const [password, setPassword] = useState("")

    const [pfpFiles, setPfpFiles] = useState<File[]>([])
    const [username, setUsername] = useState("")
    const [aboutMe, setAboutMe] = useState("")

    const [firstName, setFirstName] = useState("")
    const [lastName, setLastName] = useState("")
    const [telNumber, setTelNumber] = useState("")
    const [address, setAddress] = useState("")

    const [loaded, setLoaded] = useState(false);
    if (!loaded && accountState && accountState.value) {
        setEmail(accountInfo.email)
        setUsername(accountInfo.username)
        setAboutMe(accountInfo.aboutMe)
        setFirstName(accountInfo.firstName)
        setLastName(accountInfo.lastName)
        setTelNumber(accountInfo.telNumber)
        setAddress(accountInfo.address)
        setLoaded(true);
    }

    // Validation
    const isValidEmail = useMemo(() => validateEmail(email), [email])
    const isValidPassword = password.length == 0 || password.length >= 8;

    const isValidUsername = username.length > 0
    const isValidAboutMe = true;

    const isValidFirstName = firstName.length > 0
    const isValidLastName = lastName.length > 0
    const isValidTelNumber = validateTelNumber(telNumber)
    const isValidAddress = address.length > 0
    const isValidEverything = isValidEmail && isValidPassword && isValidUsername && isValidAboutMe && isValidFirstName && isValidLastName && isValidTelNumber && isValidAddress

    const [showTextInputErrors, setShowTextInputErrors] = useState(false)
    const [isSubmitInProgress, setSubmitInProgress] = useState(false)

    function onMyProfileClick() {
        navigate("/user/$" + accountInfo.id + "/")
    }

    function onMyProductsClick() {
        navigate("/myproducts")
    }

    function onSignOutClick() {
        navigate("/")
    }

    function submit() {
        if (!userCreds) {
            return
        }

        if (!isValidEverything) {
            setShowTextInputErrors(true)
            return
        }

        const newAccount: NewAccountInfo = {
            email,
            password,
            username,
            pfpFile: pfpFiles[0],
            aboutMe,
            firstName,
            lastName,
            telNumber,
            address
        }

        setSubmitInProgress(true)
        dataSource.updateAccountInfo(newAccount, userCreds).then(() => {
            navigate('/myaccount')
        }).catch(() => {
            setSubmitInProgress(false)
        })
    }

    return (
        <UserTypeContext.Provider value={userType.value}>
        <CartContext.Provider value={cartAndManager}>
        <PageWithSearchHeader
          dialogSwitch={undefined}
          dialogType={dialogType}
          onChangeDialogType={setDialogType}>
            <div id="my-account-content">
            <DeferredDataContainer state={accountState}>
            <div id="my-account-content-grid">

                <div id="my-account-content-left-side">
                    <UserPanel
                        id={accountInfo.id}
                        username={accountInfo.username}
                        pfpSource={accountInfo.pfpSource}/>

                    <div id="my-account-nav-panel">
                        <TextButton
                         id="my-profile-button"
                         text={strRes.myProfile}
                         onClick={onMyProfileClick}
                         highlighted="false"/>
                        <br/>
                        <TextButton
                         id="my-products-button"
                         text={strRes.myProducts}
                         onClick={onMyProductsClick}
                         highlighted="false"/>
                        <br/>
                        <TextButton
                         id="sign-out-button"
                         text={strRes.signOut}
                         onClick={onSignOutClick}
                         highlighted="false"/>
                    </div>
                </div>

                <div id="my-account-content-right-side">
                    <h1 className="primary">{strRes.myAccount}</h1>
                    <p className="gray">{strRes.myAccountPageDesc}</p>

                    <hr/>

                    <h2>{strRes.profileLook}</h2>

                    <LabeledFileImageLoaderView
                      type="only-upload"
                      label={strRes.changePfp}
                      maxImages={1}
                      files={pfpFiles}
                      onFilesChanged={setPfpFiles}/>

                    <LabeledTextInput
                        label={strRes.username}
                        text={username}
                        placeholder=""
                        onTextChanged={setUsername}
                        errorText={showTextInputErrors && !isValidUsername ? strRes.textEmptyError : undefined}/>

                    <LabeledTextAreaInput
                        label={strRes.personAboutMe}
                        text={aboutMe}
                        placeholder=""
                        onTextChanged={setAboutMe}
                        errorText={showTextInputErrors && !isValidAboutMe ? strRes.textEmptyError : undefined}/>

                    <hr/>

                    <h2>{strRes.loginDetails}</h2>

                    <LabeledTextInput
                        label={strRes.email}
                        text={email}
                        placeholder=""
                        onTextChanged={setEmail}
                        errorText={showTextInputErrors && !isValidEmail ? strRes.invalidEmail : undefined}/>

                    <LabeledTextInput
                        label={strRes.changePassword}
                        text={password}
                        placeholder=""
                        onTextChanged={setPassword}
                        errorText={showTextInputErrors && !isValidPassword ? strRes.shortPasswordError : undefined}/>

                    <hr/>

                    <h2>{strRes.personContactDetails}</h2>

                    <LabeledTextInput
                        label={strRes.personFirstName}
                        text={firstName}
                        placeholder=""
                        onTextChanged={setFirstName}
                        errorText={showTextInputErrors && !isValidFirstName ? strRes.textEmptyError : undefined}/>

                    <LabeledTextInput
                        label={strRes.personLastName}
                        text={lastName}
                        placeholder=""
                        onTextChanged={setLastName}
                        errorText={showTextInputErrors && !isValidLastName ? strRes.textEmptyError : undefined}/>

                    <LabeledTextInput
                        label={strRes.telephoneNumber}
                        text={telNumber}
                        placeholder=""
                        onTextChanged={setTelNumber}
                        errorText={showTextInputErrors && !isValidTelNumber ? strRes.invalidTelNumber : undefined}/>

                    <LabeledTextInput
                        label={strRes.personAddress}
                        text={address}
                        placeholder=""
                        onTextChanged={setAddress}
                        errorText={showTextInputErrors && !isValidAddress ? strRes.textEmptyError : undefined}/>

                    <button
                      id="submit-button"
                      className="primary"
                      disabled={isSubmitInProgress}
                      onClick={submit}>
                        {strRes.saveChanges}
                    </button>

                </div>

            </div>
            </DeferredDataContainer>
            </div>
            <Footer/>
        </PageWithSearchHeader>
        </CartContext.Provider>
        </UserTypeContext.Provider>
    )
}
