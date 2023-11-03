import { AuthCreditials, AuthStatusResult, SignUpInfo, SignUpStatusResult } from "./dataModels";
import { httpFetchAsync } from "./utils/ajaxHttp";

export interface DataSource {
    authenticateAsync(creds: AuthCreditials): Promise<AuthStatusResult>
    signUpAsync(info: SignUpInfo): Promise<SignUpStatusResult>
}

export class ServerDataSource implements DataSource {
    readonly serverUrl: string

    constructor(serverUrl: string) {
        this.serverUrl = serverUrl
    }

    private createUrl(query: string): string {
        return `${this.serverUrl}/${query}`
    }

    async authenticateAsync(creds: AuthCreditials): Promise<AuthStatusResult> {
        const response = await httpFetchAsync<any>({ 
            method: "POST", 
            url: this.createUrl(`auth?email=${creds.email}&password=${creds.password}`)
        })

        return response as AuthStatusResult
    }

    async signUpAsync(info: SignUpInfo): Promise<SignUpStatusResult> {
        const response = await httpFetchAsync<any>({
            method: "POST",
            url: this.createUrl(`signUp?firstName=${info.firstName}`)
        })

        return response as SignUpStatusResult
    }
}