package ru.vaxoid.qualitysurvey.json

import java.util.*


class Alarms {


        private val name: String? = null
        private val type: String? = null
        private val simple: String? = null
        private val easy: String? = null
        private val cases: MutableMap<String, Any> = TreeMap(
            Comparator.comparingInt { k: String ->
                k.substring(k.indexOf('_') + 1)
                    .toInt()
            }
        )

        fun setCase(caseNumber: String, value: Any) {
            if (caseNumber.matches("case_\\d+".toRegex())) cases[caseNumber] = value
        }
    }






