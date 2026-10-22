# Pitva – dekompilační protokol (lekce 04)

| # | Co jsem napsal | Generovaný člen (přesný podpis z dekompilátu) | Řádků v Javě ručně |
|---|---|---|---|
| 1 | `data class Zapis(mesto, teplota, poznamka = "")` | `getMesto()`, `getTeplota()`, `getPoznamka()`, `component1()`–`component3()`, `copy(String, double, String)`, `copy$default(...)`, `toString()`, `hashCode()`, `equals(Object)` | ~70 |
| 2 | `object Prevody` + `const val ABSOLUTNI_NULA` | `public static final Prevody INSTANCE`, `private Prevody()`, `naFahrenheita(double)` | ~10 |
| 3 | `fun Double.zaokrouhlenoNaDesetiny()` | `public static final double zaokrouhlenoNaDesetiny(double $this$zaokrouhlenoNaDesetiny)` | ~4 |
| 4 | `fun prumer(zapisy, vyber)` | `public static final double prumer(List, Function1)` | ~12 |
| 5 | `suspend fun nactiPoZdrzeni(...)` | `public static final Object nactiPoZdrzeni(List, Continuation)` | ~35 |

## Odpovědi

1. Dekompilovaná třída `Zapis` má 11 metod: `getMesto`, `getTeplota`, `getPoznamka`, `component1`, `component2`, `component3`, `copy`, statickou `copy$default`, `toString`, `hashCode` a `equals`. Ve zdrojáku Kotlinu není ani jedna z nich, vše dopsal kompilátor, protože jde o `data class`. Konstruktory jsem nepočítal.

2. Konstruktor s parametry `int` a `DefaultConstructorMarker` vznikl kvůli výchozí hodnotě `poznamka: String = ""`. JVM výchozí hodnoty parametrů nezná, a proto kompilátor vyrobí syntetický konstruktor s bitovou maskou, která říká, které argumenty volající nedodal. Chybějící argumenty se v něm doplní výchozími hodnotami. Stejný trik používá `copy$default`.

3. Prvním parametrem metody `zaokrouhlenoNaDesetiny` je `double $this$zaokrouhlenoNaDesetiny`, tedy samotné číslo, na kterém se funkce volá. Extension funkce je totiž jen statická metoda ve třídě `PitvaKt`, která dostane příjemce jako první parametr. Statická metoda nemůže být virtuální, takže funkce není polymorfní. O tom, kterou funkci zavolat, rozhoduje kompilátor podle deklarovaného typu při překladu, ne podle skutečného typu za běhu.

4. Druhý parametr funkce `prumer` má typ `Function1`. Je to rozhraní ze standardní knihovny Kotlinu s jedinou metodou `invoke`, takže funkce jako hodnota je na JVM obyčejný objekt a volá se přes `invoke`. Typ `(Zapis) -> Double` se tak v bajtkódu změní na `Function1`.

5. Návratový typ funkce `nactiPoZdrzeni` se změnil z `List<Zapis>` na `Object` a přibyl skrytý parametr `Continuation`. Funkce totiž může vrátit buď hotový výsledek, nebo značku `COROUTINE_SUSPENDED`, když se na `delay` pozastaví. Kompilátor z ní udělal stavový automat: pole `label` si pamatuje, kde se skončilo, a `switch` podle něj po obnovení skočí na správné místo.