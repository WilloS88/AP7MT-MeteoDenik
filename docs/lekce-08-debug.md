# Lekce 08 – cvičení 8.6: čtyři chyby v jedné obrazovce

Soubor: `app/src/main/java/cz/utb/meteodenik/rozbite/RozbityStav.kt`.
Čísla řádků odkazují na **rozbitou verzi** ze zadání (commit „Cviceni 8.6: rozbity kod ze zadani“).
Všechny příznaky jsem nejdřív zopakoval na emulátoru (Pixel 8, API 36), pak opravil a ověřil znovu.

| # | Příznak | Vinná deklarace / řádek | Mechanismus | Oprava |
|---|---|---|---|---|
| 1 | Počítadlo „Otevřeno detailů“ se vynuluje. Seznam měst přitom zůstane. Žádná hláška. | ř. 69 `var pocetOtevreni by remember { mutableStateOf(0) }` | `remember` drží hodnotu jen po dobu, co je composable v kompozici. Při rotaci se aktivita zničí a kompozice vznikne znovu. Při přechodu na detail obrazovka seznamu z kompozice navíc odejde. Hodnota ve `ViewModelu` přežije, protože ten je vázaný na položku zásobníku navigace (proto seznam zůstává). `remember` ne. | `rememberSaveable { mutableStateOf(0) }` – hodnota se uloží do saved state a přežije rotaci i návrat ze zásobníku. |
| 2 | „Vymazat“ smaže seznam, i když žádná metoda ViewModelu nic maže. | ř. 31 `val uiState = MutableStateFlow(…)` (veřejný zapisovatelný) a ř. 74 `viewModel.uiState.value = RozbityUiState()` | Veřejný `MutableStateFlow` dovolí UI zapsat do stavu napřímo. Konzistenci stavu pak nehlídá ViewModel, ale kdokoli, kdo ho zná. Je to porušení jednosměrného toku dat: stav teče dolů, události nahoru. | Zapisovatelný `private val _uiState`, veřejně jen `val uiState: StateFlow<…> = _uiState.asStateFlow()`. Mazání je metoda `vymaz()` a tlačítko volá `viewModel.vymaz()`. |
| 3 | Po několika rotacích zůstává v paměti zničená `MainActivity`. Žádná hláška. | ř. 29 `RozbityViewModel(private val context: Context)`, ř. 65–66 `LocalContext.current` předaný do `RozbitaFactory(context)` (ř. 47) | `LocalContext.current` je v obrazovce **aktivita**. ViewModel rotaci přežívá, aktivita ne. První instance aktivity ale zůstane v poli `context` ViewModelu, takže ji garbage collector nemůže uvolnit, a s ní celý strom jejích views. | ViewModel nedostává `Context` vůbec: `class RozbityViewModel : ViewModel()`. Nepoužívaná metoda `nazevAplikace()` zmizela. Kdyby text aplikace potřeboval, patří do UI (`stringResource`). |
| 4 | Klepnutí na město shodí aplikaci: `IllegalArgumentException: Navigation destination that matches route detail/1 cannot be found in the navigation graph`. | ř. 59 `composable("detail")` vs. ř. 82 `navigate("detail/${mesto.id}")` | Cesty se v grafu hledají jako řetězce. Destinace je zaregistrovaná jako `"detail"`, ale přechod míří na `"detail/1"`, kterému nic neodpovídá. Chybu překladač nevidí, protože jde o řetězce. | Typově bezpečná navigace: `composable<Detail>` a `navigate(Detail(mestoId = id))`. Překlep v cestě nebo chybějící argument se nezkompiluje. |

Navíc (kritérium z kontrolního bodu lekce, ne jedna ze čtyř chyb): `RozbitySeznam` měl parametr `navController` (ř. 64). Teď dostává jen lambdu `onMestoKliknuto: (Int) -> Unit` a o navigaci rozhoduje `RozbityGraf`.

## Proč zmizela třída `RozbitaFactory`

Existovala jen proto, aby předala `Context` do konstruktoru ViewModelu. Bez parametru konstruktoru ji nic nepotřebuje: `viewModel()` si bezparametrový ViewModel vytvoří sám výchozí továrnou. Navíc `RozbitaFactory(context)` vznikala při každé rekompozici a předávala dlouho žijícímu objektu krátce žijící aktivitu, což byla přímá příčina chyby 3. Kdyby ViewModel `Context` skutečně potřeboval, správně by to byl `applicationContext` (například přes `AndroidViewModel`), nikdy aktivita.

## Ověření (emulátor)

| Co | Rozbitá verze | Opravená verze |
|---|---|---|
| Klepnutí na město | pád (příznak 4) | otevře detail, Zpět vrátí seznam |
| Počítadlo po dvou detailech + Zpět + rotaci | 0 (vynulovalo se už po prvním Zpět) | 2 |
| „Vymazat“ | UI zapisuje do stavu přímo | volá `viewModel.vymaz()` |
| Počet `Activities` v `dumpsys meminfo` po 5 rotacích a opakovaném uvolnění paměti | trvale 2 (jedna zničená drží ViewModel) | 1 |

Počet instancí aktivit jsem měřil příkazem `adb shell dumpsys meminfo cz.utb.meteodenik` (řádek `Activities`) po `am send-trim-memory … COMPLETE`, ne heap dumpem v Profileru. Výsledek je stejný: u rozbité verze zůstane o jednu aktivitu víc.

Poznámka k příznaku 1: zadání popisuje, že počítadlo ukáže 2 a vynuluje se až po rotaci. U mě se vynulovalo už po prvním návratu ze Zpět, protože obrazovka seznamu při přechodu na detail opouští kompozici. Příčina i oprava jsou stejné.
