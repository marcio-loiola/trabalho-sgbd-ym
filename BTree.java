package com.iidoston;

import static com.iidoston.TheWindow.frame;
import javax.swing.JOptionPane;
import java.util.Timer;
import java.util.TimerTask;

//Esta classe define as principais funções deste programa, adicionar, adicionar do arquivo, excluir, pesquisar.
public class BinaryTree {
  // Define o nó raiz como nulo para iniciar a árvore
  Node root = null;
  
  // Obtém o nó raiz da árvore
  public Node getRoot() { return root; }
  
  /* Este método adicionará um novo nó à árvore, se a raiz for nula, o primeiro valor é colocado lá
  também a raiz obterá o primeiro valor inserido. Se a condicional não for satisfatória, o método chamará para
  o método "adicionar a"*/
  public Node addNode(int content) {
     if (root == null) {
        root = new Node(content);
        return root;
      }
      return addTo(root, content);
    }
    
  /* Este método adiciona um novo nó comparando nos nós inseridos se for maior ou menor que o conteúdo
  deles, os novos nós serão armazenados em um espaço nulo*/
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
  
  /*Este método percorre toda a árvore procurando o nó com os dados que o usuário está procurando, se a variável aux for
   null é porque seu valor inicial é esse e nunca mudou porque nunca encontrou os dados solicitados
  */
  public Node searchNode(int d, Node n){
      Node aux = null; //Not found
      if (n != null){
          if (d == n.getContent()){
            return n; //Found
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
  porém se o item estiver na árvore, o programa fará o procedimento de exclusão, cada procedimento é comentado abaixo em cada seção.
  
  E, se a árvore tiver apenas a raiz, você não pode apagá-la, pois precisa de pelo menos um filho direito para a raiz
  para este programa, por causa do código.
  */
  public Node deleteNode(int n, Node root){
      Node aux = searchNode(n, root);
      if (aux == null)
            JOptionPane.showMessageDialog(frame, "O número " + n + " não foi encontrado");
      else{
          // caso base, a árvore está vazia
         if (root == null) { 
             return root; 
         } 
         // 1.Um nó está na subárvore esquerda
         // define o filho esquerdo do root como resultado de delete(root.left...) 
         else if (n < root.getContent()) { 
             root.left = deleteNode(n, root.left); 
         } 
         // 1.B O nó está na subárvore direita
         // define o filho direito do root como resultado de delete(root.right...) 
         else if (n > root.getContent()) { 
             root.right = deleteNode(n,root.right); 
         } 
         // 2 dados encontrados!
         else { 
             // Caso 1: sem filho
             // apenas defina o nó como nulo (remova-o) e retorne-o 
             if (root.left == null && root.right == null) { 
                 root = null; 
             } 
             // Caso 2: um filho
             // 2.A: nenhum filho esquerdo
             else if (root.left == null) { 
                 Node temp = root; 
                 root = root.right; 
                 temp = null; 
             } 
             // 2.B: sem filho certo
             else if (root.right == null) { 
                 Node temp = root; 
                 root = root.left; 
                 temp = null; 
             } 
             // Caso 3: 2 filhos 
             else { 
                 // obtém o elemento mínimo na subárvore direita
                 // defina-o como `root` e remova-o de seu
                 // local original 
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
