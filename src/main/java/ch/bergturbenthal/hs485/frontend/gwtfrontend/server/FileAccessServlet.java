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
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.FileDataRepository;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.FileData;

/**
 * Servlet implementation class UploadServlet
 */
public class FileAccessServlet extends HttpServlet {
	private static final long		serialVersionUID	= 1L;
	private FileDataRepository	fileDataRepository;
	private TransactionTemplate	transactionTemplate;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileAccessServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		final FileData fileData = fileDataRepository.findOne(req.getParameter("filename"));
		resp.setContentType(fileData.getMimeType());
		resp.setContentLength(fileData.getFileDataContent().length);
		resp.getOutputStream().write(fileData.getFileDataContent());
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
				if (!file.getFieldName().equals("file"))
					continue;
				final FileData data = new FileData();
				data.setFileName(file.getName());
				data.setMimeType(file.getContentType());
				data.setFileDataContent(file.get());
				fileDataRepository.save(data);
			}

		} catch (final FileUploadException e) {
			throw new ServletException("Cannot Parse File", e);
		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
		final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"ch/bergturbenthal/hs485/frontend/gwtfrontend/server/webappContext.xml");
		final PlatformTransactionManager transactionManager = ctx.getBean("transactionManager", JpaTransactionManager.class);
		transactionTemplate = new TransactionTemplate(transactionManager);

		fileDataRepository = ctx.getBean(FileDataRepository.class);
	}

	@Override
	protected void service(final HttpServletRequest arg0, final HttpServletResponse arg1) throws ServletException, IOException {

		try {
			transactionTemplate.execute(new TransactionCallback<Void>() {

				public Void doInTransaction(final TransactionStatus status) {
					try {
						FileAccessServlet.super.service(arg0, arg1);
						return null;
					} catch (final ServletException e) {
						throw new RuntimeException(e);
					} catch (final IOException e) {
						throw new RuntimeException(e);
					}
				}
			});
		} catch (final RuntimeException ex) {
			if (ex.getCause() != null) {
				if (ex.getCause() instanceof IOException)
					throw (IOException) ex.getCause();
				if (ex.getCause() instanceof ServletException)
					throw (ServletException) ex.getCause();
			}
			throw ex;
		}

	}

}
