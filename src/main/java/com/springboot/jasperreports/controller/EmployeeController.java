package com.springboot.jasperreports.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.jasperreports.model.DownloadResponseVO;
import com.springboot.jasperreports.model.Employee;
import com.springboot.jasperreports.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class EmployeeController {

	final Logger log = LoggerFactory.getLogger(this.getClass());
	final ModelAndView model = new ModelAndView();

	// @Autowired annotation provides the automatic dependency injection.
	@Autowired
	EmployeeService eservice;

	// Method to display the index page of the application.
	@GetMapping(value= "/welcome")
	public ModelAndView index() {
		log.info("Showing the welcome page.");
		model.setViewName("welcome");
		return model;
	}

	// Method to create the pdf report via jasper framework.
	@GetMapping(value = "/view")
	public ModelAndView viewReport() {
		log.info("Preparing the pdf report via jasper.");
		try {
			createPdfReport(eservice.findAll());
			log.info("File successfully saved at the given path.");
		} catch (final Exception e) {
			log.error("Some error has occured while preparing the employee pdf report.");
			e.printStackTrace();
		}
		// Returning the view name as the index page for ease.
		model.setViewName("welcome");
		return model;
	}

	@GetMapping("/view-report")
	public ResponseEntity<Resource> gerarDareDadosBasicos() throws Exception {

		DownloadResponseVO download = printReport(eservice.findAll());

		HttpHeaders headersOut = new HttpHeaders();
		headersOut.setContentType(MediaType.valueOf(download.getMediaType()));
		ContentDisposition contentDisposition = ContentDisposition
				.builder(download.getMediaType())
				.filename(download.getNome())
				.build();
		headersOut.setContentDisposition(contentDisposition);

		return ResponseEntity.ok().headers(headersOut).body(download.getArquivo());

	}



	// Method to create the pdf file using the employee list datasource.
	private void createPdfReport(final List<Employee> employees) throws JRException {
		// Fetching the .jrxml file from the resources folder.
		final InputStream stream = this.getClass().getResourceAsStream("/report.jrxml");

		// Compile the Jasper report from .jrxml to .japser
		final JasperReport report = JasperCompileManager.compileReport(stream);

		// Fetching the employees from the data source.
		final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(employees);

		// Adding the additional parameters to the pdf.
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("createdBy", "douglas_rezende");

		// Filling the report with the employee data and additional parameters information.
		final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);

		// Users can change as per their project requrirements or can take it as request input requirement.
		// For simplicity, this tutorial will automatically place the file under the "c:" drive.
		// If users want to download the pdf file on the browser, then they need to use the "Content-Disposition" technique.
		final String filePath = "C:\\Users\\DouglasRezende\\Desktop\\DOUGLAS\\";
		// Export the report to a PDF file.
		JasperExportManager.exportReportToPdfFile(print, filePath + "Employee_report.pdf");
	}

	private DownloadResponseVO printReport(final List<Employee> employees) throws JRException {

		final InputStream stream = this.getClass().getResourceAsStream("/report.jrxml");

		// Compile the Jasper report from .jrxml to .japser
		final JasperReport report = JasperCompileManager.compileReport(stream);

		// Fetching the employees from the data source.
		final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(employees);

		// Adding the additional parameters to the pdf.
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("createdBy", "Douglas Rezende");

		// Filling the report with the employee data and additional parameters information.
		final JasperPrint print = JasperFillManager.fillReport(report, parameters, source);

       byte[] pdfFile = JasperExportManager.exportReportToPdf(print);
       Resource inputStreamSource = new ByteArrayResource(pdfFile);

       return DownloadResponseVO.builder()
				.nome("file_export.pdf")
				.mediaType(MediaType.APPLICATION_PDF_VALUE)
				.arquivo(inputStreamSource)
				.build();

       

	}
}
