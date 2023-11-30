import { useContext } from "react"
import { CategoryId, ColorId, ProductState, allCategoryIds, allColorIds, allProductStates } from "../dataModels"
import { LabeledDropdownInput, LabeledFileImageLoaderView, LabeledNumberInput, LabeledTextAreaInput, LabeledTextInput } from "./LabeledInputs"
import { StringResourcesContext } from "../StringResourcesContext"
import { FileHolder, FileImageLoaderViewType } from "./FileImageLoaderView"

export type AddEditProductFormInfo<T extends FileImageLoaderViewType> = {
    title: string,
    description: string,
    state: ProductState | undefined,
    category: CategoryId | undefined,
    color: ColorId | undefined,
    amount: number | undefined,
    price: number | undefined,
    imageFiles: FileHolder<T>[],
}

type AddEditProductFormProps<T extends FileImageLoaderViewType> = {
    fileImageType: T,
    formInfo: AddEditProductFormInfo<T>,
    showTextInputErrors: boolean
    isSubmitEnabled: boolean,
    actionText: string,
    onSubmit: () => void,
    onFormChanged: (value: AddEditProductFormInfo<T>) => void
}

export default function AddEditProductFormLayout<T extends FileImageLoaderViewType>(props: AddEditProductFormProps<T>) {
    const strRes = useContext(StringResourcesContext)
    const info = props.formInfo
    const showTextInputErrors = props.showTextInputErrors

    function setter<I extends keyof AddEditProductFormInfo<T>>(name: I): (value: AddEditProductFormInfo<T>[I]) => void {
        return value => {
            props.onFormChanged({ ...info, [name]: value })
        }
    }

    return (
        <>
            <LabeledFileImageLoaderView
              type={props.fileImageType}
              label={strRes.uploadImages}
              maxImages={5}
              files={info.imageFiles}
              onFilesChanged={setter('imageFiles')}/>

            <LabeledTextInput
              label={strRes.productTitle}
              text={info.title}
              placeholder={strRes.productTitlePlaceholder}
              onTextChanged={setter('title')}
              errorText={showTextInputErrors && info.title.length == 0 ? strRes.textEmptyError : undefined}/>

            <LabeledTextAreaInput
              label={strRes.description}
              text={info.description}
              placeholder={strRes.productDescriptionPlaceholder}
              onTextChanged={setter('description')}
              errorText={showTextInputErrors && info.description.length == 0 ? strRes.textEmptyError : undefined}/>

            <br/>

            <LabeledDropdownInput
              selectedValue={info.state}
              onSelected={setter('state')}
              allIds={allProductStates}
              labelMap={strRes.productStateLabels}
              label={strRes.productState}/>

            <LabeledDropdownInput
              selectedValue={info.color}
              onSelected={setter('color')}
              allIds={allColorIds}
              labelMap={strRes.colorLabels}
              label={strRes.color}/>

            <LabeledDropdownInput
              selectedValue={info.category}
              onSelected={setter('category')}
              allIds={allCategoryIds}
              labelMap={strRes.productCategoryLabels}
              label={strRes.category}/>

            <br/>

            <LabeledNumberInput
              value={info.amount}
              onValueChanged={value => !value || value >= 1 ? setter('amount')(value) : undefined}
              label={strRes.quantity}/>

            <LabeledNumberInput
              value={info.price}
              onValueChanged={value => !value || value >= 1 ? setter('price')(value) : undefined}
              label={strRes.price}/>

            <br/>

            <button
              className="primary"
              disabled={!props.isSubmitEnabled}
              onClick={props.onSubmit}>
                {props.actionText}
            </button>
        </>
    )
}