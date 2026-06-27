package com.example.mydiary.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataViewModel: ViewModel() {
    var records = MutableLiveData<MutableList<Triple<String, String, Pair<String, String>>>>(mutableListOf())
    var dayRecords = MutableLiveData<MutableList<Pair<String, String>>>(mutableListOf())
    var stats = MutableLiveData(Pair(0, 0))
    var gameStats = MutableLiveData<MutableList<Triple<String, Pair<String, String>, String>>>(mutableListOf())
    var selected = MutableLiveData<MutableList<Int>>(mutableListOf())
    var setList = MutableLiveData<MutableList<String>>(mutableListOf())
    var grammarList = MutableLiveData<MutableList<Triple<String, String, String>>>(mutableListOf())
}