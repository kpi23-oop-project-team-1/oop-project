import { UserCreditials } from "../user";
import { encodeBase64 } from "./base64";

export function basicAuthEncode(creds: UserCreditials): string {
    return "Basic " + encodeBase64(creds.email + ":" + creds.password)
}