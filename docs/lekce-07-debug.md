# Lekce 07 – cvičení 7.6: čtyři tiché chyby v jedné obrazovce

Soubor: `app/src/main/java/cz/utb/meteodenik/ui/RozbityMestaSeznam.kt`.
Čísla řádků odkazují na **rozbitou verzi** ze zadání (commit „Cviceni 7.6: rozbity kod ze zadani“).
Všechny příznaky jsem nejdřív zopakoval na emulátoru (Pixel 8, API 36), pak opravil a ověřil znovu.

| # | Chyba (příznak) | Vinný řádek | Proč se to děje | Oprava |
|---|---|---|---|---|
| A | Do pole nejde psát: klávesnice vyjede, kurzor bliká, ale písmena se neobjeví. Tlačítko **Přidat** pak nepřidá nic. | ř. 30 `var novyNazev = ""` (zapisuje se do ní na ř. 37, čte na ř. 44–46) | `novyNazev` je obyčejná lokální proměnná, ne sledovaný stav. Zápis do ní nespustí rekompozici, takže pole dál dostává `value = ""`. Pole se po každém písmenu vrátí na `value` a samo zavolá `onValueChange("")` – Logcat ukázal `onValueChange: [Z]` a hned potom `onValueChange: []`. Proměnná tak drží písmeno jen asi 80 ms. **Přidat** čte tutéž proměnnou, najde `""`, `isNotBlank()` je `false` a nestane se nic. (I kdyby se trefilo do těch 80 ms, přidalo by jen jedno písmeno a další rekompozice by proměnnou vytvořila znovu jako `""`.) | `var novyNazev by rememberSaveable { mutableStateOf("") }` – zápis změní stav, Compose překreslí pole. Díky `rememberSaveable` text přežije i otočení. Teď funguje i `novyNazev = ""` po přidání. |
| B | Barevná ploška těsně obepíná písmena „Počet měst: 4“ a kolem ní je 8 dp nevybarvené mezery. | ř. 55–57 `.padding(8.dp).background(…)` | Modifikátory se aplikují zvenku dovnitř v pořadí zápisu. Nejdřív se ubere 8 dp okraje a teprve zbytek (přesně velikost textu) se vybarví. Odsazení je tak **vně** barvy. | Pořadí: vnější okraj → barva → vnitřní odsazení: `.padding(vertical = 8.dp).background(barva, MaterialTheme.shapes.small).padding(horizontal = 12.dp, vertical = 6.dp)`. Podstatné je, že `padding` uvnitř je **za** `background`. |
| C | Rozbalím „Ostrava“, smažu „Zlín“. Zlín zmizí, ale rozbalená je najednou „Olomouc“ a Ostrava je zabalená. | ř. 61 `items(mesta) { nazev ->` (stav na ř. 62 `var rozbaleno by remember { … }`) | Bez `key` váže `LazyColumn` stav položky (`remember` uvnitř `items`) na **pozici**. Ostrava byla na pozici 2. Po smazání Zlína se všechno posune o jednu nahoru a na pozici 2 je Olomouc, která převezme „rozbaleno = true“. Chyba se projeví jen při mazání položky **nad** rozbalenou. | `items(items = mesta, key = { nazev -> nazev })` – stav se váže na město. Název je klíč jen proto, že tady je položka `String`. Dva stejné názvy by aplikaci shodily (`Key "Brno" was already used`), proto tlačítko Přidat nově odmítne i duplicitu (`trim()` + `ignoreCase`). Správné řešení je vlastní `id`, jako má `Mesto` v `MestaScreen.kt`. |
| D | V kódu jsou texty rozhraní v uvozovkách. Studio na ně neupozorní, aplikace funguje, ale nejde přeložit. | ř. 38 `"Název města"`, ř. 49 `"Přidat"`, ř. 53 `"Počet měst: …"`, ř. 73 `"Smazat"`, ř. 78 `"Teplotu doplníme v lekci 11"` | Lint hlídá natvrdo zapsané texty jen v XML rozvrženích, ne v Compose. Text v Kotlinu se nedostane do `values-en/strings.xml`, takže v angličtině zůstane česky. Porušuje to pravidlo z lekce 06 (všechny texty ve zdrojích). | Všechny texty přes `stringResource(…)`: `mesto_pole_label`, `mesto_pridat`, `mesto_smazat` (nový, cs + en), `mesto_teplota_neznama`. Počet přes plurál `mesta_pocet` (`pluralStringResource`), takže česky „1 město / 3 města / 5 měst“, anglicky „1 city / 3 cities“. |

## Ověření po opravě (emulátor)

- A: „Otrokovice“ se v poli objeví, **Přidat** ji přidá a pole se vyprázdní.
- B: odznak „3 města“ má barevnou plochu s vnitřním odsazením a odstup od okolí.
- C: rozbalená Ostrava zůstane rozbalená i po smazání Zlína.
- D: `Ctrl+F` na `Text("` v souboru nenajde nic.

Snímky: `docs/lekce-07/7-6-pred-A-B.png`, `7-6-pred-C.png` (před opravou) a `7-6-po-oprave.png`.

## Bonus: architektonická vada

I po opravě je celá obrazovka **jedna funkce, která stav drží i kreslí**. Proto ji nejde dát do `@Preview` se čtyřmi konkrétními městy, ani se seznamem v chybovém stavu: seznam si funkce vždy vytvoří sama uvnitř `remember`. Správně by měly být dvě funkce, stejně jako `MestaObrazovka` a `MestaObsah` v praktickém průchodu:

- **stavová** `RozbityMestaSeznam()` – jen `rememberSaveable`/`remember` a lambdy, které stav mění;
- **bezstavová** `MestaSeznamObsah(mesta, novyNazev, onNovyNazevChange, onPridat, onSmazat, modifier)` – jen kreslí z parametrů a hlásí události nahoru.

Náhled pak volá jen bezstavovou funkci s libovolnými daty. Navíc by se `rozbaleno` dalo vytáhnout jako `Set` rozbalených měst a obrazovka by měla celý stav na jednom místě.
