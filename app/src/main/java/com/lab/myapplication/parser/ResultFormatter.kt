package com.lab.myapplication.parser

import com.lab.myapplication.FoundWord

object ResultFormatter {
    fun format(result: Map<String, Int>): String=
        result.entries.joinToString("\n") { "${it.key} - ${it.value}" }
}