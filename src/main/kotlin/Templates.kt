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
    val model = hashMapOf("photo" to photo.fileName)
    template.process(model, page.path.writer())
}