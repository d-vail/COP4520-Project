import java.util.*;

public class Alignment
{
  private ArrayList<Character> alignmentA = new ArrayList<>();
  private ArrayList<Character> alignmentB = new ArrayList<>();

  public void addA(char c)
  {
    this.alignmentA.add(c);
  }

  public void addB(char c)
  {
    this.alignmentB.add(c);
  }

  public void print()
  {
    // Print alignment A
    for (int i = this.alignmentA.size() - 1; i >= 0; i--)
      System.out.print(this.alignmentA.get(i) + " ");
    
    System.out.println();

    // Print alignment string
    for (int i = this.alignmentA.size() - 1; i >= 0; i--)
      if (this.alignmentA.get(i) != '-' && this.alignmentB.get(i) != '-')
        System.out.print("| ");
      else
        System.out.print("  ");

    System.out.println();

    // Print alignment B
    for (int i = this.alignmentB.size() - 1; i >= 0; i--)
      System.out.print(this.alignmentB.get(i) + " ");
    
    System.out.println();
  }
}