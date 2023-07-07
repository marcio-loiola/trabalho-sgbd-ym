package com.iidoston;

//Define as principais ferramentas que precisamos para construir a árvore
public class Node{
    //construtor
    int content;
    Node left;
    Node right;
    
    //Este método coloca os dados no Nó da árvore
    public Node(int c) {
		this.content = c;
	}
    
    //Este método obtém os dados de um Nó na árvore
    public int getContent() { return content; }
    
    //Este método obtém o próximo nó localizado à esquerda de outro nó
    public Node getLeft() { return left; }
    /*Na árvore binária define o nó à esquerda, somente se for menor que outro nó.
    ele também recebe um int, int que será armazenado no nó definido*/
    public Node setLeft(int content) {
	left = new Node(content);
	return left;
    }
    
    //Este método obtém o próximo nó localizado à direita de outro nó
    public Node getRight() { return right; }
    /*Na árvore binária define o nó à direita, somente se for maior que o outro nó.
    ele também recebe um int, int que será armazenado no nó definido*/
    public Node setRight(int content) {
	right = new Node(content);
	return right;
    }   
}
