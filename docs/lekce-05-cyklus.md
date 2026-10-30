# Lekce 05 – tabulka životního cyklu (cvičení 5.1)

Měřeno na emulátoru Pixel 8, Android 16 (API 36), targetSdk 36, Logcat s filtrem `package:mine tag:Cyklus`.
Dlouhý obsah `Bundle[…]` je zkrácený.

## Šest scénářů

| # | Scénář | Předpověď | Skutečnost z Logcatu | Sedí? |
|---|--------|-----------|----------------------|-------|
| 1 | Start z ikony | `onCreate` (Bundle = null) → `onStart` → `onResume` | `MainActivity onCreate (savedInstanceState = null)` → `onStart` → `onResume` | ano |
| 2 | Odchod na plochu (Home) | `onPause` → `onStop` | `MainActivity onPause` → `onStop` | ano |
| 3 | Návrat z plochy | `onRestart` → `onStart` → `onResume` | `MainActivity onRestart` → `onStart` → `onResume` | ano |
| 4 | Rotace displeje | stará instance `onPause` → `onStop` → `onDestroy` (isFinishing = false), nová `onCreate` (Bundle ≠ null) → `onStart` → `onResume` | `onPause` → `onStop` → `onDestroy (isFinishing = false)` → `onCreate (savedInstanceState = Bundle[…])` → `onStart` → `onResume` | ano |
| 5a | Zpět v detailu města | detail `onPause` → `onStop` → `onDestroy` (isFinishing = true), MainActivity `onRestart` → `onStart` → `onResume` | `Detail onPause` → `Main onRestart` → `Main onStart` → `Main onResume` → `Detail onStop` → `Detail onDestroy (isFinishing = true)` | ano, prokládaně |
| 5b | Zpět na MainActivity | jen `onPause` → `onStop` (Android 12+, task jde na pozadí) | `onPause` → `onStop` → `onDestroy (isFinishing = true)` | **ne** |
| 6 | `adb shell am kill` na pozadí a návrat | žádné `onDestroy`, po návratu `onCreate` s Bundle ≠ null | před návratem nic; po návratu `onCreate (savedInstanceState = Bundle[…])` → `onStart` → `onResume` | ano |

Doplňkově:

- **Otevření detailu (klik na Brno):** `Main onPause` → `Detail onCreate (null)` → `Detail onStart` → `Detail onResume` → `Main onStop`.
- **Panel sdílení nad detailem:** jen `Detail onPause`, žádné `onStop`.
- **Tlačítko Zapsat záznam:** `Receiver onReceive mesto=Zlín` → `MainActivity dostala zprávu o uložení: Zlín`.

## Proč to vychází takhle

- **Rotace (4)** je změna konfigurace. Systém starou instanci zničí (`isFinishing = false`, nejde o zavření, ale o rekreaci) a postaví novou. Ta dostane `Bundle`, do kterého systém uložil stav okna. Vlastnosti staré instance jsou pryč.
- **Home (2) a návrat (3):** aktivita není vidět, takže skončí v `onStop`, ale instance žije dál. Při návratu `onRestart` jen ohlásí návrat ze stavu Stopped a pokračuje `onStart`/`onResume`. Nic se neobnovuje, protože se nic neztratilo.
- **Prokládané pořadí u dvou aktivit (5a a otevření detailu):** systém zastaví (`onStop`) dosavadní aktivitu až ve chvíli, kdy je nová opravdu vidět. Proto `onStop` přijde až na konci. Detail po Zpět končí definitivně (`isFinishing = true`), protože se odebírá z vrcholu zásobníku.
- **Panel sdílení:** je to systémové okno nad aktivitou, detail je pořád částečně vidět, proto jen `onPause`.
- **am kill (6):** proces zabije systém, takže callback `onDestroy` nemá kde běžet. Stav okna systém uložil už při odchodu na pozadí, proto nová instance v novém procesu dostane neprázdný `Bundle`.
- **Zpět na MainActivity (5b) – rozdíl oproti materiálu:** podle materiálu má od Androidu 12 kořenová spouštěcí aktivita po Zpět skončit jen v `onStop` a celý task jít na pozadí. Na tomto emulátoru (API 36) jsem ale opakovaně viděl i `onDestroy (isFinishing = true)`. Projevilo se to stejně u spuštění z ikony (monkey s kategorií LAUNCHER), u tlačítka Back i u gesta od okraje obrazovky. Aktivita je kořenem tasku a tlačítko Zpět ji tedy dokončila. Aplikace přitom Zpět nijak nezachytává (žádný `OnBackPressedCallback`). Pravděpodobná příčina je jiné chování systémového Zpět na Androidu 16 pro aplikace s targetSdk 36 (prediktivní Zpět je u nich zapnutý). Ověřené to ale nemám.
