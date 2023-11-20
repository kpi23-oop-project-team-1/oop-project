import { CategoryId, ColorId, ProductStatus, SearchOrder } from "./dataModels"

interface StringResources {
    readonly email: string,
    readonly password: string,
    readonly signUp: string,
    readonly signIn: string,
    readonly newToWebsite: string,
    readonly invalidEmail: string,
    readonly invalidEmailOrPassword: string,
    readonly invalidTelNumber: string,
    readonly telephoneNumber: string,
    readonly personFirstName: string,
    readonly personLastName: string,
    readonly textEmptyError: string,
    readonly headerSearchBoxPlaceholder: string,
    readonly search: string,
    readonly specialProducts: string,
    readonly addToCart: string,
    readonly aboutUs: string,
    readonly aboutShopifyDetail: string,
    readonly copyright: string,
    readonly faq: string,
    readonly userAgreement: string,
    readonly cart: string,
    readonly remove: string,
    readonly checkout: string,
    readonly filters: string,
    readonly category: string,
    readonly categories: string,
    readonly price: string,
    readonly color: string,
    readonly colorLabels: Record<ColorId, string>,
    readonly productStatus: string,
    readonly productStatusLabels: Record<ProductStatus, string>,
    readonly orderBy: string,
    readonly searchOrderLabels: Record<SearchOrder, string>,
    readonly productCountLabel: string,
    readonly allProductsCategory: string,
    readonly productCategoryLabels: Record<CategoryId, string>,
    readonly description: string,
    readonly characteristics: string,
    readonly productComments: string,
    readonly writeComment: string,
    readonly quantity: string,
    readonly productAlreadyInCart: string,
    readonly uploadImages: string,
    readonly describeProduct: string,
    readonly productTitle: string,
    readonly productTitlePlaceholder: string
    readonly productDescriptionPlaceholder: string,
    readonly selectPlaceholder: string,
    readonly addProduct: string
}

export const ukrainianStringResources : StringResources = {
    email: "Email",
    password: "Password",
    signUp: "Реєстарація",
    signIn: "Вхід",
    newToWebsite: "New to website?",
    invalidEmail: "Введіть правильний e-mail",
    invalidEmailOrPassword: "Неправильний email або пароль",
    invalidTelNumber: "Неправильний номер телефону",
    telephoneNumber: "Номер телефону",
    personFirstName: "Ім'я",
    personLastName: "Прізвище",
    textEmptyError: "Поле не має бути пустим",
    headerSearchBoxPlaceholder: "Я шукаю",
    search: "Пошук",
    specialProducts: "Спеціальні пропозиції",
    addToCart: "Додати у кошик",
    aboutUs: "Про нас",
    aboutShopifyDetail: "Ми заснували Shopify з однією метою: надавати високоякісні, ретельно розроблені продукти, створені для кожного. Наша пристрасть до досконалості рухала нами з самого початку і продовжує рухати нас вперед. Ми знаємо, що кожний товар має значення, і прагнемо зробити Ваші покупки максимально корисними. Не погоджуйтесь ні на що, окрім найкращого",
    copyright: "©2023 від Shopify",
    userAgreement: "Угода користувача",
    faq: "FAQ",
    cart: "Кошик",
    remove: "Видалити",
    checkout: "Оформити замовлення",
    filters: "Фільтри",
    category: "Категорія",
    categories: "Категорії",
    price: "Ціна",
    color: "Колір",
    colorLabels: {
        black: "Чорний",
        white: "Білий",
        cyan: "Голубий",
        red: "Червоний",
        yellow: "Жовтий",
        multicolor: "Різнокольоровий",
    },
    productStatus: "Стан",
    productStatusLabels: {
        acceptable: "Задовільний",
        good: "Хороший",
        "very-good": "Дуже хороший",
        ideal: "Ідеальний",
        new: "Новий",
    },
    orderBy: "Сортувати за",
    searchOrderLabels: {
        "recomended": "Рекомендовані",
        "most-expensive": "Найдорожчі",
        "cheapest": "Найдешевші"
    },
    productCountLabel: "Товарів: ",
    allProductsCategory: "Усі товари",
    productCategoryLabels: {
        dress: "Одяг"
    },
    description: "Опис",
    characteristics: "Характеристики",
    productComments: "Відгуки покупців",
    writeComment: "Написати відгук",
    quantity: "Кількість",
    productAlreadyInCart: "Вже в корзині",
    uploadImages: "Завантажте фото",
    describeProduct: "Опишіть вашу річ",
    productTitle: "Назва",
    productTitlePlaceholder: "Наприклад: Супер плаття в горошок Zara",
    productDescriptionPlaceholder: "Приклад: в цій блузці ви будете виглядати як зірка",
    selectPlaceholder: "Виберіть",
    addProduct: "Додати річ"
}