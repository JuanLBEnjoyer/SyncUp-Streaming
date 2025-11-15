package com.myapp.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public class SimilitudDto {

    private double similitud;

     public SimilitudDto() {}

    @DecimalMin(value = "0.0", inclusive = true, message = "La similitud no puede ser menor que 0")
    @DecimalMax(value = "1.0", inclusive = true, message = "La similitud no puede ser mayor que 1")
    public double getSimilitud() { 
        return similitud; 
    }
    public void setSimilitud(double similitud) { 
        this.similitud = similitud; 
    }
}

