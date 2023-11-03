import "../styles/AdvancedInput.scss";
import ErrorOutlineIcon from '../../public/images/error_outline.svg'
import { ChangeEventHandler } from "react";

type AdvancedInputProps = {
    placeholder: string,
    inputType?: string, 
    onInput?: ChangeEventHandler<HTMLInputElement>
    errorText?: string,
    reserveSpaceForError?: boolean
}

export default function AdvancedInput(props: AdvancedInputProps) {
    return (
        <div className={"advanced-input-container" + (props.errorText ? " in-error-state" : "")}>
            <p className="advanced-input-title">{props.placeholder}</p>

            <div className="advanced-input-field-container">
                <input type={props.inputType ?? "text"} onChange={props.onInput} className="outline"></input>

                <ErrorOutlineIcon width={20} height={20} className="advanced-input-error-icon"/>
            </div>
           
            {(props.reserveSpaceForError ?? true) || (props.errorText) ? 
                <p className="advanced-input-error error">{props.errorText}</p> 
                : null
            }
        </div> 
    );
}