import java.nio.file.Path
import kotlin.io.path.Path

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
}