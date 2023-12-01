import { useState } from "react"
import DownArrow from "../icons/down_arrow.svg"
import "../styles/Dropdown.scss"

type DropdownProps<I extends string | undefined> = {
    selectedValueId: I,
    placeholder?: string,
    allIds: readonly NonNullable<I>[],
    labelMap: Record<NonNullable<I>, string>
    onSelected: (id: I) => void
}

export function Dropdown<I extends string | undefined>(props: DropdownProps<I>) {
    const [isExpanded, setExpanded] = useState(false)

    function onSelected(id: I) {
        props.onSelected(id)
        setExpanded(false)
    }

    return <div className="dropdown">
        <div className="dropdown-header" onClick={() => setExpanded(s => !s)}>
            <p className="dropdown-selected-value">{props.selectedValueId ? props.labelMap[props.selectedValueId] : (props.placeholder ?? "")}</p>
            <DownArrow/>
        </div>

        <div className={"dropdown-list" + (isExpanded ? " expanded" : "")}>
            {props.allIds.map(id => <p key={id} onClick={() => onSelected(id)}>{props.labelMap[id]}</p>)}
        </div>
    </div>
}