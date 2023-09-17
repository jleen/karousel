import freemarker.template.Configuration
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.name
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

class BreadcrumbModel(
    val name: String,
    val dir: String,
)

class PhotoPageModel(
    val pageTitle: String,
    val browsePrefix: String,
    val galleryTitle: String,
    val breadcrumbs: List<BreadcrumbModel>,
    val finalCrumb: String,
    val prev: String?,
    val next: String?,
    val fullPhotoUrl: String,
    val framedPhotoUrl: String,
    val caption: String,
    val height: String,
    val width: String,
)

class IndexPageModel(
    val galleryTitle: String,
    val browsePrefix: String,
    val thisDir: String,
    val breadcrumbs: List<BreadcrumbModel>,
    val finalCrumb: String,
    val subDirs: List<SubDirModel>,
    val imgUrls: List<ImageModel>,
)

class SubDirModel(
    val dir: String,
    val name: String,
    val preview: String,
    val height: Number,
    val width: Number,
)

class ImageModel(
    val pageUrl: String,
    val thumbUrl: String,
    val caption: String,
    val height: Number,
    val width: Number,
)

fun templatePhotoPage(page: TargetPath, photo: TargetPath, prev: TargetPath?, next: TargetPath?) {
    val template = freemarkerConfig.getTemplate("PhotoPage.ftl")
    val viewPath = photo.withSuffix(Size.VIEW.suffix)
    val (width, height) = PhotoInfoCache.get(TargetPath(viewPath))
    val breadcrumbs = listOf(
        BreadcrumbModel("top crumb", "whatever"),
        BreadcrumbModel("top crumb", "whatever"),
    )
    val model = PhotoPageModel(
        pageTitle = page.toTitle(),
        browsePrefix = page.parent.relativize(Path(targetRoot)).toString(),
        galleryTitle = "Carousel",
        breadcrumbs = breadcrumbs,
        finalCrumb = page.toTitle(),
        prev = prev?.let { page.parent.relativize(it.path).toString() },
        next = next?.let { page.parent.relativize(it.path).toString() },
        fullPhotoUrl = photo.fileName.name,
        framedPhotoUrl = viewPath.fileName.name,
        caption = page.toCaption(),
        height = height.toString(),
        width = width.toString()
    )
    template.process(model, page.path.writer())
}

fun templateIndexPage(page: TargetPath, dir: TargetPath) {
    // TODO: For now we'll re-enumerate the directory.
    //   At some point we might want to optimize by reusing the earlier traversal.
    val template = freemarkerConfig.getTemplate("IndexPage.ftl")
    val breadcrumbs = listOf(
        BreadcrumbModel("top crumb", "whatever"),
        BreadcrumbModel("next crumb", "whatever"),
    )
    val subDirs = listOf(
        SubDirModel("", "", "", 0, 0)
    )
    val images = listOf(
        ImageModel("", "", "", 0, 0)
    )
    val model = IndexPageModel(
        galleryTitle = "Carousel",
        browsePrefix = "../../../",
        thisDir = "",
        breadcrumbs = breadcrumbs,
        finalCrumb = "",
        subDirs = subDirs,
        imgUrls = images,
    )
    template.process(model, page.path.writer())
}