# DartScore (Android · Kotlin · Jetpack Compose)

Szkielet aplikacji do liczenia punktów w darta. Ta wersja to **działająca nawigacja
na pustych ekranach** — każdy ekran ma na razie tylko tytuł i ewentualne przyciski
napędzające przepływ.

## Jak uruchomić
1. Otwórz folder `DartScore/` w Android Studio (Ladybug lub nowszym).
2. Pozwól na "Gradle sync" — IDE dociągnie zależności i wygeneruje wrapper jar.
3. Uruchom konfigurację `app` na emulatorze/urządzeniu (minSdk 26).

> Projekt nie był kompilowany w środowisku, w którym powstał. Wersje zależności
> (`gradle/libs.versions.toml`) są z końca 2024 — przy syncu AS może zaproponować
> drobne aktualizacje. To normalne, zaakceptuj.

## Co działa
- **Root sterowany `AuthState`** (`navigation/RootNavHost.kt`): `LoggedOut` -> graf Auth,
  `Guest`/`LoggedIn` -> graf Main. Przełączenie stanu przerysowuje cały podgraf.
- **Logowanie / gość** (`feature/auth`): atrapa `FakeAuthRepository` (bez Firebase),
  wstrzykiwana przez Hilt. Przyciski na ekranie Login przełączają `AuthState`.
- **Pasek 5 zakładek** (`navigation/MainScaffold.kt`): Home, Znajomi, Graj, Trening,
  Ustawienia. Pasek znika w trakcie meczu.
- **Flow meczu**: Graj (setup) -> Mecz -> Zwycięstwo -> powrót na Dashboard.
- **Wylogowanie** (Ustawienia) wraca do grafu Auth.

## Mapa do architektury
- `core/designsystem` — kolory (slate + emerald z weba) i motyw.
- `core/ui` — `PlaceholderScreen` (wspólny pusty ekran).
- `feature/auth/{domain,data,ui}` — `AuthState`, `AuthRepository` (atrapa + miejsce na Firebase), `AuthViewModel`.
- `feature/{home,friends,play,training,settings}/ui` — ekrany zakładek.
- `di/RepositoryModule.kt` — wiązania Hilta.

## Następne kroki (świadomie pominięte tu)
- Podmiana atrapy na `FirebaseAuthRepository` (logowanie anonimowe dla gościa).
- `MatchViewModel` scope'owany do **zagnieżdżonego grafu** `match` (teraz match/victory
  to płaskie trasy — patrz komentarz w `MainScaffold.kt`).
- Silnik gry X01 w `feature/play/domain` (czysty Kotlin, testowalny).
- `core/scan` + `DartboardScanRepository` (wysyłka zdjęcia do serwera w Pythonie).
