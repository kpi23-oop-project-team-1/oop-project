import { useState } from "react"
import "../styles/MainPage.scss"
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader"

export default function MainPage() {
    let [dialogType, setDialogType] = useState<PageWithFullHeaderDialogType>()

    return (
        <PageWithSearchHeader
          dialogSwitch={undefined} 
          dialogType={dialogType} 
          onChangeDialogType={setDialogType}>

        </PageWithSearchHeader>
    )
}