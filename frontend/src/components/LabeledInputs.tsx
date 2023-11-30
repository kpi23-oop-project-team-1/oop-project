import { useContext } from "react";
import "../styles/LabeledInputs.scss"
import { StringResourcesContext } from "../StringResourcesContext";
import { Dropdown } from "../components/Dropdown";
import NumberInput from "../components/NumberInput";
import FileImageLoaderView, { FileHolder, FileImageLoaderViewProps, FileImageLoaderViewType } from "../components/FileImageLoaderView";

type BaseLabeledInputProps = {
    label: string,
    isRequired?: boolean
}

function LabeledInput(props: React.PropsWithChildren<BaseLabeledInputProps>) {
    return (
        <div className="labeled-input-container">
            <label>
                {props.label}
                {props.isRequired ? <sup>*</sup> : undefined}
            </label>

            {props.children}
        </div>
    )
}

export type LabeledTextInputProps = BaseLabeledInputProps & {
    text: string,
    placeholder: string,
    onTextChanged: (text: string) => void,
    id?: string,
    errorText?: string
}

export function LabeledTextInput(props: LabeledTextInputProps) {
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

export function LabeledTextAreaInput(props: LabeledTextInputProps) {
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

export type LabeledDropdownInputProps<I extends string | undefined> = BaseLabeledInputProps & {
    selectedValue: I,
    onSelected: (value: I) => void,
    allIds: readonly NonNullable<I>[],
    labelMap: Record<NonNullable<I>, string>
}

export function LabeledDropdownInput<I extends string | undefined>(props: LabeledDropdownInputProps<I>) {
    const placeholder = useContext(StringResourcesContext).selectPlaceholder

    return (
        <LabeledInput {...props}>
            <Dropdown
              selectedValueId={props.selectedValue}
              onSelected={props.onSelected}
              placeholder={placeholder}
              allIds={props.allIds}
              labelMap={props.labelMap}/>
        </LabeledInput>
    )
}

export type LabeledNumberInputProps = BaseLabeledInputProps & {
    value: number | undefined,
    onValueChanged: (value: number | undefined) => void
}

export function LabeledNumberInput(props: LabeledNumberInputProps) {
    function onTextEmptyStatusChanged(isEmpty: boolean) {
        if (isEmpty) {
            props.onValueChanged(undefined)
        }
    }

    return (
        <LabeledInput {...props}>
            <NumberInput
              className="labeled-number-input"
              value={props.value}
              onChanged={props.onValueChanged}
              onTextEmptyStatusChanged={onTextEmptyStatusChanged}/>
        </LabeledInput>
    )
}

export type LabeledFileImageLoaderViewProps<T extends FileImageLoaderViewType> = 
    BaseLabeledInputProps & FileImageLoaderViewProps<T>

export function LabeledFileImageLoaderView<T extends FileImageLoaderViewType>(props: LabeledFileImageLoaderViewProps<T>) {
    return (
        <LabeledInput {...props}>
            <FileImageLoaderView {...props}/>
        </LabeledInput>
    )
}