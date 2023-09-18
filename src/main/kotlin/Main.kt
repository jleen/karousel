import com.twelvemonkeys.image.ResampleOp
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.FileImageOutputStream
import kotlin.io.path.*

enum class Size(val width: Int, val height: Int, val suffix: String) {
    VIEW(700, 500, "small"),
    THUMBNAIL(200, 200, "thumbnail"),
    DIRECTORY(100, 100, "dir_thumbnail"),
}

// TODO: This can't be the right way to do this.
var sourceRoot = Path("")
var targetRoot = Path("")

fun main(args: Array<String>) {
    sourceRoot = Path(args[0])
    targetRoot = Path(args[1])
    copyCss()
    traverseDirectory(SourcePath(Path(args[0])))
}

fun traverseDirectory(dir: SourcePath) {
    createTargetDirectory(dir)

    // Depth first, to create the previews and cache the photo dimensions.
    val photos = dir.path.listDirectoryEntries().sorted()
        .filter { it.extension == "jpeg" && !it.name.startsWith(".") && !it.isDirectory() }
    photos.forEachIndexed { i, photo -> traversePhoto(
            SourcePath(photo),
            prev = if (i > 0) SourcePath(photos[i - 1]) else null,
            next = if (i < photos.lastIndex) SourcePath(photos[i + 1]) else null)
    }
    val preview = dir.path.resolve(".preview.jpeg")
    if (preview.exists()) resizePhoto(SourcePath(preview), Size.DIRECTORY)
    dir.path.listDirectoryEntries().sorted().filter { it.isDirectory() }.forEach { traverseDirectory(SourcePath(it)) }

    // Now that we have done all the child directories (which includes their previews)
    // we can proceed to render the parent directory's index.
    renderDirectoryPage(dir)
}

fun createTargetDirectory(dir: SourcePath) = dir.toTarget().path.createDirectories()
fun createPhotoDirectory(photo: SourcePath) = photo.toPhotoDir().path.createDirectories()

fun traversePhoto(photo: SourcePath, prev: SourcePath?, next: SourcePath?) {
    createPhotoDirectory(photo)
    renderPreview(photo)
    renderView(photo)
    renderFull(photo)

    // Do the page last, so that the images have been created and their dimensions are known.
    renderPhotoPage(photo, prev, next)
}

fun renderFull(photo: SourcePath) = copyPhoto(photo)
fun renderPreview(photo: SourcePath) = resizePhoto(photo, Size.THUMBNAIL)
fun renderView(photo: SourcePath) = resizePhoto(photo, Size.VIEW)

fun renderPhotoPage(photo: SourcePath, prev: SourcePath?, next: SourcePath?) {
    val target = photo.toPhotoPage()
    if (isStale(photo, target)) {
        templatePhotoPage(target, photo.toTargetPhoto(), prev?.toPhotoPage(), next?.toPhotoPage())
        println("* $target")
    } else {
        println("  $target")
    }
}

fun renderDirectoryPage(dir: SourcePath) {
    val index = TargetPath(dir.toTarget().resolve("index.html"))
    if (isStale(dir, index)) {
        templateIndexPage(index, dir)
        println("* $index")
    } else {
        println("  $index")
    }
}

fun isStale(source: SourcePath, target: TargetPath): Boolean {
    // Temporary dev hack to always regenerate HTML files.
    if (target.path.extension == "html") return true

    val sourceLastModifiedTime = if (source.path.isDirectory())
        source.path.listDirectoryEntries().maxOfOrNull { it.getLastModifiedTime() }
    else
        source.path.getLastModifiedTime()
    return !target.path.exists() || target.path.getLastModifiedTime() < sourceLastModifiedTime
}

fun copyCss() {
    // TODO: Get the data stream directly from the resource, so we work in a JAR.
    val resource = TargetPath::class.java.getResource("carousel.css")!!
    val source = SourcePath(resource.toURI().toPath())
    val target = TargetPath(targetRoot.resolve("carousel.css"))
    conditionallyCopy(source, target)
}

fun copyPhoto(source: SourcePath) {
    val target = source.toTargetPhoto()
    conditionallyCopy(source, target)
}

fun conditionallyCopy(source: SourcePath, target: TargetPath) {
    if (isStale(source, target)) {
        source.path.copyTo(target.path, overwrite = true)
        println("* $target")
    } else {
        println("  $target")
    }
}

fun resizePhoto(source: SourcePath, size: Size) {
    // TODO: Refactor this!
    val target = when (size) {
        Size.DIRECTORY -> source.toTarget().withSuffix(size.suffix)
        else -> source.toTargetPhoto().withSuffix(size.suffix)
    }

    if (isStale(source, target)) {
        val original = ImageIO.read(source.path.toFile())
        val (width, height) = size.computeScaledSize(original.width, original.height)
        val lanczos = ResampleOp(width, height, ResampleOp.FILTER_LANCZOS)
        val resized = lanczos.filter(original, null)
        val jpegWriter = ImageIO.getImageWritersByFormatName("jpg").next()  // Bogus!!
        val writeParam = jpegWriter.defaultWriteParam
        writeParam.compressionMode = ImageWriteParam.MODE_EXPLICIT
        writeParam.compressionQuality = 0.95f
        jpegWriter.output = FileImageOutputStream(target.path.toFile())
        jpegWriter.write(null, IIOImage(resized, null, null), writeParam)
        jpegWriter.dispose()
        println("* $target")

        // Remember the dimensions of the original and resized images,
        // so that we can quickly emit them as part of HTML pages later.
        PhotoInfoCache.put(source.toTarget(), Pair(original.width, original.height))
        PhotoInfoCache.put(target, Pair(width, height))
    } else {
        println("  $target")
    }
}

private fun Size.computeScaledSize(width: Int, height: Int): Pair<Int, Int> {
    val shrinkWidth = width.toDouble() / this.width
    val shrinkHeight = height.toDouble() / this.height
    // Explicitly return at least one target dimension (prioritizing height)
    // to avoid embarrassing floating point off-by-one.
    return if (shrinkHeight >= shrinkWidth)
        Pair((width / shrinkHeight).toInt(), this.height)
    else
        Pair(this.width, (height / shrinkWidth).toInt())
}