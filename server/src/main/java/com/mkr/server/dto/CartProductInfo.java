package com.mkr.server.dto;

public record CartProductInfo(int id, String title, String imageSource, int price, int quantity, int totalAmount) {
}
