import { useContext } from "react";
import "../styles/LabeledInputs.scss"
import { StringResourcesContext } from "../StringResourcesContext";
import { Dropdown } from "../components/Dropdown";
import NumberInput from "../components/NumberInput";
import FileImageLoaderView from "../components/FileImageLoaderView";

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
    entries: readonly { id: I, label: string }[]
}

export function LabeledDropdownInput<I extends string | undefined>(props: LabeledDropdownInputProps<I>) {
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

export type LabeledFileImageLoaderViewProps = BaseLabeledInputProps & {
    maxImages: number,
    onFilesChanged: (files: File[]) => void
}

export function LabeledFileImageLoaderView(props: LabeledFileImageLoaderViewProps) {
    return (
        <LabeledInput {...props}>
            <FileImageLoaderView maxImages={props.maxImages} onFilesChanged={props.onFilesChanged}/>
        </LabeledInput>
    )
}