import { UserCreditials } from "../user";
import { encodeBase64 } from "../utils/base64";

export type HttpMethod = 'GET' | 'POST' | 'DELETE';

export type HttpBaseFetchInfo<M extends HttpMethod> = {
    method: M, 
    url: string, 
    creditials?: UserCreditials
    timeout?: number 
}

export type HttpBodyFetchInfo<M extends HttpMethod> = HttpBaseFetchInfo<M> & { body?: string }

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

        const creds = info.creditials
        if (creds) { 
            const encoded = encodeBase64(creds.email + ":" + creds.password)

            httpRequest.setRequestHeader("Authorization", encoded)
        }

        httpRequest.onload = () => {
            resolve(httpRequest.response)
        }
        httpRequest.onerror = () => {
            reject()
        }
        httpRequest.ontimeout = () => {
            reject();
        }

        // Async request
        httpRequest.open(info.method, info.url, true)
        httpRequest.send(info.method != 'GET' ? info.body : null);
    });
}

export async function httpFetchAsync<R>(info: HttpFetchInfo): Promise<R> {
    const rawResponse = await httpFetchRawAsync(info);

    return JSON.parse(rawResponse);
}