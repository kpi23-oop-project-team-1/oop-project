import { UserCreditials } from "../user";
import { encodeBase64 } from "../utils/base64";
import { basicAuthEncode } from "./basicAuth";

export type HttpMethod = 'GET' | 'POST' | 'DELETE';

export type HttpBaseFetchInfo<M extends HttpMethod> = {
    method: M, 
    url: string, 
    creditials?: UserCreditials
    timeout?: number 
}

export type HttpBodyFetchInfo<M extends HttpMethod> = HttpBaseFetchInfo<M> & { 
    body?: Document | XMLHttpRequestBodyInit | null 
}

export type HttpGetFetchInfo = HttpBaseFetchInfo<'GET'>
export type HttpPostFetchInfo = HttpBodyFetchInfo<'POST'>
export type HttpDeleteFetchInfo = HttpBodyFetchInfo<'DELETE'>

export type HttpFetchInfo = HttpGetFetchInfo | HttpPostFetchInfo | HttpDeleteFetchInfo

export function httpFetchRawAsync(info: HttpFetchInfo): Promise<string> {
    return new Promise<string>((resolve, reject) => {
        const httpRequest = new XMLHttpRequest()

        if (info.timeout != undefined) {
            httpRequest.timeout = info.timeout
        }

        httpRequest.onload = () => {
            if (httpRequest.status == 200) {
                resolve(httpRequest.response)
            } else {
                reject()
            }
        }
        httpRequest.onerror = () => {
            reject()
        }
        httpRequest.ontimeout = () => {
            reject();
        }

        // Async request
        httpRequest.open(info.method, info.url, true)

        const creds = info.creditials
        if (creds) { 
            httpRequest.setRequestHeader("Authorization", basicAuthEncode(creds))
        }

        httpRequest.send(info.method != 'GET' ? info.body : null);
    });
}

export async function httpFetchAsync<R>(info: HttpFetchInfo, defaultValue?: R): Promise<R> {
    const rawResponse = await httpFetchRawAsync(info);
    if (rawResponse.length == 0) {
        if (defaultValue) {
            return defaultValue
        }

        throw "Empty response"
    }

    return JSON.parse(rawResponse);
}