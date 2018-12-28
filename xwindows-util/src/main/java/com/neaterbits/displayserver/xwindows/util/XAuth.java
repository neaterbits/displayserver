package com.neaterbits.displayserver.xwindows.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nhl
 *
 */
public final class XAuth {

    private final String host;
    private final String communicationProtocol;
    private final int display;
    
    private final String authorizationProtocol;
    private final byte [] authorizationData;
    
    private XAuth(String host, String communicationProtocol, int display, String authorizationProtocol,
            byte [] authorizationData) {
        super();
        this.host = host;
        this.communicationProtocol = communicationProtocol;
        this.display = display;
        this.authorizationProtocol = authorizationProtocol;
        this.authorizationData = authorizationData;
    }

    String getHost() {
        return host;
    }

    String getCommunicationProtocol() {
        return communicationProtocol;
    }

    int getDisplay() {
        return display;
    }

    public String getAuthorizationProtocol() {
        return authorizationProtocol;
    }

    public byte [] getAuthorizationData() {
        return authorizationData;
    }

    @Override
    public String toString() {
        return "XAuth [host=" + host + ", communicationProtocol=" + communicationProtocol + ", display=" + display
                + ", authorizationProtocol=" + authorizationProtocol + ", authorizationData=" + authorizationData + "]";
    }

    
    public static XAuth getXAuthInfo(int connectDisplay, String protocol) throws IOException {
    
        final List<XAuth> xAuths = XAuth.getXAuthInfo();
        
        final XAuth found = xAuths.stream()
                .filter(xAuth -> xAuth.getAuthorizationProtocol().equals(protocol))
                .filter(xAuth -> xAuth.getCommunicationProtocol() == null)
                .filter(xAuth -> xAuth.getDisplay() == connectDisplay)
                .findFirst()
                .orElse(null);
    
        return found;
    }

    
    static List<XAuth> getXAuthInfo() throws IOException {
        
        final String home = System.getenv("HOME");
        
        final ProcessBuilder builder = new ProcessBuilder("xauth", "-f", home + "/.Xauthority", "list");
        
        final Process process = builder.start();
        
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        for (;;) {
            final byte [] data = new byte[10000];
            
            final int bytesRead = process.getInputStream().read(data);
            
            if (bytesRead < 0) {
                break;
            }
            
            baos.write(data, 0, bytesRead);
        }

        final int exitCode;
        
        try {
             exitCode = process.waitFor();
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
        
        if (exitCode != 0) {
            throw new IllegalStateException("non 0 exit code from xauth");
        }
        
        return parseXAuthList(baos.toByteArray());
    }
    
    private static List<XAuth> parseXAuthList(byte [] data) throws IOException {

        final List<XAuth> list = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)))) {

            String line;
            
            while (null != (line = reader.readLine())) {
                
                System.out.println("## got line " + line);
                
                final String [] parts = line.trim().split("  ");
            
                if (parts.length != 3) {
                    throw new IllegalStateException("parts.length=" + parts.length);
                }
                
                final String [] host = parts[0].split("[\\:]");
                
                final int display = Integer.parseInt(host[host.length - 1]);
                
                final XAuth xAuth = new XAuth(
                        host[0],
                        host.length == 3 ? host[2] : null,
                        display,
                        parts[1],
                        fromHex(parts[2]));
                
                list.add(xAuth);
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        
        return list;
    }
    
    private static byte [] fromHex(String data) {
        
        if (data.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        
        final int num = data.length() / 2;
        
        final byte [] bytes = new byte[num];
        
        int dstIdx = 0;
        
        for (int i = 0; i < data.length(); i += 2) {
            final int value = Integer.parseInt(data.substring(i, i + 2), 16);
        
            bytes[dstIdx ++] = (byte)value;
        }
        
        return bytes;
    }
}
