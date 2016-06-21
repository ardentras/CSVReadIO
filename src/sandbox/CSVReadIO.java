package sandbox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class CSVReadIO extends JFrame{
	private static final long serialVersionUID = 1L;
	
	File inputFile = new File("test.csv");
	JTable fileContents = new JTable();

	public static void main(String[] args) {
		CSVReadIO csvRead = new CSVReadIO();
		csvRead.createContext();
	}
	
	public File readFile(File inputFile) {
		DefaultTableModel model = new DefaultTableModel();
		
		inputFile = checkFileExists(inputFile);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = null;
			
			String[] row = br.readLine().split(",", -1);
			
			for (int i = 0; i < row.length; i++)
				model.addColumn(row[i].substring(1, row[i].length() - 1));
			
			while ((line = br.readLine()) != null) {
				row = line.split(",", -1);
				for (int i = 0; i < row.length; i++)
				{
					if (row[i].length() > 0 && row[i].substring(0, 1).equals("\""))
						row[i] = row[i].substring(1, row[i].length() - 1);
				}
				
				model.addRow(row);
			}
			
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		fileContents.setModel(model);
		model.fireTableStructureChanged();
		model.fireTableDataChanged();
		this.pack();
		this.repaint();
		
		return inputFile;
	}
	
	public void writeFile(File inputFile, boolean isNewFile) {
		if (!isNewFile) {
			inputFile = checkFileExists(inputFile);
		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(inputFile));
			String line = null;
			
			DefaultTableModel model = (DefaultTableModel) fileContents.getModel();
			
			for (int j = 0; j < model.getColumnCount(); j++) {					
				if (line == null)
					line = ("\"" + model.getColumnName(j).toString() + "\"");
				else
					line += ("\"" + model.getColumnName(j).toString() + "\"");
				if (j < model.getColumnCount() - 1)
					line += ",";
			}
			
			line += "\n";
			bw.write(line);
			line = null;
			
			for (int i = 0; i < model.getRowCount(); i++) {
				for (int j = 0; j < model.getColumnCount(); j++) {
					if (line == null)
						line = ("\"" + model.getValueAt(i, j).toString() + "\"");
					else
						line += ("\"" + model.getValueAt(i, j).toString() + "\"");
					
					if (j < model.getColumnCount() - 1)
						line += ",";
				}
				
				line += "\n";
				bw.write(line);
				line = null;
			}
			
			bw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void createContext() {		
		JMenuBar menuBar = createMenuBar();
		
		JPanel tablePanel = new JPanel();
		
		tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.PAGE_AXIS));
		
		tablePanel.add(fileContents.getTableHeader());
		tablePanel.add(fileContents);
		
		JScrollPane scrollPane = new JScrollPane(tablePanel);
		
		this.setTitle("CSV File Reader | " + inputFile.getName());
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(scrollPane);
		this.setJMenuBar(menuBar);
		this.setLocationByPlatform(true);
		
		inputFile = readFile(inputFile);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		this.pack();
		this.setVisible(true);
	}
	
	public JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		
		JMenuItem menuItem = new JMenuItem("New");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createFileAction(CSVReadIO.this);
			}
		});
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Open");		
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFileAction(CSVReadIO.this);
			}
		});
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menu.addSeparator();
		
		menuItem = new JMenuItem("Save");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				writeFile(inputFile, false);
			}
		});
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Save as...");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAsAction(CSVReadIO.this);
			}
		});
		menu.add(menuItem);
		
		menuBar.add(menu);
		
		menu = new JMenu("Edit");
		JMenu subMenu = new JMenu("Change number...");
		menuItem = new JMenuItem("Columns");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cols = Integer.parseInt(JOptionPane.showInternalInputDialog(CSVReadIO.this.getContentPane(), "Enter number of columns: "));
				
				DefaultTableModel model = (DefaultTableModel) fileContents.getModel();
				
				model.setColumnCount(cols);
				
				model.fireTableStructureChanged();
				CSVReadIO.this.pack();
				CSVReadIO.this.repaint();
			}
		});
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		subMenu.add(menuItem);
		
		menuItem = new JMenuItem("Rows");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int rows = Integer.parseInt(JOptionPane.showInternalInputDialog(CSVReadIO.this.getContentPane(), "Enter number of rows: "));
				
				DefaultTableModel model = (DefaultTableModel) fileContents.getModel();
				
				String[] arr = new String[model.getColumnCount()];
				
				for (int i = 0; i < model.getColumnCount(); i++) {
					arr[i] = "";
				}
				
				if (rows > model.getRowCount()) {
					while (model.getRowCount() < rows)
						model.addRow(arr);
				} else {
					while (model.getRowCount() > rows)
						model.removeRow(model.getRowCount() - 1);
				}
				
				model.fireTableStructureChanged();
				CSVReadIO.this.pack();
				CSVReadIO.this.repaint();
			}
		});
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		subMenu.add(menuItem);
		menu.add(subMenu);
		
		menuItem = new JMenuItem("Edit Header");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (CSVReadIO.this.getContentPane().getComponent(0).getName() != null && CSVReadIO.this.getContentPane().getComponent(0).getName().equals("iframe")) {
					fileContents = updateTableModel(fileContents);
					
					writeFile(inputFile, false);
					
					CSVReadIO.this.getContentPane().remove(0);
					CSVReadIO.this.pack();
					CSVReadIO.this.repaint();
				} else {
					DefaultTableModel model = (DefaultTableModel) fileContents.getModel();
					
					String[] header = new String[model.getColumnCount()];
					
					for (int i = 0; i < model.getColumnCount(); i++) {
						header[i] = model.getColumnName(i);
					}
					
					JInternalFrame iframe = new JInternalFrame("Edit Header", false, false, false);
					JTable iframeTable = new JTable(0, model.getColumnCount());
					iframeTable.putClientProperty("terminateEditOnFocusLost", true);
					((DefaultTableModel) iframeTable.getModel()).addRow(header);
					
					JButton done = new JButton("Done");
					done.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							fileContents = updateTableModel(fileContents);
							
							writeFile(inputFile, false);
							
							CSVReadIO.this.getContentPane().remove(0);
							CSVReadIO.this.pack();
							CSVReadIO.this.repaint();
						}
					});
					iframe.setLayout(new BoxLayout(iframe.getContentPane(), BoxLayout.PAGE_AXIS));
					iframe.add(iframeTable);
					
					JPanel iframePanel = new JPanel();
					iframePanel.setLayout(new BoxLayout(iframePanel, BoxLayout.LINE_AXIS));
					iframePanel.add(Box.createHorizontalGlue());
					iframePanel.add(done);
					iframe.add(iframePanel);
					iframe.setName("iframe");
					iframe.pack();
					iframe.setVisible(true);
					CSVReadIO.this.add(iframe, 0);
					CSVReadIO.this.pack();
					CSVReadIO.this.repaint();
				}
			}
		});
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		
		menuBar.add(menu);
		
		
		return menuBar;
	}
	
	public JTable updateTableModel(JTable table) {
		JInternalFrame iframe = (JInternalFrame) CSVReadIO.this.getContentPane().getComponent(0);
		JTable iframeTable = (JTable) iframe.getContentPane().getComponent(0);
		
		Object[] headers = new Object[table.getColumnCount()];
		for (int i = 0; i < table.getColumnCount(); i++) {
			headers[i] = iframeTable.getValueAt(0, i);
		}
		DefaultTableModel newModel = new DefaultTableModel(headers, table.getRowCount());
		
		for (int i = 0; i < table.getRowCount(); i++) {
			for (int j = 0; j < table.getColumnCount(); j++) {
				newModel.setValueAt(table.getValueAt(i, j), i, j);
			}
		}
		
		table.setModel(newModel);
		
		return table;
	}
	
	public void createFileAction(JFrame frame) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(inputFile);
		
		int returnVal = fileChooser.showOpenDialog(CSVReadIO.this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			DefaultTableModel model = new DefaultTableModel();
			inputFile = fileChooser.getSelectedFile();
			
			int rows = Integer.parseInt(JOptionPane.showInternalInputDialog(CSVReadIO.this.getContentPane(), "Enter number of rows: "));
			int cols = Integer.parseInt(JOptionPane.showInternalInputDialog(CSVReadIO.this.getContentPane(), "Enter number of columns: "));
			
			String[] arr = new String[cols];
			
			model.setColumnCount(cols);
			
			for (int i = 0; i < cols; i++) {
				arr[i] = "";
			}
			
			for (int i = 0; i < rows; i++) {
				model.addRow(arr);
			}
			
			CSVReadIO.this.setTitle("CSV File Reader | " + inputFile.getName());

			fileContents.setModel(model);	
			model.fireTableStructureChanged();
			model.fireTableDataChanged();
			writeFile(inputFile, true);
			CSVReadIO.this.pack();
			CSVReadIO.this.repaint();
		}
	}
	
	public void openFileAction(JFrame frame) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(inputFile);
		
		int returnVal = fileChooser.showOpenDialog(frame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			inputFile = readFile(fileChooser.getSelectedFile());
			frame.setTitle("CSV File Reader | " + inputFile.getName());
		}
	}
	
	public void saveAsAction(JFrame frame) {
		JFileChooser fileChooser = new JFileChooser(inputFile);
		fileChooser.setDialogTitle("Save as...");
		fileChooser.setApproveButtonText("Save");
		
		int returnVal = fileChooser.showOpenDialog(frame);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			inputFile = fileChooser.getSelectedFile();
			writeFile(inputFile, true);
			readFile(inputFile);
		}
	}
	
	public File checkFileExists(File inputFile) {
		JFileChooser fileChooser = new JFileChooser(inputFile);
		fileChooser.setDialogTitle("Choose Another File");
		fileChooser.setApproveButtonText("Choose");

		while (!inputFile.exists()) {
			Object[] options = { "Yes", "No" };
			
			int value = JOptionPane.showOptionDialog(this.getComponent(0), "Error! The selected file was not found!\n"
					+ "Would you like to search for the file?", "File Not Found!",
					 JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			
			if (value == JOptionPane.YES_OPTION) {
				int returnVal = fileChooser.showOpenDialog(this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					inputFile = fileChooser.getSelectedFile();
				}
			} else {
				System.exit(0);
			}
		}
		
		return inputFile;
	}
}