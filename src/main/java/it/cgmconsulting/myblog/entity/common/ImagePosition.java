package it.cgmconsulting.myblog.entity.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum ImagePosition {

    // PRE = immagine che serve per l'anteprima (PREview)
    // HDR = immagine per l'header del dettaglio del post (HeaDeR)
    // CON = immagine che il writer può linkare all'interno del contenuto del post (CONtent)

    PRE("Preview", 200,200,10240L, 1),
    HDR("Header", 600,300,102400L, 1),
    CON("content",400,200, 20480L, 5);

    private String description;
    private int width;
    private int height;
    private long size;
    private int maxImages;

}
