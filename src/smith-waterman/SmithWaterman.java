public class SmithWaterman
{
  private static final char HYPHEN = '-';

  // Setup for constant or linear gap penalty.
  public static Alignment smithWaterman(String seqA, String seqB, 
    SubstitutionMatrixType subMatrixType, GapPenaltyType gapPenaltyType, int penalty)
  {
    SubstitutionMatrix subMatrix = new SubstitutionMatrix(subMatrixType);
    GapPenalty gapPenalty = new GapPenalty(gapPenaltyType, penalty);
    return smithWaterman(seqA, seqB, subMatrix, gapPenalty);
  }

  // Setup for affine or convex gap penalty.
  public static Alignment smithWaterman(String seqA, String seqB, 
    SubstitutionMatrixType subMatrixType, GapPenaltyType gapPenaltyType, int openingPenalty, 
    int extensionPenalty)
  {
    SubstitutionMatrix subMatrix = new SubstitutionMatrix(subMatrixType);
    GapPenalty gapPenalty = new GapPenalty(gapPenaltyType, openingPenalty, extensionPenalty);
    return smithWaterman(seqA, seqB, subMatrix, gapPenalty);
  }

  // Smith-Waterman algorithm.
  private static Alignment smithWaterman(String seqA, String seqB, SubstitutionMatrix subMatrix, 
    GapPenalty gapPenalty)
  {
    int[][] scoreMatrix = scoreMatrix(seqA, seqB, subMatrix, gapPenalty);
    return traceback(seqA, seqB, scoreMatrix);
  }

  private static int[][] scoreMatrix(String seqA, String seqB, SubstitutionMatrix subMatrix, 
    GapPenalty gapPenalty)
  {
    // Initialize scoring matrix.
    int column = seqA.length() + 1;
    int row = seqB.length() + 1;
    int[][] scoreMatrix = new int[row][column];

    // Score each element from left to right, top to bottom.
    // Score is the max of (varies per gap penalty -- generalize):
    //    match: H_{i-1, j-1} + substitution_matrix(a_i, b_j)
    //    end gap a: H_{i-k, j} - W_k
    //    end gap b: H_{i, j-l} - W_l
    //    mismatch: 0
    for (int i = 1; i < row; i++)
    {
      for (int j = 1; j < column; j++)
      {
        int match = scoreMatrix[i - 1][j - 1] + subMatrix.score(seqB.charAt(i - 1) - 65, 
          seqA.charAt(j - 1) - 65);
        int endGapA = scoreMatrix[i - 1][j] - gapPenalty.penalty();
        int endGapB = scoreMatrix[i][j - 1] - gapPenalty.penalty();
        int mismatch = 0;

        scoreMatrix[i][j] = Math.max(Math.max(match, mismatch), Math.max(endGapA, endGapB));
      }
    }

    return scoreMatrix;
  }

  private static Alignment traceback(String seqA, String seqB, int[][] scoreMatrix)
  {
    int column = seqA.length() + 1;
    int row = seqB.length() + 1;
    int maxScore = Integer.MIN_VALUE;
    int iMax = 0;
    int jMax = 0;

    // Find highest score.
    for (int i = 0; i < row; i++)
    {
      for (int j = 0; j < column; j++)
      {
        if (scoreMatrix[i][j] > maxScore)
        {
          maxScore = scoreMatrix[i][j];
          iMax = i;
          jMax = j;
        }
      }
    }

    // // Build alignment result starting from the highest score.
    Alignment result = new Alignment();
    int count = 0;

    while (scoreMatrix[iMax][jMax] > 0)
    {
      int diagonal = scoreMatrix[iMax - 1][jMax - 1];
      int up = scoreMatrix[iMax - 1][jMax];
      int left = scoreMatrix[iMax][jMax - 1];

      // Found match. Move diagonally.
      if (diagonal >= up && diagonal >= left) {
        result.addA(seqA.charAt(jMax - 1));
        result.addB(seqB.charAt(iMax - 1));
        iMax -= 1;
        jMax -= 1;

        continue;
      }

      // Moving up == Mismatch. Add gap to second sequence.
      if (up > diagonal && up >= left) {
        result.addA(HYPHEN);
        result.addB(seqB.charAt(iMax - 1));
        iMax -= 1;

        continue;
      }

      // Moving to the left == Mismatch. Add gap to first sequence.
      if (left > diagonal && left > up) {
        result.addB(HYPHEN);
        result.addA(seqA.charAt(jMax - 1));
        jMax -= 1;

        continue;
      }
    }

    return result;
  }

  /**
   * Entry point into Smith-Waterman algorithm. Read to strings from the command line and initiate
   * algorithm to find local alignment.
   *
   * @todo Parse command line parameters for algorithm options: substitution matrix and gap penalty.
   */
  public static void main(String [] args)
  {
    if (args.length < 2)
    {
      System.err.println("Proper syntax: java SmithWaterman <sequence_a> <sequence_b>");
      return;
    }

    String seqA = args[0].toUpperCase();
    String seqB = args[1].toUpperCase();
    int gapPenalty = 2;

    Alignment result = smithWaterman(seqA, seqB, SubstitutionMatrixType.IDENTITY, 
      GapPenaltyType.CONSTANT, gapPenalty);
    
    result.print();
  }
}