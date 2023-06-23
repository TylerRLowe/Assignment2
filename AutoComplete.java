/**
 * An implementation of the AutoCompleteInterface using a DLB Trie.
 */

import java.util.ArrayList;

import org.w3c.dom.Node;

 public class AutoComplete implements AutoCompleteInterface {

  private DLBNode root;
  private StringBuilder currentPrefix;
  private DLBNode currentNode;
  private boolean valid;
  //TODO: Add more instance variables as needed

  public AutoComplete(){
    root = null;
    currentPrefix = new StringBuilder();
    currentNode = null;
    boolean valid = true;
  }

  /**
   * Adds a word to the dictionary in O(alphabet size*word.length()) time
   * @param word the String to be added to the dictionary
   * @return true if add is successful, false if word already exists
   * @throws IllegalArgumentException if word is the empty string
   */
    public boolean addSuffix(String word, DLBNode startNode, int start){
      DLBNode curr = root;
      if(root == null){
        root = new DLBNode(word.charAt(0));
        curr = root;
        //init curr and set it as te root child if poss
        if(word.length() > 1){
          curr = new DLBNode(word.charAt(1));
          root.child = curr;
          curr.parent = root;
        }
        DLBNode prev = root.child;
        for(int i = 2; i < word.length(); i++){
          curr = new DLBNode(word.charAt(i));
          curr.parent = prev;
          prev.child = curr;
          prev = curr;
        }
        if(curr.isWord == true) return false;
        curr.isWord = true;
        while(curr != null){
          curr.size++;
          curr = curr.parent;
        }
        return true;
      }
      DLBNode prev = startNode;
      //if the start node is not valid
      if(prev == null){
        curr = new DLBNode(word.charAt(0));
        root.previousSibling = curr;
        curr.nextSibling = root;
        root = curr;
        start++;
        prev = root;
      }
      for(int i = start;i < word.length(); i++){
        curr = new DLBNode(word.charAt(i));
        //we may need to append to child list
        if(prev.child != null){
          prev.child.previousSibling = curr;
          curr.nextSibling = prev.child;
        }
        prev.child = curr;
        curr.parent = prev;
        prev = curr;

      }
      if(curr.isWord == true) return false;
      curr.isWord = true;
      while(curr != null){
        curr.size++;
        curr = curr.parent;
      }
      return true;

  }
    public boolean add(String word){
      if(word.isEmpty()) throw new IllegalArgumentException("Must provide a string to be added");
      //checking if the root is null, must make a new tree
      if(root == null){
        return addSuffix(word, root, 0);
      }
      DLBNode curr = root;
      DLBNode prev = null;
      int i = 0;
      while(curr != null){
        if(curr.data == word.charAt(i)){
          i++;
          curr.trueNode = true;
          //if we have hit the end of the word; we make sure it is tagged as a word and return true
          if(i == word.length()){
            if(curr.isWord == false){
               curr.isWord = true;
                return true;
            }
            return false;
          }// else return false 
          //we need to store the prev found accurate node
          prev = curr;
          curr = curr.child;
        }
        //while we look for the curr sibling
        else curr = curr.nextSibling;
      }
      //TODO: implement this method
      return addSuffix(word, prev, i);
    }

  /**
   * appends the character c to the current prefix in O(alphabet size) time. 
   * This method doesn't modify the dictionary.
   * @param c: the character to append
   * @return true if the current prefix after appending c is a prefix to a word 
   * in the dictionary and false otherwise
   */
    public boolean advance(char c){
      currentPrefix.append(c);
      if(currentNode == null){
        currentNode = root;
        while(currentNode != null){
          if(currentNode.data == c)
            return true;
          currentNode = currentNode.nextSibling;
        }
        currentNode = new DLBNode(c);
        currentNode.trueNode = false;
        if(root == null){
          root = currentNode;
          return false;
        }
        currentNode.nextSibling = root.nextSibling;
        root.nextSibling = currentNode;
        return false;
      }
      DLBNode temp = currentNode.child;
      //making sure its a valide prefix;
      while(temp != null){
        if(temp.data == c){
          currentNode = temp;
          return true;
        }
        temp = temp.nextSibling;
      }
      //adding cur node to the trie if is not alread yhtere
      temp = new DLBNode(c);
      if(currentNode.child != null){
        temp.nextSibling = currentNode.child.nextSibling;
        currentNode.child.nextSibling = temp;
        temp.previousSibling = currentNode.child;
      }
      else{
        currentNode.child = temp;
      }
      temp.parent = currentNode;
      currentNode = temp;
      currentNode.trueNode = false;
      return false;
      //TODO: implement this method
    }

  /**
   * removes the last character from the current prefix in O(1) time. This 
   * method doesn't modify the dictionary.
   * @throws IllegalStateException if the current prefix is the empty string
   */
    public void retreat(){
      if(currentNode == null){
        throw new IllegalStateException();
      }
      if (currentNode.parent == null){
        //if we are at the first node, empty it
        currentPrefix.deleteCharAt(0);
        currentNode = null;
        return;
      }
      currentPrefix.deleteCharAt(currentPrefix.length()-1);
      if(currentNode.trueNode == false){
        //if it is not a true node, we delete it
        DLBNode temp = currentNode;
        currentNode = currentNode.parent;
        if(temp.previousSibling == null){
          currentNode.child = null;
        }
        else{
          temp.previousSibling.nextSibling = temp.nextSibling;
          if(temp.nextSibling != null) temp.nextSibling.previousSibling = temp.previousSibling;
        }
      }
      else currentNode = currentNode.parent;

    }

  /**
   * resets the current prefix to the empty string in O(1) time
   */
    public void reset(){
      currentPrefix = new StringBuilder();
      // while(currentNode != null && currentNode.trueNode == false){
      //   DLBNode temp = currentNode;
      //   currentNode = currentNode.parent;
      //   if(temp.nextSibling == null){
      //     currentNode.child = null;
      //   }
      //   else{
      //     temp.nextSibling.previousSibling = null;
      //     currentNode.child = temp.nextSibling;
      //   }
      // }
      currentNode = null;
      //TODO: implement this method
    }
    
  /**
   * @return true if the current prefix is a word in the dictionary and false
   * otherwise. The running time is O(1).
   */
    public boolean isWord(){
      if(currentNode == null){
        return false;
      }
      //TODO: implement this method
      return currentNode.isWord;
    }

  /**
   * adds the current prefix as a word to the dictionary (if not already a word)
   * The running time is O(alphabet size*length of the current prefix). 
   */
    public void add(){
      if(currentNode.isWord == true) return;
      currentNode.isWord = true;
      currentNode.trueNode = true;
      currentNode.size++;
      DLBNode tempNode = currentNode.parent;
      while(tempNode != null){
        tempNode.size++;
        tempNode.trueNode = true;
        tempNode = tempNode.parent;
      }
      //TODO: implement this method
    }

  /** 
   * @return the number of words in the dictionary that start with the current 
   * prefix (including the current prefix if it is a word). The running time is 
   * O(1).
   */
    public int getNumberOfPredictions(){
      if(currentNode == null) return 0;
      //TODO: implement this method
      return currentNode.size;
    }
  
  /**
   * retrieves one word prediction for the current prefix. The running time is 
   * O(prediction.length())
   * @return a String or null if no predictions exist for the current prefix
   */
    public String retrievePrediction(){
      if(currentPrefix == null) return null;
      DLBNode temp = currentNode;
      if(currentNode.size == 0)return null;
      StringBuilder returnString = new StringBuilder(currentPrefix);
      if(currentNode.trueNode == true){
        while(temp.isWord == false){
          temp = temp.child;
          returnString.append(temp.data);
        }
      }
      //TODO: implement this method
      return returnString.toString();
    }


  /* ==============================
   * Helper methods for debugging.
   * ==============================
   */

  //print the subtrie rooted at the node at the end of the start String
  public void printTrie(String start){
    System.out.println("==================== START: DLB Trie Starting from \""+ start + "\" ====================");
    if(start.equals("")){
      printTrie(root, 0);
    } else {
      DLBNode startNode = getNode(root, start, 0);
      if(startNode != null){
        printTrie(startNode.child, 0);
      }
    }
    
    System.out.println("==================== END: DLB Trie Starting from \""+ start + "\" ====================");
  }

  //a helper method for printTrie
  private void printTrie(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
      System.out.println(" (" + node.size + ")");
      printTrie(node.child, depth+1);
      printTrie(node.nextSibling, depth);
    }
  }

  //return a pointer to the node at the end of the start String 
  //in O(start.length() - index)
  private DLBNode getNode(DLBNode node, String start, int index){
    if(start.length() == 0){
      return node;
    }
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data == start.charAt(index))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data == start.charAt(index))) {
          result = node;
      } else {
          result = getNode(node.nextSibling, start, index);
      }
    }
    return result;
  } 

  //The DLB node class
  private class DLBNode{
    private char data;
    private int size;
    private boolean isWord;
    private DLBNode nextSibling;
    private DLBNode previousSibling;
    private DLBNode child;
    private DLBNode parent;
    private  boolean trueNode;

    private DLBNode(char data){
        this.data = data;
        size = 0;
        isWord = false;
        nextSibling = previousSibling = child = parent = null;
        trueNode = true;
    }
  }
}
