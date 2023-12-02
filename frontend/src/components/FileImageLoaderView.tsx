import { ChangeEvent } from "react"
import "../styles/FileImageLoaderView.scss"
import CloseIcon from '../icons/close.svg'
import { removeElementAtAndCopy } from "../utils/arrayUtils"

type BaseFileImageLoaderViewProps<F> = {
    maxImages: number,
    files: F[],
    onFilesChanged: (files: F[]) => void
}

export type FileImageLoaderViewType = 'only-upload' | 'upload-and-preloaded'
export type FileHolder<T extends FileImageLoaderViewType> = T extends 'only-upload' ? File : File | string 

export type FileImageLoaderViewProps<T extends FileImageLoaderViewType> = { type: T} & BaseFileImageLoaderViewProps<FileHolder<T>>

export default function FileImageLoaderView<T extends FileImageLoaderViewType>(props: FileImageLoaderViewProps<T>) {
    function onUpload(file: File) {
       props.onFilesChanged([...props.files, file])
    }

    function onRemove(index: number) {
        props.onFilesChanged(removeElementAtAndCopy(props.files, index))
    }

    return (
        <div className="file-image-loader-container">
            {props.files.map((f, i) => 
                <ImagePreviewBlock key={i} file={f} onRemove={() => onRemove(i)}/>
            )}
            {
                props.files.length < props.maxImages ?
                <ImageFileInput onUpload={onUpload}/>    
                : undefined
            }
        </div>
    )
}

type ImageFileInputProps = {
    onUpload: (f: File) => void
}

function ImageFileInput(props: ImageFileInputProps) {
    function onChange(e: ChangeEvent<HTMLInputElement>) {
        const files = e.target.files
        if (files && files.length > 0) {
            props.onUpload(files[0])
        }
    }

    // Always set value to "" to reset the input
    return (
        <input type="file" accept=".png" value={""} onChange={onChange}/>
    )
}

function ImagePreviewBlock(props: { file: string | File, onRemove: () => void }) {
    return <div className="file-image-loader-preview">
        <ImagePreview file={props.file}/>

        <button className="icon-button" onClick={props.onRemove}>
            <CloseIcon/>
        </button>
    </div>
}

function ImagePreview(props: { file: string | File }) {
    const file = props.file
    const src = typeof file == 'string' ? file : URL.createObjectURL(props.file as File)

    return <img src={src}/>
}

