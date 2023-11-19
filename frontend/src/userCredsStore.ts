import { getCookie, setCookie } from "./cookies"
import { UserCreditials } from './user';

export interface UserCrediatialsStore {
    getCurrentUserCrediatials(): UserCreditials | undefined
    saveUser(crediatials: UserCreditials): void
}

export class TestUserCreditialsStore implements UserCrediatialsStore {
    getCurrentUserCrediatials(): UserCreditials {
        return { email: "123", password: "123" }
    }

    saveUser(): void {
    }
}

const userCredsExpirationDays = 365

export class CookieUserCreditialsStore implements UserCrediatialsStore {
    getCurrentUserCrediatials(): UserCreditials | undefined {        
        const email = getCookie('email')
        const password = getCookie("password")

        if (email == undefined || password == undefined) {
            return undefined
        }

        return { email, password }
    }

    saveUser(crediatials: UserCreditials): void {
        setCookie("email", crediatials.email, userCredsExpirationDays)
        setCookie("password", crediatials.password, userCredsExpirationDays)
    }
}