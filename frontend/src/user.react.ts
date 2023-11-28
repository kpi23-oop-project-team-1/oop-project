import { createContext, useContext, useMemo } from "react";
import { UserType } from "./user";
import { DiContainerContext } from "./diContainer";
import { DeferredDataState } from "./deferredData";
import { useValueFromDataSource } from "./dataSource.react";
import { NavigateFunction } from "react-router";

export function useUserType(): DeferredDataState<UserType | undefined> {
    const diContainer = useContext(DiContainerContext)

    const creds = useMemo(() => diContainer.userCredsStore.getCurrentUserCrediatials(), [])
    const [userType] = useValueFromDataSource<UserType | undefined>(async ds => creds ? await ds.getUserType(creds) : undefined)

    return userType
}

export function navigateToMainPageIfNotBuyerSeller(userType: DeferredDataState<UserType | undefined>, navigate: NavigateFunction) {
    if (((userType.type == 'success' && userType.value != 'customer-trader') || userType.type == 'error')) {
        navigate('/')
    }
}

export const UserTypeContext = createContext<UserType | undefined>(undefined)