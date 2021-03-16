public class GapPenalty
{
  GapPenaltyType type;
  int penalty;
  int openingPenalty;
  int extensionPenalty;

  GapPenalty(GapPenaltyType type, int penalty)
  {
    this.type = type;
    this.penalty = penalty;
  }

  GapPenalty(GapPenaltyType type, int openingPenalty, int extensionPenalty)
  {
    this.type = type;
    this.openingPenalty = openingPenalty;
    this.extensionPenalty = extensionPenalty;
  }

  public int penalty()
  {
    return this.penalty;
  }
}