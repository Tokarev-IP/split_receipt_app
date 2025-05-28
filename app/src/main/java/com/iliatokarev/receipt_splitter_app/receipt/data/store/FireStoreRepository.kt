package com.iliatokarev.receipt_splitter_app.receipt.data.store

import com.iliatokarev.receipt_splitter_app.receipt.data.services.DataConstantsReceipt
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptUiMessage
import com.iliatokarev.receipt_splitter_app.receipt.presentation.ReceiptVertexData
import com.iliatokarev.receipt_splitter_app.receipt.presentation.UserAttemptsData
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FireStoreRepository() : FireStoreRepositoryAbstract(), FireStoreRepositoryInterface {

    override suspend fun putUserAttemptsData(
        documentId: String,
        data: UserAttemptsData
    ) {
        return putUserAttemptsDataImpl(
            documentId = documentId,
            data = data
        )
    }

    override suspend fun getUserAttemptsData(documentId: String): UserAttemptsData? {
        return getUserAttemptsDataImpl(
            documentId = documentId
        )
    }

    override suspend fun deleteUserAttemptsData(documentId: String) {
        return deleteUserAttemptsDataImpl(
            documentId = documentId
        )
    }

    override suspend fun getReceiptVertexConstants(): ReceiptVertexData {
        return getReceiptConstantsImpl() ?: throw Exception(ReceiptUiMessage.INTERNAL_ERROR.msg)
    }

    private suspend fun putUserAttemptsDataImpl(
        collectionId: String = DataConstantsReceipt.USER_ATTEMPTS_COLLECTION,
        documentId: String,
        data: UserAttemptsData
    ) {
        return putDocumentInOneCollection(
            collectionId = collectionId,
            documentId = documentId,
            data = data
        )
    }

    private suspend fun getUserAttemptsDataImpl(
        collectionId: String = DataConstantsReceipt.USER_ATTEMPTS_COLLECTION,
        documentId: String,
    ): UserAttemptsData? {
        return getDocumentFromOneCollectionImpl<UserAttemptsData>(
            collectionId = collectionId,
            documentId = documentId
        )
    }

    private suspend fun deleteUserAttemptsDataImpl(
        collectionId: String = DataConstantsReceipt.USER_ATTEMPTS_COLLECTION,
        documentId: String,
    ) {
        return deleteDocumentFromOneCollection(
            collectionId = collectionId,
            documentId = documentId
        )
    }

    private suspend fun getReceiptConstantsImpl(
        collectionId: String = DataConstantsReceipt.MAIN_CONSTANTS_COLLECTION,
        documentId: String = DataConstantsReceipt.MAIN_CONSTANTS_DOCUMENT,
    ): ReceiptVertexData? {
        return getDocumentFromOneCollectionImpl<ReceiptVertexData>(
            collectionId = collectionId,
            documentId = documentId
        )
    }

}

interface FireStoreRepositoryInterface {
    suspend fun putUserAttemptsData(
        documentId: String,
        data: UserAttemptsData,
    )

    suspend fun getUserAttemptsData(
        documentId: String,
    ): UserAttemptsData?

    suspend fun deleteUserAttemptsData(
        documentId: String,
    )

    suspend fun getReceiptVertexConstants(): ReceiptVertexData
}

abstract class FireStoreRepositoryAbstract {

    private val db = Firebase.firestore

    internal suspend inline fun <reified T : Any> getDocumentFromOneCollectionImpl(
        collectionId: String,
        documentId: String,
    ): T? {
        return suspendCancellableCoroutine { continuation ->
            db.collection(collectionId).document(documentId)
                .get()
                .addOnSuccessListener { document: DocumentSnapshot ->
                    runCatching {
                        val data = document.toObject<T>()
                        continuation.resume(data)
                    }.getOrElse { e: Throwable ->
                        continuation.resumeWithException(e)
                    }
                }
                .addOnFailureListener { e: Exception ->
                    continuation.resumeWithException(e)
                }
        }
    }

    internal suspend fun <T : Any> putDocumentInOneCollection(
        collectionId: String,
        documentId: String,
        data: T
    ) {
        return suspendCancellableCoroutine { continuation ->
            db.collection(collectionId).document(documentId)
                .set(data)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { e: Exception ->
                    continuation.resumeWithException(e)
                }
        }
    }

    internal suspend fun deleteDocumentFromOneCollection(
        collectionId: String,
        documentId: String,
    ) {
        return suspendCancellableCoroutine { continuation ->
            db.collection(collectionId).document(documentId)
                .delete()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { e: Exception ->
                    continuation.resumeWithException(e)
                }
        }
    }
}