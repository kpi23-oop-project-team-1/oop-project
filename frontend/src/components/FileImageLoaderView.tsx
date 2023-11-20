import { ChangeEvent, useEffect, useState } from "react"
import { pushElementAndCopy } from "../utils/arrayUtils"

type FileImageLoaderViewProps = {
    maxImages: number,
    onFilesChanged: (files: File[]) => void
}

export default function FileImageLoaderView(props: FileImageLoaderViewProps) {
    const [files, setFiles] = useState<File[]>([])

    function onUpload(file: File) {
        setFiles(fs => pushElementAndCopy(fs, file))
        props.onFilesChanged(pushElementAndCopy(files, file))
    }

    return (
        <div className="file-image-loader-container">
            {files.map(f => <ImagePreview file={f}/>)}
            {
                files.length < props.maxImages ?
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
        <input type="file" accept=".jpg, .jpeg, .png" value={""} onChange={onChange}/>
    )
}

function ImagePreview(props: { file: File }) {
    return <img src={URL.createObjectURL(props.file)}/>
}