import { clamp } from "../utils/mathUtils"
import "../styles/TabContainer.scss"

type Tab = {
    label: string,
    element: React.ReactElement
}

type TabContainerProps = {
    selectedIndex: number,
    onSelected: (index: number) => void
    tabs: Tab[],
    id?: string
}

export default function TabContainer(props: TabContainerProps) {
    const selectedIndex = clamp(props.selectedIndex, 0, props.tabs.length)

    return (
        <div className="tab-container" id={props.id}>
            <div className="tab-list-row">
                {props.tabs.map((tab, i) => 
                    <p 
                      className={i == selectedIndex ? 'tab-selected' : undefined}
                      onClick={() => props.onSelected(i)}>
                        {tab.label}
                    </p>
                )}
            </div>

            <div className="tab-content">
                {props.tabs[selectedIndex].element}
            </div>
        </div>
    )
}