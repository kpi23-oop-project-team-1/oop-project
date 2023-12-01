import { UserCredentials } from "../user";
import { encodeBase64 } from "../utils/base64";
import { basicAuthEncode } from "./basicAuth";

export type HttpMethod = 'GET' | 'POST' | 'DELETE';

export type HttpBaseFetchInfo<M extends HttpMethod> = {
    method: M, 
    url: string, 
    credentials?: UserCredentials
    timeout?: number 
}

export type HttpBodyFetchInfo<M extends HttpMethod> = HttpBaseFetchInfo<M> & { 
    body?: Document | XMLHttpRequestBodyInit | null 
}

export type HttpGetFetchInfo = HttpBaseFetchInfo<'GET'>
export type HttpPostFetchInfo = HttpBodyFetchInfo<'POST'>
export type HttpDeleteFetchInfo = HttpBodyFetchInfo<'DELETE'>

export type HttpFetchInfo = HttpGetFetchInfo | HttpPostFetchInfo | HttpDeleteFetchInfo

export function httpFetchRawAsync(info: HttpFetchInfo): Promise<string | undefined> {
    return new Promise<string | undefined>((resolve, reject) => {
        const httpRequest = new XMLHttpRequest()

        if (info.timeout != undefined) {
            httpRequest.timeout = info.timeout
        }

        httpRequest.onload = () => {
            if (httpRequest.status == 200) {
                resolve(httpRequest.response)
            } else if (httpRequest.status == 404) {
                resolve(undefined)
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

        const creds = info.credentials
        if (creds) { 
            httpRequest.setRequestHeader("Authorization", basicAuthEncode(creds))
        }

        httpRequest.send(info.method != 'GET' ? info.body : null);
    });
}

export async function httpFetchAsync<R>(info: HttpFetchInfo): Promise<R> {
    const rawResponse = await httpFetchRawAsync(info);
    if (!rawResponse || rawResponse.length == 0) {
        throw "Empty response"
    }

    return JSON.parse(rawResponse);
}

export async function httpFetchAsyncOr<R>(info: HttpFetchInfo, defaultValue: R): Promise<R> {
    const rawResponse = await httpFetchRawAsync(info);
    if (!rawResponse || rawResponse.length == 0) {
        return defaultValue
    }

    return JSON.parse(rawResponse);
}