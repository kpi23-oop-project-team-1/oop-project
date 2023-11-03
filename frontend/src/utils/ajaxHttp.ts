export type HttpMethod = 'GET' | 'POST' | 'DELETE';

export type HttpBaseFetchInfo<M extends HttpMethod> = { method: M, url: string, timeout?: number }
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

        httpRequest.onload = () => {
            httpRequest.responseType
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