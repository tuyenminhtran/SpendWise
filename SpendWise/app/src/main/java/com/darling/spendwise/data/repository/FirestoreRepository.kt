package com.darling.spendwise.data.repository

import com.darling.spendwise.data.local.entity.TransactionEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun userTransactions(userId: String) =
        db.collection("users").document(userId).collection("transactions")

    suspend fun uploadTransactions(
        userId: String,
        transactions: List<TransactionEntity>
    ): Result<Unit> {
        return try {
            val batch = db.batch()
            val collection = userTransactions(userId)
            transactions.forEach { tx ->
                val docRef = collection.document(tx.id.toString())
                batch.set(docRef, tx.toMap())
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadTransactions(userId: String): Result<List<TransactionEntity>> {
        return try {
            val snapshot = userTransactions(userId).get().await()
            val transactions = snapshot.documents.mapNotNull { it.toTransactionEntity() }
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun upsertTransaction(userId: String, tx: TransactionEntity): Result<Unit> {
        return try {
            userTransactions(userId)
                .document(tx.id.toString())
                .set(tx.toMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(userId: String, txId: Int): Result<Unit> {
        return try {
            userTransactions(userId).document(txId.toString()).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllTransactions(userId: String): Result<Unit> {
        return try {
            val snapshot = userTransactions(userId).get().await()
            val batch = db.batch()
            snapshot.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

fun TransactionEntity.toMap(): Map<String, Any> = mapOf(
    "localId"    to id,
    "amount"     to amount,
    "type"       to type,
    "categoryId" to categoryId,
    "walletId"   to walletId,
    "note"       to note,
    "date"       to date
)

fun com.google.firebase.firestore.DocumentSnapshot.toTransactionEntity(): TransactionEntity? {
    return try {
        TransactionEntity(
            id         = (getLong("localId") ?: 0L).toInt(),
            amount     = getDouble("amount") ?: 0.0,
            type       = getString("type") ?: "expense",
            categoryId = (getLong("categoryId") ?: 0L).toInt(),
            walletId   = (getLong("walletId") ?: 0L).toInt(),
            note       = getString("note") ?: "",
            date       = getLong("date") ?: 0L
        )
    } catch (e: Exception) {
        null
    }
}