package uz.kmax.tarixtest.expriment

import com.google.firebase.database.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GetDataFromFirebase {
    suspend fun fetchDataFromFirebase(reference: DatabaseReference): DataSnapshot {
        return suspendCancellableCoroutine { continuation ->
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot) // Ma'lumotni qaytarish
                }
                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException()) // Xatolikni qaytarish
                }
            })
        }
    }
}

