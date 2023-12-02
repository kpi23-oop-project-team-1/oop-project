import { createContext } from "react";
import { DataSource, ServerDataSource } from "./dataSource"
import { CookieUserCredentialsStore, UserCredentialsStore } from "./userCredsStore";

export type DiContainer = {
    dataSource: DataSource,
    userCredsStore: UserCredentialsStore
};

export const AppDiContainer: DiContainer = {
    dataSource: new ServerDataSource(),
    userCredsStore: new CookieUserCredentialsStore()
}

export const DiContainerContext = createContext<DiContainer>(AppDiContainer)