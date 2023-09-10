import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

fun main(args: Array<String>) {
    val files = Path(args[1]).listDirectoryEntries()
    files.forEach {
        when {
            it.isDirectory() -> traverse(it)
            it.endsWith(".jpeg") -> render(it)
        }
    }
}

fun render(photo: Path) {
    println("Handling photo $photo")
}

fun traverse(dir: Path) {
    println("Traversing directory $dir")
}