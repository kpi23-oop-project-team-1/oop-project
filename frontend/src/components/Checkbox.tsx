import { ChangeEvent, useState } from "react"
import "../styles/Checkbox.scss"

type CheckboxProps = {
    checked: boolean,
    label: string,
    onCheckedChanged: (state: boolean) => void
}

export default function Checkbox(props: CheckboxProps) {
    function onChange(e: ChangeEvent<HTMLInputElement>) {
        const newCheckedState = e.target.checked

        props.onCheckedChanged(newCheckedState)
    }

    return (
        <label className="checkbox-container">
            <input type="checkbox" checked={props.checked} onChange={onChange}/>
            <span>{props.label}</span>
        </label>
    )
}