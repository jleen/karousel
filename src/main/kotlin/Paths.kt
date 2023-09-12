import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension
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
        val nameWithSuffix = "${path.nameWithoutExtension}_$suffix.${path.extension}"
        return TargetPath(Path(nameWithSuffix))
    }
}