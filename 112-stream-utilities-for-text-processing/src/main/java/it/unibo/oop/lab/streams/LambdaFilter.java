package it.unibo.oop.lab.streams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Modify this small program adding new filters.
 * Realize this exercise using as much as possible the Stream library.
 *
 * 1) Convert to lowercase
 *
 * 2) Count the number of chars
 *
 * 3) Count the number of lines
 *
 * 4) List all the words in alphabetical order
 * 
 * 5) Write the count for each word, e.g. "word word pippo" should output "pippo -> 1 word -> 2"
 *
 */
public final class LambdaFilter extends JFrame {

    private static final long serialVersionUID = 1760990730218643730L;
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int DEFAULT_WIDTH = (int) SCREEN_SIZE.getWidth();
    private static final int DEFAULT_HEIGHT = (int) SCREEN_SIZE.getHeight();
    private static final Dimension MINIMUM_WINDOW_SIZE = new Dimension(DEFAULT_WIDTH / 2 , DEFAULT_HEIGHT / 3);

    private enum Command {
        /**
         * Commands.
         */
        IDENTITY("No modifications", Function.identity());

        private final String commandName;
        private final Function<String, String> fun;

        Command(final String name, final Function<String, String> process) {
            this.commandName = name;
            this.fun = process;
        }

        @Override
        public String toString() {
            return this.commandName;
        }

        public String translate(final String s) {
            return this.fun.apply(s);
        }

        public String lowerCasing(final String text) {
            return Stream.ofNullable(text)
                .map(String::toLowerCase)
                .reduce("", String::concat);
        }

        public String countChars(final String text) {
            return Stream.of(Objects.requireNonNull(text).split(" "))
                .map(String::length)
                .reduce(0, Integer::sum)
                .toString();
        }

        public String countLines(final String text) {
            return String.valueOf(Objects.requireNonNull(text)
                .lines()
                .count());
        }

        public String alphaOrder(final String text) {
            return Stream.of(Objects.requireNonNull(text).split(" "))
                .sorted()
                .reduce("", (o1,o2) -> o1.concat("["+ o2 +"]"));
        }

        public String countWord(final String text) {
            return Stream.of(Objects.requireNonNull(text).split(" "))
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet().stream()
                .map(o1 -> "[" + o1.getKey() + " -> " + o1.getValue() + "]")
                .reduce("", String::concat);
        }
    }

    private LambdaFilter() {
        super("Lambda filter GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel panel1 = new JPanel(new BorderLayout());
        final JPanel southPanel = new JPanel(new FlowLayout());
        final JComboBox<Command> combo = new JComboBox<>(Command.values());
        panel1.add(combo, BorderLayout.NORTH);
        final JPanel centralPanel = new JPanel(new GridLayout(1, 2));
        final JTextArea left = new JTextArea();
        left.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        final JTextArea right = new JTextArea();
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        right.setEditable(false);
        centralPanel.add(left);
        centralPanel.add(right);
        panel1.add(centralPanel, BorderLayout.CENTER);
        panel1.add(southPanel, BorderLayout.SOUTH);
        final JButton apply = new JButton("Apply");
        final JButton toLowerCase = new JButton("ToLowerCase");
        final JButton numChars = new JButton("NumberOfChars");
        final JButton numLines = new JButton("NumberOfLines");
        final JButton alphabeticalOrder = new JButton("AlphabeticalOrdering");
        final JButton wordCount = new JButton("WordCounting");
        apply.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).translate(left.getText())));
        toLowerCase.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).lowerCasing(left.getText())));
        numChars.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).countChars(left.getText())));
        numLines.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).countLines(left.getText())));
        alphabeticalOrder.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).alphaOrder(left.getText())));
        wordCount.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).countWord(left.getText())));
        southPanel.add(apply);
        southPanel.add(toLowerCase);
        southPanel.add(numChars);
        southPanel.add(numLines);
        southPanel.add(alphabeticalOrder);
        southPanel.add(wordCount);
        setContentPane(panel1);
        setSize(DEFAULT_WIDTH / 4, DEFAULT_HEIGHT / 4);
        setMinimumSize(MINIMUM_WINDOW_SIZE);
        setLocationByPlatform(true);
    }

    /**
     * @param a unused
     */
    public static void main(final String... a) {
        final LambdaFilter gui = new LambdaFilter();
        gui.setVisible(true);
    }
}
