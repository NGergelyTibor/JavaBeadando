package gui;

import dataaccess.ComputerPartDAO;
import model.ComputerPart;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class MainWindow extends JFrame {
	private DefaultTableModel tableModel;
	private JTable table;
	private ComputerPartDAO computerPartDAO;

	public MainWindow() {
		super("Computer Parts Shop");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		computerPartDAO = new ComputerPartDAO();

		// Create table model with column names
		tableModel = new DefaultTableModel();
		tableModel.addColumn("Part Name");
		tableModel.addColumn("Price");
		tableModel.addColumn("Quantity");
		// Create table with the table model
		table = new JTable(tableModel);

		// Create buttons
		JButton loadButton = new JButton("Load");
		JButton addButton = new JButton("Add");
		JButton updateButton = new JButton("Update");
		JButton deleteButton = new JButton("Delete");
		JButton saveButton = new JButton("Save");

		// Add button listeners
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
				fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
				fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files (*.json)", "json"));
				int result = fileChooser.showOpenDialog(MainWindow.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					List<ComputerPart> computerParts = computerPartDAO.loadFromFile(file);
					displayComputerParts(computerParts);
				}
			}
		});

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(MainWindow.this, "Enter part name:");
				String priceStr = JOptionPane.showInputDialog(MainWindow.this, "Enter price:");
				String quantityStr = JOptionPane.showInputDialog(MainWindow.this, "Enter quantity:");

				try {
					double price = Double.parseDouble(priceStr);
					int quantity = Integer.parseInt(quantityStr);
					ComputerPart computerPart = new ComputerPart(name, price, quantity);
					addComputerPart(computerPart);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(MainWindow.this,
							"Invalid price or quantity. Please enter numeric values.");
				}
			}
		});

		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow != -1) {
					ComputerPart computerPart = getSelectedComputerPart();
					if (computerPart != null) {
						String name = JOptionPane.showInputDialog(MainWindow.this, "Enter updated part name:",
								computerPart.getName());
						String priceStr = JOptionPane.showInputDialog(MainWindow.this, "Enter updated price:",
								computerPart.getPrice());
						String quantityStr = JOptionPane.showInputDialog(MainWindow.this, "Enter updated quantity:",
								computerPart.getQuantity());

						try {
							double price = Double.parseDouble(priceStr);
							int quantity = Integer.parseInt(quantityStr);
							computerPart.setName(name);
							computerPart.setPrice(price);
							computerPart.setQuantity(quantity);
							updateComputerPart(selectedRow, computerPart);
						} catch (NumberFormatException ex) {
							JOptionPane.showMessageDialog(MainWindow.this,
									"Invalid price or quantity. Please enter numeric values.");
						}
					}
				} else {
					JOptionPane.showMessageDialog(MainWindow.this, "Please select a row to update.");
				}
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow != -1) {
					deleteComputerPart(selectedRow);
				} else {
					JOptionPane.showMessageDialog(MainWindow.this, "Please select a row to delete.");
				}
			}
		});

		saveButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        JFileChooser fileChooser = new JFileChooser();
		        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
		        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
		        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
		        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files (*.json)", "json"));
		        int result = fileChooser.showSaveDialog(MainWindow.this);
		        if (result == JFileChooser.APPROVE_OPTION) {
		            File file = fileChooser.getSelectedFile();
		            FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) fileChooser.getFileFilter();
		            String selectedExtension = selectedFilter.getExtensions()[0];
		            String filePath = file.getAbsolutePath();

		            if (!filePath.toLowerCase().endsWith("." + selectedExtension)) {
		                file = new File(filePath + "." + selectedExtension);
		            }

		            List<ComputerPart> computerParts = getComputerParts();
		            computerPartDAO.saveToFile(file, computerParts);
		            JOptionPane.showMessageDialog(MainWindow.this, "Data saved successfully.");
		        }
		    }
		});


		// Create panel and layout
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(loadButton);
		buttonPanel.add(addButton);
		buttonPanel.add(updateButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(saveButton);
		// Add components to the frame
		add(panel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// Set frame size and visibility
		pack();
		setLocationRelativeTo(null);
	}

	private void displayComputerParts(List<ComputerPart> computerParts) {
		tableModel.setRowCount(0);
		for (ComputerPart computerPart : computerParts) {
			Object[] rowData = { computerPart.getName(), computerPart.getPrice(), computerPart.getQuantity() };
			tableModel.addRow(rowData);
		}
	}

	private void addComputerPart(ComputerPart computerPart) {
		Object[] rowData = { computerPart.getName(), computerPart.getPrice(), computerPart.getQuantity() };
		tableModel.addRow(rowData);
	}

	private void updateComputerPart(int row, ComputerPart computerPart) {
		tableModel.setValueAt(computerPart.getName(), row, 0);
		tableModel.setValueAt(computerPart.getPrice(), row, 1);
		tableModel.setValueAt(computerPart.getQuantity(), row, 2);
	}

	private void deleteComputerPart(int row) {
		tableModel.removeRow(row);
	}

	private ComputerPart getSelectedComputerPart() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow != -1) {
			String name = (String) tableModel.getValueAt(selectedRow, 0);
			double price = (double) tableModel.getValueAt(selectedRow, 1);
			int quantity = (int) tableModel.getValueAt(selectedRow, 2);
			return new ComputerPart(name, price, quantity);
		}
		return null;
	}

	private List<ComputerPart> getComputerParts() {
		List<ComputerPart> computerParts = new ArrayList<>();
		for (int row = 0; row < tableModel.getRowCount(); row++) {
			String name = (String) tableModel.getValueAt(row, 0);
			double price = (double) tableModel.getValueAt(row, 1);
			int quantity = (int) tableModel.getValueAt(row, 2);
			computerParts.add(new ComputerPart(name, price, quantity));
		}
		return computerParts;
	}
}
