package dataaccess;

import model.ComputerPart;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.opencsv.CSVReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;


public class ComputerPartDAO {
	private static final String TXT_DELIMITER = "\t";

	public List<ComputerPart> loadFromFile(File file) {
	    List<ComputerPart> computerParts = new ArrayList<>();

	    String extension = getFileExtension(file.getName());
	    switch (extension.toLowerCase()) {
	        case "csv":
	            loadFromCsv(file, computerParts);
	            break;
	        case "txt":
	            loadFromTxt(file, computerParts);
	            break;
	        case "json":
	            loadFromJson(file, computerParts);
	            break;
	        default:
	            System.out.println("Unsupported file format.");
	            break;
	    }

	    return computerParts;
	}


	private void loadFromCsv(File file, List<ComputerPart> computerParts) {
	    try (CSVReader reader = new CSVReader(new FileReader(file))) {
	        reader.skip(1);
	        String[] nextLine;
	        try {
				while ((nextLine = reader.readNext()) != null) {
				    if (nextLine.length == 3) {
				        String name = nextLine[0].trim();
				        double price = Double.parseDouble(nextLine[1].trim());
				        int quantity = Integer.parseInt(nextLine[2].trim());
				        computerParts.add(new ComputerPart(name, price, quantity));
				    }
				}
			} catch (CsvValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void loadFromTxt(File file, List<ComputerPart> computerParts) {
	    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] data = line.split(TXT_DELIMITER);
	            if (data.length == 3) {
	                String name = data[0].trim();
	                double price = Double.parseDouble(data[1].trim());
	                int quantity = Integer.parseInt(data[2].trim());
	                computerParts.add(new ComputerPart(name, price, quantity));
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	private void loadFromJson(File file, List<ComputerPart> computerParts) {
	    try (FileReader reader = new FileReader(file)) {
	        ObjectMapper objectMapper = new ObjectMapper();
	        computerParts.addAll(objectMapper.readValue(reader, new TypeReference<List<ComputerPart>>() {}));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	private String getFileExtension(String fileName) {
	    int extensionIndex = fileName.lastIndexOf('.');
	    if (extensionIndex > 0 && extensionIndex < fileName.length() - 1) {
	        return fileName.substring(extensionIndex + 1);
	    }
	    return "";
	}

	public void saveToFile(File file, List<ComputerPart> computerParts) {
	    String extension = getFileExtension(file.getName());
	    switch (extension.toLowerCase()) {
	        case "csv":
	            saveAsCsv(file, computerParts);
	            break;
	        case "txt":
	            saveAsTxt(file, computerParts);
	            break;
	        case "pdf":
	            saveAsPdf(file, computerParts);
	            break;
	        case "json":
	            saveAsJson(file, computerParts);
	            break;
	        default:
	            System.out.println("Unsupported file format.");
	            break;
	    }
	}


	private void saveAsCsv(File file, List<ComputerPart> computerParts) {
		try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
			// Write the column headers
			String[] headers = { "Name", "Price", "Quantity" };
			writer.writeNext(headers);

			// Write the data rows
			for (ComputerPart computerPart : computerParts) {
				String[] data = { computerPart.getName(), String.valueOf(computerPart.getPrice()),
						String.valueOf(computerPart.getQuantity()) };
				writer.writeNext(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveAsTxt(File file, List<ComputerPart> computerParts) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			for (ComputerPart computerPart : computerParts) {
				StringBuilder line = new StringBuilder();
				line.append(computerPart.getName()).append(TXT_DELIMITER);
				line.append(computerPart.getPrice()).append(TXT_DELIMITER);
				line.append(computerPart.getQuantity());
				writer.write(line.toString());
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveAsPdf(File file, List<ComputerPart> computerParts) {
		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage();
			document.addPage(page);

			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
			contentStream.setLeading(14.5f);

			contentStream.beginText();
			contentStream.newLineAtOffset(25, 700);
			contentStream.showText("Computer Parts List");
			contentStream.newLine();
			contentStream.newLine();
			contentStream.setFont(PDType1Font.HELVETICA, 12);

			for (ComputerPart computerPart : computerParts) {
				contentStream.showText("Name: " + computerPart.getName());
				contentStream.newLine();
				contentStream.showText("Price: $" + computerPart.getPrice());
				contentStream.newLine();
				contentStream.showText("Quantity: " + computerPart.getQuantity());
				contentStream.newLine();
				contentStream.newLine();
			}

			contentStream.endText();
			contentStream.close();

			document.save(file);
			System.out.println("Data saved as PDF successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void saveAsJson(File file, List<ComputerPart> computerParts) {
	    try (FileWriter writer = new FileWriter(file)) {
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	        objectMapper.writeValue(writer, computerParts);
	        System.out.println("Data saved as JSON successfully.");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
