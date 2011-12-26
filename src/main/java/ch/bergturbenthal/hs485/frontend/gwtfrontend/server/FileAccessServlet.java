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
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;

/**
 * Servlet implementation class UploadServlet
 */
public class FileAccessServlet extends HttpServlet {
	private static final long		serialVersionUID	= 1L;
	private FileDataRepository	fileDataRepository;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileAccessServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws ServletException {
		super.init();
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"ch/bergturbenthal/hs485/frontend/gwtfrontend/server/webappContext.xml");

		fileDataRepository = ctx.getBean(FileDataRepository.class);
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
