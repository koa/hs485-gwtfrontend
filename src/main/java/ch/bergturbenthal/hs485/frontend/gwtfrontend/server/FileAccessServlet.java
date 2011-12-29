package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;

/**
 * Servlet implementation class UploadServlet
 */
public class FileAccessServlet extends HttpServlet {
	private static final long		serialVersionUID	= 1L;
	@Autowired
	private FileDataRepository	fileDataRepository;

	@Override
	public void init() throws ServletException {
		super.init();
		final WebApplicationContext requiredWebApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		final AutowireCapableBeanFactory autowireCapableBeanFactory = requiredWebApplicationContext.getAutowireCapableBeanFactory();
		autowireCapableBeanFactory.autowireBean(this);
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		final FileData fileData = fileDataRepository.findOne(req.getParameter("filename"));
		resp.setContentType(fileData.getMimeType());
		// resp.setContentLength(fileData.getFileDataContent().length());
		resp.getWriter().print(fileData.getFileDataContent());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final FileItemFactory fileItemFactory = new DiskFileItemFactory();
		final ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
		try {
			final List<FileItem> parsedRequest = upload.parseRequest(request);
			for (final FileItem file : parsedRequest) {
				System.out.println(file.getFieldName());
				if (!file.getFieldName().equals("file"))
					continue;
				final FileData data = new FileData();
				data.setFileName(file.getName());
				data.setMimeType(file.getContentType());
				data.setFileDataContent(new String(file.get(), "utf-8"));
				System.out.println("Save: " + data.getFileName());
				fileDataRepository.save(data);
			}

		} catch (final FileUploadException e) {
			throw new ServletException("Cannot Parse File", e);
		}
	}
}
