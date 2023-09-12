import java.awt.RenderingHints.KEY_INTERPOLATION
import java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.io.path.*

enum class Size(val width: Int, val height: Int, val suffix: String) {
    VIEW(500, 700, "small"),
    THUMBNAIL(200, 200, "thumbnail"),
    DIRECTORY(100, 100, "dirthumb"),
}

// TODO: This can't be the right way to do this.
var sourceRoot: String = ""
var targetRoot: String = ""

fun main(args: Array<String>) {
    sourceRoot = args[0]
    targetRoot = args[1]
    traverseDirectory(SourcePath(Path(args[0])))
}

fun traverseDirectory(dir: SourcePath) {
    println("Traversing directory $dir")
    createTargetDirectory(dir)
    renderDirectoryPage(dir)

    val files = dir.path.listDirectoryEntries()
    files.forEach {
        when {
            it.isDirectory() -> traverseDirectory(SourcePath(it))
            it.extension == "jpeg" -> traversePhoto(SourcePath(it))
        }
    }
}

fun createTargetDirectory(dir: SourcePath) = dir.toTarget().path.createDirectories()

fun traversePhoto(photo: SourcePath) {
    println("Handling photo $photo")
    renderPhotoPage(photo)
    renderPreview(photo)
    renderView(photo)
    renderFull(photo)
}

fun renderFull(photo: SourcePath) = copyPhoto(photo)
fun renderPreview(photo: SourcePath) = resizePhoto(photo, Size.THUMBNAIL)
fun renderView(photo: SourcePath) = resizePhoto(photo, Size.VIEW)

fun renderPhotoPage(photo: SourcePath) {
    println("Rendering photo page for $photo")
}

fun renderDirectoryPage(dir: SourcePath) {
    println("Rendering directory page for $dir")
}

fun isStale(source: SourcePath, target: TargetPath): Boolean =
    !target.path.exists() || target.path.getLastModifiedTime() < source.path.getLastModifiedTime()

fun copyPhoto(source: SourcePath) {
    val target = source.toTarget()
    if (isStale(source, target)) {
        println("Copying $source to $target")
        source.path.copyTo(target.path)
    } else {
        println("Skipping up-to-date $target")
    }
}

fun resizePhoto(source: SourcePath, size: Size) {
    val target = source.toTarget().withSuffix(size.suffix)
    if (isStale(source, target)) {
        println("Resizing $source to $target at ${size.height}x${size.width}")
        val original = ImageIO.read(source.path.toFile())
        val (width, height) = size.computeScaledSize(original.width, original.height)
        val resized = BufferedImage(width, height, original.type)
        val graph = resized.createGraphics()
        graph.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR)
        graph.drawImage(original,0, 0, width, height, null)
        graph.dispose()
        ImageIO.write(resized, "jpg", target.path.toFile())
    } else {
        println("Skipping up-to-date $target")
    }
}

private fun Size.computeScaledSize(width: Int, height: Int): Pair<Int, Int> {
    val shrinkWidth = width.toDouble() / this.width
    val shrinkHeight = height.toDouble() / this.height
    // Explicitly return at least one target dimension (prioritizing height)
    // to avoid embarrassing floating point off-by-one.
    if (shrinkHeight >= shrinkWidth)
        return Pair((width / shrinkWidth).toInt(), this.height)
    else
        return Pair(this.width, (height / shrinkWidth).toInt())
}
