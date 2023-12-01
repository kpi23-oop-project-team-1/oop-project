import React, { useContext } from "react"
import { ProductStatus, UserProductSearchFilter, allProductStatuses } from "../dataModels"
import { StringResourcesContext } from "../StringResourcesContext"
import "../styles/UserSearchFilterPanel.scss"

type UserSearchFilterPanelProps = {
    filter: UserProductSearchFilter,
    onChanged: (filter: UserProductSearchFilter) => void
}

export default function UserSearchFilterPanel(props: UserSearchFilterPanelProps) {
    const filter = props.filter
    const strRes = useContext(StringResourcesContext)
    
    function onSelectedStatus(status: ProductStatus) {
        props.onChanged({ ...props.filter, status })
    }

    return (
        <div className="user-search-filter-panel">
            <Section 
              header={strRes.byStatus}
              ids={allProductStatuses}
              idLabels={strRes.productStatusLabels}
              selected={filter.status}
              onSelected={onSelectedStatus} />
        </div>
    )
}

type SectionProps<I extends string> = {
    header: string,
    ids: readonly I[],
    idLabels: Record<I, string>,
    selected: I,
    onSelected: (id: I) => void
}

function Section<I extends string>(props: SectionProps<I>) {
    return (
        <div className="user-search-filter-panel-section">
            <h2>{props.header}</h2>
            {props.ids.map(id => 
                <p 
                  className={"user-search-filter-panel-section-item" + (id == props.selected ? " selected-item" : "")}
                  onClick={() => props.onSelected(id)}>
                    {props.idLabels[id]}
                </p>    
            )}
        </div>
    )
}