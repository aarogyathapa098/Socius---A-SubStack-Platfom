package controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.UploadPathUtil;

@WebServlet("/uploads/post-images/*")
public class PostImageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String fileName = Paths.get(pathInfo).getFileName().toString();
        if (!pathInfo.equals("/" + fileName)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Path uploadDirectory = UploadPathUtil.getPostImageDirectory().toAbsolutePath().normalize();
        Path imagePath = uploadDirectory.resolve(fileName).normalize();
        if (!imagePath.startsWith(uploadDirectory) || !Files.isRegularFile(imagePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getServletContext().getMimeType(fileName);
        if (contentType == null) {
            contentType = Files.probeContentType(imagePath);
        }
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        response.setHeader("Cache-Control", "public, max-age=86400");
        Files.copy(imagePath, response.getOutputStream());
    }
}
