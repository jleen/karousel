import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

fun main(args: Array<String>) {
    traverse(Path(args[0]))
}

fun render(photo: Path) {
    println("Handling photo $photo")
}

fun traverse(dir: Path) {
    println("Traversing directory $dir")
    val files = dir.listDirectoryEntries()
    files.forEach {
        when {
            it.isDirectory() -> traverse(it)
            it.extension == "jpeg" -> render(it)
        }
    }
}