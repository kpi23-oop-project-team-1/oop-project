import React, { useCallback, useMemo } from "react";
import { TypedURLSearchParams } from "./urlUtils";
import { useSearchParams } from "react-router-dom";

type SetValue<T> = React.Dispatch<React.SetStateAction<T>>

export function useMappedSearchParams<T>(
    mapFrom: (params: TypedURLSearchParams) => T, 
    mapToParams: (value: T) => string
): [T, SetValue<T>] {
    const [searchParams, setSearchParams] = useSearchParams()

    const value = useMemo(() => mapFrom(new TypedURLSearchParams(searchParams)), [searchParams])

    const setValue: SetValue<T> = useCallback<SetValue<T>>((init) => {
        setSearchParams(
            typeof init == 'function' ? 
            (init as (value: URLSearchParams) => URLSearchParams)(searchParams) 
            : mapToParamsObject(mapToParams(init))
        )
    }, [value])

    return [value, setValue]
}

function mapToParamsObject(strParams: string): URLSearchParams {
    if (strParams.startsWith('?')) {
        strParams = strParams.substring(1)
    }

    return new URLSearchParams(strParams)
}