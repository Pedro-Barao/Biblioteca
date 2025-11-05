package biblioteca;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.animation.PauseTransition; 
import javafx.util.Duration;
import java.util.function.UnaryOperator;



/** Utilidades de formatação/máscara para campos JavaFX. */
public final class Formatters {

    
   // Mantém um Tooltip por campo
    private static Tooltip ensureTooltip(TextField field) {
        Tooltip tip = (Tooltip) field.getProperties().get("maskTooltip");
        if (tip == null) {
            tip = new Tooltip();
            tip.setAutoHide(true);
            field.getProperties().put("maskTooltip", tip);
        }
        return tip;
    }

       // Reaproveita timers por campo
    private static PauseTransition ensureTimer(TextField field, String key) {
        PauseTransition t = (PauseTransition) field.getProperties().get(key);
        if (t == null) {
            t = new PauseTransition();
            field.getProperties().put(key, t);
        }
        return t;
    }

    /** Telefone com eco visual usando timers (sem setShowDelay/setHideDelay). */
    public static void applyPhoneMaskEcho(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String digits = change.getControlNewText().replaceAll("\\D", "");
            if (digits.length() > 11) digits = digits.substring(0, 11);

            String formatted;
            if (digits.length() <= 2) {
                formatted = "(" + digits;
            } else if (digits.length() <= 6) {
                formatted = "(" + digits.substring(0,2) + ") " + digits.substring(2);
            } else if (digits.length() <= 10) {
                formatted = "(" + digits.substring(0,2) + ") " + digits.substring(2,6) + "-" + digits.substring(6);
            } else {
                formatted = "(" + digits.substring(0,2) + ") " + digits.substring(2,7) + "-" + digits.substring(7);
            }

            // Eco visual
            Tooltip tip = ensureTooltip(field);
            String typed = change.getText();
            String lastChar = (typed == null || typed.isEmpty()) ? "" : typed.substring(typed.length()-1);
            tip.setText("len=" + digits.length() + (lastChar.isEmpty() ? "" : " • tecla: '" + lastChar + "'"));

            // Timers de mostrar e esconder
            PauseTransition showTimer = ensureTimer(field, "maskTipShow");
            PauseTransition hideTimer = ensureTimer(field, "maskTipHide");

            showTimer.stop();
            hideTimer.stop();

            // pequeno delay pra abrir (evita piscar a cada tecla)
            showTimer.setDuration(Duration.millis(120));
            showTimer.setOnFinished(ev -> {
                try {
                    javafx.geometry.Bounds b = field.localToScreen(field.getBoundsInLocal());
                    if (b != null) tip.show(field, b.getMinX(), b.getMaxY() + 2);
                } catch (Exception ignore) {}
            });
            showTimer.playFromStart();

            // fecha 600 ms após a última tecla
            hideTimer.setDuration(Duration.millis(600));
            hideTimer.setOnFinished(ev -> tip.hide());
            hideTimer.playFromStart();

            // aplica formatação
            change.setText(formatted);
            change.setRange(0, change.getControlText().length());
            return change;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }
    
    private Formatters() {}

    /** Permite apenas letras (com acentos) e espaços. */
    public static void applyNameLettersOnly(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // \p{L} = qualquer letra Unicode; permite espaços simples
            if (newText.matches("[\\p{L} ]*")) {
                return change;
            }
            return null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }

    /** Mascara telefone BR: (DD) 9XXXX-XXXX ou (DD) XXXX-XXXX. Aceita apenas dígitos na digitação. */
    public static void applyPhoneMask(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText().replaceAll("\\D", ""); // mantém só dígitos
            if (text.length() > 11) text = text.substring(0, 11);

            String formatted;
            if (text.length() <= 2) {
                formatted = "(" + text;
            } else if (text.length() <= 6) { // (DD) XXXX
                formatted = "(" + text.substring(0,2) + ") " + text.substring(2);
            } else if (text.length() <= 10) { // (DD) XXXX-XXXX
                formatted = "(" + text.substring(0,2) + ") " + text.substring(2,6) + "-" + text.substring(6);
            } else { // 11 dígitos: (DD) 9XXXX-XXXX
                formatted = "(" + text.substring(0,2) + ") " + text.substring(2,7) + "-" + text.substring(7);
            }

            // Força substituição do texto inteiro
            change.setText(formatted);
            change.setRange(0, change.getControlText().length());
            return change;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }

