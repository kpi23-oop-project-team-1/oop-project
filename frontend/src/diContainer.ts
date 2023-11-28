import { createContext } from "react";
import { DataSource, TemporaryDataSource, TestDataSource } from "./dataSource"
import { CookieUserCreditialsStore, TestUserCreditialsStore, UserCrediatialsStore } from "./userCredsStore";

export type DiContainer = {
    dataSource: DataSource,
    userCredsStore: UserCrediatialsStore
};

export const TestDiContainer: DiContainer = {
    dataSource: new TestDataSource(),
    userCredsStore: new TestUserCreditialsStore()
}

export const TemporaryDiContainer: DiContainer = {
    dataSource: new TemporaryDataSource(),
    userCredsStore: new CookieUserCreditialsStore()
}

export const DiContainerContext = createContext<DiContainer>(TestDiContainer)