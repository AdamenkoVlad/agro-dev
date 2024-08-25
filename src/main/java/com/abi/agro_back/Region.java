package com.abi.agro_back;

public class Region {
    public Region(String slug, String rus) {
        this.slug = slug;
        this.rus = rus;
    }

    public Region() {
    }

    private String slug;

    private String rus;

    public String getSlug() {
        return slug;
    }

    public String getRus() {
        return rus;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setRus(String rus) {
        this.rus = rus;
    }

}
