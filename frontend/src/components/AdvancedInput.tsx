import "../styles/AdvancedInput.scss";
import { HTMLInputTypeAttribute } from "react";
import AdvancedInputField from "./AdvancedInputField";

export type AdvancedInputProps = {
    placeholder: string,
    inputType?: HTMLInputTypeAttribute, 
    onTextChanged?: (newText: string) => void
    errorText?: string,
    reserveSpaceForError?: boolean
}

export default function AdvancedInput(props: AdvancedInputProps) {
    return (
        <div className={"advanced-input-container" + (props.errorText ? " in-error-state" : "")}>
            <AdvancedInputField 
              placeholder={props.placeholder}
              inputType={props.inputType}
              onTextChanged={props.onTextChanged}
              showErrorIcon={props.errorText != undefined}/>
           
            {(props.reserveSpaceForError ?? true) || (props.errorText) ? 
                <p className="advanced-input-error error">{props.errorText}</p> 
                : null
            }
        </div> 
    );
}