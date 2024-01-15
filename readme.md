# Sudoku solver

Nimmt sich ein Sudoku von sudoku.com mittels eines Web-Scrapers.
Das Programm ist in der Lage die Werte auszulesen und diese in einem 2D-Array zu speichern.
Auf diesem Array werden die Berechnungen durchgeführt.
Es wird von einer Zelle betrachtet, ob die Reihe nur einen Wert zulässt, aufgrund der möglichen Zahlen in den anderen Zellen der Reihe.
Dasselbe wird für die Spalte und Box durchgeführt.
Zuletzt wird geprüft, ob alle Zahlen bis auf eine bereits in der Reihe, Spalte oder Box vorkommt.
Die verbleibende Zahl wäre dann das Ergebnis der Zelle.


Das Programm ist zurzeit nur durch meine Sudoku Kenntnisse beschränkt.
Es ist in der Lage Sudokus des Levels "Hard" zu lösen, aber nicht jedes Mal.
"Expert" und "Master" sind noch nicht möglich.

## Todo 
- mehr Sudoku Techniken integrieren.