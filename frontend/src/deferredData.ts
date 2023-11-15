export type DeferredDataState<T> = {
    type: 'success'
    value: T
} | {
    type: 'error' | 'loading',
    value?: undefined
}

export function mutateStateIfSuccess<T>(state: DeferredDataState<T>, fn: (value: T) => T): DeferredDataState<T> {
    if (state.type == 'success') {
        return { type: 'success', value: fn(state.value) }
    }

    return state
}