import React, { useContext, useState } from "react"
import Checkbox from "./Checkbox"
import RangeSlider from "./RangeSlider"
import { addOrRemoveElement } from "../utils/arrayUtils"
import { CategoryId, ColorId, NumberRange, ProductState, SearchFilter, SearchFilterDesc, allCategoryIds } from "../dataModels"
import { StringResourcesContext } from "../StringResourcesContext"
import PlusIcon from "../icons/plus_thin.svg"
import MinusIcon from "../icons/minus_thin.svg"
import "../styles/SearchFilterPanel.scss"
import { formatPriceToString } from "../utils/stringFormatting"

export type SearchFilterPanelProps = {
    filter: SearchFilter,
    filterDesc: SearchFilterDesc | undefined,
    urlFactory: (filter: SearchFilter) => string,
    onChanged: (filter: SearchFilter) => void,
    commitFilter: (filter: SearchFilter) => void
}

export default function SearchFilterPanel(props: SearchFilterPanelProps) {
    const strRes = useContext(StringResourcesContext)

    const filter = props.filter
    const filterDesc = props.filterDesc

    if (filterDesc == undefined) {
        return <div className="search-filter-panel"/>
    }

    const priceRange = filter.priceRange ?? filterDesc.limitingPriceRange
    const colorIds = filter.colorIds ?? []
    const states = filter.states ?? []

    function changeAndCommitFilter(filter: SearchFilter) {
        props.onChanged(filter)
        props.commitFilter(filter)
    }

    function onPriceRangeChanged(range: NumberRange) {
        props.onChanged({ ...filter, priceRange: range })
    }

    function onCommitPriceRange() {
        props.commitFilter({ ...filter, priceRange: priceRange })
    }

    function onColorSelectedStateChanged(id: ColorId, state: boolean) {
        changeAndCommitFilter({ ...filter, colorIds: addOrRemoveElement(colorIds, id, state)})
    }

    function onProductStateSelectedStateChanged(id: ProductState, state: boolean) {
        changeAndCommitFilter({...filter, states: addOrRemoveElement(states, id, state)})
    }

    function onCategoryLinkClick(category: CategoryId | undefined) {
        changeAndCommitFilter({ ...filter, category })
    }

    return (
        <div className="search-filter-panel">
            <SearchFilterSection title={strRes.categories}>
                <CategoryLink
                  href={props.urlFactory({ ...filter, category: undefined })}
                  onClick={() => onCategoryLinkClick(undefined)}>
                    {strRes.allProductsCategory}
                </CategoryLink>

                {allCategoryIds.map(id => 
                    <CategoryLink
                      key={id}
                      href={props.urlFactory({ ...filter, category: id })}
                      onClick={() => onCategoryLinkClick(id)}>
                        {strRes.productCategoryLabels[id]}
                    </CategoryLink>
                )}
            </SearchFilterSection>
            
            <SearchFilterSection title={strRes.filters}>
                <SearchFilterPropertyBlock title={strRes.price}>
                    <PriceSelector
                      limitingRange={filterDesc.limitingPriceRange}
                      valueRange={priceRange}
                      onPointerUp={onCommitPriceRange}
                      onRangeChanged={onPriceRangeChanged}/>
                </SearchFilterPropertyBlock>

                <SearchFilterPropertyBlock title={strRes.color}>
                    <ChoiceList
                      choices={filterDesc.availColorIds.map(id => ({ id, label: strRes.colorLabels[id] }))}
                      selectedValueIds={colorIds}
                      onChoiceSelectedStateChanged={onColorSelectedStateChanged}/>
                </SearchFilterPropertyBlock>

                <SearchFilterPropertyBlock title={strRes.productState}>
                    <ChoiceList
                      choices={filterDesc.availStates.map(id => ({ id, label: strRes.productStateLabels[id] }))}
                      selectedValueIds={states}
                      onChoiceSelectedStateChanged={onProductStateSelectedStateChanged}/>
                </SearchFilterPropertyBlock>
            </SearchFilterSection>
        </div>
    )
}

type CategoryLinkProps = React.PropsWithChildren<{
    href: string,
    onClick: (href: string) => void
}>

function CategoryLink(props: CategoryLinkProps) {
    return(
        <a 
          className="search-filter-category-link" 
          href={props.href} 
          onClick={e => {
            e.preventDefault()
            props.onClick(props.href)
          }}>
            {props.children}
        </a>
    )
}

function SearchFilterSection(props: React.PropsWithChildren<{ title: string }>) {
    return (
        <>
            <h3>{props.title}</h3>
            <hr/>
            <div>{props.children}</div>
        </>
    )
}

type SearchFilterPropertyBlockProps = React.PropsWithChildren<{
    title: string
}>

function SearchFilterPropertyBlock(props: SearchFilterPropertyBlockProps) {
    const [isExpanded, setExpanded] = useState(true)

    return (
        <div className="search-filter-property-block">
            <div className="search-filter-property-block-header">
                <p>{props.title}</p>
                <button 
                  className="search-filter-property-expand icon-button" 
                  onClick={() => setExpanded(s => !s)}>
                    {isExpanded ? <MinusIcon/> : <PlusIcon/>}
                </button>
            </div>

            <div className={"search-filter-property-content" + (isExpanded ? " expanded" : "")}>
                {isExpanded ? props.children : undefined}
            </div>
        </div>
    )
}

type Choice<K> = { id: K, label: string }

type SearchFilterChoiceListControl<K extends string> = {
    choices: Choice<K>[],
    selectedValueIds: K[]
    onChoiceSelectedStateChanged: (id: K, isSelected: boolean) => void
}

function ChoiceList<K extends string>(props: SearchFilterChoiceListControl<K>) {
    return (
        <>
            {props.choices.map((choice) => 
                <Checkbox 
                  checked={props.selectedValueIds.indexOf(choice.id) >= 0}
                  label={choice.label}
                  key={choice.id}
                  onCheckedChanged={state => props.onChoiceSelectedStateChanged(choice.id, state)}/> 
            )}
        </>
    )
}

type PriceRangeSelectorProps = {
    valueRange: NumberRange,
    limitingRange: NumberRange,
    onPointerUp: () => void,
    onRangeChanged: (range: NumberRange) => void
}

function PriceSelector(props: PriceRangeSelectorProps) {
    return (
        <div className="search-filter-price-selector">
            <RangeSlider
                valueRange={props.valueRange}
                limitingRange={props.limitingRange}
                onPointerStatusChanged={status => status == 'up' ? props.onPointerUp() : undefined}
                onRangeSelected={props.onRangeChanged}/>
            
            <p className="search-filter-price-selector-start">{formatPriceToString(props.valueRange.start)}</p>
            <p className="search-filter-price-selector-end">{formatPriceToString(props.valueRange.end)}</p>
        </div>
    )
}