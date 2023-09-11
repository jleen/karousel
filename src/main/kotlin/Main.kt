import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

val VIEW_SIZE: Int = 0
val PREVIEW_SIZE: Int = 0

fun main(args: Array<String>) {
    traverseDirectory(Path(args[0]))
}

fun traverseDirectory(dir: Path) {
    println("Traversing directory $dir")
    renderDirectoryPage(dir)

    val files = dir.listDirectoryEntries()
    files.forEach {
        when {
            it.isDirectory() -> traverseDirectory(it)
            it.extension == "jpeg" -> traversePhoto(it)
        }
    }
}

fun traversePhoto(photo: Path) {
    println("Handling photo $photo")
    renderPhotoPage(photo)
    renderPreview(photo)
    renderView(photo)
    renderFull(photo)
}

fun renderFull(photo: Path) {
    copyPhoto(photo)
}

fun renderPreview(photo: Path) {
    resizePhoto(photo, PREVIEW_SIZE)
}

fun renderView(photo: Path) {
    resizePhoto(photo, VIEW_SIZE)
}

fun renderPhotoPage(photo: Path) {
    TODO("Not yet implemented")
}

fun renderDirectoryPage(dir: Path) {
    TODO("Not yet implemented")
}

fun copyPhoto(photo: Path) {
    TODO("Not yet implemented")
}

fun resizePhoto(photo: Path, previewSize: Int) {
    TODO("Not yet implemented")
}