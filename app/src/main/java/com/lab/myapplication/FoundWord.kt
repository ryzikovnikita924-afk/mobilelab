package com.lab.myapplication

import com.lab.myapplication.parser.ResultFormatter
import com.lab.myapplication.parser.WordParser


object FoundWord{
    fun countwords(parsed: String, wordList: String): Map<String, Int> {
        val wordsFound = WordParser.parse(wordList).toSet()
        return WordParser.parse(parsed).filter { it in wordsFound }.groupingBy { it }.eachCount()
    }

    fun runsort(text: String, wordlist : String): String {
        val found = countwords(text, wordlist )

        if (found.isEmpty()) return ""
        return ResultFormatter.format(found)
    }
}
