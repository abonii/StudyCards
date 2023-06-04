package abm.co.feature.book.detailed.utils

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import androidx.core.graphics.drawable.toDrawable
import java.io.File
import java.io.InputStream
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup

fun createEpubBookWithSiegman(inputStream: InputStream): EpubBook {
    val book: Book = inputStream.use { EpubReader().readEpub(it) }
    val chapterTitles = book.tableOfContents.tocReferences.map { it.completeHref to it.title }
    val contents = book.contents.mapIndexed { index, content ->
        val document = Jsoup.parse(content.inputStream, "UTF-8", "")
//        document.select("img").forEach {
//            val absBasePath: String = File("").canonicalPath
//            val relPathEncoded = it.attr("src")
//            val absPath = File(relPathEncoded.decodedURL).canonicalPath
//                .removePrefix(absBasePath)
//                .replace("""\""", "/")
//                .removePrefix("/")
//
//            it.attr("src", absPath)
//            it.tagName("$tag$absPath")
//        }
        content to document
    }
    val chapters = contents.map { (content, body) ->
        EpubChapter(
            url = content.href,
            title = chapterTitles.find { content.href == it.first }?.second,
            body = getNodeStructuredText(body.body())
        )
    }.filter { it.body.isNotBlank() }
    val images = book.resources.all.mapNotNull { resource ->
        if (resource.mediaType.name.startsWith("image/")) {
            resource.href to resource.inputStream.use { it.readBytes() }
        } else null
    }.map { (path, image)->
        EpubImage(
            path = path,
            encodedImage = toEncodedImage(image)
        )
    }
    return EpubBook(
        fileName = book.title,
        title = "empty",
        coverImagePath = "empty for now",
        chapters = chapters,
        images = emptyList()
    )
}

internal fun toEncodedImage(image: ByteArray): String {
    val b64Image: ByteArray = Base64.encode(image, Base64.DEFAULT)
    return String(b64Image)
}

fun toDecodedImage(encodedImage: String): BitmapDrawable {
    val a = Base64.decode(encodedImage, Base64.DEFAULT)
    val bitmapDrawable = BitmapFactory.decodeByteArray(a, 0, a.size)
        .toDrawable(Resources.getSystem()).apply {
            setBounds(
                0, 0, (bitmap.width), (bitmap.height)
            )
        }
    return bitmapDrawable
}