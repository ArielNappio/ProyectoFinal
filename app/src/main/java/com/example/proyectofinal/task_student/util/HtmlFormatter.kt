package com.example.proyectofinal.task_student.util

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.CharacterStyle
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import android.graphics.Typeface
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

fun htmlToAnnotatedStringFormatted(htmlText: String): AnnotatedString {
    val spanned: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(htmlText)
    }

    return buildAnnotatedString {
        var index = 0
        while (index < spanned.length) {
            val next = spanned.nextSpanTransition(index, spanned.length, CharacterStyle::class.java)
            val styles = spanned.getSpans(index, next, CharacterStyle::class.java)

            val text = spanned.subSequence(index, next).toString()
            var spanStyle = SpanStyle()

            styles.forEach { style ->
                spanStyle = when (style) {
                    is StyleSpan -> when (style.style) {
                        Typeface.BOLD -> spanStyle.merge(SpanStyle(fontWeight = FontWeight.Bold))
                        Typeface.ITALIC -> spanStyle.merge(SpanStyle(fontStyle = FontStyle.Italic))
                        else -> spanStyle
                    }
                    is UnderlineSpan -> spanStyle.merge(SpanStyle(textDecoration = TextDecoration.Underline))
                    is RelativeSizeSpan -> spanStyle.merge(
                        SpanStyle(fontSize = (16.sp * style.sizeChange))
                    )
                    is AbsoluteSizeSpan -> spanStyle.merge(
                        SpanStyle(fontSize = style.size.pxToSp())
                    )
                    else -> spanStyle
                }
            }

            withStyle(spanStyle) {
                append(text)
            }

            index = next
        }
    }
}

fun Int.pxToSp(): TextUnit = (this / Resources.getSystem().displayMetrics.scaledDensity).sp
