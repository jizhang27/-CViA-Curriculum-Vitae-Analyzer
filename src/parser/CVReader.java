package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;  
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
  
public class CVReader {  
	private static String path = "test/temp/";
	private static String txt = ".txt";
	private static int txtIndex = 0;
	private static String pdf = ".pdf";
	private static int pdfIndex = 1;
	private static String docx = ".docx";
	private static int docIndex = 2;
	private static String doc = ".doc";
	private static int docxIndex = 3;
	   
	public static File[] convertDirectory(File directory) {
		if (!directory.exists() || !directory.isDirectory()) {
			return null;
		}
		File[] files = directory.listFiles();
		return convertFileList(files);
	}
	
	public static File[] convertFileList(File[] files) {
		ArrayList<File> fileList = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			File file = convertFile(files[i]);
			if (file != null && file.isFile()) {
				fileList.add(file);
			}
		}
		//return fileNames.toArray(new String[fileNames.size()]);
		return fileList.toArray(new File[fileList.size()]);
	}
	
	public static File convertFile(File file) {
    	File pathCheck = new File(path);
    	if (!pathCheck.exists() || !pathCheck.isDirectory()) {
    		pathCheck.mkdir();
    	}
    	
    	if (!file.exists() || !file.isFile()) {
    		return null;
    	}
    		
    	String fileName = file.getName();
    	int fileType = checkFiletype(fileName);
    	
    	if (fileType == txtIndex) {
    		return TXT2TXT(file);
    	}
    	
    	if (fileType == pdfIndex) {
    		return PDF2TXT(file);
    	}

    	if (fileType == docxIndex) {
    		return DOCX2TXT(file);
    	}
    	
    	if (fileType == docIndex) {
    		return DOC2TXT(file);
    	}
		return null;
	}    


	private static String getFileName(String fileName) {
    	int fileType = checkFiletype(fileName);
    	if (fileType == pdfIndex) {
    		return fileName.replace(pdf, "").trim();
    	}
    	if (fileType == txtIndex) {
    		return fileName.replace(txt, "").trim();
    	}
    	if (fileType == docxIndex) {
    		return fileName.replace(docx, "").trim();
    	}
    	if (fileType == docIndex) {
    		return fileName.replace(doc, "").trim();
    	}
		return null;
	}


	private static int checkFiletype(String fileName) {
    	if (fileName.contains(txt)) {
    		return txtIndex;
    	}
    	if (fileName.contains(pdf)) {
    		return pdfIndex;
    	}
    	if (fileName.contains(docx)) {
    		return docxIndex;
    	}
    	if (fileName.contains(doc)) {
    		return docIndex;
    	}
		return -1;
	}
	
    private static File TXT2TXT(File file) {
		if (file == null) {
			return null;
		}
		
        String fileName = getFileName(file.getName());
        
        File outFile = null;
        BufferedReader br = null;
        
        try {  
            outFile = new File(path + fileName + txt);
            outFile.createNewFile();

            FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			String sCurrentLine;

    		br = new BufferedReader(new FileReader(file));

    		while ((sCurrentLine = br.readLine()) != null) {
    			bw.write(sCurrentLine);
    			bw.newLine();
    			bw.flush();
    		}
            
    		br.close();
			bw.close();
			
			return outFile;
        } catch (FileNotFoundException e) {   
            e.printStackTrace();  
        } catch (IOException e) {   
            e.printStackTrace();  
        }
		return null;
	}


	private static File PDF2TXT(File file) {  
		if (file == null) {
			return null;
		}
		
    	String result = null;  
        FileInputStream is = null;  
        PDDocument document = null;	
        File outFile = null;
        
        String fileName = getFileName(file.getName());
        
        try {  
            outFile = new File(path + fileName + txt);
            outFile.createNewFile();
            
            is = new FileInputStream(file);
            
            FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
            
            PDFParser parser = new PDFParser(is);  
            parser.parse();  
            document = parser.getPDDocument();  
            PDFTextStripper stripper = new PDFTextStripper();  
            result = stripper.getText(document);  
			bw.write(result);
			bw.close();
			
			return outFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();  
        } catch (IOException e) {
            e.printStackTrace();  
        } finally {
            if (is != null) {  
                try {  
                    is.close();  
                } catch (IOException e) {
                    e.printStackTrace();  
                }  
            }  
            if (document != null) {  
                try {
                    document.close();  
                } catch (IOException e) {
                    e.printStackTrace();  
                }  
            }  
        }  
        
    	return null;
    }

	private static File DOC2TXT(File file){
		if (file == null) {
			return null;
		}
		
		String result = null;  
        FileInputStream is = null;  
        File outFile = null;
        
        String fileName = getFileName(file.getName());
        
        try {  
            outFile = new File(path + fileName + txt);
            outFile.createNewFile();
            
            is = new FileInputStream(file);
            
            FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
            
			HWPFDocument doc = new HWPFDocument(is);     
			Range rang = doc.getRange();     
		    result = rang.text();
		        
			bw.write(result);
			bw.close();
			fw.close();
			
			return outFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();  
        } catch (IOException e) {
            e.printStackTrace();  
        } finally {
            if (is != null) {  
                try {  
                    is.close();  
                } catch (IOException e) {
                    e.printStackTrace();  
                }  
            }
        }  
		
		return null;
	}
	
	private static File DOCX2TXT(File file){
		if (file == null) {
			return null;
		}
		
		String result = null;  
        FileInputStream is = null;  
        File outFile = null;
        
        String fileName = getFileName(file.getName());
        
        try {  
            outFile = new File(path + fileName + txt);
            outFile.createNewFile();
            
            is = new FileInputStream(file);
            
            FileWriter fw = new FileWriter(outFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			POITextExtractor ex = ExtractorFactory.createExtractor(file);
			result = ex.getText();
		   
			bw.write(result);
			bw.close();
			fw.close();
			
			return outFile;
        } catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (OpenXML4JException e) {
			e.printStackTrace();
		} catch (XmlException e) {
			e.printStackTrace();
		}catch (FileNotFoundException e) {
            e.printStackTrace();  
        } catch (IOException e) {
            e.printStackTrace();  
        } finally {
            if (is != null) {  
                try {  
                    is.close();  
                } catch (IOException e) {
                    e.printStackTrace();  
                }  
            }
        }  
		
		return null;
	}
	
//	//*
//    public static void main(String[] args) {
//    	File result = convertFile(new File("test/CVs/Different Formats/Russell Ong CV.docx"));
//    	System.out.println(result.getName());
//    	/*
//    	File[] results = convertDirectory(new File("test/CVs/LinkedIn"));
//    	System.out.println(results.length);
//    	for (File res : results) {
//    		System.out.println(res.getName());
//    	}
//    	//*/
//    }
//    //*/
}  