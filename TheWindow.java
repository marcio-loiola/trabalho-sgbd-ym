package com.iidoston;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import java.util.*;
//Esta classe é feita para estabelecer os itens que precisamos para desenhar a árvore dentro do JPanel.
public class TheWindow extends JPanel implements ActionListener {
    
    static JFrame frame;
    static JOptionPane message = new JOptionPane();
    // a árvore binária
    private BinaryTree tree = null;
    // a localização do nó da árvore
    private HashMap nodeLocations = null;
    // os tamanhos das subárvores
    private HashMap subtreeSizes = null;
    // precisamos calcular as localizações?
    private boolean dirty = true;
    // Espaço padrão entre nós
    private int parent2child = 10, child2child = 20;
   
    private Dimension empty = new Dimension(0, 0);
    private FontMetrics fm = null;
    
    /*Quando um botão é pressionado no menu como "A P D H" fará uma opção diferente
    a = adicionar, p = pesquisar, d = Deletar, H = Ajuda.*/
    public TheWindow(BinaryTree tree){
      this.tree = tree;
      nodeLocations = new HashMap();
      subtreeSizes = new HashMap();
      registerKeyboardAction(this, "add", KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), WHEN_IN_FOCUSED_WINDOW);
      registerKeyboardAction(this, "search", KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), WHEN_IN_FOCUSED_WINDOW);
      registerKeyboardAction(this, "delete", KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), WHEN_IN_FOCUSED_WINDOW);
      registerKeyboardAction(this, "help", KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), WHEN_IN_FOCUSED_WINDOW);
    }
    
    /* manipulador de eventos para pressionar um botão do menu, isso também mostrará
    messageboxes e fará coisas diferentes, dependendo da opção selecionada*/
    public void actionPerformed(ActionEvent e) {
      String input;
      int a;
      if (e.getActionCommand().equals("add")) {
        input = JOptionPane.showInputDialog("Adicione um número inteiro:");
        try{ //O usuário digitou um número?
                a =Integer.parseInt(input);
                tree.addNode(a);
                dirty = true;
                repaint();
            }
            //Ok, queremos lembrar dele para escrever um número
        catch(NumberFormatException z){
                JOptionPane.showMessageDialog(frame, "Por favor, digite um número inteiro");
            }
        }
      
      if (e.getActionCommand().equals("delete")) {
        input = JOptionPane.showInputDialog("Excluir um número inteiro:");
        try{ //O usuário digitou um número?
                a = Integer.parseInt(input);
                tree.deleteNode(a, tree.getRoot());
                dirty = true;
                repaint();
            }
        
        catch(NumberFormatException z){
                JOptionPane.showMessageDialog(frame, "Por favor, digite um número inteiro");
            }
      }
      
      if (e.getActionCommand().equals("search")) {
        input = JOptionPane.showInputDialog("Pesquisar um número inteiro:");
        try{ //O usuário digitou um número?
                a =Integer.parseInt(input);
                Node aux = tree.searchNode(a, tree.getRoot());
                if (aux == null)
                    JOptionPane.showMessageDialog(frame, "O número " + a + " não foi encontrado");
                else
                    JOptionPane.showMessageDialog(frame, "O número " + a + " foi encontrado");
                dirty = true;
                repaint();
            }
        
        catch(NumberFormatException z){
                JOptionPane.showMessageDialog(frame, "Por favor, digite um número inteiro");
            }
      }
      
     if (e.getActionCommand().equals("help")) {
        JOptionPane.showMessageDialog(frame, "As operações que você pode usar são:"
               + "\n a --- Adicionar um número inteiro"
               + "\n p --- Pesquisar um número inteiro"
               + "\n d --- Deletar um número inteiro"
               + "\n h --- Ajuda");
    }
}
    
    // Este método calcula as localizações dos nós, para torná-los estáveis 
    private void calculateLocations() {
      nodeLocations.clear();
      subtreeSizes.clear();
      Node root = tree.getRoot();
      if (root != null) {
        calculateSubtreeSize(root);
        calculateLocation(root, Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
      }
    }
    
    // Este método calcula o tamanho de uma subárvore com raiz em n
    private Dimension calculateSubtreeSize(Node n) {
      if (n == null) return new Dimension(0, 0);
      String s = Integer.toString(n.getContent());
      Dimension ld = calculateSubtreeSize(n.getLeft());
      Dimension rd = calculateSubtreeSize(n.getRight());
      int h = fm.getHeight() + parent2child + Math.max(ld.height, rd.height);
      int w = ld.width + child2child + rd.width;
      Dimension d = new Dimension(w, h);
      subtreeSizes.put(n, d);
      return d;
    }
    
    // Este método calcula a localização dos nós na subárvore com raiz em n
    private void calculateLocation(Node n, int left, int right, int top) {
      if (n == null) return;
      Dimension ld = (Dimension) subtreeSizes.get(n.getLeft());
      if (ld == null) ld = empty;
      Dimension rd = (Dimension) subtreeSizes.get(n.getRight());
      if (rd == null) rd = empty;
      int center = 0;
      if (right != Integer.MAX_VALUE)
        center = right - rd.width - child2child/2;
      else if (left != Integer.MAX_VALUE)
        center = left + ld.width + child2child/2;
      int width = fm.stringWidth(Integer.toString(n.getContent()));
      Rectangle r = new Rectangle(center - width/2 - 3, top, width + 6, fm.getHeight());
      nodeLocations.put(n, r);
      calculateLocation(n.getLeft(), Integer.MAX_VALUE, center - child2child/2, top + fm.getHeight() + parent2child);
      calculateLocation(n.getRight(), center + child2child/2, Integer.MAX_VALUE, top + fm.getHeight() + parent2child);
    }
    
    /* Este método desenha a árvore usando os locais pré-calculados. 
    Drawtree plota interativamente um diagrama de árvore não enraizada, 
    com muitas opções, incluindo orientação de árvore e ramos, tamanhos 
    e ângulos de rótulos e tamanhos de margem. 
    */
    private void drawTree(Graphics2D g, Node n, int px, int py, int yoffs) {
      if (n == null) return;
      Rectangle r = (Rectangle) nodeLocations.get(n);
      g.draw(r);
      g.drawString(Integer.toString(n.getContent()), r.x + 3, r.y + yoffs);
     if (px != Integer.MAX_VALUE)
       g.drawLine(px, py, r.x + r.width/2, r.y);
     drawTree(g, n.getLeft(), r.x + r.width/2, r.y + r.height, yoffs);
     drawTree(g, n.getRight(), r.x + r.width/2, r.y + r.height, yoffs);
   }
   
    //Este método vai desenhar a árvore, este recebe um gráfico chamado "g" 
    public void paint(Graphics g) {
     super.paint(g);
     fm = g.getFontMetrics();
     // se as localizações dos nós não forem calculadas
     if (dirty) {
       calculateLocations();
       dirty = false;
     }
     Graphics2D g2d = (Graphics2D) g;
     g2d.translate(getWidth() / 2, parent2child);
     drawTree(g2d, tree.getRoot(), Integer.MAX_VALUE, Integer.MAX_VALUE, fm.getLeading() + fm.getAscent());
     fm = null;
   }
   
   /*Ao iniciar o programa irá mostrar uma caixa de mensagem com todos os comandos que
    pode ser usado para trabalhar neste programa.*/ 
   public static void main(String[] args) {
        
       BinaryTree tree = new BinaryTree();
       JFrame f = new JFrame("B Tree");
       JOptionPane.showMessageDialog(frame, "Bem-vindo"
               + "\n\nEste programa funciona digitando algumas letras do seu teclado"
               + "\nEntão, as operações que você pode usar são:"
               + "\n a --- Adicionar um número inteiro"
               + "\n p --- Pesquisar um número inteiro"
               + "\n d --- Deletar um número inteiro"
               + "\n h --- Ajuda (se você esqueceu o menu)");
        f.getContentPane().add(new TheWindow(tree));
        // cria e adiciona um manipulador de eventos para o evento de fechamento da janela
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
     });
     /*Definição da dimensão da janela principal*/
     f.setBounds(50, 50, 700, 700);
     f.show();
    }    
}
