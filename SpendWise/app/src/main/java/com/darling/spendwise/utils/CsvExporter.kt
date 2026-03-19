package com.darling.spendwise.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.darling.spendwise.data.local.entity.TransactionEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun exportCsvToDownloads(
    context: Context,
    transactions: List<TransactionEntity>
): Result<String> {
    return try {
        val fileName = "SpendWise_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        }.csv"

        val csvContent = buildString {
            // Header
            appendLine("ID,Số tiền,Loại,Danh mục,Ghi chú,Ngày")
            // Rows
            transactions.forEach { tx ->
                val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(Date(tx.date))
                val type = when (tx.type) {
                    "expense"  -> "Chi tiêu"
                    "income"   -> "Thu nhập"
                    else       -> "Chuyển khoản"
                }
                // Escape note nếu có dấu phẩy
                val note = if (tx.note.contains(",")) "\"${tx.note}\"" else tx.note
                appendLine("${tx.id},${tx.amount},$type,${tx.categoryId},$note,$date")
            }
        }

        val outputStream: OutputStream
        val filePath: String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ dùng MediaStore
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
            ) ?: throw Exception("Không thể tạo file")

            outputStream = context.contentResolver.openOutputStream(uri)
                ?: throw Exception("Không thể mở file")
            filePath = "Downloads/$fileName"
        } else {
            // Android 9 trở xuống
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            dir.mkdirs()
            val file = File(dir, fileName)
            outputStream = FileOutputStream(file)
            filePath = file.absolutePath
        }

        outputStream.use { it.write(csvContent.toByteArray()) }
        Result.success(filePath)

    } catch (e: Exception) {
        Result.failure(e)
    }
}