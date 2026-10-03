package com.lab.myapplication

import com.lab.myapplication.parser.ResultFormatter
import com.lab.myapplication.parser.WordParser


object FoundWord {
    fun countwords(parsed: String, wordList: String): Map<String, Int> {
        val wordsFound = WordParser.parse(wordList).toSet()
        if (WordParser.parse(parsed).any { it in wordsFound }) {
            return WordParser.parse(parsed).filter { it in wordsFound }.groupingBy { it }
                .eachCount()
        } else {
            return emptyMap()
        }
    }

    fun runsort(text: String, wordlist: String): String {
        val found = countwords(text, wordlist)

        if (found.isEmpty()) return "Слов не найдено"
        return ResultFormatter.format(found)
    }
}

