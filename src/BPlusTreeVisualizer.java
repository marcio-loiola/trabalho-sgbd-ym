import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/**
 * A BPlusTreeVisualizer can display a collection of BPlusTrees, one at a time.
 */
public class BPlusTreeVisualizer extends util.ZoomView implements java.awt.event.KeyListener {

    private static final long serialVersionUID = -3741562432319457809L;
    protected java.util.Vector<util.Pair<BPlusTree, String>> trees;
    protected int currentFrame = 0;
    protected static int keyWidth = 50;
    protected static int keyHeight = 20;

    public BPlusTreeVisualizer(java.util.Vector<util.Pair<BPlusTree, String>> trees) {
        this.trees = trees;
        addKeyListener(this);
        setFocusable(true);
        zoomGraphics.setMinXY(-keyWidth, -2 * keyHeight);
    }

    protected util.Pair<Integer, Integer> draw(BPlusTree.Node node, int level, int leafNodes, int fanout) {
        if (node == null)
            return null;
        Integer[] childrenPos = null;
        int x = (leafNodes) * keyWidth * (fanout);
        int y = (level - 1) * 2 * keyHeight;
        if (!node.isLeafNode()) {
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            childrenPos = new Integer[node.pointers.length];
            for (int i = 0; i < node.pointers.length; i++) {
                Object child = node.pointers[i];
                if (child instanceof BPlusTree.Node) {
                    util.Pair<Integer, Integer> widthPos = draw((BPlusTree.Node) child, level + 1, leafNodes, fanout);
                    leafNodes = widthPos.getFirst();
                    childrenPos[i] = widthPos.getSecond();
                    if (((BPlusTree.Node) child).isLeafNode())
                        leafNodes++;
                    minX = Math.min(minX, childrenPos[i]);
                    maxX = Math.max(maxX, childrenPos[i]);
                }
            }
            x = (minX + maxX) / 2;
        } else {
            if (node.pointers[node.pointers.length - 1] != null)
                zoomGraphics.drawLine(x + keyWidth * (fanout - 1), y + keyHeight / 2, x + keyWidth * (fanout) - 3,
                        y + keyHeight / 2);
        }
        zoomGraphics.setColor(Color.WHITE);
        zoomGraphics.fillRect(x - 3, y, keyWidth * (fanout - 1) + 6, keyHeight);
        for (int i = 0; i < node.pointers.length; i++) {
            zoomGraphics.setColor(Color.GRAY);
            zoomGraphics.fillRect(x + i * keyWidth - 3, y, 6, keyHeight);
            zoomGraphics.setColor(Color.BLACK);
            if (childrenPos != null && childrenPos[i] != null) {
                zoomGraphics.drawLine(x + i * keyWidth, y + keyHeight - 4,
                        childrenPos[i] + keyWidth * (fanout - 1) / 2, level * 2 * keyHeight);
            }
            if (i < node.keys.length && node.keys[i] != null)
                zoomGraphics.drawStrings(new String[]{node.keys[i].toString()}, x + i * keyWidth + 4, y,
                        keyWidth - 6, keyHeight);
        }
        zoomGraphics.drawRect(x - 3, y, keyWidth * (fanout - 1) + 6, keyHeight);
        return new util.Pair<Integer, Integer>(leafNodes, x);
    }

    @Override
    public void draw() {
        try {
            BPlusTree tree = trees.elementAt(currentFrame).getFirst();
            zoomGraphics.setColor(Color.BLACK);
            draw(tree.root, 1, 0, tree.fanout);
        } catch (Exception e) {
        }
    }

    @Override
    public void draw(java.awt.Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.draw(g);
        g.drawString("" + currentFrame + " : " + trees.elementAt(currentFrame).getSecond(), 10, g.getFontMetrics()
                .getHeight());
    }

    public static void main(String[] args) throws Exception {
        BPlusTree tree = new BPlusTree(3);
        java.util.Vector<util.Pair<BPlusTree, String>> trees = new java.util.Vector<util.Pair<BPlusTree, String>>();
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader("input.txt"));
        try {
            for (; ; ) {
                String line = reader.readLine();
                String[] command = line.split(" ");
                if (command[0].equals("insert")) {
                    tree.insert(Integer.parseInt(command[1]), null);
                } else if (command[0].equals("delete")) {
                    tree.delete(Integer.parseInt(command[1]), null);
                }
                trees.add(new util.Pair<BPlusTree, String>(new BPlusTree(tree), line));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            reader.close();
        }

        JFrame frame = new JFrame("B+Tree Visualizer");
        JPanel panel = new BPlusTreeVisualizer(trees);
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        // Create the fanout selection panel
        JPanel fanoutPanel = createFanoutPanel(trees, panel);
        frame.getContentPane().add(fanoutPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private static JPanel createFanoutPanel(java.util.Vector<util.Pair<BPlusTree, String>> trees, JPanel panel) {
        JPanel fanoutPanel = new JPanel();
        fanoutPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        fanoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel fanoutLabel = new JLabel("Fanout:");
        JComboBox<Integer> fanoutComboBox = new JComboBox<>(new Integer[]{2, 3, 4, 5});
        fanoutComboBox.setSelectedItem(3);
        fanoutComboBox.addActionListener(e -> updateFanout(fanoutComboBox, trees, panel));

        fanoutPanel.add(fanoutLabel);
        fanoutPanel.add(fanoutComboBox);

        return fanoutPanel;
    }

    private static void updateFanout(JComboBox<Integer> fanoutComboBox, java.util.Vector<util.Pair<BPlusTree, String>> trees, JPanel panel) {
        int fanout = (int) fanoutComboBox.getSelectedItem();
        BPlusTree tree = new BPlusTree(fanout);
        trees.clear();
        trees.add(new util.Pair<>(tree, "Initial Tree"));
        ((BPlusTreeVisualizer) panel).currentFrame = 0;
        panel.repaint();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_LEFT:
                if (e.isControlDown()) {
                    zoomGraphics.moveBy(keyWidth, 0);
                } else {
                    currentFrame = Math.max(0, currentFrame - 1);
                }
                repaint();
                break;
            case KeyEvent.VK_RIGHT:
                if (e.isControlDown()) {
                    zoomGraphics.moveBy(-keyWidth, 0);
                } else {
                    currentFrame = Math.min(trees.size() - 1, currentFrame + 1);
                }
                repaint();
                break;
            case KeyEvent.VK_UP: // zoom in
                if (e.isControlDown()) {
                    zoomGraphics.moveBy(0, keyHeight);
                } else {
                    zoomGraphics.changeScale(1.2);
                }
                repaint();
                break;
            case KeyEvent.VK_DOWN:
                if (e.isControlDown()) {
                    zoomGraphics.moveBy(0, -keyHeight);
                } else {
                    zoomGraphics.changeScale(1 / 1.2);
                }
                repaint();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}