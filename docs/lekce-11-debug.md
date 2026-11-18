# Lekce 11 – cvičení 11.6: šest chyb v jedné síťové vrstvě

Soubor: `app/src/main/java/cz/utb/meteodenik/rozbite/RozbitaSit.kt`.
Čísla řádků odkazují na **rozbitou verzi** ze zadání (commit „Cviceni 11.6: rozbity kod ze zadani“).
Zadání nedodává žádnou obrazovku, proto je v balíčku `rozbite` ještě malá `RozbitaObrazovka`, která umožňuje chyby spustit.
Všechny příznaky jsem postupně opravoval a po každé opravě znovu spustil na emulátoru (Pixel 8, API 36).

Důležité upozornění: zadaný `nacti()` obaluje volání do `catch (e: Exception)`, který výjimku **spolkne**. Aplikace proto při žádné z chyb 1 až 5 nespadne (kromě chybějícího oprávnění, viz chyba 4), jen ukáže „-“ a v Logcatu je ticho. Abych výjimky viděl, přidal jsem při zkoumání dočasně do `catch` řádek `Log.e`. V odevzdané verzi není.

| # | Příznak | Vinná deklarace / řádek | Mechanismus | Oprava |
|---|---|---|---|---|
| 1 | `IllegalArgumentException: Unable to create call adapter for class …RozbitePredpovedDto`. Po přidání `suspend` se kód nepřeloží (`Suspend function … can only be called from a coroutine`). | ř. 32 `fun aktualniPocasi(…): RozbitePredpovedDto` v `RozbitaApi` a ř. 52 `nacti()` | Retrofit u metody bez `suspend` čeká návratový typ `Call<T>`. Obyčejný objekt neumí obalit, tak se při prvním zavolání vzdá. Po přidání `suspend` kompilátor správně odmítne volat ji mimo korutinu. | `suspend fun aktualniPocasi(…)` a volání z `viewModelScope.launch { … }`. |
| 2 | `IllegalArgumentException: Unable to create converter for class …RozbitePredpovedDto`, `Serializer for class … is not found` | ř. 15 až 28 DTO bez `@Serializable` | Převodník potřebuje serializátor, který generuje kompilátorový plugin jen pro třídy s `@Serializable`. Bez ní se projekt přeloží a selže až za běhu při prvním volání. Anotaci potřebuje každá třída v hierarchii. | `@Serializable` na všech třech DTO. |
| 3 | `UnknownServiceException: CLEARTEXT communication to api.open-meteo.com not permitted by network security policy` | ř. 41 `baseUrl("http://api.open-meteo.com/")` | Android od verze 9 zakazuje nešifrovaný provoz. Politika sítě se kontroluje **dřív než DNS**, proto tato chyba zakryje ostatní. | `https://`. |
| 4 | `SecurityException: Permission denied (missing INTERNET permission?)` s příčinou `EPERM`. Aplikace spadne ve vlákně `OkHttp Dispatcher`. | `AndroidManifest.xml`, chybí `<uses-permission android:name="android.permission.INTERNET" />` | Bez oprávnění systém zakáže vytvoření socketu. Chyba není ani v Kotlinu, ani v `Json { }`. Hláška se podobá výpadku sítě (`EAI_NODATA`, „No address associated with hostname“), protože překlad jména selže ze stejného pohledu jako bez internetu. Ověří se tím, že `curl` a prohlížeč na emulátoru fungují a aplikace ne. | Oprávnění `INTERNET` v manifestu. |
| 5 | `JsonDecodingException: Encountered an unknown key 'longitude'` | ř. 42 `Json.asConverterFactory(…)` s výchozím `Json` | Server posílá víc polí, než DTO zná (`longitude`, `elevation`, …). Výchozí `Json` je přísný a neznámý klíč bere jako chybu. | `Json { ignoreUnknownKeys = true }`. |
| 6 | Bez chybové hlášky. Když uživatel odejde z obrazovky během načítání, ViewModel i tak zapíše výsledek. | ř. 56 `catch (e: Exception)` v `nacti()` | `CancellationException` je podtyp `Exception`, takže ji `catch` chytí a spolkne. Korutina si myslí, že je vše v pořádku, a pokračuje, ač ji systém rušil (ověřeno: po odchodu z obrazovky se v `catch` objevila `JobCancellationException: Job was cancelled`). Zapisuje pak do `MutableStateFlow` po zániku obrazovky a obrazovka, na kterou uživatel mezitím přešel, může dostat cizí stav. Stack trace chybí, protože se výjimka nikde nevypíše. | Žádné `catch (e: Exception)`. Chyby sítě řeší `bezpecneVolani`, ve kterém je `CancellationException` první a vždy se vyhodí dál. |

## Poznámky k měření

- **Chyba 4 se s touto sadou knihoven sama neprojeví.** OkHttp 5 (`okhttp-android`) deklaruje `INTERNET` ve vlastním manifestu a ten se při sestavení sloučí do manifestu aplikace. Chybějící oprávnění jsem proto musel vynutit dočasným `tools:node="remove"`. Po té úpravě se objevil pád výše. S oprávněním v manifestu je pád pryč.
- **Pořadí chyb.** S `http://` a bez oprávnění se dříve objeví `CLEARTEXT` (chyba 3) a oprávnění (chyba 4) je vidět až po opravě na `https://`. Zadání uvádí obě v opačném pořadí.

## Stavy po opravě

Opravená `RozbityViewModel` drží stav `Nacitam`, `Hotovo`, `Chyba`. Kolečko a data se tak nikdy neukážou zároveň. Bez sítě se zobrazí chybová obrazovka s tlačítkem „Zkusit znovu“ (`11-6-po-oprave-chyba.png`), po zapnutí sítě tlačítko data načte (`11-6-po-oprave-data.png`) a v Logcatu není žádná výjimka.
