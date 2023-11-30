import { useContext, useEffect, useState } from "react";
import { StringResourcesContext } from "../StringResourcesContext";
import { UserTypeContext, navigateToMainPageIfNotBuyerSeller, useUserType } from "../user.react";
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader";
import { CartContext, useCart } from "../cart";
import "../styles/AddEditProductPage.scss"
import { UpdateProductInfo } from "../dataModels";
import Footer from "../components/Footer";
import { DiContainerContext } from "../diContainer";
import { useNavigate, useParams } from "react-router";
import AddEditProductFormLayout, { AddEditProductFormInfo } from "../components/AddEditProductFormLayout";
import { useValueFromDataSource } from "../dataSource.react";
import DeferredDataContainer from "../components/DeferredDataContainer";
import { isValidNumber } from "../utils/dataValidation";

export default function EditProductPage() {
    const [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)

    const dataSource = diContainer.dataSource
    const userCreds = diContainer.userCredsStore.getCurrentUserCredentials()

    const cartAndManager = useCart()
    const userType = useUserType()
    const navigate = useNavigate()
    const productId = useProductId()

    useEffect(() => {
        if (productId == undefined) {
            navigate("/")
        }
    }, [productId])

    navigateToMainPageIfNotBuyerSeller(userType, navigate)

    const [showTextInputErrors, setShowTextInputErrors] = useState(false)
    const [formInfo, setFormInfo] = useState<AddEditProductFormInfo<'upload-and-preloaded'>>()

    const [productInfoState] = useValueFromDataSource(ds => ds.getProductInfo(productId ?? 0), [productId])

    useEffect(() => {
        if (productInfoState.type == 'success') {
            const productInfo =  productInfoState.value

            setFormInfo({ ...productInfo, amount: productInfo.totalAmount, imageFiles: productInfo.imageSources })
        }
    }, [productInfoState])

    const [isSubmitInProgress, setSubmitInProgress] = useState(false)

    const isSubmitEnabled = 
      formInfo != undefined &&
      formInfo.state != undefined && 
      formInfo.category != undefined && 
      formInfo.amount != undefined &&
      formInfo.price != undefined && 
      formInfo.imageFiles.length > 0

    function submit() {
        if (!userCreds || !formInfo || !productId) {
            return
        }
        
        const {title, description, state, category, amount, price, color, imageFiles} = formInfo
        if (title.length == 0 || description.length == 0 || !state || !category || !amount || !price || !color) {
            setShowTextInputErrors(true)
            return
        }

        let resolvedImageFiles = imageFiles.slice() as (File | undefined)[]
        for (let i = 0; i < imageFiles.length; i++) {
            const elem = resolvedImageFiles[i]
            if (typeof elem == 'string') {
                resolvedImageFiles[i] = undefined
            }
        }

        const product: UpdateProductInfo = {
            id: productId,
            title, description, state, category, color, price,
            totalAmount: amount,
            images: resolvedImageFiles
        }

        setSubmitInProgress(true)
        dataSource.updateProduct(product, userCreds).then(() => {
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

                <DeferredDataContainer state={productInfoState}>
                    {formInfo && 
                        <AddEditProductFormLayout
                          fileImageType="upload-and-preloaded"
                          formInfo={formInfo}
                          onFormChanged={setFormInfo}
                          isSubmitEnabled={isSubmitEnabled && !isSubmitInProgress}
                          showTextInputErrors={showTextInputErrors}
                          onSubmit={submit}
                          actionText={strRes.editProduct}/>
                    }
                </DeferredDataContainer>
                
                
            </div>
                

            <Footer/>
        </PageWithSearchHeader>

        </UserTypeContext.Provider>
        </CartContext.Provider>
    )
}

function useProductId(): number | undefined {
    const params = useParams()
    const productIdStr = params.productId

    return productIdStr != undefined && isValidNumber(productIdStr) ? parseInt(productIdStr) : undefined
}