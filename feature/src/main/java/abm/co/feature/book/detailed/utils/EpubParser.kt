package abm.co.feature.book.detailed.utils

import abm.co.domain.model.library.BookEntity
import abm.co.domain.model.library.ChapterEntity
import abm.co.domain.model.library.ImageEntity
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.File
import java.io.InputStream
import java.net.URLDecoder
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory

val String.decodedURL: String get() = URLDecoder.decode(this, "UTF-8")
data class EpubChapter(val url: String, val title: String?, val body: String)
data class EpubImage(val path: String, val encodedImage: String)
data class EpubBook(
    val fileName: String,
    val title: String,
    val coverImagePath: String,
    val chapters: List<EpubChapter>,
    val images: List<EpubImage>
)

fun EpubBook.toBook(): BookEntity {
    return BookEntity(
        url = fileName,
        title = title,
        chaptersName = chapters.map { it.url },
        coverImageUrl = coverImagePath,
        imagesPath = images.map { it.path }
    )
}

fun EpubChapter.toChapter(bookUrl: String): ChapterEntity {
    return ChapterEntity(
        url = url,
        title = title,
        body = body,
        bookUrl = bookUrl
    )
}

fun EpubImage.toEncodedImage(bookUrl: String): ImageEntity {
    return ImageEntity(
        path = this.path,
        bookUrl = bookUrl,
        encodedImage = this.encodedImage
    )
}

private fun getPTraverse(node: org.jsoup.nodes.Node): String {
    fun innerTraverse(node: org.jsoup.nodes.Node): String =
        node.childNodes().joinToString("") { child ->
            when {
                child.nodeName() == "br" -> "\n"
                child.nodeName() == "img" -> "" // todo
                child is TextNode -> child.text()
                else -> innerTraverse(child)
            }
        }

    val paragraph = innerTraverse(node).trim()
    return if (paragraph.isEmpty()) "" else innerTraverse(node).trim() + "\n\n"
}

private fun getNodeTextTraverse(node: org.jsoup.nodes.Node): String {
    val children = node.childNodes()
    if (children.isEmpty())
        return ""

    return children.joinToString("") { child ->
        when {
            child.nodeName() == "p" -> getPTraverse(child)
            child.nodeName() == "br" -> "\n"
            child.nodeName() == "hr" -> "\n"
            child.nodeName() == "img" -> "" // todo
            child is TextNode -> {
                val text = child.text().trim()
                if (text.isEmpty()) "" else text + "\n"
            }
            else -> getNodeTextTraverse(child)
        }
    }
}

fun getNodeStructuredText(node: org.jsoup.nodes.Node): String {
    val children = node.childNodes()
    if (children.isEmpty())
        return ""

    return children.joinToString("") { child ->
        when {
            child.nodeName() == "p" -> getPTraverse(child)
            child.nodeName() == "br" -> "\n"
            child.nodeName() == "hr" -> "\n"
            child.nodeName() == "img" -> "" // todo
            child is TextNode -> child.text().trim()
            else -> getNodeTextTraverse(child)
        }
    }
}
