package com.iliatokarev.receipt_splitter_app.receipt.data.room.folder

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Upsert
    suspend fun insertFolder(folder: FolderEntity)

    @Transaction
    @Query("SELECT * FROM folder_data")
    fun getAllFoldersFlow(): Flow<List<FolderEntity>>

    @Transaction
    @Query("SELECT * FROM folder_data WHERE id = :id")
    suspend fun getFolderById(id: Long): FolderEntity?

    @Transaction
    @Query("SELECT * FROM folder_data WHERE is_archived = :isArchived")
    fun getFoldersByArchived(isArchived: Boolean): Flow<List<FolderEntity>>

    @Transaction
    @Query("DELETE FROM folder_data WHERE id = :id")
    suspend fun deleteFolderById(id: Long)

}