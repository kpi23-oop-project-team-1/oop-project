import "../styles/NumberSpinner.scss"
import MinusIcon from "../../public/images/minus.svg"
import PlusIcon from "../../public/images/plus.svg"
import { isValidNumber } from "../utils/dataValidation"
import NumberInput from "./NumberInput"

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
    
    return (
        <div className="number-spinner-container">
            <button 
              className="number-spinner-control number-spinner-minus icon-button"
              onClick={controlButtonOnClick(-1)}
              disabled={props.value <= props.minValue}>
                <MinusIcon/>
            </button>

            <NumberInput 
              value={props.value} 
              onChanged={props.onValueChanged} 
              validateNumber={value => value >= props.minValue && value <= props.maxValue}/>

            <button 
              className="number-spinner-control number-spinner-plus icon-button"
              onClick={controlButtonOnClick(1)}
              disabled={props.value >= props.maxValue}>
                <PlusIcon/>
            </button>
        </div>
    )
}