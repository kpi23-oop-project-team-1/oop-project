import { useContext, useState } from "react";
import { StringResourcesContext } from "../StringResourcesContext";
import { UserTypeContext, navigateToMainPageIfNotBuyerSeller, useCurrentUserType } from "../user.react";
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader";
import { CartContext, useCart } from "../cart";
import "../styles/AddEditProductPage.scss"
import { NewProductInfo } from "../dataModels";
import Footer from "../components/Footer";
import { DiContainerContext } from "../diContainer";
import { useNavigate } from "react-router";
import AddEditProductFormLayout, { AddEditProductFormInfo } from "../components/AddEditProductFormLayout";

export default function AddProductPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)

    const dataSource = diContainer.dataSource
    const userCreds = diContainer.userCredsStore.getCurrentUserCredentials()

    const cartAndManager = useCart()
    const userType = useCurrentUserType()
    const navigate = useNavigate()

    navigateToMainPageIfNotBuyerSeller(userType, navigate)

    const defaultFormInfo: AddEditProductFormInfo<'only-upload'> = {
        title: "",
        description: "",
        state: undefined,
        category: undefined,
        color: undefined,
        amount: 150,
        price: 100,
        imageFiles: []
    }

    const [showTextInputErrors, setShowTextInputErrors] = useState(false)
    const [formInfo, setFormInfo] = useState(defaultFormInfo)

    const [isSubmitInProgress, setSubmitInProgress] = useState(false)

    const isSubmitEnabled = 
      formInfo.state != undefined && 
      formInfo.category != undefined && 
      formInfo.amount != undefined &&
      formInfo.price != undefined && 
      formInfo.imageFiles.length > 0

    function submit() {
        if (!userCreds) {
            return
        }
        
        const {title, description, state, category, amount, price, color, imageFiles} = formInfo
        if (title.length == 0 || description.length == 0 || !state || !category || !amount || !price || !color) {
            setShowTextInputErrors(true)
            return
        }

        const product: NewProductInfo = {
            title, description, state, category, color, price,
            totalAmount: amount,
            images: imageFiles
        }

        setSubmitInProgress(true)
        dataSource.addProduct(product, userCreds).then(() => {
            navigate('/myproducts')
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
            <div id="add-edit-product-page-content">
                <h1 className="primary">{strRes.describeProduct}</h1>
                <hr/>

                <AddEditProductFormLayout
                  fileImageType="only-upload"
                  formInfo={formInfo}
                  onFormChanged={setFormInfo}
                  isSubmitEnabled={isSubmitEnabled && !isSubmitInProgress}
                  showTextInputErrors={showTextInputErrors}
                  onSubmit={submit}
                  actionText={strRes.addProduct}/>
            </div>

            <Footer/>
        </PageWithSearchHeader>

        </UserTypeContext.Provider>
        </CartContext.Provider>
    )
}