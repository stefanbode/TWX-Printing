import com.thingworx.common.RESTAPIConstants;
import com.thingworx.common.exceptions.InvalidRequestException;
import com.thingworx.entities.utils.ThingUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.resources.Resource;
import com.thingworx.things.Thing;
import com.thingworx.things.repository.FileRepositoryThing;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.primitives.StringPrimitive;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;

public class PDFtoPrinter extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(PDFtoPrinter.class);
	
	public PDFtoPrinter() {
		// TODO Auto-generated constructor stub
	}

	@ThingworxServiceDefinition(name = "PrintPDF", description = "", category = "", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "", baseType = "NOTHING", aspects = {})
	public void PrintPDF(
			@ThingworxServiceParameter(name = "FileRepository", description = "", baseType = "THINGNAME", aspects = {
					"isRequired:true", "thingTemplate:FileRepository" }) String FileRepository,
			@ThingworxServiceParameter(name = "printerName", description = "", baseType = "STRING") String printerName,
			@ThingworxServiceParameter(name = "pdfToPrinter", description = "", baseType = "STRING") String pdfToPrinter,
			@ThingworxServiceParameter(name = "pathToRepository", description = "", baseType = "STRING") String pathToRepository,
			@ThingworxServiceParameter(name = "pdfFileName", description = "", baseType = "STRING") String pdfFileName) throws Exception {
		_logger.trace("Entering Service: PrintPDF");
		_logger.trace("Exiting Service: PrintPDF");
		
		final Thing xlsRepositoryThing = ThingUtilities.findThing(FileRepository);
		if (xlsRepositoryThing == null) {
            throw new InvalidRequestException("File Repository [" + FileRepository + "] Does Not Exist", RESTAPIConstants.StatusCode.STATUS_NOT_FOUND);
        }
        if (!(xlsRepositoryThing instanceof FileRepositoryThing)) {
            throw new InvalidRequestException("Thing [" + FileRepository + "] Is Not A File Repository", RESTAPIConstants.StatusCode.STATUS_NOT_FOUND);
        }
		
		FileRepositoryThing filerepo = (FileRepositoryThing)ThingUtilities.findThing(FileRepository);
        filerepo.processServiceRequest("GetDirectoryStructure", (ValueCollection)null);

		String pdfToPrinterString = pathToRepository + "\\" + FileRepository + "\\" + pdfToPrinter;
		String pdfFileString = pathToRepository + "\\" + FileRepository + "\\" + pdfFileName;
		String cmd = pdfToPrinterString + " " + pdfFileString + " \"" + printerName + "\"";
//			cmd = pdfToPrinter + " " + PDFFullFilePath + " \"" + printerName + "\"";
		
//		_logger.warn("CMD COMMAND " + cmd);
		
		//Use the Java Runtime API to execute the command
		Runtime r = Runtime.getRuntime();
		Process pr = r.exec(cmd);
		
		//Process the results
//		BufferedReader stdInput = new BufferedReader(new InputStreamReader( pr.getInputStream() ));

//		String s;
//		//As you loop through the output, add it to the result InfoTable
//		while ((s = stdInput.readLine()) != null) {
//			ValueCollection row = new ValueCollection();
//			row.put("FileName", new StringPrimitive(s));
//			result.addRow(row);
//		}		
				
	}

}
