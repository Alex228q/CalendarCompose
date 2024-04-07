package com.example.compositionlocal.util

import java.time.LocalDate

class WorkShifts(
    numberOfTeam: Int
) {

    private val teamDay1: LocalDate = when (numberOfTeam) {
        1 -> LocalDate.of(2024, 3, 1)
        2 -> LocalDate.of(2024, 3, 3)
        3 -> LocalDate.of(2024, 3, 5)
        4 -> LocalDate.of(2024, 3, 7)
        else -> throw Exception("bad team")

    }
    private val teamDay2: LocalDate = when (numberOfTeam) {
        1 -> LocalDate.of(2024, 3, 2)
        2 -> LocalDate.of(2024, 3, 4)
        3 -> LocalDate.of(2024, 3, 6)
        4 -> LocalDate.of(2024, 3, 8)
        else -> throw Exception("bad team")

    }
    private val teamEvening1: LocalDate = when (numberOfTeam) {
        1 -> LocalDate.of(2024, 3, 3)
        2 -> LocalDate.of(2024, 3, 5)
        3 -> LocalDate.of(2024, 3, 7)
        4 -> LocalDate.of(2024, 3, 9)
        else -> throw Exception("bad team")

    }
    private val teamEvening2: LocalDate = when (numberOfTeam) {
        1 -> LocalDate.of(2024, 3, 4)
        2 -> LocalDate.of(2024, 3, 6)
        3 -> LocalDate.of(2024, 3, 8)
        4 -> LocalDate.of(2024, 3, 10)
        else -> throw Exception("bad team")

    }
    private val teamNight1: LocalDate = when (numberOfTeam) {
        1 -> LocalDate.of(2024, 3, 7)
        2 -> LocalDate.of(2024, 3, 9)
        3 -> LocalDate.of(2024, 3, 11)
        4 -> LocalDate.of(2024, 3, 13)
        else -> throw Exception("bad team")

    }
    private val teamNight2: LocalDate = when (numberOfTeam) {
        1 -> LocalDate.of(2024, 3, 8)
        2 -> LocalDate.of(2024, 3, 10)
        3 -> LocalDate.of(2024, 3, 12)
        4 -> LocalDate.of(2024, 3, 14)
        else -> throw Exception("bad team")

    }


    fun getDays(): List<String> {
        var firstDay = teamDay1
        var secondDay = teamDay2
        val days = mutableListOf<String>()
        repeat(800) {
            days.add(firstDay.toString())
            days.add(secondDay.toString())
            firstDay = firstDay.plusDays(10)
            secondDay = secondDay.plusDays(10)
        }
        return days.toList()
    }

    fun getEvening(): List<String> {
        var firstEvening = teamEvening1
        var secondEvening = teamEvening2
        val evening = mutableListOf<String>()
        repeat(800) {
            evening.add(firstEvening.toString())
            evening.add(secondEvening.toString())
            firstEvening = firstEvening.plusDays(10)
            secondEvening = secondEvening.plusDays(10)
        }
        return evening.toList()
    }

    fun getNight(): List<String> {
        var firstNight = teamNight1
        var secondNight = teamNight2
        val night = mutableListOf<String>()
        repeat(800) {
            night.add(firstNight.toString())
            night.add(secondNight.toString())
            firstNight = firstNight.plusDays(10)
            secondNight = secondNight.plusDays(10)
        }
        return night.toList()
    }
}