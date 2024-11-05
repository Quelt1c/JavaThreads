import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

public class MultiWindowApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AnimationWindow();
            new CalculationWindow();
            new MarqueeTextWindow();
        });
    }
}

// Вікно для анімації
class AnimationWindow extends JFrame {
    private AnimationPanel animationPanel;
    private Thread animationThread;
    private JSlider prioritySlider;
    private JTextField delayField;

    public AnimationWindow() {
        setTitle("Animation Window");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(100, 100);

        animationPanel = new AnimationPanel();
        add(animationPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        // Слайдер для зміни пріоритету
        prioritySlider = new JSlider(1, 10, 5);
        prioritySlider.setMajorTickSpacing(1);
        prioritySlider.setPaintTicks(true);
        prioritySlider.setPaintLabels(true);

        // Поле для зміни затримки
        delayField = new JTextField("20", 5);

        controlPanel.add(new JLabel("Priority:"));
        controlPanel.add(prioritySlider);
        controlPanel.add(new JLabel("Delay (ms):"));
        controlPanel.add(delayField);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startAnimation());
        stopButton.addActionListener(e -> stopAnimation());

        setVisible(true);
    }

    private void startAnimation() {
        if (animationThread == null || !animationThread.isAlive()) {
            animationThread = new Thread(animationPanel);
            animationPanel.setDelay(Integer.parseInt(delayField.getText()));
            animationThread.setPriority(prioritySlider.getValue());
            animationThread.start();
        }
    }

    private void stopAnimation() {
        animationPanel.stop();
    }
}

class AnimationPanel extends JPanel implements Runnable {
    private double x = 50, y = 50;
    private double deltaX = 2, deltaY = 2;
    private boolean running = false;
    private int delay = 20;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gradient = new GradientPaint((float) x, (float) y, Color.RED,
                (float) (x + 50), (float) (y + 50), Color.BLUE);
        g2d.setPaint(gradient);
        g2d.fill(new Ellipse2D.Double(x, y, 50, 50));
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            x += deltaX;
            y += deltaY;

            if (x < 0 || x + 50 > getWidth()) deltaX = -deltaX;
            if (y < 0 || y + 50 > getHeight()) deltaY = -deltaY;

            repaint();

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// Вікно для обчислень
class CalculationWindow extends JFrame {
    private JTextArea outputArea;
    private Thread calculationThread;
    private JSlider prioritySlider;
    private JTextField delayField;

    public CalculationWindow() {
        setTitle("Calculation Window");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(600, 100);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        prioritySlider = new JSlider(1, 10, 5);
        prioritySlider.setMajorTickSpacing(1);
        prioritySlider.setPaintTicks(true);
        prioritySlider.setPaintLabels(true);

        delayField = new JTextField("500", 5);

        controlPanel.add(new JLabel("Priority:"));
        controlPanel.add(prioritySlider);
        controlPanel.add(new JLabel("Delay (ms):"));
        controlPanel.add(delayField);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startCalculation());
        stopButton.addActionListener(e -> stopCalculation());

        setVisible(true);
    }

    private void startCalculation() {
        if (calculationThread == null || !calculationThread.isAlive()) {
            calculationThread = new Thread(new CalculationTask(outputArea, Integer.parseInt(delayField.getText())));
            calculationThread.setPriority(prioritySlider.getValue());
            calculationThread.start();
        }
    }

    private void stopCalculation() {
        calculationThread.interrupt();
    }
}

class CalculationTask implements Runnable {
    private JTextArea outputArea;
    private int delay;

    public CalculationTask(JTextArea outputArea, int delay) {
        this.outputArea = outputArea;
        this.delay = delay;
    }

    @Override
    public void run() {
        int n = 0;
        long a = 0, b = 1;

        while (!Thread.currentThread().isInterrupted()) {
            outputArea.append("Fib(" + n + ") = " + a + "\n");
            long temp = a;
            a = b;
            b = temp + b;
            n++;

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// Вікно для біжучого тексту
class MarqueeTextWindow extends JFrame {
    private JLabel label;
    private Timer timer;
    private JSlider prioritySlider;
    private JTextField delayField;

    public MarqueeTextWindow() {
        setTitle("Marquee Text Window");
        setSize(400, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(1100, 100);

        label = new JLabel("Біжучий текст", SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setBounds(0, 0, 200, 30);

        JPanel textPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                label.setLocation(label.getX(), getHeight() / 2 - label.getHeight() / 2);
            }
        };
        textPanel.setLayout(null);
        textPanel.add(label);
        add(textPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        prioritySlider = new JSlider(1, 10, 5);
        prioritySlider.setMajorTickSpacing(1);
        prioritySlider.setPaintTicks(true);
        prioritySlider.setPaintLabels(true);

        delayField = new JTextField("30", 5);

        controlPanel.add(new JLabel("Priority:"));
        controlPanel.add(prioritySlider);
        controlPanel.add(new JLabel("Delay (ms):"));
        controlPanel.add(delayField);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startMarquee());
        stopButton.addActionListener(e -> stopMarquee());

        setVisible(true);
    }

    private void startMarquee() {
        if (timer != null && timer.isRunning()) {
            return;
        }
        int delay = Integer.parseInt(delayField.getText());
        timer = new Timer(delay, e -> {
            int x = label.getX() - 2;
            if (x + label.getWidth() < 0) {
                x = getWidth();
            }
            label.setLocation(x, label.getY());
            repaint();
        });
        timer.start();
    }

    private void stopMarquee() {
        if (timer != null) {
            timer.stop();
        }
    }
}
