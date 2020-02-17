# bad-seqence
Zadanie do rozwiązania w procesie rekrutacji na stanowisko programisty Java

# Wstęp
Sekwencja to jeden z najistotniejszych elementów systemów przetwarzających dane. Odpowiedzialna jest za generowanie 
kolejnych, unikalnych wartości liczbowych najczęściej wykorzystywanych jako identyfikatory danych składowanych 
w relacyjnej bazie. Narażona jest na wielowątkowe, konkurencyjne żądania stąd prawidłowa, optymalna jej implementacja 
stanowi podstawę sprawnego systemu.

# Zadanie
W projekcie można znaleźć klasę abstrakcyjną `pl.scisoftware.sample.bad.seqence.MSequenceSupport` zawierającą metody 
inkrementacji wartości sekwencji o podanej nazwie. 
1. Zaproponuj implementację mechanizmu sekwencji opartego o wskazaną abstrakcję, która składować będzie dane sekwencji 
   w relacyjnej bazie danych.
2. Opisz własnymi słowami ideę przyświecającą implementacji algorytmu generacji kolejnego numeru zawartą w metodzie
   `public Object incremetSequenceId(String seqClazzName, final Long value) throws MercuryException;` 
3. Napisz test sekwencji uwzględniający scenariusz, w którym dwa niezależne JVM (Maszyny Wirtualne Java) korzystać będą 
   z jednej sekwencji (sekwencji o tej samej nazwie) i dla których generowane będą unikalne wartości.
4. Znajdź błąd w implementacji metody w klasie abstrakcyjnej: 
   `public Object incremetSequenceId(String seqClazzName, final Long value) throws MercuryException;`
   Wskazówka: Błąd nie pozwala na przejście scenariusza testowego opisanego w pkt. 2
 
Czas na realizację zadania: 5 dni.