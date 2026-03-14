package com.abdullahhalis.overlai.utils

import android.graphics.Rect
import android.util.Log
import com.abdullahhalis.overlai.data.model.OcrResult
import kotlin.math.abs

fun List<OcrResult>.mergeBlocks(
    sorted: List<OcrResult>,
    isSameGroup: (Rect, Rect) -> Boolean,
    sortGroupBy: (OcrResult) -> Int
): List<OcrResult> {
    val merged = mutableListOf<OcrResult>()
    val visited = mutableSetOf<Int>()

    sorted.forEachIndexed { i, current ->
        if (i in visited) return@forEachIndexed
        val currentBox = current.boundingBox ?: run {
            merged.add(current)
            return@forEachIndexed
        }

        val group = mutableListOf(current)
        visited.add(i)
        var expandingBox = Rect(currentBox)

        sorted.forEachIndexed { j, candidate ->
            if (j in visited) return@forEachIndexed
            val candidateBox = candidate.boundingBox ?: return@forEachIndexed

            val result = isSameGroup(expandingBox, candidateBox)
            Log.d("MergeBlocks", "i=$i j=$j | expanding=${expandingBox} | candidate=${candidateBox} | merge=$result")

            if (result) {
                group.add(candidate)
                visited.add(j)
                expandingBox = Rect(
                    minOf(expandingBox.left, candidateBox.left),
                    minOf(expandingBox.top, candidateBox.top),
                    maxOf(expandingBox.right, candidateBox.right),
                    maxOf(expandingBox.bottom, candidateBox.bottom)
                )
                Log.d("MergeBlocks", "expanded to: $expandingBox")
            }
        }

        if (group.size == 1) {
            merged.add(current)
        } else {
            val mergedText = group.sortedBy(sortGroupBy).joinToString(" ") { it.text }
            merged.add(OcrResult(text = mergedText, boundingBox = expandingBox))
        }
    }

    return merged
}
fun List<OcrResult>.mergeHorizontalBlocks(
    columnThreshold: Int,
    verticalThreshold: Int
): List<OcrResult> {
    val sorted = sortedBy { it.boundingBox?.top ?: 0 }
    return mergeBlocks(
        sorted = sorted,
        isSameGroup = { current, candidate ->
            val verticalOverlap = current.top <= candidate.bottom + verticalThreshold &&
                    current.bottom >= candidate.top - verticalThreshold
            val horizontalGap = minOf(
                abs(candidate.left - current.right),
                abs(current.left - candidate.right)
            )
            verticalOverlap && horizontalGap < columnThreshold
        },
        sortGroupBy = { it.boundingBox!!.left }
    )
}

fun List<OcrResult>.mergeVerticalBlocks(
    columnThreshold: Int,
    verticalThreshold: Int
): List<OcrResult> {
    val sorted = sortedByDescending { it.boundingBox?.right ?: 0 }

    sorted.forEach {
        Log.d("MergeDebug", "block: '${it.text}' box: ${it.boundingBox}")
    }

    return mergeBlocks(
        sorted = sorted,
        isSameGroup = {current, candidate ->

            val horizontalOverlap = current.left <= candidate.right + columnThreshold &&
                    current.right >= candidate.left - columnThreshold

            val verticalOverlap = current.top <= candidate.bottom + verticalThreshold &&
                    current.bottom >= candidate.top - verticalThreshold

            horizontalOverlap && verticalOverlap
        },
        sortGroupBy = { -(it.boundingBox!!.top) }
    )
}

fun List<OcrResult>.mergeNearbyBlocks(
    language: OcrLanguage,
    columnThreshold: Int = 60,
    verticalThreshold: Int = 20
): List<OcrResult> {
    if (isEmpty()) return this

    return when(language) {
        OcrLanguage.JAPANESE, OcrLanguage.CHINESE -> mergeVerticalBlocks(columnThreshold, verticalThreshold)
        OcrLanguage.KOREAN, OcrLanguage.LATIN -> mergeHorizontalBlocks(columnThreshold, verticalThreshold)
    }
}