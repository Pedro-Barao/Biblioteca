package biblioteca;

public final class Validators {

    private Validators() {}

    /** Remove tudo que não é dígito. */
    public static String onlyDigits(String s) {
        return s == null ? "" : s.replaceAll("\\D", "");
    }

    /** Valida telefone BR com DDD; aceita 10 (fixo) ou 11 dígitos (celular). */
    public static boolean isValidPhoneBR(String s) {
        String d = onlyDigits(s);
        if (d.length() == 10) return true; // (DD) XXXX-XXXX
        if (d.length() == 11 && d.charAt(2) == '9') return true; // (DD) 9XXXX-XXXX
        return false;
    }

    /** Valida que tem ao menos 2 letras e apenas letras/espaços. */
    public static boolean isValidName(String s) {
        if (s == null) return false;
        if (!s.matches("[\\p{L} ]+")) return false;
        return s.trim().replaceAll(" +", " ").length() >= 2;
    }

    /** Valida ISBN-10 ou ISBN-13 (com ou sem hífens). */
    public static boolean isValidISBN(String s) {
        if (s == null) return false;
        String clean = s.replaceAll("-", "").toUpperCase();
        if (clean.length() == 10) return isValidIsbn10(clean);
        if (clean.length() == 13) return isValidIsbn13(clean);
        return false;
    }

    private static boolean isValidIsbn10(String s) {
        if (!s.matches("[0-9]{9}[0-9X]")) return false;
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (i + 1) * (s.charAt(i) - '0');
        }
        int checksum = sum % 11;
        char checkChar = checksum == 10 ? 'X' : (char)('0' + checksum);
        return checkChar == s.charAt(9);
    }

    private static boolean isValidIsbn13(String s) {
        if (!s.matches("[0-9]{13}")) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = s.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checksum = (10 - (sum % 10)) % 10;
        return checksum == (s.charAt(12) - '0');
    }



    /** Valida CPF (11 dígitos) com cálculo dos dígitos verificadores. */
    public static boolean isValidCPF(String s) {
        if (s == null) return false;
        String d = onlyDigits(s);
        if (d.length() != 11) return false;
        // rejeita sequências repetidas
        if (d.matches("(\\d)\\1{10}")) return false;

        int sum1 = 0, sum2 = 0;
        for (int i = 0; i < 9; i++) {
            int num = d.charAt(i) - '0';
            sum1 += num * (10 - i);
            sum2 += num * (11 - i);
        }
        int dv1 = (sum1 * 10) % 11;
        if (dv1 == 10) dv1 = 0;
        sum2 += dv1 * 2;
        int dv2 = (sum2 * 10) % 11;
        if (dv2 == 10) dv2 = 0;

        return dv1 == (d.charAt(9) - '0') && dv2 == (d.charAt(10) - '0');
    }
}