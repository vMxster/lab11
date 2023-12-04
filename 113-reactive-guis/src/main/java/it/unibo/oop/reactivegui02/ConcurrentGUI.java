package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class ConcurrentGUI extends JFrame {
    
    private static final long serialVersionUID = 2L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display;
    private final JButton stop;
    private final JButton up;
    private final JButton down;

    /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
        super();
        this.display = new JLabel();
        this.stop = new JButton("stop");
        this.up = new JButton("up");
        this.down = new JButton("down");
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel(new FlowLayout());
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        new Thread(agent).start();
        stop.addActionListener(e -> agent.stopCounting());
        up.addActionListener(e -> agent.setIncrementMode());
        down.addActionListener(e -> agent.setDecrementMode());
    }

    private class Agent implements Runnable {
        
        private volatile boolean stop = false;
        private boolean downCondition = false;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    // The EDT doesn't access `counter` anymore, it doesn't need to be volatile 
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    if (!downCondition) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        public void setIncrementMode() {
            setEnabled(false);
        }

        public void setDecrementMode() {
            setEnabled(true);
        }

        private void setEnabled(final boolean downCondition) {
            if (downCondition) {
                this.downCondition = true;
            } else {
                this.downCondition = false;
            }
        }
    }
}
