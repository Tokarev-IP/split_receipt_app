package com.iliatokarev.receipt_splitter_app.receipt.data.room.folder

import com.iliatokarev.receipt_splitter_app.receipt.presentation.FolderData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FolderDbRepository(
    private val folderDao: FolderDao,
    private val folderAdapter: FolderAdapter,
) : FolderDbRepositoryInterface {

    override suspend fun getAllFoldersFlow(): Flow<List<FolderData>> {
        return folderDao.getAllFoldersFlow().map { folderEntityList ->
            folderAdapter.transformFolderEntityListToFolderDataList(folderEntityList = folderEntityList)
        }
    }

    override suspend fun getFolderById(id: Long): FolderData? {
        return folderDao.getFolderById(id).let { folderEntity ->
            folderEntity?.let {
                folderAdapter.transformFolderEntityToFolderData(folderEntity = folderEntity)
            }
        }
    }

    override suspend fun getFoldersByArchived(isArchived: Boolean): Flow<List<FolderData>> {
        return folderDao.getFoldersByArchived(isArchived = isArchived).map { folderEntityList ->
            folderAdapter.transformFolderEntityListToFolderDataList(folderEntityList = folderEntityList)
        }
    }

    override suspend fun insertFolder(folderData: FolderData) {
        return folderDao.insertFolder(
            folder = folderAdapter.transformFolderDataToFolderEntity(folderData = folderData)
        )
    }

    override suspend fun insertNewFolder(folderData: FolderData) {
        return folderDao.insertFolder(
            folder = folderAdapter.transformNewFolderDataToFolderEntity(folderData = folderData)
        )
    }

    override suspend fun deleteFolder(id: Long) {
        return folderDao.deleteFolderById(id)
    }

}

interface FolderDbRepositoryInterface {
    suspend fun getAllFoldersFlow(): Flow<List<FolderData>>
    suspend fun getFolderById(id: Long): FolderData?
    suspend fun getFoldersByArchived(isArchived: Boolean): Flow<List<FolderData>>
    suspend fun insertFolder(folderData: FolderData)
    suspend fun insertNewFolder(folderData: FolderData)
    suspend fun deleteFolder(id: Long)
}