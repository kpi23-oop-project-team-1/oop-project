export function pushElementAndCopy<T>(array: T[], element: T): T[] {
    const copied = array.slice()
    copied.push(element)

    return copied
}

export function removeElementAndCopy<T>(array: T[], predicate: (element: T) => boolean): T[] {
    const index = array.findIndex(predicate)
    
    if (index >= 0) {
        return removeElementAtAndCopy(array, index)
    }
    return array
}

export function removeElementAtAndCopy<T>(array: T[], index: number): T[] {
    const copiedArray = array.slice()
    copiedArray.splice(index, 1)

    return copiedArray
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

export function addOrRemoveElement<T>(array: T[], value: T, state: boolean): T[] {
    if (state) {
        return pushElementAndCopy(array, value)
    } else {
        return removeElementAndCopy(array, e => e == value)
    }
}