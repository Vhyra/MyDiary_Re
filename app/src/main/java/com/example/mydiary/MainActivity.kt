package com.example.mydiary

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mydiary.authentication.LoginFragment
import com.example.mydiary.data.DataViewModel
import com.example.mydiary.data.FirebaseDBHelper
import com.example.mydiary.interfaces.MainActInterface
import com.example.mydiary.pages.DiaryPage
import com.example.mydiary.pages.Dictionary
import com.example.mydiary.pages.Homepage
import com.example.mydiary.pages.MemoGameFrag
import com.example.mydiary.pages.NewDayPage
import com.example.mydiary.pages.NewWord
import com.example.mydiary.pages.StatsFrag
import com.example.mydiary.services.AudioService
import com.google.firebase.auth.FirebaseAuth
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MainActivity : AppCompatActivity(), MainActInterface {

    //lateinit var dbHelper: DBHelper
    lateinit var dbHelper: FirebaseDBHelper
    private var currentFragmentName = "login_page"
    lateinit var audio_intent: Intent
    private lateinit var viewModel: DataViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //dbHelper = DBHelper(this, null)

        dbHelper = FirebaseDBHelper()

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        viewModel = ViewModelProvider(this)[DataViewModel::class.java]

        CoroutineScope(Dispatchers.Main).launch {
            try {

                FirebaseAuth.getInstance().addAuthStateListener { auth ->
                    if (auth.currentUser != null) {
                        lifecycleScope.launch { load_data() }
                    }
                }

                setContentView(R.layout.activity_main)

                if (savedInstanceState == null) {
                    // Prima apertura dell'app
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, LoginFragment(), "login_frag")
                        .commit()
                } else {
                    // L'activity è stata ricreata dopo una rotazione, recupera il fragment corrente
                    currentFragmentName = savedInstanceState.getString("currentFragment", "homepage")

                    // Controlla se esiste già un fragment nel container
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                    if (currentFragment == null) {
                        changeFrag(currentFragmentName)
                    }
                }

            } catch (e: Exception) {
                println(e)
            }
        }

        //backupDatabase() Crea backup per trasferimento database sqlite su firebase
        //migrateDataToFirebase()


        /*
        records = dbHelper.getAllWordsRecords()
        records.sortBy { it.second.toLowerCase(Locale.ROOT) }
        */

        //val db = dbHelper.writableDatabase //Nel caso serva modificare il database
        //dbHelper.migrateDatabaseDates(db)



    }

    override fun changeFrag(frag: String){

        val existingFragment = supportFragmentManager.findFragmentByTag(frag)

        if (existingFragment != null && frag != "statsFrag") {
            // Se il fragment esiste già, lo riporta in primo piano
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, existingFragment)
                .addToBackStack(null)
                .commit()
        } else {
            var newFragment: Fragment? = null
            when (frag) {
                "homepage" -> newFragment = Homepage()
                "new_word" -> newFragment = NewWord()
                "dictionary" -> newFragment = Dictionary()
                "new_day" -> newFragment = NewDayPage()
                "diary" -> newFragment = DiaryPage()
                "memoryGame" -> newFragment = MemoGameFrag()
                "statsFrag" -> newFragment = StatsFrag()
                "login_frag" -> newFragment = LoginFragment()
            }
            if (newFragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, newFragment, frag)
                    .addToBackStack(null)
                    .commit()
            }
        }


    }

    override fun setCurrentFragment(frag: String) {
        currentFragmentName = frag
    }

    override suspend fun addRecord(date: String, text: String): Boolean = suspendCoroutine { continuation ->

        dbHelper.addRecord(date, text) { success ->
            if(success){
                val updated = viewModel.dayRecords.value ?: mutableListOf()
                updated.add(Pair(date, text))
                updated.sortByDescending { it.first }
                viewModel.dayRecords.postValue(updated)
            }
            continuation.resume(success)
        //viewModel.dayRecords.add(Pair(convertDateToISO(date.text.toString()), text.text.toString()))
        //viewModel.dayRecords.sortByDescending { it.first }
        }

        updateStats()

    }


    override fun removeDay(
        oldDate: String,
        oldData: String,
        newDate: String?,
        newData: String?,
        editOrNot: Boolean
    ) {
        dbHelper.removeDay(oldDate, oldData, newDate, newData, editOrNot)
        updateStats()
    }
