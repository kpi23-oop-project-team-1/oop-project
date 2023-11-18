import React from "react";
import "../styles/PageNavRow.scss";

type PageNavRowProps = {
    pageCount: number,
    createLink: (index: number) => string,
    onNavigateLink: (index: number) => void 
}

export default function PageNavRow(props: PageNavRowProps) {
    return (<div className="page-nav-row">
        {(() => {
            const links: React.ReactElement[] = []
            for (let i = 1; i <= props.pageCount; i++) {
                links.push(
                    <a 
                      href={props.createLink(i)} 
                      key={i}
                      onClick={e => {
                        e.preventDefault()
                        props.onNavigateLink(i)
                      }}>
                        {i}
                    </a>
                )
            }

            return links
        })()} 
    </div>)
}