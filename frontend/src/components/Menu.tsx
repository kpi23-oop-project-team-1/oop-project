import "../styles/Menu.scss"

export default function Menu(props: React.PropsWithChildren) {
    return (
        <div className="menu">
            {props.children}
        </div>
    )
}

export type MenuItemProps = {
    icon?: React.ReactElement,
    className?: string
    text: string,
    onClick: () => void
}

export function MenuItem(props: MenuItemProps) {
    return (
        <button className="menu-item" onClick={props.onClick}>
            {props.icon}
            {props.text}
        </button>
    )
}