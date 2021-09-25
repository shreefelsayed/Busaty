package com.armjld.busaty.Utill;


public  class ArabicNormalizer {
    public String normalize(String input){
        //Replace Waw Hamza Above by Waw
        input = input.replaceAll("\u0624", "\u0648");

        //Replace Ta Marbuta by Ha
        input = input.replaceAll("\u0629", "\u0647");

        //Replace Ya
        // and Ya Hamza Above by Alif Maksura
        input = input.replaceAll("\u064A", "\u0649");
        input = input.replaceAll("\u0626", "\u0649");

        // Replace Alifs with Hamza Above/Below
        // and with Madda Above by Alif
        input = input.replaceAll("\u0622", "\u0627");
        input = input.replaceAll("\u0623", "\u0627");
        input = input.replaceAll("\u0625", "\u0627");

        return input;
    }
}