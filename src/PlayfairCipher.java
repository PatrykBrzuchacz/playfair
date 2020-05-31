import java.awt.Point;
import java.util.Scanner;

public class PlayfairCipher {
    private static char[][] charTable;
    private static Point[] positions;
    private static String secretKey;
    private static String text;
    private static boolean replaceJWithI;

    public static void main(String[] args) {
        onInit();
        createTable(secretKey, replaceJWithI);
        String enc = encode(prepareText(text, replaceJWithI));
        System.out.printf("%nEncoded message: %n%s%n", enc);
        System.out.printf("%nDecoded message: %n%s%n", decode(enc));
    }

    private static void onInit(){
        Scanner sc = new Scanner(System.in);
         secretKey = prompt("Enter an encryption secretKey (min length 6): ", sc, 6);
         text = prompt("Enter the message: ", sc, 1);
        //Ze względu na fakt, że liter alfabetu łacińskiego jest 26 a w tablicy mieści się 25 wybierz czy chcesz zastąpić jakąś literę inną literą (najczęściej J->I)
        replaceJWithI = prompt("Replace J with I? y/n: ", sc, 1).equalsIgnoreCase("y");
    }

    private static String prompt(String promptText, Scanner sc, int minLen) {
        String s;
        do {
            System.out.print(promptText);
            s = sc.nextLine().trim();
        } while (s.length() < minLen);
        return s;
    }

    private static String prepareText(String s, boolean changeJtoI) {
        //usuwamy jedna litere, jezeli chce zamienic j na i to zamieniamy, w przeciwnym wypadku domyslnie usuwamy Q
        s = s.toUpperCase().replaceAll("[^A-Z]", "");
        return changeJtoI ? s.replace("J", "I") : s.replace("Q", "");
    }

    private static void createTable(String key, boolean changeJtoI) {
        charTable = new char[5][5];
        positions = new Point[26];

        String s = prepareText(key + "ABCDEFGHIJKLMNOPQRSTUVWXYZ", changeJtoI);
//uzupelnianie tabeli 5x5, ktora ma zawierac wszystkie litery od A do Z, pierwszo wpisujemy do niej poziomo nasz key,
// nastepnie wpisujemy reszte od A do Z, jezeli ktoras z tych liter, ktore teraz wpisujemy znajduje sie w key (czyli
// jest juz w tej tabeli 5x5, to ją nie wpisujemy i opuszczamy, bierzemy nastepna)
        int len = s.length();
        for (int i = 0, k = 0; i < len; i++) {
            char c = s.charAt(i);
            //sprawdzamy czy pozycja literka jest juz w tabeli
            if (positions[c - 'A'] == null) {
                //nie jest, wiec ustawiamy ją pokolei ([k/5],[k%5] - w petli daje nam to iterowanie [0,0], [1,0]...
                // [0,1] [1,1] itd,
                charTable[k / 5][k % 5] = c;
                positions[c - 'A'] = new Point(k % 5, k / 5);
                k++;
            }
        }
    }

    private static String encode(String s) {
        StringBuilder sb = new StringBuilder(s);


        for (int i = 0; i < sb.length(); i += 2) {

            //dodaje X na koncu jesli nie jest parzyste
            if (i == sb.length() - 1)
                sb.append(sb.length() % 2 == 1 ? 'X' : "");
            //dodaje X pomiedzy dwie takie same litery, jesli sa ze soba w parze
            else if (sb.charAt(i) == sb.charAt(i + 1))
                sb.insert(i + 1, 'X');
        }
        return codec(sb, 1);
    }

    private static String decode(String s) {
        return codec(new StringBuilder(s), 4);
    }

    private static String codec(StringBuilder text, int direction) {
        //direction - jezeli robimy encode, to gdy sa w tym samym wierszu/kolumnie, bierzemy jedna dalej, jezeli
        // robimy decode, to robimy 4 kroki w tyl
        int len = text.length();
        for (int i = 0; i < len; i += 2) {
            //bierzemy pare liczb
            char a = text.charAt(i);
            char b = text.charAt(i + 1);

            int row1 = positions[a - 'A'].y;
            int row2 = positions[b - 'A'].y;
            int col1 = positions[a - 'A'].x;
            int col2 = positions[b - 'A'].x;

            //jezeli litery sa w tym samym wierszu, bierzemy te z nastepnego wiersza lub w przypadku decode - 4 w tyl
            if (row1 == row2) {
                col1 = (col1 + direction) % 5;
                col2 = (col2 + direction) % 5;

            }
            //jezeli litery sa w tym samej columnie, bierzemy te z nastepnej columny lub w przypadku decode - 4 w tyl
            else if (col1 == col2) {
                row1 = (row1 + direction) % 5;
                row2 = (row2 + direction) % 5;

            } else {
                //jezeli sa w innej kolumnie oraz wierszu, robimy boxa i bierzemy z przeciecia, to troszke do
                // dogadania w jaki sposob najlepiej to wytlumaczyc, moze lepiej sobie to narysowac nawet, to co sie
                // dzieje po zamienieniu wartosci kolumn
                int tmp = col1;
                col1 = col2;
                col2 = tmp;
            }

            text.setCharAt(i, charTable[row1][col1]);
            text.setCharAt(i + 1, charTable[row2][col2]);
        }
        return text.toString();
    }
}