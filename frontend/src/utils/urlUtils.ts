import { NumberRange, numberRangeToString } from "../dataModels"

export class UrlSearchParamsBuilder<T> {
    private params: string = ""
  
    private appendKeyValue(key: string, value: string) {
        this.params += this.params.length == 0 ? '?' : '&' 
        this.params += key
        this.params += '='
        this.params += value
    }

    appendPrimitive(key: string, value: number | boolean) {
        this.appendKeyValue(key, value.toString())
    }

    appendString(key: string, value: string) {
        if (value.length > 0) {
            this.appendKeyValue(key, value)
        }
    }

    appendRange(key: string, value: NumberRange) {
        this.appendKeyValue(key, numberRangeToString(value))
    }

    appendStringArray(key: string, value: string[]) {
        if (value.length > 0) {
            this.appendKeyValue(key, value.join(';'))
        }
    }

    result(): string {
        return this.params
    }
}