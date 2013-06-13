package analysis.extraction.parser;


//seqname - The name of the sequence. Must be a chromosome or scaffold.
//source - The program that generated this feature.
//feature - The name of this type of feature. Some examples of standard feature types are "CDS", "start_codon", "stop_codon", and "exon".
//start - The starting position of the feature in the sequence. The first base is numbered 1.
//end - The ending position of the feature (inclusive).
//score - A score between 0 and 1000. If the track line useScore attribute is set to 1 for this annotation data set, the score value will determine the level of gray in which this feature is displayed (higher numbers = darker gray). If there is no score value, enter ".".
//strand - Valid entries include '+', '-', or '.' (for don't know/don't care).
//frame - If the feature is a coding exon, frame should be a number between 0-2 that represents the reading frame of the first base. If the feature is not a coding exon, the value should be '.'.
//group - All lines with the same group are linked together into a single item.
public class GFFParser implements CoordParser {

	@Override
	public String getDirection(String[] temp) {
		return temp[6];
	}

	@Override
	public int getStart(String[] temp) {
		int start = Integer.parseInt(temp[3]);
		return start;
	}

	@Override
	public String getChrom(String[] temp) {
		return temp[0];
	}

	@Override
	public String getGene(String[] temp) {
		String madeGene = temp[0] + "_" + temp[3] + "_" + temp[4];
		return madeGene;
	}

	@Override
	public String getId(String[] temp) {
		String madeId = temp[0] + "_" + temp[3] + "_" + temp[4];
		return madeId;
	}
}
