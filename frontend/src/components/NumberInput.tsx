import React, { ChangeEvent, useEffect, useState } from "react"
import { isValidNumber } from "../utils/dataValidation";

type NumberInputProps = 
    Omit<React.DetailedHTMLProps<React.InputHTMLAttributes<HTMLInputElement>, HTMLInputElement>, 'value'> &
    {
        value: number | undefined,
        validateNumber?: (value: number) => boolean,
        onTextEmptyStatusChanged?: (isEmpty: boolean) => void
        onChanged: (value: number) => void
    };

export default function NumberInput(props: NumberInputProps) {
    const [text, setText] = useState(props.value?.toString() ?? "")

    useEffect(() => {
        setText(props.value?.toString() ?? "")
    }, [props.value])
    
    function onChange(e: ChangeEvent<HTMLInputElement>) {
        const newText = e.target.value

        if(props.onTextEmptyStatusChanged) {
            props.onTextEmptyStatusChanged(newText.length == 0)
        }

        if (newText.length == 0) {
            setText("")
        } else if (isValidNumber(newText)) {
            const newNum = parseInt(newText)

            if (props.validateNumber ? props.validateNumber(newNum) : true) {
                setText(newText)
                props.onChanged(parseInt(newText))
            }
        }
    }
    
    return <input {...props} type="text" value={text} onChange={onChange}></input>
}