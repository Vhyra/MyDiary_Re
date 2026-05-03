package com.example.mydiary.interfaces

interface MainActInterface {
    fun changeFrag(frag: String)
    fun removeDay(oldDate: String, oldData: String, newDate: String?, newData: String?, editOrNot: Boolean)
    fun removeWord(word: String, tranlsation: String, notes: String)
    suspend fun addWordRecord(
        newWord: String,
        newTranslation: String,
        newNotes: String,
        newSet: String
    ): Boolean

    fun manage_audio()
    fun addScore(
        date: String,
        correct: String,
        missed: String,
        words: String
    )

    fun setCurrentFragment(frag: String)
    suspend fun addRecord(date: String, text: String): Boolean
}