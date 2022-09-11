package com.dgorod.callmonitor.fakes

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

/**
 * Fake content provider for test purposes based on [MatrixCursor]. Supports only insert and query.
 */
class FakeContentProvider(private val columns: Array<String>): ContentProvider() {

    private val cursor = MatrixCursor(columns)

    override fun onCreate(): Boolean = true

    override fun getType(uri: Uri): String = "text"

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor = cursor

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        cursor.newRow().also { row ->
            columns.forEach { column -> row.add(column, values?.get(column)) }
        }
        return Uri.withAppendedPath(uri, cursor.count.toString())
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 1

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 1
}