    /** Permite apenas dígitos e hífen/X para ISBN-10/13; não formata, só bloqueia lixo. */
    public static void applyIsbnFilter(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9xX-]*")) {
                return change;
            }
            return null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }



    /** Máscara CPF: 000.000.000-00 (limita a 11 dígitos). */
    public static void applyCpfMask(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            // Texto com a edição aplicada (antes da formatação)
            String onlyDigits = change.getControlNewText().replaceAll("\\D", "");
            if (onlyDigits.length() > 11) onlyDigits = onlyDigits.substring(0, 11);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < onlyDigits.length(); i++) {
                char c = onlyDigits.charAt(i);
                if (i == 3 || i == 6) sb.append('.');
                if (i == 9) sb.append('-');
                sb.append(c);
            }
            String formatted = sb.toString();

            change.setText(formatted);
            change.setRange(0, change.getControlText().length());
            return change;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }

    // Helpers and editable masks
    private static int countDigits(String s) {
        int c = 0; if (s == null) return 0;
        for (int i = 0; i < s.length(); i++) if (Character.isDigit(s.charAt(i))) c++;
        return c;
    }
    private static int caretForDigits(String formatted, int digitsCount) {
        int d = 0;
        for (int i = 0; i < formatted.length(); i++) {
            if (Character.isDigit(formatted.charAt(i))) d++;
            if (d >= digitsCount) return i + 1;
        }
        return formatted.length();
    }
    private static String formatCPF(String digits) {
        if (digits == null) return "";
        if (digits.length() > 11) digits = digits.substring(0, 11);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i == 3 || i == 6) sb.append('.');
            if (i == 9) sb.append('-');
            sb.append(digits.charAt(i));
        }
        return sb.toString();
    }
    private static String formatPhoneBR(String digits) {
        if (digits == null) return "";
        if (digits.length() > 11) digits = digits.substring(0, 11);
        if (digits.length() <= 2) return "(" + digits;
        if (digits.length() <= 6) return "(" + digits.substring(0,2) + ") " + digits.substring(2);
        if (digits.length() <= 10) return "(" + digits.substring(0,2) + ") " + digits.substring(2,6) + "-" + digits.substring(6);
        return "(" + digits.substring(0,2) + ") " + digits.substring(2,7) + "-" + digits.substring(7);
    }
    public static void applyCpfMaskEditable(TextField field) {
        final TextFormatter.Change[] re = new TextFormatter.Change[1];
        field.setTextFormatter(new TextFormatter<>(change -> {
            if (re[0] == change) return change;
            String newText = change.getControlNewText();
            String digits = newText.replaceAll("\\D", "");
            String formatted = formatCPF(digits);
            int caretDigits = 0;
            String left = newText.substring(0, change.getCaretPosition());
            for (int i = 0; i < left.length(); i++) if (Character.isDigit(left.charAt(i))) caretDigits++;
            int newCaret = caretForDigits(formatted, caretDigits);
            change.setText(formatted);
            change.setRange(0, change.getControlText().length());
            change.setCaretPosition(newCaret);
            change.setAnchor(newCaret);
            re[0] = change;
            return change;
        }));
    }
    public static void applyPhoneMaskEditable(TextField field, boolean showEchoTooltip) {
        final TextFormatter.Change[] re = new TextFormatter.Change[1];
        field.setTextFormatter(new TextFormatter<>(change -> {
            if (re[0] == change) return change;
            String newText = change.getControlNewText();
            String digits = newText.replaceAll("\\D", "");
            String formatted = formatPhoneBR(digits);
            int caretDigits = 0;
            String left = newText.substring(0, change.getCaretPosition());
            for (int i = 0; i < left.length(); i++) if (Character.isDigit(left.charAt(i))) caretDigits++;
            int newCaret = caretForDigits(formatted, caretDigits);

            if (showEchoTooltip) {
                Tooltip tip = ensureTooltip(field);
                String typed = change.getText();
                String lastChar = (typed == null || typed.isEmpty()) ? "" : typed.substring(typed.length()-1);
                tip.setText("len=" + digits.length() + (lastChar.isEmpty() ? "" : " • '" + lastChar + "'"));
                try {
                    javafx.geometry.Bounds b = field.localToScreen(field.getBoundsInLocal());
                    if (b != null) tip.show(field, b.getMinX(), b.getMaxY()+2);
                } catch (Exception ignore) {}
                PauseTransition hide = ensureTimer(field, "maskTipHide");
                hide.stop();
                hide.setDuration(Duration.millis(600));
                hide.setOnFinished(ev -> tip.hide());
                hide.playFromStart();
            }

            change.setText(formatted);
            change.setRange(0, change.getControlText().length());
            change.setCaretPosition(newCaret);
            change.setAnchor(newCaret);
            re[0] = change;
            return change;
        }));
    }
}