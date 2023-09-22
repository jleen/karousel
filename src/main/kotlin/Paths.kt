import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

class SourcePath(val path: Path) {
    private fun toTarget(): TargetPath {
        val relative = sourceRoot.relativize(path)
        val target = targetRoot.resolve(relative)
        return TargetPath(target)
    }

    fun toIndexPage(): TargetPath = TargetPath(toTarget().resolve("index.html"))
    fun toDirDir(): TargetPath = toTarget()
    fun toPhotoDir(): TargetPath = TargetPath(Path(toTarget().toString().substringBeforeLast(".")))
    fun toPhotoPage(): TargetPath = TargetPath(toPhotoDir().resolve("index.html"))
    fun toTargetPhoto(): TargetPath = TargetPath(toPhotoDir().resolve(path.name))
    fun toScaledPhoto(size: Size): TargetPath = toTargetPhoto().withSuffix(size.suffix)
    fun toDirPreview(): TargetPath = TargetPath(toTarget().resolve(".preview.jpeg"))
    override fun toString() = path.toString()
}

class TargetPath(val path: Path) : Path by path {
    override fun toString() = path.toString()

    fun withSuffix(suffix: String): TargetPath {
        val nameWithSuffix = "${path.toString().substringBeforeLast(".")}_$suffix.${path.extension}"
        return TargetPath(Path(nameWithSuffix))
    }

    fun toTitle(): String {
        val name = path.nameWithoutExtension
        return toTitle(name)
    }

    fun toCaption(): String = if (isSerial(path.nameWithoutExtension)) "" else toTitle()
}

fun toTitle(name: String) = when {
    isSerial(name) -> name.substringAfterLast("_").toInt().toString()
    isBoring(name) -> ""
    else -> name.replace("_", " ").replace(Regex("""^\d\d """), "")
}

fun isSerial(name: String): Boolean = name.matches(Regex("""\p{Alpha}+_20\d\d_\w*_\d*"""))
fun isBoring(name: String): Boolean {
    if (name.matches(Regex("""\d{4}"""))) return false  // Years are *not* boring.
    return name.matches(Regex("""\d*"""))
}
