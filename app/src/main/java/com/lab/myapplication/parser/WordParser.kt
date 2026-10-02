package com.lab.myapplication.parser

object WordParser {
    fun parse(input: String): List<String>{
        if (input.isBlank()) return emptyList()

        return input.lowercase().split(Regex("[,\\s]+"))

    }
}