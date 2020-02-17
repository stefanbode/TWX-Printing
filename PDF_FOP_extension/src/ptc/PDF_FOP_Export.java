package ptc;

import com.thingworx.common.RESTAPIConstants;
import com.thingworx.common.exceptions.InvalidRequestException;
import com.thingworx.entities.utils.ThingUtilities;
import com.thingworx.logging.LogUtilities;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.resources.Resource;
import com.thingworx.things.Thing;
import com.thingworx.things.repository.FileRepositoryThing;
import com.thingworx.types.collections.ValueCollection;

import java.io.File;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;

public class PDF_FOP_Export extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(PDF_FOP_Export.class);
	private String XMLFullFilePath = "";
	private String XSLFullFilePath = "";
	private String OutputPDFFullFilePath = "";
	private String FOPCONFFullFilePath = "";
	
	public PDF_FOP_Export() {
		// TODO Auto-generated constructor stub
	}
	
	@ThingworxServiceDefinition(name = "CreatePDF", description = "Create PDF from XML and XSL file using FOP Apache.", category = "", isAllowOverride = false, aspects = {
			"isAsync:false" })
	@ThingworxServiceResult(name = "Result", description = "True - Success, False - Error.", baseType = "BOOLEAN", aspects = {})
	public Boolean CreatePDF(
			@ThingworxServiceParameter(name = "FileRepository", description = "", baseType = "THINGNAME", aspects = {
					"isRequired:true", "thingTemplate:FileRepository" }) String FileRepository,
			@ThingworxServiceParameter(name = "OutputFileName", description = "", baseType = "STRING", aspects = {
					"isRequired:true" }) String OutputFileName,
			@ThingworxServiceParameter(name = "XMLFileName", description = "", baseType = "STRING") String XMLFileName,
			@ThingworxServiceParameter(name = "FOPCONFFileName", description = "", baseType = "STRING") String FOPCONFFileName,
			@ThingworxServiceParameter(name = "XSLFileName", description = "", baseType = "STRING", aspects = {
					"isRequired:true" }) String XSLFileName) throws Exception {
		
		
		final Thing xlsRepositoryThing = ThingUtilities.findThing(FileRepository);
		if (xlsRepositoryThing == null) {
            throw new InvalidRequestException("File Repository [" + FileRepository + "] Does Not Exist", RESTAPIConstants.StatusCode.STATUS_NOT_FOUND);
        }
        if (!(xlsRepositoryThing instanceof FileRepositoryThing)) {
            throw new InvalidRequestException("Thing [" + FileRepository + "] Is Not A File Repository", RESTAPIConstants.StatusCode.STATUS_NOT_FOUND);
        }
		
		FileRepositoryThing filerepo = (FileRepositoryThing)ThingUtilities.findThing(FileRepository);
        filerepo.processServiceRequest("GetDirectoryStructure", (ValueCollection)null);
        
        XMLFullFilePath = filerepo.getRootPath() + File.separator + XMLFileName;
        XSLFullFilePath = filerepo.getRootPath() + File.separator + XSLFileName;
        OutputPDFFullFilePath = filerepo.getRootPath() + File.separator + OutputFileName;
        FOPCONFFullFilePath = filerepo.getRootPath() + File.separator + FOPCONFFileName;
        
		try {
			_logger.trace("FOP ExampleXML2PDF\n");
			_logger.trace("Preparing...");
            
            File xmlfile = new File(XMLFullFilePath);
            File xsltfile = new File(XSLFullFilePath);
            File pdffile = new File(OutputPDFFullFilePath);
    		
    		_logger.trace("Transforming...");
            // configure fopFactory as desired
            FopFactory fopFactory = FopFactory.newInstance(new File(FOPCONFFullFilePath));

            //new File("src/fop.xconf");
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired
            // Setup output
            OutputStream out = new java.io.FileOutputStream(pdffile);
            out = new java.io.BufferedOutputStream(out);
            try {
                // Construct fop with desired output format
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF,
                        foUserAgent, out);
                // Setup XSLT
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory
                        .newTransformer(new StreamSource(xsltfile));
                // Set the value of a <param> in the stylesheet
                transformer.setParameter("versionParam", "2.0");
                // Setup input for XSLT transformation
                Source src = new StreamSource(xmlfile);
                // Resulting SAX events (the generated FO) must be piped through
                // to FOP
                Result res = new SAXResult(fop.getDefaultHandler());
                // Start XSLT transformation and FOP processing
                transformer.transform(src, res);
                
            } finally {
                out.close();
            }
            return true;
        } catch (Exception e) {
        	_logger.error("ERROR!!");
        	_logger.error(e.toString());
            return false;
        }
		
	}

}
