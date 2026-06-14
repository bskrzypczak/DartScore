package com.dartscore.feature.training.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class TrainingMode(
    val id: String,
    val name: String,
    val shortDescription: String,
    val rules: List<String>,
    val icon: ImageVector,
)

object TrainingModes {
    val all = listOf(
        TrainingMode(
            id = "clock",
            name = "Around the Clock",
            shortDescription = "Rzucaj po kolei we wszystkie wartości od 1 do 20.",
            rules = listOf(
                "Zaczynasz od pola 1 i idziesz po kolei aż do 20.",
                "Trafienie aktualnej liczby przesuwa Cię na następną.",
                "Cel: zaliczyć całą tarczę jak najmniejszą liczbą rzutek.",
            ),
            icon = Icons.Default.Refresh,
        ),
        TrainingMode(
            id = "bob27",
            name = "Bob's 27",
            shortDescription = "Zacznij z 27 punktami i trafiaj duble. Nie trafisz – tracisz.",
            rules = listOf(
                "Zaczynasz z 27 punktami i rzucasz w duble po kolei (D1, D2, ...).",
                "Trafiony dubel dodaje jego wartość; brak trafienia odejmuje.",
                "Gra kończy się po D20 albo gdy spadniesz poniżej zera.",
            ),
            icon = Icons.Default.Star,
        ),
        TrainingMode(
            id = "checkout",
            name = "Trening checkoutów",
            shortDescription = "Ćwicz kończenie legów z wybranych wartości.",
            rules = listOf(
                "Dostajesz wynik do zamknięcia (np. 40, 60, 100).",
                "Masz 3 rzutki, by skończyć zgodnie z regułą wyjścia.",
                "Cel: jak najwyższy procent udanych checkoutów.",
            ),
            icon = Icons.Default.Check,
        ),
    )

    fun byId(id: String?): TrainingMode? = all.firstOrNull { it.id == id }
}
