package com.example.mydiary.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class FirebaseDBHelper {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("") /// TODO: Controlla url
    private val uid get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val wordsRef get() = database.getReference("users/$uid/my_words")
    private val scoreRef get() = database.getReference("users/$uid/memogame_score")
    private val diaryRef get() = database.getReference("users/$uid/my_diary")
    private val setRef get() = database.getReference("users/$uid/max_set")

    private fun uid(): String = FirebaseAuth.getInstance().currentUser?.uid
        ?: throw IllegalStateException("User not logged in")
    fun addRecord(date: String, dayData: String, onComplete: (Boolean) -> Unit) {
        val isoDate = convertDateToISO(date)
        val key = diaryRef.push().key ?: return onComplete(false)
        val data = mapOf("date" to isoDate, "day_data" to dayData)
        diaryRef.child(key).setValue(data) { error, _ ->
            onComplete(error == null)
        }
    }

    fun addWordRecord(word: String, translation: String, notes: String, set: String, onComplete: (Boolean) -> Unit) {
        val key = wordsRef.push().key ?: return onComplete(false)
        val data = mapOf(
            "word" to word,
            "translation" to translation,
            "notes" to notes,
            "set" to set
        )
        setRef.orderByChild("set").equalTo(set)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        val key = setRef.push().key ?: return onComplete(false)
                        val data = mapOf("set" to set)
                        setRef.child(key).setValue(data)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(false)
                }
            })
        wordsRef.child(key).setValue(data){ error, _ ->
            onComplete(error == null)
        }
    }

    suspend fun getSets(): List<String>{

        val deferred = CompletableDeferred<List<String>>()

        setRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = mutableListOf<String>()
                for (entry in snapshot.children) {
                    val set = entry.child("set").getValue(String::class.java) ?: "0"
                    Log.d("LOG SET", set)
                    records.add(set)
                }
                deferred.complete(records)
            }
            override fun onCancelled(error: DatabaseError) {
                deferred.completeExceptionally(error.toException())
            }
        } )
        return deferred.await()
    }

    fun addScore(date: String, correct: String, missed: String, words: String, onComplete: (Boolean) -> Unit){
        val key = scoreRef.push().key ?: return onComplete(false)
        val data = mapOf(
            "date" to date,
            "correct" to correct,
            "missed" to missed,
            "words" to words
        )
        scoreRef.child(key).setValue(data){error, _ ->
            onComplete(error == null)
        }
    }

    suspend fun getScore(): List<Triple<String, Pair<String, String>, String>> {
        val deferred = CompletableDeferred<List<Triple<String, Pair<String, String>, String>>>()

        scoreRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = mutableListOf<Triple<String, Pair<String, String>, String>>()
                for (entry in snapshot.children) {
                    var date = entry.child("date").getValue(String::class.java) ?: ""
                    date = convertDateToISO(date)
                    val correct = entry.child("correct").getValue(String::class.java) ?: ""
                    val missed = entry.child("missed").getValue(String::class.java) ?: ""
                    val wordsList = entry.child("words").getValue(String::class.java) ?: ""
                    records.add(Triple(date, Pair(correct, missed), wordsList))
                }
                deferred.complete(records)
            }
            override fun onCancelled(error: DatabaseError) {
                deferred.completeExceptionally(error.toException())
            }
        } )
        return deferred.await()
    }

    suspend fun getAllRecords(): List<Pair<String, String>> {
        val deferred = CompletableDeferred<List<Pair<String, String>>>()

        diaryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = mutableListOf<Pair<String, String>>()
                for (entry in snapshot.children) {
                    val date = entry.child("date").getValue(String::class.java) ?: ""
                    val data = entry.child("day_data").getValue(String::class.java) ?: ""
                    records.add(Pair(date, data))
                }
                deferred.complete(records)
            }

            override fun onCancelled(error: DatabaseError) {
                deferred.completeExceptionally(error.toException())
            }
        })

        return deferred.await()
    }

    suspend fun getAllWordRecords(): List<Triple<String, String, Pair<String, String>>> {
        val deferred = CompletableDeferred<List<Triple<String, String, Pair<String, String>>>>()

        wordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = mutableListOf<Triple<String, String, Pair<String, String>>>()
                for (entry in snapshot.children) {
                    val word = entry.child("word").getValue(String::class.java) ?: ""
                    val translation = entry.child("translation").getValue(String::class.java) ?: ""
                    val notes = entry.child("notes").getValue(String::class.java) ?: ""
                    val set = entry.child("set").getValue(String::class.java) ?: "0"
                    records.add(Triple(word, translation, Pair(notes, set)))
                }
                deferred.complete(records)
            }

            override fun onCancelled(error: DatabaseError) {
                deferred.completeExceptionally(error.toException())
            }
        })

        return deferred.await()
    }

    fun removeWord(word: String, translation: String, notes: String) {
        wordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (entry in snapshot.children) {
                    val w = entry.child("word").getValue(String::class.java)
                    val t = entry.child("translation").getValue(String::class.java)
                    val n = entry.child("notes").getValue(String::class.java)

                    if (w == word && t == translation && n == notes) {
                        entry.ref.removeValue()
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDBHelper", "Failed to delete word", error.toException())
            }
        })
    }

    fun removeDay(oldDate: String, oldData: String, newDate: String?, newData: String?, editOrNot: Boolean) {
        diaryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (entry in snapshot.children) {
                    val date = entry.child("date").getValue(String::class.java)
                    val data = entry.child("day_data").getValue(String::class.java)

                    if (date == oldDate && data == oldData) {
                        entry.ref.removeValue()
                        break
                    }
                }

                if (editOrNot && newDate != null && newData != null) {
                    addRecord(newDate, newData) { success ->
                        if (success) {
                            Log.d("Firebase", "Record aggiunto correttamente")
                        } else {
                            Log.e("Firebase", "Errore nell'aggiunta del record")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDBHelper", "Failed to delete day entry", error.toException())
            }
        })
    }

    suspend fun getDim(): Pair<Int, Int> = coroutineScope {
        val daysDeferred = async {
            diaryRef.get().await().childrenCount.toInt()
        }

        val wordsDeferred = async {
            wordsRef.get().await().childrenCount.toInt()
        }

        val (days, words) = awaitAll(daysDeferred, wordsDeferred)
        Pair(days as Int, words as Int)
    }

    fun convertDateToISO(dateString: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString // fallback
        }
    }
}
