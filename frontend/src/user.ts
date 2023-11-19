
export type UserType = 'buyer-seller' | 'admin'
export type UserCreditials = {
    email: string,
    password: string
}

export type ConciseUserInfo = {
    id: number,
    displayName: string,
}

export type CurrentUserInfo = {
    type: UserType
}