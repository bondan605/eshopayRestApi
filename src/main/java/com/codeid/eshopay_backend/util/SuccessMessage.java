package com.codeid.eshopay_backend.util;

public class SuccessMessage {
    public static class Http {

        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int ACCEPTED = 202;
        public static final int NO_CONTENT = 204;

        public static String getMessage(int statusCode) {
            switch (statusCode) {
                case OK:
                    return "OK";
                case CREATED:
                    return "Created";
                case ACCEPTED:
                    return "Accepted";
                case NO_CONTENT:
                    return "No Content";

                default:
                    return "Unknown Error";
            }
        }

    }

    public static class Cart {
        public static final String GET_CART_USER = "success get cart by user id";
        public static final String CREATE_CART_USER = "success create cart by user id";
        public static final String ADD_CART_ITEM = "success add cart item by cart id";
        public static final String UPDATE_CART_ITEM = "success update cart item";
        public static final String CLEAN_CART_ITEM = "success clean cart item";
        public static final String REMOVE_CART_ITEM = "success remove cart item";
    }

    public static class Order {
        public static final String CREATE_ORDER_USER = "success create order by user id";
        public static final String GET_ORDER_ID = "success get order by id";
        public static final String GET_ORDER_BY_USER = "success get order by user id";

    }

    public static class UploadImage {
        public static final String UPLOAD_IMAGE = "success upload image";
    }

    public static class FindAll {
        public static final String FIND_DATA = "success get all data";
    }

    public static class Update {
        public static final String Update_Multipart = "success update multipart";
        public static final String UPDATE_DATA = "success update data";
    }

    public static class Created {
        public static final String CREATED_DATA = "Success created data";
    }

    public static class GetById {
        public static final String GET_BY_ID = "success get Id";
    }

    public static class Delete {
        public static final String DELETE = "success delete data";
    }

}