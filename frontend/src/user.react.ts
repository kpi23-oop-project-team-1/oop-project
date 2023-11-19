import { createContext, useContext, useMemo } from "react";
import { CurrentUserInfo, UserType } from "./user";
import { DiContainerContext } from "./diContainer";
import { DeferredDataState } from "./deferredData";
import { useValueFromDataSource } from "./dataSource.react";

export function useUserType(): DeferredDataState<UserType | undefined> {
    const diContainer = useContext(DiContainerContext)

    const creds = useMemo(() => diContainer.userCredsStore.getCurrentUserCrediatials(), [])
    const [userType] = useValueFromDataSource<UserType | undefined>(async ds => creds ? await ds.getUserType(creds) : undefined)

    return userType
}

export const UserTypeContext = createContext<UserType | undefined>(undefined)