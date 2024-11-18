import java.nio.file.Path
import kotlin.io.path.*

class SourcePath(val path: Path) {
    private fun toTarget() = sourceRoot.relativize(path).map(::prettify).fold(targetRoot, Path::resolve).let(::TargetPath)

    private fun prettify(path: Path) = Path(path.name.replace(Regex("""^\d\d_"""), ""))

    fun toIndexPage(): TargetPath = TargetPath(toTarget().path.resolve("index.html"))
    fun toDirDir(): TargetPath = toTarget()
    fun toPhotoDir(): TargetPath = TargetPath(Path(toTarget().path.pathString.substringBeforeLast(".")))
    fun toPhotoPage(): TargetPath = TargetPath(toPhotoDir().path.resolve("index.html"))
    fun toTargetPhoto(): TargetPath {
        val base = toTarget().path.nameWithoutExtension
        val targetBase = if (isBoring(base)) {
            val relative = targetRoot.relativize(toTarget().path)
            val year = relative.getName(0)
            val top = relative.getName(1)
            val rest = relative.relativeTo(relative.subpath(0, 2)).parent
            if (rest == null || rest.pathString.isEmpty()) "${top}_${year}_${base}"
            else {
                val restComps = rest.map(Path::pathString).joinToString("_")
                "${top}_${year}_${restComps}_${base}"
            }
        } else base
        return TargetPath(toPhotoDir().path.resolve("$targetBase.jpeg"))
    }
    fun toScaledPhoto(size: Size): TargetPath = toTargetPhoto().withSuffix(size.suffix)
    fun toDirPreview(): TargetPath = TargetPath(toTarget().path.resolve(".preview.jpeg"))
    override fun toString(): String = path.pathString
}

class TargetPath(val path: Path) {
    fun withSuffix(suffix: String): TargetPath {
        val nameWithSuffix = "${path.pathString.substringBeforeLast(".")}_$suffix.${path.extension}"
        return TargetPath(Path(nameWithSuffix))
    }

    fun toTitle(): String {
        val name = path.nameWithoutExtension
        return toTitle(name)
    }

    fun toCaption(): String = if (isBoring(path.nameWithoutExtension)) "" else toTitle()
    override fun toString(): String = path.pathString
}

fun toTitle(name: String) = when {
    isSerial(name) -> name.substringAfterLast("_")
    else -> name.replace("_", " ").replace(Regex("""^\d\d """), "")
}

fun isSerial(name: String): Boolean = name.matches(Regex("""\p{Alpha}+_20\d\d_\w*_\d*"""))
fun isBoring(name: String): Boolean {
    if (name.matches(Regex("""\d{4}"""))) return false  // Years are *not* boring.
    return name.matches(Regex("""\d*"""))
}
