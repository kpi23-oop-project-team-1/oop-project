import { deleteCookie, getCookie, setCookie } from "./cookies"
import { UserCredentials } from './user';

export interface UserCredentialsStore {
    getCurrentUserCredentials(): UserCredentials | undefined
    saveUser(credentials: UserCredentials): void
    updatePassword(value: string): void
    deleteCredentials(): void
}

export class TestUserCredentialsStore implements UserCredentialsStore {
    getCurrentUserCredentials(): UserCredentials {
        return { email: "test@email.com", password: "123" }
    }

    saveUser(): void {
    }

    updatePassword(value: string): void {
    }

    deleteCredentials(): void {
    }
}

const userCredsExpirationDays = 365

export class CookieUserCredentialsStore implements UserCredentialsStore {
    getCurrentUserCredentials(): UserCredentials | undefined {
        const email = getCookie('email')
        const password = getCookie("password")

        if (email == undefined || password == undefined) {
            return undefined
        }

        return { email, password }
    }

    saveUser(credentials: UserCredentials): void {
        setCookie("email", credentials.email, userCredsExpirationDays)
        setCookie("password", credentials.password, userCredsExpirationDays)
    }

    updatePassword(value: string): void {
        setCookie("password", value, userCredsExpirationDays)
    }

    deleteCredentials(): void {
        deleteCookie("email")
        deleteCookie("password")
    }
}