/*
    fun updateData(data: String){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                when (data) {
                    "words" -> {
                        viewModel.records = dbHelper.getAllWordRecords().toMutableList()
                        viewModel.records.sortBy { it.second.lowercase(Locale.ROOT) }
                    }

                    "days" -> {
                        viewModel.dayRecords = dbHelper.getAllRecords().toMutableList()
                        viewModel.dayRecords.sortByDescending { it.first }
                    }

                    "scores" -> {
                        viewModel.gameStats = dbHelper.getScore().toMutableList()
                        viewModel.gameStats.sortByDescending { it.first }
                    }
                }
            } catch (e: Exception){
            println(e)
            }
        }
    }
*/
    override fun removeWord(word: String, tranlsation: String, notes: String) {
        dbHelper.removeWord(word, tranlsation, notes)
        updateStats()
    }

    override suspend fun addWordRecord(
        newWord: String,
        newTranslation: String,
        newNotes: String,
        newSet: String
    ): Boolean = suspendCoroutine { continuation ->

        Toast.makeText(this, getString(R.string.saveInProgress), Toast.LENGTH_SHORT).show()

        dbHelper.addWordRecord(newWord, newTranslation, newNotes, newSet) { success ->
            if(success){
                val updated = viewModel.records.value ?: mutableListOf()
                updated.add(Triple(newWord, newTranslation, Pair(newNotes, newSet)))
                updated.sortBy { it.second.lowercase() }
                viewModel.records.postValue(updated)
            }
            continuation.resume(success) // riprende la coroutine con il risultato
        }

        updateStats()

    }

    override fun manage_audio(){
        if(AudioService.isRunning){
            stopService(audio_intent)
        }else{
            audio_intent = Intent(this, AudioService::class.java)
            startService(audio_intent)
        }
    }

    override fun addScore(
        date: String,
        correct: String,
        missed: String,
        words: String
    ) {
        dbHelper.addScore(
            date,
            correct,
            missed,
            words
        )
        {success ->

            Toast.makeText(this, getString(R.string.saveInProgress), Toast.LENGTH_SHORT).show()

            if(success){
                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT)
                    .show()
            }
            else{
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    suspend fun load_data(){
        try {

            viewModel.stats.postValue(dbHelper.getDim())

            if (viewModel.records.value.isNullOrEmpty()) {
                viewModel.records.postValue(
                    dbHelper.getAllWordRecords().sortedBy { it.second.lowercase() }.toMutableList()
                )
            }

            if (viewModel.dayRecords.value.isNullOrEmpty()) {
                viewModel.dayRecords.postValue(
                    dbHelper.getAllRecords().sortedByDescending { it.first }.toMutableList()
                )
            }

            if(viewModel.gameStats.value.isNullOrEmpty()){
                viewModel.gameStats.postValue(
                    dbHelper.getScore().sortedByDescending { it.first }.toMutableList()
                )
            }

            if(viewModel.setList.value.isNullOrEmpty()){
                viewModel.setList.postValue(
                    dbHelper.getSets().sortedBy { it.lowercase(Locale.getDefault()) }.toMutableList()
                )
            }

        } catch (e: Exception) {
            println(e)
        }
    }

    /*
    fun migrateDataToFirebase() {
        val database = FirebaseDatabase.getInstance("https://mydiary-5b08c-default-rtdb.europe-west1.firebasedatabase.app").reference

        // MIGRAZIONE MY_DIARY
        val diaryList = dbHelper.getAllRecords()
        diaryList.forEach { (date, dayData) ->
            val diaryEntry = mapOf(
                "date" to date,
                "day_data" to dayData
            )
            database.child("my_diary").push().setValue(diaryEntry)
        }

        // MIGRAZIONE MY_WORDS
        val wordsList = dbHelper.getAllWordsRecords()
        wordsList.forEach { (word, translation, notes) ->
            val wordEntry = mapOf(
                "word" to word,
                "translation" to translation,
                "notes" to notes
            )
            database.child("my_words").push().setValue(wordEntry)
        }
    }

    fun backupDatabase() {
        print("Provo il backup -------------------------------------------------------------------")
        try {
            val dbFile = getDatabasePath("DIARY_DATABASE") // Sostituisci con il tuo nome DB esatto
            val backupDir = getExternalFilesDir(null) // Oppure Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val backupFile = File(backupDir, "MyDiary_backup_${System.currentTimeMillis()}.db")

            dbFile.copyTo(backupFile, overwrite = true)

            println("Backup salvato in: ${backupFile.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    */

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (::audio_intent.isInitialized) {
            stopService(audio_intent)
        }
        //dbHelper.close() //Per db sqlite
        super.onDestroy()
    }

    private fun updateStats(){
        lifecycleScope.launch { viewModel.stats.postValue(dbHelper.getDim()) }
    }

}
