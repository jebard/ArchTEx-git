package analysis.extraction.parser;

public class BED3Parser implements CoordParser{
	@Override
	public String getDirection(String[] temp) {
		if(temp[5].isEmpty() || !temp[5].equals("+") || !temp[5].equals("-") || !temp[5].equals(".")){
			return "+";
		}
		else{
			return temp[5];
		}
	}

	@Override
	public int getStart(String[] temp) {
		int start = Integer.parseInt(temp[1]);
		return start;								
	}

	@Override
	public String getChrom(String[] temp) {
		return temp[0];
	}

	@Override
	public String getGene(String[] temp) {
		if(temp[3].isEmpty()){
			String madeGene = temp[0] + "_" + temp[1] + "_" + temp[2];
			return madeGene;
		}
		else{
			return temp[3];
		}
	}	

	@Override
	public String getId(String[] temp) {
		if(temp[3].isEmpty()){
			String madeId = temp[0] + "_" + temp[1] + "_" + temp[2];
			return madeId;
		}
		else{
			return temp[3];
		}
	}

}
