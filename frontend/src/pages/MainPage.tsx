import { useState } from "react"
import FullHeader from "../components/FullHeader"
import ModalDialogHolder, { ModalDialogType } from "../components/ModalDialogHolder"
import "../styles/MainPage.scss"

type MainPageDialogType = 'search' | 'none'

export default function MainPage() {
    let [modalDialogType, setModalDialogType] = useState<ModalDialogType>()
    let [activeDialogType, setActiveDialogType] = useState<MainPageDialogType>('none')

    return (
        <ModalDialogHolder type={modalDialogType} active={activeDialogType != 'none'}>
            <FullHeader/>
        </ModalDialogHolder>
    )
}