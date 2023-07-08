package com.iidoston;

import static com.iidoston.TheWindow.frame;
import javax.swing.JOptionPane;
import java.util.Timer; //Contagem do tempo
import java.util.TimerTask;

//Esta classe define as principais funções deste programa, adicionar, procurar e excluir.
public class BTree {
  // Define o nó raiz como nulo para iniciar a árvore
  Node root = null;
  
  // Obtém o nó raiz da árvore
  public Node getRoot() { return root; }
  
  /* Este método adicionará um novo nó à árvore*/
  public Node addNode(int content) {
     if (root == null) {
        root = new Node(content);
        return root;
      }
      return addTo(root, content);
    }
    
  /* Este método adiciona um novo nó comparando os nós inseridos se for maior ou menor que o conteúdo
  deles, os novos nós serão inseridos*/
  private Node addTo(Node n, int c) {
      if (c < n.getContent()) {
        Node l = n.getLeft();
        if (l == null)
          return n.setLeft(c);
        else
          return addTo(l, c);
      } 
      else {
         Node r = n.getRight();
        if (r == null)
          return n.setRight(c);
        else
          return addTo(r, c);
      }
    }
  
  /*Este método percorre toda a árvore procurando o nó com os dados que o usuário está procurando
  se os dados não forem encontrados será exibida uma caixa de mensagem mostrando que o elemento solicitado não foi encontrado,
  porém se o item estiver na árvore, uma caixa de mensagem será exibida informando que o elemento foi encontrado.
  */
  public Node searchNode(int d, Node n){
      Node aux = null; 
      if (n != null){
          if (d == n.getContent()){
            return n; 
            }
            else {
                if (d < n.getContent()) {
                    Node der = n.getLeft();
                    aux = searchNode(d, der);
                }
                else {
                    Node izq = n.getRight();
                    aux = searchNode(d, izq);
                }
            }
        }
        return aux;
      }
  
  /*Este método primeiro usa o método searchNode para procurar os dados que o usuário está procurando,
  se os dados não forem encontrados será exibida uma caixa de mensagem mostrando que o elemento solicitado não foi encontrado,
  porém se o item estiver na árvore, o programa fará o procedimento de exclusão.
  */
  public Node deleteNode(int n, Node root){
      Node aux = searchNode(n, root);
      if (aux == null)
            JOptionPane.showMessageDialog(frame, "O número " + n + " não foi encontrado");
      else{
          // A árvore está vazia
         if (root == null) { 
             return root; 
         } 
         // 1.Um nó está na subárvore esquerda 
         else if (n < root.getContent()) { 
             root.left = deleteNode(n, root.left); 
         } 
         // 1.B O nó está na subárvore direita
         else if (n > root.getContent()) { 
             root.right = deleteNode(n,root.right); 
         } 
         // 2 dados encontrados!
         else { 
             // Caso 1: sem filho
             if (root.left == null && root.right == null) { 
                 root = null; 
             } 
             // Caso 2: um filho
             else if (root.left == null) { 
                 Node temp = root; 
                 root = root.right; 
                 temp = null; 
             } 
            
             else if (root.right == null) { 
                 Node temp = root; 
                 root = root.left; 
                 temp = null; 
             } 
             // Caso 3: 2 filhos 
             else { 
                  
                 Node temp = findMin(root.right); 
                 root.content = temp.content; 
                 root.right = deleteNode(temp.getContent(),root.right); 
             } 
         } 
         return root; 
      }
      return root;
  }
  
  //Este método procura o valor mais baixo da árvore.
  public static Node findMin(Node root) { 
         while (root.left != null) 
             root = root.left; 
         return root; 
     } 
} 
