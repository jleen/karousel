import freemarker.template.Configuration
import javax.imageio.ImageIO
import kotlin.io.path.*

val freemarkerConfig by lazy {
    val config = Configuration(Configuration.VERSION_2_3_32)
    config.defaultEncoding = "UTF-8"
    config.setClassForTemplateLoading(TargetPath::class.java, "templates")
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

private const val SITE_NAME = "Carousel"

fun templatePhotoPage(page: TargetPath, photo: TargetPath, prev: TargetPath?, next: TargetPath?) {
    // TODO: All of the path handling needs to convert target paths to URLs.
    val template = freemarkerConfig.getTemplate("PhotoPage.ftl")
    val viewPath = photo.withSuffix(Size.VIEW.suffix)
    val (width, height) = PhotoInfoCache.get(TargetPath(viewPath))
    val breadcrumbPath = targetRoot.relativize(page.parent.parent)
    val crumbComponents = listOf(SITE_NAME) + breadcrumbPath.map { it.name }
    val numCrumbs = crumbComponents.size
    val breadcrumbs = crumbComponents.mapIndexed { i, comp ->
        val dots = "../".repeat(numCrumbs - i)
        BreadcrumbModel(toTitle(comp), dots)
    }
    val dirPath = TargetPath(page.parent)
    val model = PhotoPageModel(
        pageTitle = dirPath.toTitle(),
        browsePrefix = page.parent.relativize(targetRoot).toString().replace('\\', '/'),
        galleryTitle = SITE_NAME,
        breadcrumbs = breadcrumbs,
        finalCrumb = dirPath.toTitle(),
        prev = prev?.let { page.parent.relativize(it.path).toString() },
        next = next?.let { page.parent.relativize(it.path).toString() },
        fullPhotoUrl = photo.fileName.name,
        framedPhotoUrl = viewPath.fileName.name,
        caption = dirPath.toCaption(),
        height = height.toString(),
        width = width.toString()
    )
    template.process(model, page.path.writer())
}

fun templateIndexPage(page: TargetPath, dir: SourcePath) {
    // TODO: For now we'll re-enumerate the directory.
    //   At some point we might want to optimize by reusing the earlier traversal.
    val template = freemarkerConfig.getTemplate("IndexPage.ftl")
    val breadcrumbPath = targetRoot.relativize(page.parent.parent)
    val crumbComponents = when (breadcrumbPath.toString()) {
        ".." -> listOf()
        "" -> listOf(SITE_NAME)
        else -> listOf(SITE_NAME) + breadcrumbPath.map { it.name }
    }
    val numCrumbs = crumbComponents.size
    val breadcrumbs = crumbComponents.mapIndexed { i, comp ->
        val dots = "../".repeat(numCrumbs - i)
        BreadcrumbModel(toTitle(comp), dots)
    }
    val subDirs = dir.path.listDirectoryEntries().filter { it.isDirectory() }.sorted().map {
        val dirThumb = SourcePath(it.resolve(".preview.jpeg")).toTarget().withSuffix(Size.DIRECTORY.suffix)
        val (width, height) = PhotoInfoCache.get(dirThumb)
        SubDirModel(dir = it.name, name = TargetPath(it).toCaption(),
            preview = page.path.parent.relativize(dirThumb.path).toString().replace('\\', '/'),
            height = height, width = width)
    }
    val images = dir.path.listDirectoryEntries()
        .filter { !it.isDirectory() && !it.name.startsWith(".") }.sorted()
        .map {
            val thumbnail = SourcePath(it).toTargetPhoto().withSuffix(Size.THUMBNAIL.suffix)
            val (width, height) = PhotoInfoCache.get(thumbnail)
            ImageModel(
                pageUrl = SourcePath(it).toPhotoDir().name + "/index.html",
                thumbUrl = SourcePath(it).toPhotoDir().name + "/" + thumbnail.name,
                caption = TargetPath(it).toCaption(),
                height = height,
                width = width
            )
    }
    val browsePrefix = page.parent.relativize(targetRoot).toString().replace('\\', '/')
    val model = IndexPageModel(
        galleryTitle = SITE_NAME,
        browsePrefix = if (browsePrefix.isNotEmpty()) "$browsePrefix/" else "",
        thisDir = page.toTitle(),
        breadcrumbs = breadcrumbs,
        finalCrumb = if (breadcrumbs.isEmpty()) SITE_NAME else TargetPath(page.parent).toTitle(),
        subDirs = subDirs,
        imgUrls = images,
    )
    template.process(model, page.path.writer())
}