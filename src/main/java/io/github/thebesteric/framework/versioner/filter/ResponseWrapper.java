package io.github.thebesteric.framework.versioner.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintWriter printWriter = new PrintWriter(outputStream);

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) throws IOException {
                outputStream.write(b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                outputStream.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                outputStream.write(b, off, len);
            }

            @Override
            public void flush() throws IOException {
                outputStream.flush();
            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    public void flush() {
        try {
            printWriter.flush();
            printWriter.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public byte[] getContent() {
        flush();
        return outputStream.toByteArray();
    }
}
