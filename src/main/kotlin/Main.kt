import java.awt.RenderingHints.KEY_INTERPOLATION
import java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.io.path.*

enum class Size(val width: Int, val height: Int, val suffix: String) {
    VIEW(700, 500, "small"),
    THUMBNAIL(200, 200, "thumbnail"),
    DIRECTORY(100, 100, "dirthumb"),
}

// TODO: This can't be the right way to do this.
var sourceRoot: String = ""
var targetRoot: String = ""

fun main(args: Array<String>) {
    sourceRoot = args[0]
    targetRoot = args[1]
    copyCss()
    traverseDirectory(SourcePath(Path(args[0])))
}

fun traverseDirectory(dir: SourcePath) {
    createTargetDirectory(dir)

    // Depth first, to create the previews and cache the photo dimensions.
    val files = dir.path.listDirectoryEntries()
    files.forEach {
        when {
            it.isDirectory() -> traverseDirectory(SourcePath(it))
            it.extension == "jpeg" -> traversePhoto(SourcePath(it))
        }
    }

    renderDirectoryPage(dir)
}

fun createTargetDirectory(dir: SourcePath) = dir.toTarget().path.createDirectories()

fun traversePhoto(photo: SourcePath) {
    renderPreview(photo)
    renderView(photo)
    renderFull(photo)

    // Do the page last, so that the images have been created and their dimensions are known.
    renderPhotoPage(photo)
}

fun renderFull(photo: SourcePath) = copyPhoto(photo)
fun renderPreview(photo: SourcePath) = resizePhoto(photo, Size.THUMBNAIL)
fun renderView(photo: SourcePath) = resizePhoto(photo, Size.VIEW)

fun renderPhotoPage(photo: SourcePath) {
    val target = TargetPath(Path(photo.toTarget().toString().substringBeforeLast(".") + ".html"))
    if (isStale(photo, target)) {
        templatePhotoPage(target, photo.toTarget())
        println("* $target")
    } else {
        println("  $target")
    }
}

fun renderDirectoryPage(dir: SourcePath) {
    val index = TargetPath(dir.toTarget().resolve("index.html"))
    if (isStale(dir, index)) {
        templateIndexPage(index, dir.toTarget())
        println("* $index")
    } else {
        println("  $index")
    }
}

fun isStale(source: SourcePath, target: TargetPath): Boolean {
    // Temporary dev hack to always regenerate HTML files.
    if (target.path.extension == "html") return true;

    val sourceLastModifiedTime = if (source.path.isDirectory())
        source.path.listDirectoryEntries().map { it.getLastModifiedTime() }.max()
    else
        source.path.getLastModifiedTime()
    return !target.path.exists() || target.path.getLastModifiedTime() < sourceLastModifiedTime
}

fun copyCss() {
    val source = SourcePath(Path("carousel.css"))
    val target = TargetPath(Path(targetRoot).resolve("carousel.css"))
    conditionallyCopy(source, target)
}

fun copyPhoto(source: SourcePath) {
    val target = source.toTarget()
    conditionallyCopy(source, target)
}

fun conditionallyCopy(source: SourcePath, target: TargetPath) {
    if (isStale(source, target)) {
        source.path.copyTo(target.path)
        println("* $target")
    } else {
        println("  $target")
    }
}

fun resizePhoto(source: SourcePath, size: Size) {
    val target = source.toTarget().withSuffix(size.suffix)
    if (isStale(source, target)) {
        val original = ImageIO.read(source.path.toFile())
        val (width, height) = size.computeScaledSize(original.width, original.height)
        val resized = BufferedImage(width, height, original.type)
        val graph = resized.createGraphics()
        graph.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR)
        graph.drawImage(original,0, 0, width, height, null)
        graph.dispose()
        ImageIO.write(resized, "jpg", target.path.toFile())
        println("* $target")
    } else {
        println("  $target")
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