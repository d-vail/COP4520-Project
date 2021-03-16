import java.io.*;
import java.util.*;

public class SubstitutionMatrix
{
  private int[][] matrix;

  SubstitutionMatrix(SubstitutionMatrixType type)
  {
    Scanner scanner;

    switch(type)
    {
      case IDENTITY:
      case DNAFULL:
      case PAM:
      case BLOSUM:
      default:
        try
        {
          scanner = new Scanner(new File("substitution-matrix/identity.txt"));
        
          int n = Integer.parseInt(scanner.nextLine());
          this.matrix = new int[n][n];

          for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
              this.matrix[i][j] = scanner.nextInt();

          scanner.close();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        
        break;
    }
  }

  public int score(int row, int column)
  {
    return this.matrix[row][column];
  }
}