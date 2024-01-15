package org.example;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.remote.Augmenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {

        int[][] sudoku = new int[9][9];
        getSudokuValues("https://sudoku.com/", sudoku);

        int counter = 0;
        int failure = 0;

        // 81 because that is the maximum amount of values in a normal sudoku
        while (counter < 81){

            failure = counter;
            counter = 0;

            for(int i = 0; i < 9; i++){

                for(int j = 0; j < 9; j++){

                    if(sudoku[i][j] != 0){
                        // Increase the counter for every previous known or solved value
                        counter++;
                        continue;
                    }
                    // solve the sudoku
                    if(calculateRow(sudoku, i, j)) continue;
                    if(calculateColumn(sudoku, i, j)) continue;
                    if(calculateField(sudoku, i, j)) continue;
                    checkLastPossibleNumber(sudoku, i, j);

                }
            }


            // if the loop goes an through every cell without solving a value, stop the loop
            if(failure == counter){
                break;
            }
        }
        System.out.println("Gelöste Zahlen: " + counter);

        printSudoku(sudoku);
    }


    /**
     * Gibt die nicht verwendeten Zahlen dieser Reihe, als ein Set<Integer>, aus.
     * @param sudoku
     * @param row
     * @return
     */
    public static Set<Integer> checkRow(int[][] sudoku, int row){


        Set<Integer> numbers = new HashSet<>();
        Set<Integer> result = new HashSet<>();

        for(int number : sudoku[row]){
            numbers.add(number);
        }


        for(int i = 1; i <= 9; i++){
            if(!numbers.contains(i)){
                result.add(i);
            }
        }

        return result;
    }
    /**
     * Gibt die nicht verwendeten Zahlen dieser Spalte, als ein Set<Integer>, aus.
     * @param sudoku
     * @param column
     * @return
     */
    public static Set<Integer> checkColumn(int[][] sudoku, int column){
        Set<Integer> numbers = new HashSet<>();
        Set<Integer> result = new HashSet<>();

        for(int i = 0; i < 9; i++){
            numbers.add(sudoku[i][column]);
        }

        for(int i = 1; i <= 9; i++){
            if(!numbers.contains(i)){
                result.add(i);
            }
        }

        return result;
    }

    /**
     * Gibt die nicht verwendeten Zahlen dieser Box, als ein Set<Integer>, aus.
     * @param sudoku
     * @param row
     * @param column
     * @return
     */
    public static Set<Integer> checkField(int[][] sudoku, int row, int column){

        Set<Integer> numbers = new HashSet<>();
        Set<Integer> result = new HashSet<>();
        int rowBegin = (row / 3) * 3;
        int columnBegin = (column / 3) * 3;


        for(int i = rowBegin; i < rowBegin+3; i++){

            for(int j = columnBegin; j <columnBegin+3; j++){
                numbers.add(sudoku[i][j]);
            }
        }



        for(int i = 1; i <= 9; i++){
            if(!numbers.contains(i)){
                result.add(i);
            }
        }

        return result;

    }

    public static Set<Integer> checkCell(int[][] sudoku, int row, int column){

        Set<Integer> rowSet = checkRow(sudoku, row);
        Set<Integer> columnSet = checkColumn(sudoku, column);
        Set<Integer> fieldSet = checkField(sudoku, row, column);

        Set<Integer> temp = new HashSet<>();


        // Schaut ob die möglichen Nummern bereits in den anderen Sets vorhanden sind
        // Dann kommen diese ins temp Set um nachher entfernt zu werden
        for(int number : rowSet){
            if(!columnSet.contains(number)){
                temp.add(number);
            }
            if(!fieldSet.contains(number)){
                temp.add(number);
            }
        }

        for(int number : columnSet){
            if(!rowSet.contains(number)){
                temp.add(number);
            }

            if(!fieldSet.contains(number)){
                temp.add(number);
            }
        }

        for(int number : fieldSet){
            if(!rowSet.contains(number)){
                temp.add(number);
            }

            if(!columnSet.contains(number)){
                temp.add(number);
            }
        }

        rowSet.removeAll(temp);
        columnSet.removeAll(temp);
        fieldSet.removeAll(temp);

/*        if(rowSet.size() == 1){
            sudoku[row][column] = (int) rowSet.toArray()[0];
        }*/

        return rowSet;

    }

    public static boolean cellIsNotZero(int[][] sudoku, int row, int column){
        return sudoku[row][column] != 0;
    }

    /**
     * Zuerst wird geprüft, welche Zahlen für die angegebene Zelle möglich sind.
     * Dasselbe wird für jede andere Zelle in der Zeile gemacht und von dem ersten Set entfernt.
     * Übrig bleibt idealerweise eine einzige Zahl.
     * @param sudoku
     * @param row
     * @param column
     * @return True, wenn ein Ergebnis gefunden wurde. Ansonsten false.
     */
    public static boolean calculateRow(int[][] sudoku, int row, int column){

        // Gehe die Zeile durch und entferene die möglichen Nummern von diesem Set
        Set<Integer> resultSet = checkCell(sudoku, row, column);

        for(int i = 0; i < 9; i++){
            if(cellIsNotZero(sudoku, row, i) || i == column){
                continue;
            }
            resultSet.removeAll(checkCell(sudoku, row, i));
        }


        // Es ist egal welches Set ausgegeben wird, weil die möglichen Zahlen kommen in allen Sets vor.

        if(resultSet.size() == 1){
            sudoku[row][column] = (int) resultSet.toArray()[0];
            return true;
        }
        return false;
    }

    /**
     * Zuerst wird geprüft, welche Zahlen für die angegebene Zelle möglich sind.
     * Dasselbe wird für jede andere Zelle in der Spalte gemacht und von dem ersten Set entfernt.
     * Übrig bleibt idealerweise eine einzige Zahl.
     * @param sudoku
     * @param row
     * @param column
     * @return True, wenn ein Ergebnis gefunden wurde. Ansonsten false.
     */
    public static boolean calculateColumn(int[][] sudoku, int row, int column){

        // Gehe die Column durch und entferene die möglichen Nummern von diesem Set
        Set<Integer> resultSet = checkCell(sudoku, row, column);

        for(int i = 0; i < 9; i++){
            if(cellIsNotZero(sudoku, i, column) || i == row){
                continue;
            }
            resultSet.removeAll(checkCell(sudoku, i, column));
        }


        // Es ist egal welches Set ausgegeben wird, weil die möglichen Zahlen kommen in allen Sets vor.

        if(resultSet.size() == 1){
            sudoku[row][column] = (int) resultSet.toArray()[0];
            return true;
        }
        return false;
    }

    /**
     * Zuerst wird geprüft, welche Zahlen für die angegebene Zelle möglich sind.
     * Dasselbe wird für jede andere Zelle in der Box gemacht und von dem ersten Set entfernt.
     * Übrig bleibt idealerweise eine einzige Zahl.
     * @param sudoku
     * @param row
     * @param column
     * @return True, wenn ein Ergebnis gefunden wurde. Ansonsten false.
     */
    public static boolean calculateField(int[][] sudoku, int row, int column){

        // Gehe die Box durch und entferene die möglichen Nummern von diesem Set
        Set<Integer> resultSet = checkCell(sudoku, row, column);

        int rowBegin = (row / 3) * 3;
        int columnBegin = (column / 3) * 3;


        for(int i = rowBegin; i < rowBegin+3; i++){

            for(int j = columnBegin; j <columnBegin+3; j++){
                if(cellIsNotZero(sudoku, i, j) || (i == row && j == column)){
                    continue;
                }
                resultSet.removeAll(checkCell(sudoku, i, j));
            }
        }


        // Es ist egal welches Set ausgegeben wird, weil die möglichen Zahlen kommen in allen Sets vor.

        if(resultSet.size() == 1){
            sudoku[row][column] = (int) resultSet.toArray()[0];
            return true;
        }
        return false;
    }

    /**
     * Wenn jede Zahl außer einer in der Reihe, Spalte oder Box der angegebenen Zelle vorhanden ist, dann ist die verbleibende Zahl das Ergebnis dieser Zelle.
     * @param sudoku
     * @param row
     * @param column
     * @return
     */
    public static boolean checkLastPossibleNumber(int[][] sudoku, int row, int column){
        Set<Integer> numbers = new HashSet<>();

        for(int number : sudoku[row]){
            numbers.add(number);
        }

        for(int i = 0; i < 9; i++){
            numbers.add(sudoku[i][column]);
        }
        int rowBegin = (row / 3) * 3;
        int columnBegin = (column / 3) * 3;


        for(int i = rowBegin; i < rowBegin+3; i++){

            for(int j = columnBegin; j <columnBegin+3; j++){
                numbers.add(sudoku[i][j]);
            }
        }
        numbers.remove(0);

        if(numbers.size() == 8){
            for(int i = 1; i <= 9; i++){
                if(!numbers.contains(i)){
                    sudoku[row][column] = i;
                    return true;
                }
            }
        }
        return false;
    }

    public static void printSudoku(int[][] sudoku){

        System.out.println("_______________________________");
        printSudokuline(sudoku, 0);
        System.out.println("                               ");
        printSudokuline(sudoku, 1);
        System.out.println("                               ");
        printSudokuline(sudoku, 2);
        System.out.println("_______________________________");
        System.out.println("                               ");
        printSudokuline(sudoku, 3);
        System.out.println("                               ");
        printSudokuline(sudoku, 4);
        System.out.println("                               ");
        printSudokuline(sudoku, 5);
        System.out.println("_______________________________");
        System.out.println("                               ");
        printSudokuline(sudoku, 6);
        System.out.println("                               ");
        printSudokuline(sudoku, 7);
        System.out.println("                               ");
        printSudokuline(sudoku, 8);

    }

    public static void printSudokuline(int[][] sudoku, int row){
        System.out.print("|" +   sudoku[row][0]+ "|" + sudoku[row][1]+ "|" + sudoku[row][2]+ "|    ");
        System.out.print("|" +   sudoku[row][3]+ "|" + sudoku[row][4]+ "|" + sudoku[row][5]+ "|    ");
        System.out.println("|" + sudoku[row][6]+ "|" + sudoku[row][7]+ "|" + sudoku[row][8]+ "|");
    }


    public static void getSudokuValues(String url, int[][] sudoku){
/*        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-profile", "C:/Users/B/AppData/Roaming/Mozilla/Firefox/Profiles/w9gazlvi.default-release");
        //options.addArguments("-headless");
        WebDriver driver = new FirefoxDriver(options);*/

        // Setup of the browser
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=C:\\Users\\B\\AppData\\Local\\Google\\Chrome\\User Data");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get("https://sudoku.com/");

        WebElement element = driver.findElement(By.className("game-controls-pencil"));
        element.click();
        element.click();

        // Get the sudoku values from the local storage
        WebStorage webStorage = (WebStorage) new Augmenter().augment(driver);
        LocalStorage localStorage = webStorage.getLocalStorage();
        String sudokuValues = localStorage.getItem("main_game");


        // The value in the localstorage is a long string, so we grab only the values we need with a regex
        List<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile("\"val\":\\d")
                .matcher(sudokuValues);
        while (m.find()) {
            allMatches.add(m.group());
        }

        // one additional value is grabbed by the regex we need to get rid of
        allMatches.remove(allMatches.size()-1);

        // Convert all values to integers
        List<Integer> numbers = new ArrayList<>();
        for(String value : allMatches){
            numbers.add(Integer.parseInt(String.valueOf(value.charAt(6))));
        }


        // Fill the sudoku
        int row = 0;
        int column = 0;
        for(int number : numbers){
            sudoku[row][column] = number;
            column++;
            if(column == 9){
                column = 0;
                row++;
            }
        }

        // This is optional to see how many values were originally present
        while(numbers.contains(0)){
            numbers.remove(numbers.indexOf(0));
        }
        System.out.println("Anfangs bekannte Zahlen: " + numbers.size());
        //driver.quit();

    }
}