package com.codeid.eshopay_backend.util;

public class ErrorMessage {

    public static class Http {
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;

        public static String getMessage(int statusCode) {
            switch (statusCode) {
                case BAD_REQUEST:
                    return "Bad Request";
                case UNAUTHORIZED:
                    return "Unauthorized";
                case FORBIDDEN:
                    return "Forbidden";
                case NOT_FOUND:
                    return "Not Found";
                default:
                    return "Unknown Error";
            }
        }
    }

    public static class Photo {
        public static final String UPLOAD_IMAGE = "need upload photo";
    }

    public static class Product {
        public static final String PRODUCT_NOT_FOUND = "product not found";
        public static final String PRODUCT_NOT_EXIST = "product not exist in database";
        public static final String PRODUCT_DELETED = "product is marked as deleted";
        public static final String PRODUCT_UNIT_PRICE_NULL = "unit price null for product";
        public static final String PRODUCT_STOCK_INSUNFFICIENT = "product stock insunfficient";
    }

    public static class Category {
        public static final String CATEGORY_NOT_FOUND = "category not found";

    }

    public static class Supplier {
        public static final String SUPPLIER_NOT_FOUND = "supplier not found";
    }

    public static class shipper {
        public static final String SHIPPER_NOT_FOUND = "shipper not found";

    }

    public static class Cart {
        public static final String CART_NOT_FOUND = "cart not found";
        public static final String CART_ALREADY_EXIST = "cart already exist";
        public static final String CART_EMPTY = "cart is empty or doesnot exist for id";
    }

    public static class CartItem {
        public static final String CART_ITEM_NOT_FOUND = "cart item not found";
    }

    public static class Order {
        public static final String ORDER_NOT_FOUND = "order not found";
    }

    public static class Bank {
        public static final String BANK_NOT_FOUND = "bank not found";
    }

    public static class Employee {
        public static final String EMPLOYEE_NOT_FOUND = "employee not found";
    }

    public static class Location {
        public static final String LOCATION_NOT_FOUND = "location not found";
    }

    public static class User {
        public static final String USER_NOT_FOUND = "user not found";
    }
}