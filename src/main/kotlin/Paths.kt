import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.pathString

class SourcePath(val path: Path) {
    private fun toTarget(): TargetPath {
        val relative = sourceRoot.relativize(path)
        val target = relative.map(::prettify).fold(targetRoot, Path::resolve)
        return TargetPath(target)
    }

    private fun prettify(path: Path) = path

    fun toIndexPage(): TargetPath = TargetPath(toTarget().resolve("index.html"))
    fun toDirDir(): TargetPath = toTarget()
    fun toPhotoDir(): TargetPath = TargetPath(Path(toTarget().pathString.substringBeforeLast(".")))
    fun toPhotoPage(): TargetPath = TargetPath(toPhotoDir().resolve("index.html"))
    fun toTargetPhoto(): TargetPath = TargetPath(toPhotoDir().resolve(path.name))
    fun toScaledPhoto(size: Size): TargetPath = toTargetPhoto().withSuffix(size.suffix)
    fun toDirPreview(): TargetPath = TargetPath(toTarget().resolve(".preview.jpeg"))
    override fun toString(): String = path.pathString
}

class TargetPath(val path: Path) : Path by path {
    fun withSuffix(suffix: String): TargetPath {
        val nameWithSuffix = "${path.pathString.substringBeforeLast(".")}_$suffix.${path.extension}"
        return TargetPath(Path(nameWithSuffix))
    }

    fun toTitle(): String {
        val name = path.nameWithoutExtension
        return toTitle(name)
    }

    fun toCaption(): String = if (isSerial(path.nameWithoutExtension)) "" else toTitle()
    override fun toString(): String = path.pathString
}

fun toTitle(name: String) = when {
    isSerial(name) -> name.substringAfterLast("_")
    isBoring(name) -> ""
    else -> name.replace("_", " ").replace(Regex("""^\d\d """), "")
}

fun isSerial(name: String): Boolean = name.matches(Regex("""\p{Alpha}+_20\d\d_\w*_\d*"""))
fun isBoring(name: String): Boolean {
    if (name.matches(Regex("""\d{4}"""))) return false  // Years are *not* boring.
    return name.matches(Regex("""\d*"""))
}
