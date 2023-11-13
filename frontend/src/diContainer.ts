import { createContext } from "react";
import { DataSource, TestDataSource } from "./dataSource"

export type DiContainer = {
    dataSource: DataSource
};

export const TestDiContainer: DiContainer = {
    dataSource: new TestDataSource()
}

export const DiContainerContext = createContext<DiContainer>(TestDiContainer)