package com.vsmoon.editor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ImageUtils {

    /**
     * Carga un icono desde el classpath (/assets/...) y lo escala.
     */
    public static ImageIcon loadIcon(String path, int width, int height) {
        try {
            // Se asegura de buscar desde la raíz del classpath
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            
            InputStream is = ImageUtils.class.getResourceAsStream(path);
            if (is == null) {
                System.err.println("No se encontró el recurso: " + path);
                return null;
            }
            
            BufferedImage img = ImageIO.read(is);
            if (img != null) {
                Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + path);
            e.printStackTrace();
        }
        return null;
    }
}