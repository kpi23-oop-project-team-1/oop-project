import { useNavigate, useParams } from "react-router"
import { isValidNumber } from "../utils/dataValidation"
import { useContext, useEffect, useMemo, useState } from "react"
import { CartContext, useCart } from "../cart"
import { UserTypeContext, useCurrentUserType } from "../user.react"
import PageWithSearchHeader, { PageWithFullHeaderDialogType } from "./PageWithFullHeader"
import { useValueFromDataSource } from "../dataSource.react"
import DeferredDataContainer from "../components/DeferredDataContainer"
import Footer from "../components/Footer"
import TabContainer from "../components/TabContainer"
import { StringResourcesContext } from "../StringResourcesContext"
import { CommentInfo } from "../dataModels"
import { CommentView } from "../components/CommentView"
import "../styles/UserPage.scss"
import { DialogInfo } from "../components/Dialogs"
import PostCommentDialog from "../components/PostCommentDialog"
import { DiContainerContext } from "../diContainer"

type OwnPageDialogType = 'post-comment'
type DialogType = PageWithFullHeaderDialogType | OwnPageDialogType

export default function UserPage() {
    const userId = useParamUserId()
    const navigate = useNavigate()
    const cartAndManager = useCart()
    const currentUserType = useCurrentUserType()
    const strRes = useContext(StringResourcesContext)
    const diContainer = useContext(DiContainerContext)
    const dataSource = diContainer.dataSource
    const [dialogType, setDialogType] = useState<DialogType>()

    useEffect(() => {
        if (userId == undefined) {
            navigate("/")
        }
    }, [userId])

    const [userInfoState] = useValueFromDataSource(ds => ds.getDetailedUserInfo(userId ?? 0), [userId])
    const userInfo = userInfoState.value

    const userCreds = useMemo(() => diContainer.userCredsStore.getCurrentUserCredentials(), [])

    const [tabSelectedIndex, setTabSelectedIndex] = useState(0)

    function onWriteComment() {
        setDialogType('post-comment')
    }

    function doPostComment(info: { rating: number, text: string }) {
        if (userCreds) {
            dataSource.postUserComment({ targetId: userId ?? -1, ...info }, userCreds).then(() => {
                setDialogType(undefined)
            }).catch(() => {

            })
        }
    }

    function dialogSwitch(): DialogInfo {
        // Currently there's only 'post-comment' dialog type
        return { 
            mode: 'fullscreen',
            factory: () => <PostCommentDialog 
              onClose={() => setDialogType(undefined)} 
              onPost={doPostComment} 
              headerText={strRes.rateTraderHeader}/> 
        }
    }

    return (
        <UserTypeContext.Provider value={currentUserType.value}>
        <CartContext.Provider value={cartAndManager}>
        
        <PageWithSearchHeader
          dialogType={dialogType}
          onChangeDialogType={setDialogType}
          dialogSwitch={dialogSwitch}>
            <DeferredDataContainer state={userInfoState}>
                <div id="user-page-info-block">
                    <img id="user-page-pfp" src={userInfo?.pfpSource}/>
                    <p id="user-page-displayname">{userInfo?.displayName}</p>
                </div>

                <TabContainer
                  id="user-page-tab-container"
                  selectedIndex={tabSelectedIndex}
                  onSelected={setTabSelectedIndex}
                  tabs={[
                    {
                        label: `${strRes.comments} (${userInfo?.comments?.length ?? 0})`,
                        element: <CommentTabContent 
                          comments={userInfo?.comments ?? []} 
                          onWriteComment={onWriteComment} 
                          canComment={currentUserType.value == 'customer-trader'}/>
                    },
                    {
                        label: strRes.personAboutMe,
                        element: <DescriptionTabContent description={userInfo?.description}/>
                    }
                  ]}/>
            </DeferredDataContainer>
            
            <Footer/>
        </PageWithSearchHeader>

        </CartContext.Provider>
        </UserTypeContext.Provider>
    )
}

type CommentTabContentProps = {
    onWriteComment: () => void,
    canComment: boolean,
    comments: CommentInfo[]
}

function CommentTabContent(props: CommentTabContentProps) {
    const strRes = useContext(StringResourcesContext)

    return (
        <div id="user-page-comment-tab-content">
            { props.canComment &&
                <button id="user-page-write-comment-button" onClick={props.onWriteComment}>
                    {strRes.writeComment}
                </button>
            }

            <div id="user-page-comment-list">
                {props.comments.map(comment => 
                    <CommentView key={comment.id} comment={comment}/>
                )}
            </div>
        </div>
    )
}

type DescriptionTabContent = {
    description: string | undefined
}

export function DescriptionTabContent(props: DescriptionTabContent) {
    return (
        <div id="user-page-description-tab-content">
            <p>{props.description}</p>
        </div>
    )
}

function useParamUserId(): number | undefined {
    const params = useParams()
    const userIdStr = params.userId

    return userIdStr != undefined && isValidNumber(userIdStr) ? parseInt(userIdStr) : undefined
}