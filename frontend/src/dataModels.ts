export type AuthSuccessResult = {
    type: 'success',
    accessKey: string
}

export type FailStatusResult = { type: 'fail', reason?: string }
export type SuccessVoidStatusResult = { type: 'success' }
export type SuccessStatusValueResult<T> = SuccessVoidStatusResult & { value: T }

export type StatusValueResult<T> = SuccessStatusValueResult<T> | FailStatusResult
export type StatusVoidResult = SuccessVoidStatusResult | FailStatusResult

// Auth

export type AuthResult = { 
    accessKey: string
}

export type AuthStatusResult = StatusValueResult<AuthResult>

export type AuthCreditials = { 
    email: string,
    password: string
}

// Sign up

export type SignUpInfo = {
    firstName: string,
    lastName: string,
    telNumber: string
    email: string,
    password: string,
}

export type SignUpStatusResult = StatusVoidResult