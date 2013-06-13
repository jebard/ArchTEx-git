package components;

import gui.base.GUI;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.param.BamFilter;
import data.param.ExParameter;
import data.param.XmlFile;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import analysis.extraction.ExtractLoad;

@SuppressWarnings("serial")
public class ExtractionParameterWindow extends JDialog {
	public ExParameter param;

	private JPanel contentPane;
	protected JFileChooser fc;
	private GUI gui;

	private JDialog frame = this;
	private JLabel lblCoordinateFileName;

	private JTextField txtWind;
	private JTextField txtRes;
	private JTextField txtExt;
	private JTextField txtThread;

	final DefaultListModel expList;
	final DefaultListModel indList;
	private JTextField txtTagCount;
	private JTextField txtStdDevSize;
	private JTextField txtNumStdDev;
	private JTextField txtCondWindow;
	private JTextField txtShift;

	//Create the frame.
	public ExtractionParameterWindow(ExParameter templateparam, GUI gu, JFileChooser f) {
		gui = gu;
		fc = f;
		this.setModal(true);
		param = templateparam;

		setTitle("Extraction Parameters");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 537, 795);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblcoord = new JLabel("Reference File:");
		lblcoord.setBounds(56, 38, 103, 19);
		contentPane.add(lblcoord);

		JLabel lblBamData = new JLabel("BAM Data:");
		lblBamData.setBounds(56, 96, 103, 14);
		contentPane.add(lblBamData);

		if(param.getCoord() == null) lblCoordinateFileName = new JLabel("Coordinate File Name");
		else lblCoordinateFileName = new JLabel(param.getCoord().getName());
		lblCoordinateFileName.setBounds(291, 40, 195, 14);
		contentPane.add(lblCoordinateFileName);

		JLabel lblInputFiles = new JLabel("Input Files:");
		lblInputFiles.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblInputFiles.setBounds(238, 11, 77, 14);
		contentPane.add(lblInputFiles);

