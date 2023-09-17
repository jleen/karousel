import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension

class SourcePath(val path: Path) {
    fun toTarget(): TargetPath {
        val relative = Path(sourceRoot).relativize(path)
        val target = Path(targetRoot).resolve(relative)
        return TargetPath(target)
    }

    override fun toString() = path.toString()
}

class TargetPath(val path: Path) : Path by path {
    fun toSource(): SourcePath {
        return SourcePath(path)
    }

    override fun toString() = path.toString()

    fun withSuffix(suffix: String): TargetPath {
        val nameWithSuffix = "${path.toString().substringBeforeLast(".")}_$suffix.${path.extension}"
        return TargetPath(Path(nameWithSuffix))
    }

    fun toTitle(): String {
        val name = path.nameWithoutExtension
        return when {
            isSerial(name) -> name.substringAfterLast("_").toInt().toString()
            isBoring(name) -> ""
            else -> name.replace("_", " ")
        }
    }

    private fun isSerial(name: String): Boolean = name.matches(Regex("""\p{Alpha}+_20\d\d_\w*_\d*"""))
    private fun isBoring(name: String): Boolean = true
    fun toCaption(): String = if (isSerial(path.nameWithoutExtension)) "" else toTitle()
}