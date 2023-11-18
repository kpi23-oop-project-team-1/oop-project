import "../styles/ProductImageWithStripe.scss"

export type ProductImageWithStripeProps = {
    stripeText?: string
    imageSource: string
}

export default function ProductImageWithStripe(props: ProductImageWithStripeProps) {
    return (<div className="product-image-with-stripe">
        {props.stripeText ? <p className="product-image-stripe">{props.stripeText}</p> : undefined}
        
        <img src={props.imageSource}></img>
    </div>)
}