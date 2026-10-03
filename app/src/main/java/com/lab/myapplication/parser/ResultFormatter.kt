package com.lab.myapplication.parser


object ResultFormatter {
    fun format(result: Map<String, Int>): String =
        result.entries.joinToString("\n") { "${it.key} - ${it.value}" }

    }
