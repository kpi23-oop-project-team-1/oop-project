import React, { useContext, useEffect, useState } from "react";
import { DeferredDataState } from "./deferredData";
import { DiContainerContext } from "./diContainer";
import { DataSource } from "./dataSource";

type DataAndSetterArray<T> = [T, React.Dispatch<React.SetStateAction<T>>]

export function useValueFromDataSource<T>(
    method: (ds: DataSource) => Promise<T>,
    ds?: DataSource,
    dependencies: React.DependencyList = []
): DataAndSetterArray<DeferredDataState<T>> {
    const dataSource = ds ?? useContext(DiContainerContext).dataSource

    const [defState, setDefState] = useState<DeferredDataState<T>>({ type: 'loading' })

    useEffect(() => {
        setDefState({ type: 'loading' })
        
        method(dataSource).then(result => {
            setDefState({ type: 'success', value: result })
        }).catch(() => {
            setDefState({ type: 'error' })
        })
    }, dependencies)

    return [defState, setDefState]
}