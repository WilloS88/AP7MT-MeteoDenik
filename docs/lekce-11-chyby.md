# Lekce 11 – cvičení 11.2: tři stavy a různé chyby

Aplikace má tři stavy seznamu: **načítání** (lineární indikátor nad seznamem), **úspěch** (teploty bez hlášky) a **chyba** (text ze `strings.xml` a tlačítko „Zkusit znovu“). Už načtená data se při chybě neschovávají, chybový řádek se zobrazí nad nimi.

Každou chybu jsem vyvolal na emulátoru (Pixel 8, API 36). Úpravy kódu potřebné k vyvolání jsem po každém pokusu vrátil, odevzdaná verze je beze změn.

| # | Jak vyvoláno | Výjimka (Logcat) | `DuvodSelhani` | Hláška v aplikaci | Snímek |
|---|---|---|---|---|---|
| A | letadlový režim, klepnutí na „Obnovit“ | `ConnectException` a `UnknownHostException` | `BEZ_PRIPOJENI` | Nejste připojeni k internetu. Zkontrolujte Wi-Fi nebo mobilní data. | `11-2-a-letadlovy-rezim.png` |
| B | v `Sit.kt` dočasně `readTimeout(1, MILLISECONDS)` | `SocketTimeoutException: Read timed out` | `SERVER_NEODPOVIDA` | Server neodpovídá. Zkuste to prosím za chvíli. | `11-2-b-timeout.png` |
| C | v `MeteoApi.kt` dočasně `@GET("v1/neexistuje")` | HTTP 404 (`HttpException`) | `CHYBA_POZADAVKU` | Data se nepovedlo načíst. Zkuste město odebrat a přidat znovu. | `11-2-c-404.png` |
| D | v DTO dočasně `@SerialName("temp")` místo `temperature_2m` | HTTP 200, pak `MissingFieldException` (`SerializationException`) | `NEPLATNA_ODPOVED` | Server poslal neočekávanou odpověď. Pokud jste na veřejné Wi-Fi, přihlaste se do ní. | `11-2-d-neplatna-odpoved.png` |

Řádky z Logcatu k jednotlivým chybám jsou v `lekce-11/11-2-logcat.txt`.

## Hlášky se liší

Čtyři různé příčiny dávají čtyři různé hlášky, protože uživatel má dělat něco jiného. Při chybějícím připojení zkontroluje síť, při pomalém serveru počká, při 404 nemá smysl zkoušet znovu a při poškozené odpovědi může být za přihlašovací stránkou veřejné Wi-Fi. Kdyby byla hláška pořád stejná, uživatel by nevěděl, co má dělat.

## Obnovení po výpadku

Po vypnutí letadlového režimu a klepnutí na „Zkusit znovu“ se data načetla a chybový řádek zmizel (`11-2-a-po-obnoveni-site.png`).

## Co jsem při měření zjistil

V letadlovém režimu první požadavek často skončí `ConnectException` (mobilní systém ještě drží otevřené spojení), a teprve další `UnknownHostException`. Podle zadání se tehdy ukázala hláška „Spojení se nepovedlo dokončit“, protože `ConnectException` spadne do obecné `IOException`. Pro uživatele je to ale stejná situace jako chybějící připojení, proto `bezpecneVolani` mapuje i `ConnectException` na `BEZ_PRIPOJENI`. Pořadí `catch` zůstává: `CancellationException` první a `IOException` poslední.

Pozor: `UnknownHostException` vznikne i při překlepu v doméně, proto jsem offline chování ověřoval letadlovým režimem a ne špatnou adresou.

## Snímky z Network Inspectoru

Network Inspector je okno Android Studia (View, Tool Windows, App Inspection), které jsem v tomto prostředí nemohl použít. Stejné údaje (URL, kód, doba, velikost, `Content-Type`) jsou v `lekce-11/11-1-sit-logcat.txt`, vypsané `HttpLoggingInterceptor` v debug buildu.
