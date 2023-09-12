import freemarker.template.Configuration
import java.io.File
import kotlin.io.path.writer

val freemarkerConfig by lazy {
    val config = Configuration(Configuration.VERSION_2_3_32)
    config.defaultEncoding = "UTF-8"
    config.setDirectoryForTemplateLoading(File("."))
    config
}

fun templatePhotoPage(page: TargetPath, photo: TargetPath) {
    val template = freemarkerConfig.getTemplate("PhotoPage.ftl")
    val breadcrumbs = listOf(
        hashMapOf("name" to "top crumb", "dir" to "whatever"),
        hashMapOf("name" to "next crumb", "dir" to "whatever"),
    )
    val model = hashMapOf(
        "photo" to photo.fileName,
        "pageTitle" to "Page Title",
        "browsePrefix" to "../../../",
        "galleryTitle" to "Carousel",
        "breadcrumbs" to breadcrumbs,
        "finalCrumb" to "12",
        "prev" to "11.html",
        "next" to "13.html",
        "fullPhotoUrl" to "12.png",
        "framedPhotoUrl" to "tiny12.png",
        "caption" to "Amazing Art"
        )
    template.process(model, page.path.writer())
}