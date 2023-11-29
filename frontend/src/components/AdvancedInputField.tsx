import { HTMLInputTypeAttribute, useState } from 'react'
import ErrorOutlineIcon from '../icons/error_outline.svg'
import "../styles/AdvancedInputField.scss"

export type AdvancedInputProps = {
    id?: string,
    placeholder: string,
    inputType?: HTMLInputTypeAttribute, 
    onTextChanged?: (newText: string) => void,
    showErrorIcon?: boolean
}

export default function AdvancedInputField(props: AdvancedInputProps) {
    let [isFocused, setIsFocused] = useState(false);
    let [isEmptyText, setIsEmptyText] = useState(true);

    const isMiniPlaceholder = !isEmptyText || isFocused

    function onTextChanged(e: React.ChangeEvent<HTMLInputElement>) {
        const newText = e.target.value ?? "";

        setIsEmptyText(newText.length == 0)
        props.onTextChanged?.(newText)
    }

    function createContainerClass(): string {
        let result = "advanced-input-field-container"
        if (props.showErrorIcon) {
            result += " in-error-state"
        }

        if (isMiniPlaceholder) {
            result += " mini-placeholder"
        }

        return result
    }

    return (
        <div className={createContainerClass()} id={props.id}>
            <p className="advanced-input-field-placeholder">{props.placeholder}</p>
            <input 
              type={props.inputType} 
              onChange={onTextChanged} 
              onFocus={() => setIsFocused(true)}
              onBlur={() => setIsFocused(false)}
              className="outline"/>
              
            <ErrorOutlineIcon className="advanced-input-error-icon"/>
        </div>
    )
}