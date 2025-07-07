/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.coletivoJava.fw.projetos.erpColetivoJava.implementacao.cucumber.fluxomensagemorigemwhatsapp.etapas.testes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 *
 * @author salvio
 */
public class SimpleServletInputStreamTestes extends
        ServletInputStream {

    private final InputStream delegate;

    public SimpleServletInputStreamTestes(String bodyUtf8) {
        this.delegate = new ByteArrayInputStream(bodyUtf8.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

    @Override
    public boolean isFinished() {
        try {
            return delegate.available() == 0;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
        /* não‑bloqueante? ignora */ }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

}
