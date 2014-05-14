package ch.tkuhn.nanopub.server;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class PeerListPage extends Page {

	private boolean asHtml;

	public static void show(ServerRequest req, HttpServletResponse httpResp) throws IOException {
		PeerListPage obj = new PeerListPage(req, httpResp);
		obj.show();
	}

	public PeerListPage(ServerRequest req, HttpServletResponse httpResp) {
		super(req, httpResp);
		String rf = getReq().getPresentationFormat();
		if (rf == null) {
			String suppFormats = "text/plain,text/html";
			asHtml = "text/html".equals(Utils.getMimeType(getHttpReq(), suppFormats));
		} else {
			asHtml = "text/html".equals(getReq().getPresentationFormat());
		}
	}

	public void show() throws IOException {
		DBCollection coll = NanopubDb.get().getPeerCollection();
		DBCursor cursor = coll.find();
		int c = 0;
		printStart();
		while (cursor.hasNext()) {
			c++;
			printElement(cursor.next().get("_id").toString());
		}
		if (c == 0 && asHtml) {
			println("<li><em>(no known peers)</em></li>");
		}
		printEnd();
		if (asHtml) {
			getResp().setContentType("text/html");
		} else {
			getResp().setContentType("text/plain");
		}
	}

	private void printStart() throws IOException {
		if (asHtml) {
			printHtmlHeader("Nanopub Server: List of peers");
			println("<h3>List of peers</h3>");
			println("<p>[ <a href=\"peers.txt\">as plain text</a> | <a href=\".\">home</a> ]</p>");
			println("<ul>");
		}
	}

	private void printElement(String peerUrl) throws IOException {
		if (asHtml) {
			println("<li><a href=\"" + peerUrl + "\">" + peerUrl + "</a></li>");
		} else {
			println(peerUrl);
		}
	}

	private void printEnd() throws IOException {
		if (asHtml) {
			println("</ul>");
			printHtmlFooter();
		}
	}

}