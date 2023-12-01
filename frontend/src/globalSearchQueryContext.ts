import { createContext } from "react"

type SetSearchQuery = React.Dispatch<React.SetStateAction<string>>

export const GlobalSearchQueryContext = createContext<string | undefined>(undefined)