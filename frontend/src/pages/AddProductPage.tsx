import { useContext, useState } from "react";
import { StringResourcesContext } from "../StringResourcesContext";
import { UserTypeContext, navigateToMainPageIfNotBuyerSeller, useUserType } from "../user.react";
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader";
import { CartContext, useCart } from "../cart";
import "../styles/AddProductPage.scss"
import { CategoryId, ColorId, NewProductInfo, ProductState, allCategoryIds, allColorIds, allProductStates } from "../dataModels";
import { LabeledTextInputProps, LabeledTextInput, LabeledTextAreaInput, LabeledDropdownInputProps, LabeledDropdownInput, LabeledNumberInputProps, LabeledNumberInput, LabeledFileImageLoaderView } from "../components/LabeledInputs";
import Footer from "../components/Footer";
import { DiContainerContext } from "../diContainer";
import { useNavigate } from "react-router";

export default function AddProductPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)

    const dataSource = diContainer.dataSource
    const userCreds = diContainer.userCredsStore.getCurrentUserCredentials()

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
                <h1 className="primary">{strRes.describeProduct}</h1>

                <hr/>

                <LabeledFileImageLoaderView
                  label={strRes.uploadImages}
                  maxImages={5}
                  onFilesChanged={setImageFiles}/>

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

                <br/>

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

                <br/>

                <LabeledNumberInput
                  value={amount}
                  onValueChanged={value => (value ?? 1) >= 1 ? setAmount(value) : undefined}
                  label={strRes.quantity}/>

                <LabeledNumberInput
                  value={price}
                  onValueChanged={value => (value ?? 1) >= 1 ? setPrice(value) : undefined}
                  label={strRes.price}/>

                <br/>

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