package com.myapp.service;


import com.myapp.ClasesPropias.ListaEnlazada.ListaEnlazada;
import com.myapp.model.Cancion;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;



@Service
public class CsvService {


    public byte[] generarCsvFavoritos(ListaEnlazada<Cancion> favoritos, String username) 
            throws IOException {
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
    
        out.write(0xEF);
        out.write(0xBB);
        out.write(0xBF);
        
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Título", "Artista", "Género", "Año", "Duración (segundos)")
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (int i = 0; i < favoritos.tamaño(); i++) {
                Cancion c = favoritos.obtener(i);
                
                csvPrinter.printRecord(
                    c.getId(),
                    c.getTitulo(),
                    c.getArtista(),
                    c.getGenero().toString(),
                    c.getAño(),
                    String.format("%.2f", c.getDuracion())
                );
            }
            
            csvPrinter.flush();
        }

        return out.toByteArray();
    }

    public String generarNombreArchivo(String username) {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("favoritos_%s_%s.csv", username, timestamp);
    }
}
