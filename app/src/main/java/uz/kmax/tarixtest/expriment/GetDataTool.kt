package uz.kmax.tarixtest.expriment

import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun getDataFromFirebase(databaseReference: DatabaseReference): DataSnapshot {
    return suspendCancellableCoroutine { continuation ->
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Ma'lumot kelganida qayta ishlash
                continuation.resume(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
                // Xatolik bo'lganida
                continuation.resumeWithException(error.toException())
            }
        })
    }
}
