import { createContext } from "react";
import { DataSource, TemporaryDataSource, TestDataSource } from "./dataSource"
import { CookieUserCredentialsStore, TestUserCredentialsStore, UserCredentialsStore } from "./userCredsStore";

export type DiContainer = {
    dataSource: DataSource,
    userCredsStore: UserCredentialsStore
};

export const TestDiContainer: DiContainer = {
    dataSource: new TestDataSource(),
    userCredsStore: new TestUserCredentialsStore()
}

export const TemporaryDiContainer: DiContainer = {
    dataSource: new TemporaryDataSource(),
    userCredsStore: new CookieUserCredentialsStore()
}

export const DiContainerContext = createContext<DiContainer>(TestDiContainer)