		//Add Select Buttons to Content Pane
		// Button and Action Listener for the Coordinate File Button
		JButton coordButton = new JButton("Select File");
		coordButton.setBounds(165, 36, 103, 23);
		contentPane.add(coordButton);
		coordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fc.setFileFilter(fc.getAcceptAllFileFilter());
				File newcoord = getCoordFile();
				if(newcoord != null) param.setCoord(newcoord);
			}

		});

		//Frame to contain all experiments
		JScrollPane scrlPane_Input = new JScrollPane();
		scrlPane_Input.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrlPane_Input.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrlPane_Input.setBounds(25, 126, 226, 135);
		contentPane.add(scrlPane_Input);	
		expList = new DefaultListModel();
		String[] exp_names = param.getInputNames();
		for(int x = 0; x < exp_names.length; x++) expList.addElement(exp_names[x]);
		final JList lstExp = new JList(expList);
		lstExp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrlPane_Input.setViewportView(lstExp);
		JScrollPane scrlPane_Index = new JScrollPane();
		scrlPane_Index.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrlPane_Index.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrlPane_Index.setBounds(278, 126, 226, 135);
		contentPane.add(scrlPane_Index);
		indList = new DefaultListModel();
		String[] ind_names = param.getIndexNames();
		for(int x = 0; x < ind_names.length; x++) indList.addElement(ind_names[x]);
		final JList lstIndex = new JList(indList);
		lstIndex.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrlPane_Index.setViewportView(lstIndex);

		lstExp.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lstIndex.setSelectedIndex(lstExp.getSelectedIndex());
			}
		});
		lstIndex.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				lstExp.setSelectedIndex(lstIndex.getSelectedIndex());
			}
		});

		//Handles the input/index selection event
		fc.setMultiSelectionEnabled(true);
		JButton bamButton = new JButton("Select File");
		bamButton.setBounds(165, 92, 103, 23);
		contentPane.add(bamButton);
		bamButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fc.setFileFilter(new BamFilter());
				//will get all files the user selected BR
				File[] newBamFiles = getBamFile();
				for(File newBamFile : newBamFiles){
					if(newBamFile != null) {
						// look for the index file BR
						File baiFile = new File(newBamFile + ".bai");
						//File newBamIndex = baiFile;
						if(baiFile.exists()){
							param.addInput(newBamFile);
							expList.addElement(newBamFile.getName());
							param.addIndex(baiFile);
							indList.addElement(baiFile.getName());
						}
						else{
							IndexBuilderWindow index = new IndexBuilderWindow(param, fc);
							index.setVisible(true);
							File newBamIndex = index.getIndexFile();
							if(index.getIndexFile() != null) {
								if(newBamIndex.getName().equals("NONE")) {
									newBamIndex = param.generateIndex(newBamFile,newBamFile+".bai",gui);
								}
								if(!newBamIndex.getName().equals(newBamFile.getName() + ".bai")) {
									JOptionPane.showMessageDialog(null, "BAM-BAI File Names Do Not Match!!!");
								} else {
									param.addInput(newBamFile);
									expList.addElement(newBamFile.getName());
									param.addIndex(newBamIndex);
									indList.addElement(newBamIndex.getName());
								}
							}
						}
					}
				}
			}
		});		

		JSeparator separator = new JSeparator();
		separator.setBounds(35, 272, 479, 4);
		contentPane.add(separator);

		JLabel lblExtractionParameters = new JLabel("Extraction Parameters");
		lblExtractionParameters.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblExtractionParameters.setBounds(192, 287, 133, 14);
		contentPane.add(lblExtractionParameters);

		JLabel lblWindowSizebp = new JLabel("Window Size (bp):");
		lblWindowSizebp.setBounds(35, 315, 113, 14);
		contentPane.add(lblWindowSizebp);
		lblWindowSizebp.setToolTipText("<html>Size of the sampling window (in base pairs).</html>");
		
		JLabel lblResolutionbp = new JLabel("Resolution (bp):");
		lblResolutionbp.setBounds(277, 315, 99, 14);
		contentPane.add(lblResolutionbp);
		lblResolutionbp.setToolTipText("<html>Bin sizes for the extracted window in basepairs.</html>");
		
		JLabel lblTagExtensionbp_1 = new JLabel("Tag Extension (bp):");
		lblTagExtensionbp_1.setBounds(35, 340, 124, 14);
		contentPane.add(lblTagExtensionbp_1);
		lblTagExtensionbp_1.setToolTipText("<html>Length to extend from the start of a sequencing tag (to true fragment length)</html>");

		JLabel lblCpusToUse = new JLabel("CPU's to Use:");
		lblCpusToUse.setBounds(278, 340, 99, 14);
		contentPane.add(lblCpusToUse);

		JLabel lblStrandShift = new JLabel("Strand Shift (bp):");
		lblStrandShift.setBounds(35, 398, 113, 14);
		contentPane.add(lblStrandShift);
		lblStrandShift.setToolTipText("<html>Shifts the tag start position by the given basepairs.</html>");

		//Set Text boxes to contain the extraction parameters
		txtWind = new JTextField(Integer.toString(param.getWindow()));
		txtWind.setHorizontalAlignment(SwingConstants.LEFT);
		txtWind.setBounds(181, 312, 49, 20);
		contentPane.add(txtWind);
		txtWind.setColumns(10);

		txtRes = new JTextField(Integer.toString(param.getResolution()));
		txtRes.setBounds(386, 312, 31, 20);
		contentPane.add(txtRes);
		txtRes.setColumns(10);

		txtExt = new JTextField(Integer.toString(param.getExtension()));
		txtExt.setBounds(181, 337, 49, 20);
		contentPane.add(txtExt);
		txtExt.setColumns(10);

		txtThread = new JTextField(Integer.toString(param.getThreads()));
		txtThread.setBounds(387, 337, 31, 20);
		contentPane.add(txtThread);
		txtThread.setColumns(10);

		txtShift = new JTextField(Integer.toString(param.getTagShift()));
		txtShift.setHorizontalAlignment(SwingConstants.LEFT);
		txtShift.setColumns(10);
		txtShift.setBounds(181, 395, 49, 20);
		contentPane.add(txtShift);

		JLabel lblTransformation = new JLabel("Transformation:");
		lblTransformation.setBounds(191, 436, 109, 14);
		contentPane.add(lblTransformation);

		//Variable fields for transformations
		final JLabel lblStandard = new JLabel("Standardize to Tag Count (bp):");
		lblStandard.setBounds(50, 546, 201, 14);
		contentPane.add(lblStandard);
		lblStandard.setVisible(false);
		txtTagCount = new JTextField();
		txtTagCount.setText(Integer.toString(param.getStandardWindow()));
		txtTagCount.setBounds(290, 543, 86, 20);
		contentPane.add(txtTagCount);
		txtTagCount.setColumns(10);	
		txtTagCount.setVisible(false);
		final JLabel lblGauss = new JLabel("Gaussian Smoothing:");
		lblGauss.setBounds(25, 542, 133, 23);
		contentPane.add(lblGauss);
		lblGauss.setVisible(false);
		final JLabel lblStdDevSize = new JLabel("Std Dev Size (bp):");
		lblStdDevSize.setBounds(165, 546, 109, 14);
		contentPane.add(lblStdDevSize);
		lblStdDevSize.setVisible(false);
		final JLabel lblOfStd = new JLabel("# of Std Devs:");
		lblOfStd.setBounds(340, 546, 94, 14);	
		contentPane.add(lblOfStd);
		lblOfStd.setVisible(false);
		txtStdDevSize = new JTextField();
		txtStdDevSize.setText(Integer.toString(param.getSDSize()));
		txtStdDevSize.setBounds(278, 543, 49, 20);
		contentPane.add(txtStdDevSize);
		txtStdDevSize.setVisible(false);
		txtStdDevSize.setColumns(10);
		txtNumStdDev = new JTextField();
		txtNumStdDev.setText(Integer.toString(param.getSDNum()));
		txtNumStdDev.setBounds(444, 543, 31, 20);
		contentPane.add(txtNumStdDev);
		txtNumStdDev.setVisible(false);
		txtNumStdDev.setColumns(10);

		final JLabel lblCondWindow = new JLabel("Conditional Window Size (bp):");
		lblCondWindow.setBounds(56, 574, 191, 14);
		contentPane.add(lblCondWindow);
		lblCondWindow.setVisible(false);
		txtCondWindow = new JTextField();
		txtCondWindow.setText(Integer.toString(param.getCondWindow()));
		txtCondWindow.setBounds(289, 571, 49, 20);
		contentPane.add(txtCondWindow);
		txtCondWindow.setColumns(10);
		txtCondWindow.setVisible(false);

		final JRadioButton rdbtnStandardize = new JRadioButton("Standardize");
		rdbtnStandardize.setBounds(56, 460, 109, 23);
		contentPane.add(rdbtnStandardize);
		final JRadioButton rdbtnSquareroot = new JRadioButton("Squareroot");
		rdbtnSquareroot.setBounds(56, 486, 108, 23);
		contentPane.add(rdbtnSquareroot);
		final JRadioButton rdbtnLog = new JRadioButton("Log2");
		rdbtnLog.setBounds(165, 460, 65, 23);
		contentPane.add(rdbtnLog);
		final JRadioButton rdbtnZLog = new JRadioButton("ZLog");
		rdbtnZLog.setBounds(165, 486, 65, 23);
		contentPane.add(rdbtnZLog);
		final JRadioButton rdbtnCndPos = new JRadioButton("Conditional Positioning");
		rdbtnCndPos.setBounds(278, 486, 197, 23);
		contentPane.add(rdbtnCndPos);
		final JRadioButton rdbtnGauss = new JRadioButton("Gaussian Smoothing");
		rdbtnGauss.setBounds(278, 460, 197, 23);
		contentPane.add(rdbtnGauss);
		final JRadioButton rdbtnPoisson = new JRadioButton("Poisson Probability");
		rdbtnPoisson.setBounds(278, 512, 197, 23);
		contentPane.add(rdbtnPoisson);
		final JRadioButton rdbtnNone = new JRadioButton("None");
		rdbtnNone.setBounds(165, 512, 109, 23);
		contentPane.add(rdbtnNone);

		//Add radio buttons for transformation analysis
		ButtonGroup TransStrand = new ButtonGroup();
		//Adds text tools BR
		rdbtnStandardize.setToolTipText("<html>Standardize experiment to set number of mapped tags.</html>");
		rdbtnSquareroot.setToolTipText("<html>Take the squareroot of data at each base pair.</html>");
		rdbtnLog.setToolTipText("<html>Log2 of data divided by genome ratio.</html>");
		rdbtnZLog.setToolTipText("<html>Z-score standardized version of log2.</html>");
		rdbtnCndPos.setToolTipText("<html>Described in length http://genomebiology.com/content/11/11/140</html>");
		rdbtnPoisson.setToolTipText("<html>Probability given poisson distribution of data.</html>");
		rdbtnNone.setToolTipText("<html>No transformation.</html>");
		rdbtnGauss.setToolTipText("<html>Smoothing of data using gaussian kernel.</html>");
		TransStrand.add(rdbtnStandardize);
		TransStrand.add(rdbtnSquareroot);
		TransStrand.add(rdbtnLog);
		TransStrand.add(rdbtnZLog);
		TransStrand.add(rdbtnCndPos);
		TransStrand.add(rdbtnGauss);
		TransStrand.add(rdbtnPoisson);
		TransStrand.add(rdbtnNone);
		if(param.getTrans().equals("standard")) {
			rdbtnStandardize.setSelected(true);
			lblStandard.setVisible(true);
			txtTagCount.setVisible(true);
		}
		if(param.getTrans().equals("squareroot")) rdbtnSquareroot.setSelected(true);
		if(param.getTrans().equals("log2")) rdbtnLog.setSelected(true);
		if(param.getTrans().equals("zlog")) rdbtnZLog.setSelected(true);
		if(param.getTrans().equals("poisson")) rdbtnPoisson.setSelected(true);
		if(param.getTrans().equals("condpos")) {
			rdbtnCndPos.setSelected(true);
			lblGauss.setVisible(true);
			lblStdDevSize.setVisible(true);
			lblOfStd.setVisible(true);
			txtStdDevSize.setVisible(true);
			txtNumStdDev.setVisible(true);
			lblCondWindow.setVisible(true);
			txtCondWindow.setVisible(true);
		}
		if(param.getTrans().equals("gauss")) {
			rdbtnGauss.setSelected(true);
			lblGauss.setVisible(true);
			lblStdDevSize.setVisible(true);
			lblOfStd.setVisible(true);
			txtStdDevSize.setVisible(true);
			txtNumStdDev.setVisible(true);
		}
		if(param.getTrans().equals("none")) rdbtnNone.setSelected(true);

		ActionListener standardizeprompts = new ActionListener() {@Override
		public void actionPerformed(ActionEvent arg0) {
			lblStandard.setVisible(true);
			txtTagCount.setVisible(true);
			lblGauss.setVisible(false);
			lblStdDevSize.setVisible(false);
			lblOfStd.setVisible(false);
			txtStdDevSize.setVisible(false);
			txtNumStdDev.setVisible(false);
			lblCondWindow.setVisible(false);
			txtCondWindow.setVisible(false);
		};};
		ActionListener nullprompts = new ActionListener() {@Override
		public void actionPerformed(ActionEvent arg0) {
			lblStandard.setVisible(false);
			txtTagCount.setVisible(false);
			lblGauss.setVisible(false);
			lblStdDevSize.setVisible(false);
			lblOfStd.setVisible(false);
			txtStdDevSize.setVisible(false);
			txtNumStdDev.setVisible(false);
			lblCondWindow.setVisible(false);
			txtCondWindow.setVisible(false);
		};};
		ActionListener gaussprompts = new ActionListener() {@Override
		public void actionPerformed(ActionEvent arg0) {
			lblStandard.setVisible(false);
			txtTagCount.setVisible(false);
			lblGauss.setVisible(true);
			lblStdDevSize.setVisible(true);
			lblOfStd.setVisible(true);
			txtStdDevSize.setVisible(true);
			txtNumStdDev.setVisible(true);
			lblCondWindow.setVisible(false);
			txtCondWindow.setVisible(false);
		};};
		ActionListener condprompts = new ActionListener() {@Override
		public void actionPerformed(ActionEvent arg0) {
			lblStandard.setVisible(false);
			txtTagCount.setVisible(false);
			lblGauss.setVisible(true);
			lblStdDevSize.setVisible(true);
			lblOfStd.setVisible(true);
			txtStdDevSize.setVisible(true);
			txtNumStdDev.setVisible(true);
			lblCondWindow.setVisible(true);
			txtCondWindow.setVisible(true);
		};};

		rdbtnStandardize.addActionListener(standardizeprompts);
		rdbtnSquareroot.addActionListener(nullprompts);
		rdbtnLog.addActionListener(nullprompts);
		rdbtnZLog.addActionListener(nullprompts);
		rdbtnPoisson.addActionListener(nullprompts); 
		rdbtnNone.addActionListener(nullprompts); 
		rdbtnGauss.addActionListener(gaussprompts);
		rdbtnCndPos.addActionListener(condprompts);

		//Radio Buttons for metric used to calculate preliminary cutoffs
		JLabel lblParser = new JLabel("Parser Type");
		lblParser.setBounds(46, 640, 105, 14);
		contentPane.add(lblParser);
		final JRadioButton rdbtnRefGene = new JRadioButton("RefGene");
		rdbtnRefGene.setBounds(345, 636, 109, 23);
		contentPane.add(rdbtnRefGene);
		final JRadioButton rdbtnGFF = new JRadioButton("GFF");
		rdbtnGFF.setBounds(255, 635, 87, 23);
		contentPane.add(rdbtnGFF);
		final JRadioButton rdbtnCustom = new JRadioButton("Custom");
		rdbtnCustom.setSelected(true);
		rdbtnCustom.setBounds(150, 636, 97, 23);
		contentPane.add(rdbtnCustom);
		//make a BED file selection BR
		final JRadioButton rdbtnBED = new JRadioButton("BED");
		rdbtnBED.setBounds(450, 636, 97, 23);
		contentPane.add(rdbtnBED);
		ButtonGroup InputGroup = new ButtonGroup();
		//add tool tips to the different parser types BR
		rdbtnBED.setToolTipText("<html> Fields of a BED file:<br>" +
				"First three required fields:<br>" +
				"1. Chrom: The name of the chromosome or scaffold.<br> " +
				"2. ChromStart: The starting position of the feature in the chromosome or scaffold. The first base in a chromosome is numbered 0.<br> " +
				"3. ChromEnd: The ending position of the feature in the chromosome or scaffold. The chromEnd base is not included in the display of the feature<br> " +
				"The 9 additional optional BED fields are:<br>" +
				"4. Name: Defines the name of the BED line. <br> " +
				"5. Score: A score between 0 and 1000. If the track line useScore attribute is set to 1 for this annotation data set, the score value will <br>determine the level of gray in which this feature is displayed (higher numbers = darker gray). This table shows the Genome Browser's translation <br>of BED score values into shades of gray<br> " +
				"6. Strand: Valid entries include '+', '-', or '.' (for don't know/don't care).<br> " +
				"7. ThickStart: The starting position at which the feature is drawn thickly (for example, the start codon in gene displays).<br>" +
				"8. ThickEnd: The ending position at which the feature is drawn thickly (for example, the stop codon in gene displays).<br>" +
				"9. ItemRgb:  An RGB value of the form R,G,B (e.g. 255,0,0). If the track line itemRgb attribute is set to \"On\", this RBG value will <br>determine the display color of the data contained in this BED line.<br>" +
				"10. BlockCount: The number of blocks (exons) in the BED line.<br>" +
				"11. BlockSizes:  A comma-separated list of the block sizes. The number of items in this list should correspond to blockCount.<br> " +
				"12. BlockStarts: A comma-separated list of block starts. All of the blockStart positions should be calculated relative to chromStart. The number<br> of items in this list should correspond to blockCount.</html>");
		rdbtnCustom.setToolTipText("<html> Fields of the custom file:<br>" +
				"1.Id: The name of the gene.<br>" +
				"2.Chrom: The name of the chromosome or scaffold.<br>" +
				"3.Start: The starting position of the feature in the sequence.<br>" +
				"4.Strand: Valid entries include '+', '-', or '.' (for don't know/don't care).</html>");
		rdbtnGFF.setToolTipText("<html> Fields of a GFF file:<br>" +
				"1. Seqname: The name of the sequence. Must be a chromosome or scaffold.<br> " +
				"2. Source: The program that generated this feature.<br> " +
				"3. Feature: The name of this type of feature. Some examples of standard feature types are \"CDS\", \"start_codon\", \"stop_codon\", and \"exon\".<br> " +
				"4. Start: The starting position of the feature in the sequence. The first base is numbered 1.<br> " +
				"5. End: The ending position of the feature (inclusive).<br> " +
				"6. Score: A score between 0 and 1000. If the track line useScore attribute is set to 1 for this annotation data set, the score value will <br>determine the level of gray in which this feature is displayed (higher numbers = darker gray). If there is no score value, enter \".\".<br> " +
				"7. Strand: Valid entries include '+', '-', or '.' (for don't know/don't care).<br>" +
				"8. Frame: If the feature is a coding exon, frame should be a number between 0-2 that represents the reading frame of the first base. If the <br>feature is not a coding exon, the value should be '.'.<br>" +
				"9. Group:All lines with the same group are linked together into a single item.</html>");		
		rdbtnRefGene.setToolTipText("<html> Fields of a RefGene file:<br>" +
				"1. Name: The name of the gene.<br> " +
				"2. Chrom: The name of the chromosome.<br> " +
				"3. Strand: Valid entries include '+', '-', or '.' (for don't know/don't care).<br> " +
				"4. TxStart: The transcription start position. <br> " +
				"5. TxEnd: The transcription end position.<br> " +
				"6. CdsStart: The coding region start position<br> " +
				"7. CdsEnd: The coding region end position.<br>" +
				"8. ExonCount: The number of exons.<br>" +
				"9. ExonStarts: The exon start positions.<br>" +
				"10. ExonEnds: The exon end positions.<html>");
				
		InputGroup.add(rdbtnRefGene);
		InputGroup.add(rdbtnGFF);
		InputGroup.add(rdbtnCustom);
		//add the BED file selection to the GUI BR
		InputGroup.add(rdbtnBED);

		JLabel lblOutType = new JLabel("Output Type");
		lblOutType.setBounds(46,663,105,14);
		contentPane.add(lblOutType);
		final JRadioButton rdbtnBasic = new JRadioButton("Basic");
		rdbtnBasic.setSelected(true);
		rdbtnBasic.setBounds(150,659,109,23);
		contentPane.add(rdbtnBasic);
		final JRadioButton rdbtnTree = new JRadioButton("TreeView");
		rdbtnTree.setBounds(255, 659, 87, 23);
		contentPane.add(rdbtnTree);
		final JRadioButton rdbtnWig = new JRadioButton("Wig");
		rdbtnWig.setBounds(345, 659, 97, 23);
		contentPane.add(rdbtnWig);
		ButtonGroup OutputGroup = new ButtonGroup();
		//adds tool tips to the different output types BR
		rdbtnBasic.setToolTipText("<html>Tab deliminated, sorted by row, first column, unique ID, and raw basepair values for each.</html>");
		rdbtnTree.setToolTipText("<html>Direct integration into Java treeview, for visualization of data.</html>");
		rdbtnWig.setToolTipText("<html> The wiggle (WIG) format is for display of dense, continuous data such as GC percent, probability scores, and transcriptome data. <br>Wiggle data elements must be equally sized.</html>");
		OutputGroup.add(rdbtnBasic);
		OutputGroup.add(rdbtnTree);
		OutputGroup.add(rdbtnWig);
		if(param.getOutType().equals("treeview")) rdbtnTree.setSelected(true);
		else if (param.getOutType().equals("wig")) rdbtnWig.setSelected(true);
		else rdbtnBasic.setSelected(true);

		JLabel label = new JLabel("Strand to Examine:");
		label.setBounds(35, 370, 126, 14);
		contentPane.add(label);

		final JRadioButton rdbtnCombined = new JRadioButton("Combined");
		rdbtnCombined.setBounds(159, 366, 109, 23);
		contentPane.add(rdbtnCombined);
		final JRadioButton rdbtnForward = new JRadioButton("Forward");
		rdbtnForward.setBounds(267, 366, 109, 23);
		contentPane.add(rdbtnForward);
		final JRadioButton rdbtnReverse = new JRadioButton("Reverse");
		rdbtnReverse.setBounds(378, 366, 109, 23);
		contentPane.add(rdbtnReverse);
		ButtonGroup StrandGroup = new ButtonGroup();
		//adds tool tips BR
		label.setToolTipText("<html>Differentiates between forward and reverse strands (Watson or Crick) Looks at strand independently.</html>");
		StrandGroup.add(rdbtnCombined);
		StrandGroup.add(rdbtnForward);
		StrandGroup.add(rdbtnReverse);
		if(param.getStrand().equals("C")) rdbtnCombined.setSelected(true);
		else if(param.getStrand().equals("F")) rdbtnForward.setSelected(true);
		else if(param.getStrand().equals("R")) rdbtnReverse.setSelected(true);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(25, 599, 479, 4);
		contentPane.add(separator_1);

		//Handles the Cancel event
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(285, 708, 103, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});

		JButton btnLoadTemplate = new JButton("Run Extraction");
		btnLoadTemplate.setBounds(109, 708, 142, 23);
		contentPane.add(btnLoadTemplate);		

		JButton btnResetBam = new JButton("Reset BAM");
		btnResetBam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ResetBamWindow reset = new ResetBamWindow();
				reset.setVisible(true);
				if(reset.getConfirm()) {
					param.setIndex(new Vector<File>());
					param.setInput(new Vector<File>());
					expList.removeAllElements();
					indList.removeAllElements();
				}
			}
		});
		btnResetBam.setBounds(387, 65, 117, 23);
		contentPane.add(btnResetBam);

		JLabel lblReferenceParameters = new JLabel("Reference Parameters");
		lblReferenceParameters.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblReferenceParameters.setBounds(192, 614, 133, 14);
		contentPane.add(lblReferenceParameters);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(25, 693, 479, 4);
		contentPane.add(separator_2);

		JButton removeButton = new JButton("Remove BAM");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(lstExp.getSelectedIndex() > -1) {
					param.getInput().remove(lstExp.getSelectedIndex());
					param.getIndex().remove(lstIndex.getSelectedIndex());
					expList.remove(lstExp.getSelectedIndex());
					indList.remove(lstIndex.getSelectedIndex());
				}
			}
		});
		removeButton.setBounds(387, 92, 117, 23);
		contentPane.add(removeButton);

		//Handles the Start of a run
		btnLoadTemplate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(param.getCoord() == null) {
						JOptionPane.showMessageDialog(null, "No Coordinate File Loaded!!!");
					}
					else if(param.getInput().size() < 1) {
						JOptionPane.showMessageDialog(null, "No BAM Files Loaded!!!");
					}
					else {
						System.out.println("Loading Parameters...");
						gui.printToGui("Loading Parameters...");
						param.setWindow(Integer.parseInt(txtWind.getText()));
						param.setResolution(Integer.parseInt(txtRes.getText()));	
						param.setExtension(Integer.parseInt(txtExt.getText()));	
						param.setThreads(Integer.parseInt(txtThread.getText()));
						param.setTagShift(Integer.parseInt(txtShift.getText()));
						//TODO make sure correct transformation is performed
						if(rdbtnStandardize.isSelected()) {
							param.setTrans("standard");
							param.setStandardWindow(Integer.parseInt(txtTagCount.getText()));
						} else if(rdbtnSquareroot.isSelected()) param.setTrans("squareroot");
						else if(rdbtnLog.isSelected()) param.setTrans("log2");
						else if(rdbtnZLog.isSelected()) param.setTrans("zlog");
						else if(rdbtnPoisson.isSelected()) param.setTrans("poisson");
						else if(rdbtnNone.isSelected()) param.setTrans("none");
						else if(rdbtnGauss.isSelected()) {
							param.setSDSize(Integer.parseInt(txtStdDevSize.getText()));
							param.setSDNum(Integer.parseInt(txtNumStdDev.getText()));
							param.setTrans("gauss");
						}
						else if(rdbtnCndPos.isSelected()) {
							param.setSDSize(Integer.parseInt(txtStdDevSize.getText()));
							param.setSDNum(Integer.parseInt(txtNumStdDev.getText()));
							param.setCondWindow(Integer.parseInt(txtCondWindow.getText()));
							param.setTrans("condpos");
						}

						if(rdbtnRefGene.isSelected()) param.setInType("refGene");
						else if(rdbtnGFF.isSelected()) param.setInType("gff");
						//sets an action to be performed when the BED file selector is selected BR
						else if(rdbtnBED.isSelected()) param.setInType("bed");
						else if(rdbtnCustom.isSelected()) param.setInType("custom");
						String outHandle = ".txt";
						if (rdbtnBasic.isSelected()) param.setOutType("standard");
						else if (rdbtnTree.isSelected()) {
							outHandle = ".cdt";
							param.setOutType("treeview");
						}
						else if (rdbtnWig.isSelected()) { 
							outHandle = ".wig";
							param.setOutType("wig");;
						}
						if(rdbtnCombined.isSelected()) param.setStrand("C");
						else if(rdbtnForward.isSelected()) param.setStrand("F");
						else param.setStrand("R");

						param.setWindow(Integer.parseInt(txtWind.getText()));
						param.setResolution(Integer.parseInt(txtRes.getText()));	
						param.setExtension(Integer.parseInt(txtExt.getText()));	
						param.setThreads(Integer.parseInt(txtThread.getText()));

						Calendar timestamp = new GregorianCalendar();
						String time = timestamp.get(Calendar.MONTH) + "-" + timestamp.get(Calendar.DAY_OF_MONTH) + "-" + timestamp.get(Calendar.YEAR)+"_" + timestamp.get(Calendar.HOUR_OF_DAY) + "-" + timestamp.get(Calendar.MINUTE) + "-" + timestamp.get(Calendar.SECOND);

						gui.printToGui(param.printStats());
						System.out.println("Parameters Loaded\n");
						gui.printToGui("Parameters Loaded\n");

						param.generateStats();
						ExtractLoad eloader = new ExtractLoad(gui, param);
						for(int i = 0; i < param.getInput().size();i++) {
							param.setExp(i);
							String newoutput = time + "_" + param.getInput().get(i).getName() + outHandle;
							param.setOutput(new File(newoutput));
							eloader.run();
						}
						frame.dispose();
						//creates an XML file holding the parameters of the run BR
						for(int i = 0; i < param.getInput().size(); i++){
							//XmlFile(String analysis, String refName, String bamFile, int windowS, int numbOfCPUs, int res, int tagExtens, String strndToExam, int strndShift, String typeTransf, int tgCount, String pType, String oType){
							XmlFile file = new XmlFile("Mass-Data-Extraction", param.getCoord().getPath(), param.getInput().get(i).getPath(), Integer.parseInt(txtWind.getText()), Integer.parseInt(txtThread.getText()), Integer.parseInt(txtRes.getText()), Integer.parseInt(txtExt.getText()), param.getStrand(), Integer.parseInt(txtShift.getText()), param.getTrans(), Integer.parseInt(txtStdDevSize.getText()), Integer.parseInt(txtNumStdDev.getText()), Integer.parseInt(txtCondWindow.getText()), Integer.parseInt(txtTagCount.getText()), param.getInType(), param.getOutType());
							file.createMassExtractXML();
						}
					}
				} catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, "Input Fields Must Contain Integers");
				}
			}
		});
	}

	public File[] getBamFile(){
		File[] bamFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bamFile = fc.getSelectedFiles();
		}
		return bamFile;
	}

	public File getCoordFile(){
		File coordFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			coordFile = fc.getSelectedFile();
		}
		if(coordFile != null) lblCoordinateFileName.setText(coordFile.getName());
		//else lblCoordinateFileName.setText("Coordinate File Name");
		return coordFile;
	}

	public String getName(Vector<File> file) {
		String name = "";
		for(int x = 0; x < file.size(); x++) {
			name += file.get(x).getName() + "\n";
		}
		return name;
	}
}