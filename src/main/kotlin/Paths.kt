import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension

class SourcePath(val path: Path) {
    fun toTarget(): TargetPath {
        val relative = sourceRoot.relativize(path)
        val target = targetRoot.resolve(relative)
        return TargetPath(target)
    }

    fun toPhotoPagePath(): TargetPath =
        TargetPath(Path(toTarget().toString().substringBeforeLast(".") + ".html"))

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
