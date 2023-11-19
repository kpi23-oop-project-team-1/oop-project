import { createContext } from "react";
import { DataSource, TestDataSource } from "./dataSource"
import { TestUserCreditialsStore, UserCrediatialsStore } from "./userCredsStore";

export type DiContainer = {
    dataSource: DataSource,
    userCredsStore: UserCrediatialsStore
};

export const TestDiContainer: DiContainer = {
    dataSource: new TestDataSource(),
    userCredsStore: new TestUserCreditialsStore()
}

export const DiContainerContext = createContext<DiContainer>(TestDiContainer)