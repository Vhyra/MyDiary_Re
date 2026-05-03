package com.example.mydiary.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import java.text.SimpleDateFormat
import java.util.Locale

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + MyDiaryContract.DayEntry.TABLE_NAME + " ("
                + MyDiaryContract.DayEntry.ID_COL + " INTEGER PRIMARY KEY, " +
                MyDiaryContract.DayEntry.DATE_COL + " DATE," +
                MyDiaryContract.DayEntry.DAY_DATA + " TEXT" + ")")

        val queryDic = ("CREATE TABLE " + MyDiaryContract.Words.TABLE_NAME + " ("
                + MyDiaryContract.Words.ID_COL + " INTEGER PRIMARY KEY, " +
                MyDiaryContract.Words.WORDS_COL + " TEXT," +
                MyDiaryContract.Words.TRANSLATION_COL + " TEXT," +
                MyDiaryContract.Words.NOTES_COL + " TEXT" + ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
        db.execSQL(queryDic)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        
        val newCol = "NUOVA_COLONNA"
        val type = "NEW_TYPE"
        
        if (oldVersion < 2) {
            val queryDic = ("CREATE TABLE " + MyDiaryContract.Words.TABLE_NAME + " ("
                    + MyDiaryContract.Words.ID_COL + " INTEGER PRIMARY KEY, " +
                    MyDiaryContract.Words.WORDS_COL + " TEXT," +
                    MyDiaryContract.Words.TRANSLATION_COL + " TEXT," +
                    MyDiaryContract.Words.NOTES_COL + " TEXT" + ")")
            db.execSQL(queryDic)
        }
    }

    // This method is for adding data in our database
    fun addRecord(date : String, day_data : String ): Long{

        val isoDate = convertDateToISO(date)

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        values.put(MyDiaryContract.DayEntry.DATE_COL, isoDate)
        values.put(MyDiaryContract.DayEntry.DAY_DATA, day_data)

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        val id = db.insert(MyDiaryContract.DayEntry.TABLE_NAME, null, values)

        db.close()
        return id
    }

    fun addWordRecord(word : String, translantion : String, notes: String): Long{

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        values.put(MyDiaryContract.Words.WORDS_COL, word)
        values.put(MyDiaryContract.Words.TRANSLATION_COL, translantion)
        values.put(MyDiaryContract.Words.NOTES_COL, notes)

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        val id = db.insert(MyDiaryContract.Words.TABLE_NAME, null, values)

        db.close()
        return id
    }

    // below method is to get
    // all data from our database
    fun getAllRecords(): MutableList<Pair<String, String>> {

        val records = mutableListOf<Pair<String, String>>()

        val db = this.readableDatabase

        val projection = arrayOf(MyDiaryContract.DayEntry.ID_COL, MyDiaryContract.DayEntry.DATE_COL, MyDiaryContract.DayEntry.DAY_DATA)

        val sortOrder = "${MyDiaryContract.DayEntry.DATE_COL} DESC"

        val cursor = db.query(
            MyDiaryContract.DayEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            sortOrder               // The sort order
        )

        with(cursor) {
            while (moveToNext()) {
                val date = getString(getColumnIndexOrThrow(MyDiaryContract.DayEntry.DATE_COL))
                val dayData = getString(getColumnIndexOrThrow(MyDiaryContract.DayEntry.DAY_DATA))
                records.add(Pair(date, dayData))
            }
        }
        cursor.close()
        db.close()

        return records
    }

    fun getAllWordsRecords(): MutableList<Triple<String, String, String>> {

        val records = mutableListOf<Triple<String, String, String>>()

        val db = this.readableDatabase

        val projection = arrayOf(MyDiaryContract.Words.ID_COL, 
            MyDiaryContract.Words.WORDS_COL, 
            MyDiaryContract.Words.TRANSLATION_COL,
            MyDiaryContract.Words.NOTES_COL)

        val cursor = db.query(
            MyDiaryContract.Words.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        )

        with(cursor) {
            while (moveToNext()) {
                val word = getString(getColumnIndexOrThrow(MyDiaryContract.Words.WORDS_COL))
                val translantion = getString(getColumnIndexOrThrow(MyDiaryContract.Words.TRANSLATION_COL))
                val notes = getString(getColumnIndexOrThrow(MyDiaryContract.Words.NOTES_COL))
                records.add(Triple(word, translantion, notes))
            }
        }
        cursor.close()
        db.close()
        
        return records
    }

    fun removeWord(word: String, translation: String, notes: String): Int {
        val db = this.writableDatabase

        // Definisci la parte 'where' della query, includendo anche translation e notes
        val selection = "${MyDiaryContract.Words.WORDS_COL} LIKE ? AND ${MyDiaryContract.Words.TRANSLATION_COL} LIKE ? AND ${MyDiaryContract.Words.NOTES_COL} LIKE ?"

        // Specifica gli argomenti in ordine di segnaposto
        val selectionArgs = arrayOf(word, translation, notes)

        // Esegui la query SQL per eliminare la riga
        val deletedRows = db.delete(MyDiaryContract.Words.TABLE_NAME, selection, selectionArgs)

        return deletedRows
    }

    fun removeDay(date: String?, day_data: String?, oldDay: String, oldData: String, editOrNot: Boolean): Int {
        val db = this.writableDatabase

        // Definisci la parte 'where' della query, includendo anche translation e notes
        val selection = "${MyDiaryContract.DayEntry.DATE_COL} LIKE ? AND ${MyDiaryContract.DayEntry.DAY_DATA} LIKE ?"

        // Specifica gli argomenti in ordine di segnaposto
        val selectionArgs = arrayOf(oldDay, oldData)

        // Esegui la query SQL per eliminare la riga
        val deletedRows = db.delete(MyDiaryContract.DayEntry.TABLE_NAME, selection, selectionArgs)

        if(editOrNot){
            if (date != null && day_data != null) {
                addRecord(date, day_data)
            }
        }

        return deletedRows
    }

    fun convertDateToISO(dateString: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString // Se la conversione fallisce, restituisce la data originale
        }
    }

    fun migrateDatabaseDates(db: SQLiteDatabase) {
        val query = "SELECT ${MyDiaryContract.DayEntry.ID_COL}, ${MyDiaryContract.DayEntry.DATE_COL} FROM ${MyDiaryContract.DayEntry.TABLE_NAME}"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(MyDiaryContract.DayEntry.ID_COL))
            val oldDate = cursor.getString(cursor.getColumnIndexOrThrow(MyDiaryContract.DayEntry.DATE_COL))
            val newDate = convertDateToISO(oldDate)

            // Aggiornare la data nel database
            val updateQuery = "UPDATE ${MyDiaryContract.DayEntry.TABLE_NAME} SET ${MyDiaryContract.DayEntry.DATE_COL} = ? WHERE ${MyDiaryContract.DayEntry.ID_COL} = ?"
            db.execSQL(updateQuery, arrayOf(newDate, id.toString()))
        }

        cursor.close()
    }

    fun getDim(): Pair<Int, Int>{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(${MyDiaryContract.DayEntry.ID_COL}) FROM ${MyDiaryContract.DayEntry.TABLE_NAME}", null)
        val cursor2 = db.rawQuery("SELECT COUNT(${MyDiaryContract.Words.ID_COL}) FROM ${MyDiaryContract.Words.TABLE_NAME}", null)

        var count = 0
        var count1 = 0

        cursor.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }

        cursor2.use {
            if (it.moveToFirst()) {
                count1 = it.getInt(0)  // Ottieni il conteggio delle righe dalla prima colonna
            }
        }

        cursor.close()
        cursor2.close()
        db.close()

        return Pair(count, count1)
    }


    companion object{

            // below is variable for database name
        private val DATABASE_NAME = "DIARY_DATABASE"

            // below is the variable for database version
        private val DATABASE_VERSION = 2

    }
}

object MyDiaryContract{
    // here we have defined variables for our database
    object DayEntry : BaseColumns {

        // below is the variable for table name
        val TABLE_NAME = "my_diary"

        // below is the variable for id column
        val ID_COL = "id"

        // below is the variable for name column
        val DATE_COL = "date"

        // below is the variable for age column
        val DAY_DATA = "day_data"
    }

    object Words : BaseColumns {

        // below is the variable for table name
        val TABLE_NAME = "my_words"

        // below is the variable for id column
        val ID_COL = "id"

        // below is the variable for name column
        val WORDS_COL = "word"

        // below is the variable for age column
        val TRANSLATION_COL = "translation"

        val NOTES_COL = "notes"
    }

}
