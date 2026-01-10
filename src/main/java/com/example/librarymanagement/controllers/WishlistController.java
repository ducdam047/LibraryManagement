package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.WishlistModel;
import com.example.librarymanagement.dtos.requests.wishlist.WishlistAddRequest;
import com.example.librarymanagement.dtos.responses.api.ApiResponse;
import com.example.librarymanagement.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ApiResponse<List<WishlistModel>> getWishlist() {
        List<WishlistModel> wishlistModels = wishlistService.getWishlist();
        return ApiResponse.<List<WishlistModel>>builder()
                .code(200)
                .message("List book wishlist")
                .data(wishlistModels)
                .build();
    }

    @GetMapping("/{wishlistId}")
    public ApiResponse<WishlistModel> getWishlistBook(@PathVariable int wishlistId) {
        WishlistModel wishlistModel = wishlistService.getWishlistBook(wishlistId);
        return ApiResponse.<WishlistModel>builder()
                .code(200)
                .message("Book wishlist")
                .data(wishlistModel)
                .build();
    }

    @PostMapping()
    public ApiResponse<WishlistModel> addToWishlist(@RequestBody WishlistAddRequest request) {
        return ApiResponse.<WishlistModel>builder()
                .code(201)
                .message("The book was added to wish list")
                .data(wishlistService.addToWishlist(request))
                .build();
    }

    @DeleteMapping("/{wishlistId}")
    public ApiResponse<String> deleteFromWishlist(@PathVariable int wishlistId) {
        wishlistService.deleteFromWishlist(wishlistId);
        return ApiResponse.<String>builder()
                .code(204)
                .message("The book was deleted from wish list successfully")
                .data("Book removed")
                .build();
    }
}
