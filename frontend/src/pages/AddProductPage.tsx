import { useContext, useState } from "react";
import { StringResourcesContext } from "../StringResourcesContext";
import { UserTypeContext, navigateToMainPageIfNotBuyerSeller, useUserType } from "../user.react";
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader";
import { CartContext, useCart } from "../cart";
import FileImageLoaderView from "../components/FileImageLoaderView";
import "../styles/AddProductPage.scss"
import { CategoryId, ColorId, NewProductInfo, ProductState, allCategoryIds, allColorIds, allProductStates } from "../dataModels";
import AdvancedInputField from "../components/AdvancedInputField";
import { Dropdown } from "../components/Dropdown";
import NumberInput from "../components/NumberInput";
import Footer from "../components/Footer";
import { DiContainerContext } from "../diContainer";
import { useNavigate } from "react-router";

export default function AddProductPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)

    const dataSource = diContainer.dataSource
    const userCreds = diContainer.userCredsStore.getCurrentUserCrediatials()

    const cartAndManager = useCart()
    const userType = useUserType()
    const navigate = useNavigate()

    navigateToMainPageIfNotBuyerSeller(userType, navigate)

    const [showTextInputErrors, setShowTextInputErrors] = useState(false)

    const [title, setTitle] = useState("")
    const [description, setDescription] = useState("")
    const [state, setState] = useState<ProductState | undefined>()
    const [category, setCategory] = useState<CategoryId | undefined>()
    const [color, setColor] = useState<ColorId | undefined>()
    const [amount, setAmount] = useState<number | undefined>(1)
    const [price, setPrice] = useState<number | undefined>(150)
    const [imageFiles, setImageFiles] = useState<File[]>([])
    const [isSubmitInProgress, setSubmitInProgress] = useState(false)

    const isSubmitEnabled = state && category && amount && price && imageFiles.length > 0

    function submit() {
        if (!userCreds) {
            return
        }

        if (title.length == 0 || description.length == 0 || !state || !category || !amount || !price || !color) {
            setShowTextInputErrors(true)
            return
        } 

        const product: NewProductInfo = {
            title, description, state: state, category, color, price, 
            totalAmount: amount, 
            images: imageFiles
        }

        setSubmitInProgress(true)
        dataSource.addProduct(product, userCreds).then(() => {
            navigate('/')
        }).catch(() => {
            setSubmitInProgress(false)
        })
    }

    return (
        <CartContext.Provider value={cartAndManager}>
        <UserTypeContext.Provider value={userType.value}>

        <PageWithSearchHeader
          dialogType={dialogType}
          onChangeDialogType={setDialogType}>
            <div id="add-product-page-content">
                <h2>{strRes.uploadImages}</h2>
                <FileImageLoaderView maxImages={5} onFilesChanged={setImageFiles}/>

                <h2>{strRes.describeProduct}</h2>

                <LabeledTextInput 
                  label={strRes.productTitle} 
                  text={title}
                  placeholder={strRes.productTitlePlaceholder} 
                  onTextChanged={setTitle}
                  errorText={showTextInputErrors && title.length == 0 ? strRes.textEmptyError : undefined}/>
                
                <LabeledTextAreaInput 
                  label={strRes.description} 
                  text={description}
                  placeholder={strRes.productDescriptionPlaceholder} 
                  onTextChanged={setDescription}
                  errorText={showTextInputErrors && description.length == 0 ? strRes.textEmptyError : undefined}/>

                <LabeledDropdownInput
                  selectedValue={state}
                  onSelected={setState}
                  entries={allProductStates.map(id => ({ id, label: strRes.productStateLabels[id] }))}
                  label={strRes.productState}/>

                <LabeledDropdownInput
                  selectedValue={color}
                  onSelected={setColor}
                  entries={allColorIds.map(id => ({ id, label: strRes.colorLabels[id] }))}
                  label={strRes.color}/>

                <LabeledDropdownInput
                  selectedValue={category}
                  onSelected={setCategory}
                  entries={allCategoryIds.map(id => ({ id, label: strRes.productCategoryLabels[id] }))}
                  label={strRes.category}/>

                <LabeledNumberInput
                  value={amount}
                  onValueChanged={value => (value ?? 1) >= 1 ? setAmount(value) : undefined}
                  label={strRes.quantity}/>
                
                <LabeledNumberInput
                  value={price}
                  onValueChanged={value => (value ?? 1) >= 1 ? setPrice(value) : undefined}
                  label={strRes.price}/>

                <button
                  id="add-product-button"
                  className="primary"
                  disabled={!isSubmitEnabled && !isSubmitInProgress}
                  onClick={submit}>
                    {strRes.addProduct}
                </button>
            </div>

            <Footer/>
        </PageWithSearchHeader>

        </UserTypeContext.Provider>
        </CartContext.Provider>
    )
}

type BaseLabeledInputProps = { 
    label: string, 
    isRequired?: boolean 
}

function LabeledInput(props: React.PropsWithChildren<BaseLabeledInputProps>) {
    return (
        <>
            <label>
                {props.label}
                {props.isRequired ? <sup>*</sup> : undefined}
            </label>

            {props.children}
        </>
    )
}

type LabeledTextInputProps = BaseLabeledInputProps & {
    text: string,
    placeholder: string,
    onTextChanged: (text: string) => void,
    id?: string,
    errorText?: string
}

function LabeledTextInput(props: LabeledTextInputProps) {
    return (
        <LabeledInput {...props}>
            <input
              id={props.id}
              value={props.text}
              type="text"
              onChange={e => props.onTextChanged(e.target.value)}
              placeholder={props.placeholder}/>

            <p className="error">{props.errorText}</p>
        </LabeledInput>
    )
}

function LabeledTextAreaInput(props: LabeledTextInputProps) {
    return (
        <LabeledInput {...props}>
            <textarea
              id={props.id}
              value={props.text}
              onChange={e => props.onTextChanged(e.target.value)}
              placeholder={props.placeholder}/>

            <p className="error">{props.errorText}</p>
        </LabeledInput>
    )
} 

type LabeledDropdownInputProps<I extends string | undefined> = BaseLabeledInputProps & {
    selectedValue: I,
    onSelected: (value: I) => void,
    entries: readonly { id: I, label: string }[]
}

function LabeledDropdownInput<I extends string | undefined>(props: LabeledDropdownInputProps<I>) {
    const placeholder = useContext(StringResourcesContext).selectPlaceholder

    return (
        <LabeledInput {...props}>
            <Dropdown
              selectedValueId={props.selectedValue}
              entries={props.entries}
              onSelected={props.onSelected}
              placeholder={placeholder}/>
        </LabeledInput>
    )
}

type LabeledNumberInputProps = BaseLabeledInputProps & {
    value: number | undefined,
    onValueChanged: (value: number | undefined) => void
}

function LabeledNumberInput(props: LabeledNumberInputProps) {
    function onTextEmptyStatusChanged(isEmpty: boolean) {
        if (isEmpty) {
            props.onValueChanged(undefined)
        }
    }

    return (
        <LabeledInput {...props}>
            <NumberInput 
              className="add-product-page-number-input"
              value={props.value} 
              onChanged={props.onValueChanged} 
              onTextEmptyStatusChanged={onTextEmptyStatusChanged}/>
        </LabeledInput>
    )
}