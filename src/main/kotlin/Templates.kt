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
        "pageTitle" to "Page Title",
        "browsePrefix" to "../../../",
        "galleryTitle" to "Carousel",
        "breadcrumbs" to breadcrumbs,
        "finalCrumb" to "12",
        "prev" to "11.html",
        "next" to "13.html",
        "fullPhotoUrl" to photo.fileName,
        "framedPhotoUrl" to photo.withSuffix(Size.VIEW.suffix).fileName,
        "caption" to "Amazing Art",
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
            "medurl" to "", "thumburl" to "", "bigurl" to "",
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