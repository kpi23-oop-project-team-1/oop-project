import "../styles/NumberSpinner.scss"
import MinusIcon from "../../public/images/minus.svg"
import PlusIcon from "../../public/images/plus.svg"
import { isValidNumber } from "../utils/dataValidation"

export type NumberSpinnerProps = {
    value: number,
    minValue: number,
    maxValue: number,
    onValueChanged: (value: number) => void
}

export default function NumberSpinner(props: NumberSpinnerProps) {
    function controlButtonOnClick(delta: number): () => void {
        return () => props.onValueChanged(props.value + delta)
    }

    function onInput(e: React.ChangeEvent<HTMLInputElement>) {
        const newText = e.target.value
        if (isValidNumber(newText)) {
            const newValue = parseInt(newText)

            if (newValue >= props.minValue && newValue <= props.maxValue) {
                props.onValueChanged(newValue)
            }
        }
    }
    
    return (
        <div className="number-spinner-container">
            <button 
              className="number-spinner-control number-spinner-minus icon-button"
              onClick={controlButtonOnClick(-1)}
              disabled={props.value <= 0}>
                <MinusIcon/>
            </button>

            <input value={props.value.toString()} onChange={onInput}/>

            <button 
              className="number-spinner-control number-spinner-plus icon-button"
              onClick={controlButtonOnClick(1)}
              disabled={props.value >= props.maxValue}>
                <PlusIcon/>
            </button>
        </div>
    )
}