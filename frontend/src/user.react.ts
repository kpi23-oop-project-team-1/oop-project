import { createContext, useContext, useMemo } from "react";
import { UserType, UserCredentials } from "./user";
import { DiContainerContext } from "./diContainer";
import { DeferredDataState } from "./deferredData";
import { useValueFromDataSource } from "./dataSource.react";
import { NavigateFunction } from "react-router";

export function useCurrentUserType(): DeferredDataState<UserType | undefined> {
    const diContainer = useContext(DiContainerContext)

    const creds = useMemo(() => diContainer.userCredsStore.getCurrentUserCredentials(), [])
    const [userType] = useValueFromDataSource<UserType | undefined>(async ds => creds ? await ds.getUserType(creds) : undefined)

    return userType
}

export function navigateToMainPageIfNotBuyerSeller(userType: DeferredDataState<UserType | undefined>, navigate: NavigateFunction) {
    if (((userType.type == 'success' && userType.value != 'customer-trader') || userType.type == 'error')) {
        navigate('/')
    }
}

export function navigateToSignInPageIfNotSignedIn(userCreds: UserCredentials | undefined, navigate: NavigateFunction) {
    if (!userCreds) {
        navigate('/signin')
    }
}

export const UserTypeContext = createContext<UserType | undefined>(undefined)