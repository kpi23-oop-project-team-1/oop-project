export function removeElementAndCopy<T>(array: T[], predicate: (element: T) => boolean): T[] {
    const index = array.findIndex(predicate)
    
    if (index >= 0) {
        const copiedArray = array.slice()
        copiedArray.splice(index, 1)

        return copiedArray
    }
    return array
}

export function mutateElementAndCopy<T>(array: T[], predicate: (element: T) => boolean, mutate: (element: T) => T): T[] {
    const index = array.findIndex(predicate)

    if (index >= 0) {
        const copiedArray = array.slice()
        copiedArray[index] = mutate(copiedArray[index])

        return copiedArray
    }

    return array
}