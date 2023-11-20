import { ChangeEvent } from "react"
import AdvancedInput from "./AdvancedInput"
import AdvancedInputField from "./AdvancedInputField"

type TextInputWithErrorProps = {
    value: string,
    placeholder: string,
    onValueChanged: (value: string) => void,
    error?: string
}

export default function TextInputWithError(props: TextInputWithErrorProps) {
    return (
        <div className="text-input-with-error-container">
            <AdvancedInputField 
              placeholder={props.placeholder} 
              showErrorIcon={props.error != undefined}
              inputType="text" 
              onTextChanged={props.onValueChanged}/>

            {
                props.error ?
                <p className="error">{props.error}</p>
                : undefined}
        </div>
    )
}