package it.unibo.oop.reactivegui03;

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
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 2L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display;
    private final JButton stop;
    private final JButton up;
    private final JButton down;

    public AnotherConcurrentGUI() {
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
        final Agent1 agent1 = new Agent1();
        new Thread(agent1).start();
        final Agent2 agent2 = new Agent2(agent1);
        new Thread(agent2).start();
        stop.addActionListener(e -> agent1.stopCounting());
        up.addActionListener(e -> agent1.setIncrementMode());
        down.addActionListener(e -> agent1.setDecrementMode());
    }

    private class Agent1 implements Runnable {
        
        private volatile boolean stop = false;
        private boolean downCondition = false;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (!downCondition) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

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

    private class Agent2 implements Runnable {

        private final Agent1 agent1;

        public Agent2(final Agent1 agent1) {
            this.agent1 = agent1;
        }

        @Override
        public void run() {
            try {
                    Thread.sleep(10000);
                    agent1.stopCounting();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
        }

    }
}
