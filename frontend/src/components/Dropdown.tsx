import { useState } from "react"
import DownArrow from "../../public/images/down_arrow.svg"
import "../styles/Dropdown.scss"

type DropdownProps<I extends string | undefined> = {
    entries: readonly { id: I, label: string }[],
    selectedValueId: I,
    placeholder?: string,
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
            <p className="dropdown-selected-value">{props.entries.find(e => e.id == props.selectedValueId)?.label ?? (props.placeholder ?? "")}</p>
            <DownArrow/>
        </div>

        <div className={"dropdown-list" + (isExpanded ? " expanded" : "")}>
            {props.entries.map(e => 
                <p key={e.id} onClick={() => onSelected(e.id)}>{e.label}</p>
            )}
        </div>
    </div>
}