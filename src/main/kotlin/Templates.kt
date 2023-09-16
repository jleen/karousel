import freemarker.template.Configuration
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.writer

val freemarkerConfig by lazy {
    val config = Configuration(Configuration.VERSION_2_3_32)
    config.defaultEncoding = "UTF-8"
    config.setDirectoryForTemplateLoading(File("."))
    config
}

object PhotoInfoCache {
    private val photoInfo: MutableMap<TargetPath, Pair<Int, Int>> = mutableMapOf()
    fun get(path: TargetPath): Pair<Int, Int> {
        return photoInfo.getOrPut(path) {
            val image = ImageIO.read(path.path.toFile())
            Pair(image.width, image.height)
        }
    }

    fun put(path: TargetPath, dims: Pair<Int, Int>) = photoInfo.put(path, dims)
}

fun templatePhotoPage(page: TargetPath, photo: TargetPath) {
    val template = freemarkerConfig.getTemplate("PhotoPage.ftl")
    val viewPath = photo.withSuffix(Size.VIEW.suffix)
    val (width, height) = PhotoInfoCache.get(TargetPath(viewPath))
    val breadcrumbs = listOf(
        hashMapOf("name" to "top crumb", "dir" to "whatever"),
        hashMapOf("name" to "next crumb", "dir" to "whatever"),
    )
    val model = hashMapOf(
        "pageTitle" to "Page Title",
        "browsePrefix" to "../../../",
        "galleryTitle" to "Carousel",
        "breadcrumbs" to breadcrumbs,
        "finalCrumb" to "12",
        "prev" to "11.html",
        "next" to "13.html",
        "fullPhotoUrl" to photo.fileName,
        "framedPhotoUrl" to viewPath.fileName,
        "caption" to "Amazing Art",
        "height" to height,
        "width" to width,
    )
    template.process(model, page.path.writer())
}

fun templateIndexPage(page: TargetPath, dir: TargetPath) {
    // TODO: For now we'll re-enumerate the directory.
    //   At some point we might want to optimize by reusing the earlier traversal.
    val template = freemarkerConfig.getTemplate("IndexPage.ftl")
    val breadcrumbs = listOf(
        hashMapOf("name" to "top crumb", "dir" to "whatever"),
        hashMapOf("name" to "next crumb", "dir" to "whatever"),
    )
    val subdirs = listOf(
        hashMapOf("dir" to "", "name" to "", "preview" to "", "height" to 0, "width" to 0),
    )
    val images = listOf(
        hashMapOf(
            "pageurl" to "", "thumburl" to "",
            "caption" to "", "height" to 0, "width" to 0),
    )
    val model = hashMapOf(
        "galleryTitle" to "Carousel",
        "browsePrefix" to "../../../",
        "thisdir" to "",
        "breadcrumbs" to breadcrumbs,
        "finalCrumb" to "",
        "subdirs" to subdirs,
        "imgurls" to images,
    )
    template.process(model, page.path.writer())
}